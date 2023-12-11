plugins {
    id("gradle-plugin-common-configuration")
}

dependencies {
    api(platform(project(":kotlin-gradle-plugins-bom")))

    compileOnly(project(":kotlin-gradle-plugin"))
    compileOnly(project(":kotlin-compiler-embeddable"))
}

gradlePlugin {
    plugins {
        create("jso") {
            id = "org.jetbrains.kotlin.plugin.jso"
            displayName = "Kotlin compiler plugin for kotlinx.jso library"
            description = displayName
            implementationClass = "org.jetbrains.kotlinx.jso.gradle.JsoKotlinGradleSubplugin"
        }
    }
}