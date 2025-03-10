/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.statistics

import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ProviderFactory
import org.gradle.initialization.BuildRequestMetaData
import org.gradle.invocation.DefaultGradle
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import org.jetbrains.kotlin.gradle.plugin.BuildEventsListenerRegistryHolder
import org.jetbrains.kotlin.gradle.plugin.PropertiesBuildService
import org.jetbrains.kotlin.gradle.plugin.internal.ConfigurationTimePropertiesAccessor
import org.jetbrains.kotlin.gradle.plugin.internal.configurationTimePropertiesAccessor
import org.jetbrains.kotlin.gradle.plugin.internal.usedAtConfigurationTime
import org.jetbrains.kotlin.gradle.plugin.statistics.KotlinBuildStatHandler.Companion.runSafe
import org.jetbrains.kotlin.statistics.BuildSessionLogger
import org.jetbrains.kotlin.statistics.BuildSessionLogger.Companion.STATISTICS_FOLDER_NAME
import org.jetbrains.kotlin.statistics.metrics.BooleanMetrics
import org.jetbrains.kotlin.statistics.metrics.StatisticsValuesConsumer
import org.jetbrains.kotlin.statistics.metrics.NumericalMetrics
import org.jetbrains.kotlin.statistics.metrics.StringMetrics
import java.io.Closeable
import java.io.File
import javax.management.MBeanServer
import javax.management.ObjectName
import kotlin.system.measureTimeMillis

/**
 * Interface for populating statistics collection method via JXM interface
 * JMX could be used for reporting both from other JVMs, other versions
 * of Kotlin Plugin and other classloaders
 */

interface KotlinBuildStatsMXBean {
    fun reportBoolean(name: String, value: Boolean, subprojectName: String?, weight: Long?): Boolean

    fun reportNumber(name: String, value: Long, subprojectName: String?, weight: Long?): Boolean

    fun reportString(name: String, value: String, subprojectName: String?, weight: Long?): Boolean
}

