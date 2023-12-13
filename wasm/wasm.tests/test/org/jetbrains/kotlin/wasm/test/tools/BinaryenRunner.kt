/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.wasm.test.tools

import org.jetbrains.kotlin.utils.addToStdlib.runIf
import java.io.ByteArrayOutputStream
import kotlin.test.fail

sealed interface WasmOptimizer {
    fun run(wasmInput: ByteArray, withText: Boolean = false): OptimizationResult

    data class OptimizationResult(val wasm: ByteArray, val wat: String?)

    object Binaryen : WasmOptimizer {
        private val binaryenPath = System.getProperty("binaryen.path")

        private val binaryenArgs = arrayOf(
            // Proposals
            "--enable-gc",
            "--enable-reference-types",
            "--enable-exception-handling",
            "--enable-bulk-memory",  // For array initialization from data sections

            // Other options
            "--enable-nontrapping-float-to-int",
            // It's turned out that it's not safe
            // "--closed-world",

            // Optimizations:
            // Note the order and repetition of the next options matter.
            //
            // About Binaryen optimizations:
            // GC Optimization Guidebook -- https://github.com/WebAssembly/binaryen/wiki/GC-Optimization-Guidebook
            // Optimizer Cookbook -- https://github.com/WebAssembly/binaryen/wiki/Optimizer-Cookbook
            //
            "--inline-functions-with-loops",
            "--traps-never-happen",
            "--fast-math",
            // without "--type-merging" it produces increases the size
            // "--type-ssa",
            "-O3",
            "-O3",
            "--gufa",
            "-O3",
            // requires --closed-world
            // "--type-merging",
            "-O3",
            "-Oz",
            "-o",
            "-"
        )

        override fun run(wasmInput: ByteArray, withText: Boolean): OptimizationResult {
            val command = arrayOf(binaryenPath, *binaryenArgs)
            return OptimizationResult(
                exec(command, wasmInput),
                runIf(withText) { exec(command + "-S", wasmInput).toString(Charsets.UTF_8) }
            )
        }

        private fun exec(command: Array<String>, input: ByteArray): ByteArray {
            val processBuilder = ProcessBuilder(*command)
                .redirectErrorStream(true)

            val process = processBuilder.start()
            val processInputStream = process.inputStream
            val processOutputStream = process.outputStream

            processOutputStream.write(input)
            processOutputStream.close()

            // Print process output
            val stdout = ByteArrayOutputStream()

            while (process.isAlive) {
                stdout.write(processInputStream.readBytes())
            }

            val exitValue = process.waitFor()

            if (exitValue != 0) {
                val commandString = command.joinToString(" ") { escapeShellArgument(it) }
                fail("Command \"$commandString\" terminated with exit code $exitValue")
            }

            return stdout.toByteArray()
        }
    }
}