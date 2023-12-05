/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.backend.handlers

import com.google.common.collect.Sets
import org.jetbrains.kotlin.abicmp.defects.Location
import org.jetbrains.kotlin.abicmp.reports.*
import org.jetbrains.kotlin.abicmp.tag
import org.jetbrains.kotlin.abicmp.tasks.ClassTask
import org.jetbrains.kotlin.abicmp.tasks.checkerConfiguration
import org.jetbrains.kotlin.codegen.getClassFiles
import org.jetbrains.kotlin.test.backend.ir.AbiCheckerSuppressor
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.services.JUnit5Assertions
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.moduleStructure
import org.jetbrains.kotlin.test.utils.withExtension
import org.jetbrains.org.objectweb.asm.ClassReader
import org.jetbrains.org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter

class JvmAbiConsistencyHandler(testServices: TestServices) : AnalysisHandler<BinaryArtifacts.JvmFromK1AndK2>(testServices, true, true) {
    override val artifactKind: TestArtifactKind<BinaryArtifacts.JvmFromK1AndK2>
        get() = ArtifactKinds.JvmFromK1AndK2


    private class ModuleReport(
        val missingInK1: Set<String>,
        val missingInK2: Set<String>,
        val nonEmptyClassReports: MutableList<ClassReport>,
    )

    private class TestReport {
        private val modulesWithNonEmptyReports = mutableMapOf<String, ModuleReport>()

        fun addModuleReport(moduleName: String, report: ModuleReport) {
            modulesWithNonEmptyReports[moduleName] = report
        }

        fun isEmpty(): Boolean = modulesWithNonEmptyReports.isEmpty()

        fun dumpAsPlainText(): String {

            fun TextTreeBuilderContext.reportMissing(frontendName: String, missing: Set<String>) {
                if (missing.isEmpty()) return
                node("Missing in $frontendName") {
                    node(missing.joinToString("\n"))
                }
            }

            val outputStream = ByteArrayOutputStream()
            val out = PrintWriter(outputStream)

            dumpTree(out) {
                modulesWithNonEmptyReports.forEach { (module, report) ->
                    node("MODULE $module") {
                        reportMissing("K1", report.missingInK1)
                        reportMissing("K2", report.missingInK2)
                        report.nonEmptyClassReports.forEach { classReport ->
                            with(classReport) { appendClassReport() }
                        }
                    }
                }
            }

            return outputStream.toString()
        }

        // Might be used while debugging
        @Suppress("unused")
        fun dumpAsHtml(): String {
            fun PrintWriter.reportMissing(frontendName: String, missing: Set<String>) {
                if (missing.isEmpty()) return
                println("Missing in $frontendName:")
                tag("br")
                println(missing.joinToString())
                tag("br")
                tag("br")
            }

            val outputStream = ByteArrayOutputStream()
            PrintWriter(outputStream, true).use { out ->
                out.tag("html") {
                    out.tag("head") {
                        out.tag("style", REPORT_CSS)
                    }
                    out.tag("body") {
                        modulesWithNonEmptyReports.forEach { (module, report) ->
                            out.tag("h2", "Module: $module")
                            out.reportMissing("K1", report.missingInK1)
                            out.reportMissing("K2", report.missingInK2)
                            report.nonEmptyClassReports.forEach { it.writeAsHtml(out) }
                        }

                    }
                }
            }
            return outputStream.toString()
        }
    }

    private val testReport = TestReport()

