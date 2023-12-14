/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.commonizer.toolsDir
import org.jetbrains.kotlin.gradle.plugin.KotlinProjectSetupCoroutine
import org.jetbrains.kotlin.gradle.targets.native.internal.konanDistribution
import org.jetbrains.kotlin.gradle.tasks.locateOrRegisterTask

internal val KotlinLLDBScriptSetupAction = KotlinProjectSetupCoroutine {
    locateOrRegisterLLDBScriptTask()
}

internal fun Project.locateOrRegisterLLDBScriptTask(): TaskProvider<DefaultTask> {
    return locateOrRegisterTask("setupLLDBScript") { task ->
        task.description = "Generate lldbinit file with imported konan_lldb.py script"
        task.group = "other"
        task.doFirst {
            project
                .apply {
                    file("lldbinit")
                        .writeText("command script import ${konanDistribution.toolsDir.resolve("konan_lldb.py")}")
                }
        }
    }
}