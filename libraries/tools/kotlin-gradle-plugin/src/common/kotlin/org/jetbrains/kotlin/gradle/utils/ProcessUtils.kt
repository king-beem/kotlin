/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.gradle.api.logging.Logger
import kotlin.concurrent.thread

/**
 * Run a command with the given parameters and optional fallback configuration
 *
 * @param command The command to be executed, specified as a list of strings.
 * @param logger An optional logger to log information about the command execution.
 * @param errorHandler An optional function that handles errors returned by the command.
 * @param processConfiguration An optional function that configures the process before it is executed.
 * @return The output text of the command or the fallback text if there is an error.
 */
internal fun runCommand(
    command: List<String>,
    logger: Logger? = null,
    errorHandler: ((retCode: Int, output: String, process: Process) -> String?)? = null,
    processConfiguration: ProcessBuilder.() -> Unit = { },
): String = runCommandWithFallback(
    command,
    logger,
    { retCode, output, process ->
        handleError {
            errorHandler?.invoke(retCode, output, process)
        }
    },
    processConfiguration
)

internal class ProcessFallbackBuilder {
    private var action: (() -> String)? = null
    private var error: (() -> String?)? = null

    fun performFallback(action: () -> String) {
        this.action = action
    }

    fun handleError(error: () -> String?) {
        this.error = error
    }

    val fallbackText: String? get() = action?.let { it().takeIf { it.isNotBlank() } }
    val errorText: String? get() = error?.let { it()?.takeIf { it.isNotBlank() } }
}


/**
 * Executes a command with the given parameters and returns the output text or fallback text in case of an error.
 *
 * @param command The command to be executed, specified as a list of strings.
 * @param logger An optional logger to log information about the command execution.
 * @param fallbackConfiguration An optional function that configures the fallback behavior in case of an error.
 * @param processConfiguration An optional function that configures the process before it is executed.
 * @return The output text of the command or the fallback text if there is an error.
 */
internal fun runCommandWithFallback(
    command: List<String>,
    logger: Logger? = null,
    fallbackConfiguration: ProcessFallbackBuilder.(retCode: Int, output: String, process: Process) -> Unit,
    processConfiguration: ProcessBuilder.() -> Unit = { },
): String {
    val runResult = assembleAndRunProcess(command, logger, processConfiguration)
    return if (runResult.retCode != 0) {
        val fallback = ProcessFallbackBuilder().apply {
            this.fallbackConfiguration(runResult.retCode, runResult.output, runResult.process)
        }

        checkNotNull(fallback.fallbackText) {
            fallback.errorText ?: createErrorMessage(command, runResult)
        }
    } else {
        runResult.inputText
    }
}

/**
 * Represents the result of running a process.
 *
 * @property inputText The text written to the input stream of the process.
 * @property errorText The text written to the error stream of the process.
 * @property retCode The return code of the process.
 * @property process The process object.
 */
private data class RunProcessResult(
    val inputText: String,
    val errorText: String,
    val retCode: Int,
    val process: Process,
) {
    val output: String get() = inputText.ifBlank { errorText }
}

/**
 * Assembles and runs a process with the given command, logger, and process configuration.
 *
 * @param command The command to be executed, specified as a list of strings.
 * @param logger An optional logger to log information about the command execution.
 * @param processConfiguration An optional function that configures the process before it is executed.
 * @return A [RunProcessResult] object containing the input text, error text, return code, and process.
 */
private fun assembleAndRunProcess(
    command: List<String>,
    logger: Logger? = null,
    processConfiguration: ProcessBuilder.() -> Unit = { },
): RunProcessResult {

    val process = ProcessBuilder(command).apply {
        this.processConfiguration()
    }.start()

    var inputText = ""
    var errorText = ""

    val inputThread = thread {
        inputText = process.inputStream.use {
            it.reader().readText()
        }
    }

    val errorThread = thread {
        errorText = process.errorStream.use {
            it.reader().readText()
        }
    }

    inputThread.join()
    errorThread.join()

    val retCode = process.waitFor()
    logger?.info(
        """
            |Information about "${command.joinToString(" ")}" call:
            |
            |${inputText}
        """.trimMargin()
    )

    return RunProcessResult(inputText, errorText, retCode, process)
}

private fun createErrorMessage(command: List<String>, runResult: RunProcessResult): String {
    return """
           |Executing of '${command.joinToString(" ")}' failed with code ${runResult.retCode} and message: 
           |
           |${runResult.inputText}
           |
           |${runResult.errorText}
           |
           """.trimMargin()
}