internal abstract class KotlinBuildStatsService internal constructor() : StatisticsValuesConsumer, Closeable {
    companion object {
        // Property name for disabling saving statistical information
        private const val ENABLE_STATISTICS_PROPERTY_NAME = "enable_kotlin_performance_profile"

        // Property used for tests. Build will fail fast if collected value doesn't fit regexp
        const val FORCE_VALUES_VALIDATION = "kotlin_performance_profile_force_validation"

        // default state
        private const val DEFAULT_STATISTICS_STATE = true

        // "emergency file" collecting statistics is disabled it the file exists
        private const val DISABLE_STATISTICS_FILE_NAME = "${STATISTICS_FOLDER_NAME}/.disable"

        /**
         * Method for getting IStatisticsValuesConsumer for reporting some statistics
         * Could be invoked during any build phase after applying first Kotlin plugin and
         * until build is completed
         */
        @JvmStatic
        @Synchronized
        fun getInstance(): KotlinBuildStatsService? {
            if (statisticsIsEnabled != true) {
                return null
            }
            return kotlinBuildStatsServicesRegistry?.getDefaultService()
        }

        /**
         * Method for creating new instance of IStatisticsValuesConsumer
         * It could be invoked only when applying Kotlin gradle plugin.
         * When executed, this method checks, whether it is already executed in the current build.
         * If it was not executed, the new instance of IStatisticsValuesConsumer is created
         *
         * If it was already executed in the same classpath (i.e., with the same version of Kotlin plugin),
         * the previously returned instance is returned.
         *
         * If it was already executed in the other classpath, a JXM implementation is returned.
         *
         * [closeServices] must be called at the end of the build in order to release resources.
         */
        @JvmStatic
        @Synchronized
        internal fun getOrCreateInstance(project: Project): KotlinBuildStatsService? {

            return runSafe("${KotlinBuildStatsService::class.java}.getOrCreateInstance") {
                val gradle = project.gradle
                val configurationTimePropertiesAccessor = project.configurationTimePropertiesAccessor
                statisticsIsEnabled = statisticsIsEnabled
                    ?: checkStatisticsEnabled(gradle, project.providers, configurationTimePropertiesAccessor)
                if (statisticsIsEnabled != true) {
                    null
                } else {
                    val registry = kotlinBuildStatsServicesRegistry ?: KotlinBuildStatsServicesRegistry().also {
                        kotlinBuildStatsServicesRegistry = it
                    }

                    val defaultServiceName =
                        KotlinBuildStatsServicesRegistry.getBeanName(KotlinBuildStatsServicesRegistry.DEFAULT_SERVICE_QUALIFIER)
                    val instance = kotlinBuildStatsServicesRegistry?.getDefaultService()
                    if (instance != null) {
                        registry.logger.debug("$defaultServiceName is already instantiated. Current instance is $instance")
                        return@runSafe instance
                    }

                    registry.registerServices(project)

                    BuildEventsListenerRegistryHolder.getInstance(project).listenerRegistry.onTaskCompletion(project.provider {
                        OperationCompletionListener { event ->
                            if (event is TaskFinishEvent) {
                                reportTaskIfNeed(event.descriptor.name)
                            }
                        }
                    })

                    registry.getDefaultService() ?: error("The default kotlin build stats $defaultServiceName service wasn't initialized")
                }
            }
        }

        fun closeServices() {
            kotlinBuildStatsServicesRegistry?.close()
            kotlinBuildStatsServicesRegistry = null
        }

        protected fun reportTaskIfNeed(task: String) {
            val metric = when (task.substringAfterLast(":")) {
                "dokkaHtml" -> BooleanMetrics.ENABLED_DOKKA_HTML
                "dokkaGfm" -> BooleanMetrics.ENABLED_DOKKA_GFM
                "dokkaJavadoc" -> BooleanMetrics.ENABLED_DOKKA_JAVADOC
                "dokkaJekyll" -> BooleanMetrics.ENABLED_DOKKA_JEKYLL
                "dokkaHtmlMultiModule" -> BooleanMetrics.ENABLED_DOKKA_HTML_MULTI_MODULE
                "dokkaGfmMultiModule" -> BooleanMetrics.ENABLED_DOKKA_GFM_MULTI_MODULE
                "dokkaJekyllMultiModule" -> BooleanMetrics.ENABLED_DOKKA_JEKYLL_MULTI_MODULE
                "dokkaHtmlCollector" -> BooleanMetrics.ENABLED_DOKKA_HTML_COLLECTOR
                "dokkaGfmCollector" -> BooleanMetrics.ENABLED_DOKKA_GFM_COLLECTOR
                "dokkaJavadocCollector" -> BooleanMetrics.ENABLED_DOKKA_JAVADOC_COLLECTOR
                "dokkaJekyllCollector" -> BooleanMetrics.ENABLED_DOKKA_JEKYLL_COLLECTOR
                else -> null
            }
            metric?.also { getInstance()?.report(it, true) }
        }


        /**
         * Invokes provided collector if the reporting service is initialised.
         * The duration of collector's wall time is reported into overall overhead metric.
         */
        fun applyIfInitialised(collector: (KotlinBuildStatsService) -> Unit) {
            getInstance()?.apply {
                try {
                    val duration = measureTimeMillis {
                        collector.invoke(this)
                    }
                    this.report(NumericalMetrics.STATISTICS_COLLECT_METRICS_OVERHEAD, duration)
                } catch (e: Throwable) {
                    KotlinBuildStatHandler.logException("Could collect statistics metrics", e)
                }
            }
        }

        private var kotlinBuildStatsServicesRegistry: KotlinBuildStatsServicesRegistry? = null

        private var statisticsIsEnabled: Boolean? = null

        private fun checkStatisticsEnabled(
            gradle: Gradle,
            providerFactory: ProviderFactory,
            configurationTimePropertiesAccessor: ConfigurationTimePropertiesAccessor,
        ): Boolean {
            return if (File(gradle.gradleUserHomeDir, DISABLE_STATISTICS_FILE_NAME).exists()) {
                false
            } else {
                providerFactory.gradleProperty(ENABLE_STATISTICS_PROPERTY_NAME)
                    .usedAtConfigurationTime(configurationTimePropertiesAccessor)
                    .orNull?.toBoolean() ?: DEFAULT_STATISTICS_STATE
            }
        }
    }

    override fun close() {
    }
    /**
     * Collects metrics at the end of a build
     */
    open fun recordBuildFinish(action: String?, buildFailed: Boolean, metricsContainer: NonSynchronizedMetricsContainer, buildId: String) {}

    open fun recordProjectsEvaluated(project: Project) {}
}

