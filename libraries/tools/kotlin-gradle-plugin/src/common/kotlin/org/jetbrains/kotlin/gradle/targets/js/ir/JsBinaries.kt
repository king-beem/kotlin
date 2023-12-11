/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.ir

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptionsHelper
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.extension
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenExec
import org.jetbrains.kotlin.gradle.targets.js.dsl.Distribution
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsBinaryContainer.Companion.generateBinaryName
import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject
import org.jetbrains.kotlin.gradle.targets.js.subtargets.createDefaultDistribution
import org.jetbrains.kotlin.gradle.targets.js.typescript.TypeScriptValidationTask
import org.jetbrains.kotlin.gradle.tasks.configuration.KotlinJsIrLinkConfig
import org.jetbrains.kotlin.gradle.tasks.registerTask
import org.jetbrains.kotlin.gradle.tasks.withType
import org.jetbrains.kotlin.gradle.utils.filesProvider
import org.jetbrains.kotlin.gradle.utils.lowerCamelCaseName
import org.jetbrains.kotlin.gradle.utils.mapToFile
import java.nio.file.Path

interface JsBinary {
    val compilation: KotlinJsCompilation
    val name: String
    val mode: KotlinJsBinaryMode
    val distribution: Distribution
}

sealed class JsIrBinary(
    final override val compilation: KotlinJsIrCompilation,
    final override val name: String,
    override val mode: KotlinJsBinaryMode,
) : JsBinary {
    override val distribution: Distribution =
        createDefaultDistribution(compilation.target.project, compilation.target.targetName, name)

    val linkTaskName: String = linkTaskName()

    val linkSyncTaskName: String = linkSyncTaskName()

    val validateGeneratedTsTaskName: String = validateTypeScriptTaskName()

    var generateTs: Boolean = false

    val linkTask: TaskProvider<KotlinJsIrLink> = project.registerTask(linkTaskName, KotlinJsIrLink::class.java, listOf(project))

    val linkSyncTask: TaskProvider<DefaultIncrementalSyncTask> = project.registerTask<DefaultIncrementalSyncTask>(
        linkSyncTaskName
    ) { task ->
        task.from.from(
            linkTask.flatMap { linkTask ->
                linkTask.destinationDirectory.map { it.asFile }
            }
        )

        task.from.from(project.tasks.named(compilation.processResourcesTaskName))

        task.destinationDirectory.set(compilation.npmProject.dist.mapToFile())
    }

    val mainFileName: Provider<String> = linkTask.flatMap {
        it.compilerOptions.moduleName.zip(compilation.extension) { moduleName, extension ->
            "$moduleName.$extension"
        }
    }

    val mainFilePath: Provider<Path> = linkTask.map {
        it.destinationDirectory.get().asFile.toPath().resolve(mainFileName.get())
    }

    val mainFileSyncPath: Provider<Path> = linkSyncTask.map {
        it.destinationDirectory.get().toPath().resolve(mainFileName.get())
    }

    private fun linkTaskName(): String =
        lowerCamelCaseName(
            "compile",
            name,
            "Kotlin",
            target.targetName
        )

    private fun linkSyncTaskName(): String =
        lowerCamelCaseName(
            compilation.target.disambiguationClassifier,
            compilation.name.takeIf { it != KotlinCompilation.MAIN_COMPILATION_NAME },
            name,
            COMPILE_SYNC
        )

    private fun validateTypeScriptTaskName(): String =
        lowerCamelCaseName(
            compilation.target.disambiguationClassifier,
            compilation.name.takeIf { it != KotlinCompilation.MAIN_COMPILATION_NAME },
            name,
            TypeScriptValidationTask.NAME
        )

    val target: KotlinTarget
        get() = compilation.target

    val project: Project
        get() = target.project

    companion object {
        fun JsIrBinary.configLinkTask() {
            val configAction = KotlinJsIrLinkConfig(this)
            configAction.configureTask {
                it.libraries.from(project.filesProvider { compilation.runtimeDependencyFiles })
            }
            configAction.configureTask { task ->
                val targetCompilerOptions = (compilation.target as KotlinJsIrTarget).compilerOptions
                KotlinJsCompilerOptionsHelper.syncOptionsAsConvention(
                    targetCompilerOptions,
                    task.compilerOptions
                )

                task.modeProperty.set(mode)
                task.dependsOn(compilation.compileTaskProvider)
            }

            configAction.execute(linkTask)
        }
    }
}

open class Executable(
    compilation: KotlinJsIrCompilation,
    name: String,
    mode: KotlinJsBinaryMode,
) : JsIrBinary(
    compilation,
    name,
    mode
) {
    override val distribution: Distribution =
        createDefaultDistribution(
            compilation.target.project,
            compilation.target.targetName,
            super.distribution.distributionName
        )

    val executeTaskBaseName: String =
        generateBinaryName(
            compilation,
            mode,
            null
        )
}

open class ExecutableWasm(
    compilation: KotlinJsIrCompilation,
    name: String,
    mode: KotlinJsBinaryMode,
) : Executable(
    compilation,
    name,
    mode
) {
    val optimizeTaskName: String = optimizeTaskName()

    val optimizeTask: TaskProvider<BinaryenExec> by lazy {
        target.project.tasks
            .withType<BinaryenExec>()
            .named(optimizeTaskName)
    }

    private fun optimizeTaskName(): String =
        "${linkTaskName}Optimize"
}

class Library(
    compilation: KotlinJsIrCompilation,
    name: String,
    mode: KotlinJsBinaryMode,
) : JsIrBinary(
    compilation,
    name,
    mode
)

internal const val COMPILE_SYNC = "compileSync"