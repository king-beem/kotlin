/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt4

import com.intellij.openapi.util.text.StringUtil
import com.sun.tools.javac.comp.CompileStates
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.util.JCDiagnostic
import com.sun.tools.javac.util.List
import com.sun.tools.javac.util.Log
import org.jetbrains.kotlin.kapt3.base.javac.KaptJavaLogBase
import org.jetbrains.kotlin.kapt3.base.parseJavaFiles
import org.jetbrains.kotlin.kapt3.test.KaptTestDirectives
import org.jetbrains.kotlin.kapt3.test.handlers.ClassFileToSourceKaptStubHandler
import org.jetbrains.kotlin.kapt3.test.handlers.checkTxtAccordingToBackend
import org.jetbrains.kotlin.kapt3.test.handlers.removeMetadataAnnotationContents
import org.jetbrains.kotlin.kapt3.test.messageCollectorProvider
import org.jetbrains.kotlin.test.model.AnalysisHandler
import org.jetbrains.kotlin.test.model.TestArtifactKind
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.getRealJavaFiles
import org.jetbrains.kotlin.test.services.sourceFileProvider
import org.jetbrains.kotlin.test.util.trimTrailingWhitespacesAndAddNewlineAtEOF
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import java.io.File
import java.util.*

class Kapt4Handler(testServices: TestServices) : AnalysisHandler<Kapt4ContextBinaryArtifact>(
    testServices,
    failureDisablesNextSteps = true,
    doNotRunIfThereWerePreviousFailures = true
) {
    override val artifactKind: TestArtifactKind<Kapt4ContextBinaryArtifact>
        get() = Kapt4ContextBinaryArtifact.Kind

    override fun processModule(module: TestModule, info: Kapt4ContextBinaryArtifact) {
        val generateNonExistentClass = KaptTestDirectives.NON_EXISTENT_CLASS in module.directives
        val validate = KaptTestDirectives.NO_VALIDATION !in module.directives
        val expectedErrors = module.directives[KaptTestDirectives.EXPECTED_ERROR].sorted()

        val (kaptContext, kaptStubs) = info
        val convertedFiles = getJavaFiles(info, module)
        kaptContext.javaLog.interceptorData.files = convertedFiles.associateBy { it.sourceFile }
        if (validate) kaptContext.compiler.enterTrees(convertedFiles)

        val actualRaw = convertedFiles
            .sortedBy { it.sourceFile.name }
            .joinToString(ClassFileToSourceKaptStubHandler.FILE_SEPARATOR) { it.prettyPrint(kaptContext.context) }

        val actual = StringUtil.convertLineSeparators(actualRaw.trim { it <= ' ' })
            .trimTrailingWhitespacesAndAddNewlineAtEOF()
            .let { removeMetadataAnnotationContents(it) }

        if (kaptContext.compiler.shouldStop(CompileStates.CompileState.ENTER)) {
            val log = Log.instance(kaptContext.context) as KaptJavaLogBase

            val actualErrors = log.reportedDiagnostics
                .filter { it.type == JCDiagnostic.DiagnosticType.ERROR }
                .map {
                    // Unfortunately, we can't use the file name as it can contain temporary prefix
                    val name = it.source?.name?.substringAfterLast("/") ?: ""
                    val kind = when (name.substringAfterLast(".").lowercase()) {
                        "kt" -> "kotlin"
                        "java" -> "java"
                        else -> "other"
                    }

                    val javaLocation = "($kind:${it.lineNumber}:${it.columnNumber}) "
                    javaLocation + it.getMessage(Locale.US).lines().first()
                }
                .sorted()

            log.flush()

            val lineSeparator = System.getProperty("line.separator")
            val actualErrorsStr = actualErrors.joinToString(lineSeparator) { it.toDirectiveView() }

            if (expectedErrors.isEmpty()) {
                assertions.fail { "There were errors during analysis:\n$actualErrorsStr\n\nStubs:\n\n$actual" }
            } else {
                val expectedErrorsStr = expectedErrors.joinToString(lineSeparator) { it.toDirectiveView() }
                if (expectedErrorsStr != actualErrorsStr) {
                    assertions.assertEquals(expectedErrorsStr, actualErrorsStr) {
                        System.err.println(testServices.messageCollectorProvider.getErrorStream(module).toString("UTF8"))
                        "Expected error matching failed"
                    }
                }
            }
        }

        assertions.checkTxtAccordingToBackend(module, actual)
    }

    private fun getJavaFiles(
        info: Kapt4ContextBinaryArtifact,
        module: TestModule
    ): List<JCTree.JCCompilationUnit> {
        val (kaptContext, kaptStubs) = info
        val convertedFiles = kaptStubs.mapIndexed { index, stub ->
            val sourceFile = createTempJavaFile("stub$index.java", stub.file.prettyPrint(kaptContext.context))
            stub.writeMetadataIfNeeded(forSource = sourceFile)
            sourceFile
        }

        val javaFiles = testServices.sourceFileProvider.getRealJavaFiles(module)
        val allJavaFiles = javaFiles + convertedFiles

        // A workaround needed for Javac to parse files correctly even if errors were already reported
        // If nerrors > 0, "parseFiles()" returns the empty list
        val oldErrorCount = kaptContext.compiler.log.nerrors
        kaptContext.compiler.log.nerrors = 0

        try {
            val parsedJavaFiles = kaptContext.parseJavaFiles(allJavaFiles)

            for (tree in parsedJavaFiles) {
                val actualFile = File(tree.sourceFile.toUri())

                // By default, JavaFileObject.getName() returns the absolute path to the file.
                // In our test, such a path will be temporary, so the comparison against it will lead to flaky tests.
                tree.sourcefile = KaptJavaFileObject(tree, tree.defs.firstIsInstance(), actualFile)
            }

            return parsedJavaFiles
        } finally {
            kaptContext.compiler.log.nerrors = oldErrorCount
        }
    }

    override fun processAfterAllModules(someAssertionWasFailed: Boolean) {}

    private fun createTempJavaFile(name: String, text: String): File {
        return testServices.sourceFileProvider.javaSourceDirectory.resolve(name).also {
            it.writeText(text)
        }
    }

    private fun String.toDirectiveView(): String = "// ${KaptTestDirectives.EXPECTED_ERROR.name}: $this"
}