internal class JMXKotlinBuildStatsService(private val mbs: MBeanServer, private val beanName: ObjectName) :
    KotlinBuildStatsService() {

    private fun callJmx(method: String, type: String, metricName: String, value: Any, subprojectName: String?, weight: Long?): Any? {
        return mbs.invoke(
            beanName,
            method,
            arrayOf(metricName, value, subprojectName, weight),
            arrayOf("java.lang.String", type, "java.lang.String", "java.lang.Long")
        )
    }

    override fun report(metric: BooleanMetrics, value: Boolean, subprojectName: String?, weight: Long?) =
        runSafe("report metric ${metric.name}") {
            callJmx("reportBoolean", "boolean", metric.name, value, subprojectName, weight)
        } as? Boolean ?: false

    override fun report(metric: NumericalMetrics, value: Long, subprojectName: String?, weight: Long?) =
        runSafe("report metric ${metric.name}") {
            callJmx("reportNumber", "long", metric.name, value, subprojectName, weight)
        } as? Boolean ?: false

    override fun report(metric: StringMetrics, value: String, subprojectName: String?, weight: Long?) =
        runSafe("report metric ${metric.name}") {
            callJmx("reportString", "java.lang.String", metric.name, value, subprojectName, weight)
        } as? Boolean ?: false

}

internal abstract class AbstractKotlinBuildStatsService(
    project: Project,
    private val beanName: ObjectName,
) : KotlinBuildStatsService() {
    companion object {
        //test only
        const val CUSTOM_LOGGER_ROOT_PATH = "kotlin.session.logger.root.path"

        private val logger = Logging.getLogger(AbstractKotlinBuildStatsService::class.java)
    }

    private val forcePropertiesValidation: Boolean

    private val customSessionLoggerRootPath: String?

    init {
        val propertiesBuildService = PropertiesBuildService.registerIfAbsent(project).get()
        forcePropertiesValidation = propertiesBuildService.get(FORCE_VALUES_VALIDATION, project)?.toBoolean() ?: false
        customSessionLoggerRootPath = propertiesBuildService.get(CUSTOM_LOGGER_ROOT_PATH, project)?.also {
            logger.warn("$CUSTOM_LOGGER_ROOT_PATH property for test purpose only")
        }
    }

    private val sessionLoggerRootPath =
        customSessionLoggerRootPath?.let { File(it) } ?: project.gradle.gradleUserHomeDir

    protected val sessionLogger = BuildSessionLogger(
        sessionLoggerRootPath,
        forceValuesValidation = forcePropertiesValidation,
    )

    private fun gradleBuildStartTime(gradle: Gradle): Long? {
        return (gradle as? DefaultGradle)?.services?.get(BuildRequestMetaData::class.java)?.startTime
    }

    override fun recordProjectsEvaluated(project: Project) {
        runSafe("${DefaultKotlinBuildStatsService::class.java}.projectEvaluated") {
            if (!sessionLogger.isBuildSessionStarted()) {
                sessionLogger.startBuildSession(
                    DaemonReuseCounter.incrementAndGetOrdinal(),
                    gradleBuildStartTime(project.gradle),
                )
            }
        }
    }

    @Synchronized
    override fun close() {
        KotlinBuildStatHandler().buildFinished(beanName)
        super.close()
    }
}

internal class DefaultKotlinBuildStatsService internal constructor(
    project: Project,
    beanName: ObjectName,
) : AbstractKotlinBuildStatsService(project, beanName),
    KotlinBuildStatsMXBean {

    override fun report(metric: BooleanMetrics, value: Boolean, subprojectName: String?, weight: Long?): Boolean =
        KotlinBuildStatHandler().report(sessionLogger, metric, value, subprojectName, weight)

    override fun report(metric: NumericalMetrics, value: Long, subprojectName: String?, weight: Long?): Boolean =
        KotlinBuildStatHandler().report(sessionLogger, metric, value, subprojectName, weight)

    override fun report(metric: StringMetrics, value: String, subprojectName: String?, weight: Long?): Boolean =
        KotlinBuildStatHandler().report(sessionLogger, metric, value, subprojectName, weight)

    override fun reportBoolean(name: String, value: Boolean, subprojectName: String?, weight: Long?): Boolean =
        report(BooleanMetrics.valueOf(name), value, subprojectName, weight)

    override fun reportNumber(name: String, value: Long, subprojectName: String?, weight: Long?): Boolean =
        report(NumericalMetrics.valueOf(name), value, subprojectName, weight)

    override fun reportString(name: String, value: String, subprojectName: String?, weight: Long?): Boolean =
        report(StringMetrics.valueOf(name), value, subprojectName, weight)

    //only one jmx bean service should report global metrics
    override fun recordBuildFinish(action: String?, buildFailed: Boolean, metricsContainer: NonSynchronizedMetricsContainer, buildId: String) {
        KotlinBuildStatHandler().reportBuildFinished(sessionLogger, action, buildFailed, buildId, metricsContainer)
    }
}
