// DO NOT EDIT MANUALLY!
// Generated by org/jetbrains/kotlin/generators/arguments/GenerateGradleOptions.kt
// To regenerate run 'generateGradleOptions' task
@file:Suppress("RemoveRedundantQualifierName", "Deprecation", "DuplicatedCode")

package org.jetbrains.kotlin.gradle.dsl

interface KotlinJsOptions : org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions {
    override val options: org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions

    /**
     * Disable internal declaration export.
     * Default value: false
     */
    var friendModulesDisabled: kotlin.Boolean
        get() = options.friendModulesDisabled.get()
        set(value) = options.friendModulesDisabled.set(value)

    private val kotlin.String.mainCompilerOption get() = org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode.fromMode(this)

    private val org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode.mainKotlinOption get() = this.mode

    /**
     * Specify whether the 'main' function should be called upon execution.
     * Possible values: "call", "noCall"
     * Default value: JsMainFunctionExecutionMode.CALL
     */
    var main: kotlin.String
        get() = options.main.get().mainKotlinOption
        set(value) = options.main.set(value.mainCompilerOption)

    /**
     * Generate .meta.js and .kjsm files with metadata. Use this to create a library.
     * Default value: true
     */
    var metaInfo: kotlin.Boolean
        get() = options.metaInfo.get()
        set(value) = options.metaInfo.set(value)

    private val kotlin.String.moduleKindCompilerOption get() = org.jetbrains.kotlin.gradle.dsl.JsModuleKind.fromKind(this)

    private val org.jetbrains.kotlin.gradle.dsl.JsModuleKind.moduleKindKotlinOption get() = this.kind

    /**
     * The kind of JS module generated by the compiler.
     * Possible values: "plain", "amd", "commonjs", "umd"
     * Default value: JsModuleKind.MODULE_PLAIN
     */
    var moduleKind: kotlin.String
        get() = options.moduleKind.get().moduleKindKotlinOption
        set(value) = options.moduleKind.set(value.moduleKindCompilerOption)

    /**
     * Base name of generated files.
     * Default value: null
     */
    var moduleName: kotlin.String?
        get() = options.moduleName.orNull
        set(value) = options.moduleName.set(value)

    /**
     * Don't automatically include the default Kotlin/JS stdlib in compilation dependencies.
     * Default value: true
     */
    var noStdlib: kotlin.Boolean
        get() = options.noStdlib.get()
        set(value) = options.noStdlib.set(value)

    /**
     * Destination *.js file for the compilation result.
     * Default value: null
     */
    @Deprecated(message = "Only for legacy backend. For IR backend please use task.destinationDirectory and moduleName", level = DeprecationLevel.WARNING)
    var outputFile: kotlin.String?
        get() = options.outputFile.orNull
        set(value) = options.outputFile.set(value)

    /**
     * Generate a source map.
     * Default value: false
     */
    var sourceMap: kotlin.Boolean
        get() = options.sourceMap.get()
        set(value) = options.sourceMap.set(value)

    private val kotlin.String?.sourceMapEmbedSourcesCompilerOption get() = this?.let { org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode.fromMode(it) }

    private val org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode?.sourceMapEmbedSourcesKotlinOption get() = this?.mode

    /**
     * Embed source files into the source map.
     * Possible values: "never", "always", "inlining"
     * Default value: null
     */
    var sourceMapEmbedSources: kotlin.String?
        get() = options.sourceMapEmbedSources.orNull.sourceMapEmbedSourcesKotlinOption
        set(value) = options.sourceMapEmbedSources.set(value.sourceMapEmbedSourcesCompilerOption)

    private val kotlin.String?.sourceMapNamesPolicyCompilerOption get() = this?.let { org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy.fromPolicy(it) }

    private val org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy?.sourceMapNamesPolicyKotlinOption get() = this?.policy

    /**
     * Mode for mapping generated names to original names (IR backend only).
     * Possible values: "no", "simple-names", "fully-qualified-names"
     * Default value: null
     */
    var sourceMapNamesPolicy: kotlin.String?
        get() = options.sourceMapNamesPolicy.orNull.sourceMapNamesPolicyKotlinOption
        set(value) = options.sourceMapNamesPolicy.set(value.sourceMapNamesPolicyCompilerOption)

    /**
     * Add the specified prefix to the paths in the source map.
     * Default value: null
     */
    var sourceMapPrefix: kotlin.String?
        get() = options.sourceMapPrefix.orNull
        set(value) = options.sourceMapPrefix.set(value)

    /**
     * Generate JS files for the specified ECMA version.
     * Possible values: "v5"
     * Default value: "v5"
     */
    var target: kotlin.String
        get() = options.target.get()
        set(value) = options.target.set(value)

    /**
     * Translate primitive arrays into JS typed arrays.
     * Default value: true
     */
    var typedArrays: kotlin.Boolean
        get() = options.typedArrays.get()
        set(value) = options.typedArrays.set(value)

    /**
     * Let generated JavaScript code use ES2015 classes.
     * Default value: false
     */
    var useEsClasses: kotlin.Boolean
        get() = options.useEsClasses.get()
        set(value) = options.useEsClasses.set(value)
}