    override fun processAfterAllModules(someAssertionWasFailed: Boolean) {
        val txtDiffPath = testServices.moduleStructure.originalTestDataFiles.first().withExtension(".jvm_abi.txt")
        val isDifferenceExplained = testServices.moduleStructure.allDirectives.contains(CodegenTestDirectives.JVM_ABI_K1_K2_DIFF)

        if (!testReport.isEmpty()) {
            JUnit5Assertions.assertEqualsToFile(
                txtDiffPath,
                testReport.dumpAsPlainText(),
                sanitizer = { it },
                differenceObtainedMessage = { "Actual K1/K2 JVM ABI difference differs from expected".withFileIsToBigAdvice() },
                fileNotFoundMessageTeamCity = fileNotFoundMessageBuilder(isTeamCityVersion = true, isDifferenceExplained),
                fileNotFoundMessageLocal = fileNotFoundMessageBuilder(isTeamCityVersion = false, isDifferenceExplained)
            )

            if (!isDifferenceExplained) {
                failWithFileIsTooBigAdvice(
                    "K1/K2 JVM ABI difference obtained. Add ${CodegenTestDirectives.JVM_ABI_K1_K2_DIFF.name} directive with an explanation"
                )
            }
        } else {
            if (isDifferenceExplained && txtDiffPath.exists()) {
                failWithFileIsTooBigAdvice(
                    "No K1/K2 JVM ABI difference found. Remove ${CodegenTestDirectives.JVM_ABI_K1_K2_DIFF.name} directive and $txtDiffPath"
                )
            }
            if (isDifferenceExplained) {
                failWithFileIsTooBigAdvice(
                    "No K1/K2 JVM ABI difference found. Remove ${CodegenTestDirectives.JVM_ABI_K1_K2_DIFF.name} directive"
                )
            }
            if (txtDiffPath.exists()) {
                failWithFileIsTooBigAdvice("No K1/K2 JVM ABI difference found. Remove $txtDiffPath")
            }
        }
    }

    private fun fileNotFoundMessageBuilder(isTeamCityVersion: Boolean, isDiffExplained: Boolean): ((File) -> String) = {
        buildString {
            if (isTeamCityVersion) {
                append("Expected data file did not exist `$it`")
            } else {
                append("Expected data file did not exist. Generating: $it")
            }
            if (!isDiffExplained) {
                append("\n")
                append("Also add ${CodegenTestDirectives.JVM_ABI_K1_K2_DIFF.name} directive with an explanation")
            }
        }.withFileIsToBigAdvice()
    }

    private fun failWithFileIsTooBigAdvice(message: String) {
        JUnit5Assertions.fail { message.withFileIsToBigAdvice() }
    }

    private val fileIsTooBigAdvice = """
            |NOTICE: it might be impossible to run this test via IntelliJ with default settings as the file is too big.
            |In this case, adjust the settings (see https://www.jetbrains.com/help/objc/configuring-file-size-limit.html) or run via console.
            """.trimMargin()

    private fun String.withFileIsToBigAdvice() = """
        |$this
        |
        |$fileIsTooBigAdvice
        """.trimMargin()

    override fun processModule(module: TestModule, info: BinaryArtifacts.JvmFromK1AndK2) {
        if (AbiCheckerSuppressor.ignoredByBackendOrInliner(testServices)) return
        val classesFromK1 = info.fromK1.classFileFactory.getClassFiles().associate { it.relativePath to it.asByteArray() }
        val classesFromK2 = info.fromK2.classFileFactory.getClassFiles().associate { it.relativePath to it.asByteArray() }
        val missingInK2 = Sets.difference(classesFromK1.keys, classesFromK2.keys)
        val missingInK1 = Sets.difference(classesFromK2.keys, classesFromK1.keys)
        val commonClasses = Sets.intersection(classesFromK1.keys, classesFromK2.keys)

        val nonEmptyClassReports = mutableListOf<ClassReport>()
        commonClasses.forEach { classInternalName ->
            val k1ClassNode = parseClassNode(classesFromK1[classInternalName]!!)
            val k2ClassNode = parseClassNode(classesFromK2[classInternalName]!!)
            val classReport = ClassReport(Location.Class("", classInternalName), classInternalName, "K1", "K2", DefectReport())
            ClassTask(checkerConfiguration {
                // Indication of constants is different in K2 as expected
                // We simply turn the checker off to avoid producing an enormous number of difference reports
                disable("class.metadata.property.hasConstant")
            }, k1ClassNode, k2ClassNode, classReport).run()
            if (classReport.isNotEmpty()) {
                nonEmptyClassReports.add(classReport)
            }
        }

        if (nonEmptyClassReports.isNotEmpty() || missingInK1.isNotEmpty() || missingInK2.isNotEmpty()) {
            testReport.addModuleReport(module.name, ModuleReport(missingInK1, missingInK2, nonEmptyClassReports))
        }
    }

    private fun parseClassNode(byteArray: ByteArray) =
        ClassNode().also { ClassReader(ByteArrayInputStream(byteArray)).accept(it, ClassReader.SKIP_CODE) }
}