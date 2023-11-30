/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.native.internal

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.jetbrains.kotlin.commonizer.*
import org.jetbrains.kotlin.compilerRunner.konanHome
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtensionOrNull
import org.jetbrains.kotlin.gradle.internal.KOTLIN_MODULE_GROUP
import org.jetbrains.kotlin.gradle.internal.PLATFORM_INTEGERS_SUPPORT_LIBRARY
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.ide.ideaImportDependsOn
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.compilationImpl.KotlinCompilationSideEffectCoroutine
import org.jetbrains.kotlin.gradle.plugin.sources.*
import org.jetbrains.kotlin.gradle.plugin.sources.internal
import org.jetbrains.kotlin.gradle.targets.metadata.findMetadataCompilation
import org.jetbrains.kotlin.gradle.targets.metadata.isKotlinGranularMetadataEnabled
import org.jetbrains.kotlin.gradle.utils.filesProvider
import org.jetbrains.kotlin.gradle.utils.konanDistribution
import java.io.File

internal fun Project.setupKotlinNativePlatformDependencies() {
    val kotlin = multiplatformExtensionOrNull ?: return

    launch {
        if (isPlatformIntegerCommonizationEnabled) {
            kotlin.nativeRootSourceSets().forEach { sourceSet ->
                dependencies.add(
                    sourceSet.implementationConfigurationName,
                    "$KOTLIN_MODULE_GROUP:$PLATFORM_INTEGERS_SUPPORT_LIBRARY:${getKotlinPluginVersion()}"
                )
            }
        }
    }
}

internal suspend fun KotlinMultiplatformExtension.nativeRootSourceSets(): Collection<KotlinSourceSet> {
    val nativeSourceSets = sourceSets.filter { sourceSet -> sourceSet.internal.commonizerTarget.await() != null }
    return nativeSourceSets.filter { sourceSet ->
        val allVisibleSourceSets = sourceSet.dependsOn + getVisibleSourceSetsFromAssociateCompilations(sourceSet)
        allVisibleSourceSets.none { dependency ->
            dependency in nativeSourceSets
        }
    }
}

/**
 * Function signature needs to be kept stable since this is used during import
 * in IDEs (KotlinCommonizerModelBuilder) < 222
 *
 * IDEs >= will use the [ideaImportDependsOn] infrastructure
 */
@JvmName("isAllowCommonizer")
internal fun Project.isAllowCommonizer(): Boolean {
    assert(state.executed) { "'isAllowCommonizer' can only be called after project evaluation" }
    multiplatformExtensionOrNull ?: return false

    return multiplatformExtension.targets.any { it.platformType == KotlinPlatformType.native }
            && isKotlinGranularMetadataEnabled
}
