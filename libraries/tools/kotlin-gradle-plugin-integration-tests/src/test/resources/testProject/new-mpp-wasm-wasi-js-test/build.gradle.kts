plugins {
    kotlin("multiplatform").version("<pluginMarkerVersion>").apply(false)
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "20.2.0"

    tasks.named<org.jetbrains.kotlin.gradle.targets.js.npm.LockCopyTask>("kotlinStorePackageLock") {
        //A little hacky way to make yarn results
        inputFile.value(projectDir.resolve("packageLockStub"))
    }
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.d8.D8RootPlugin> {
    // Test that we can set the version and it is a String.
    // But use the default version since update this place every time anyway.
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.d8.D8RootExtension>().version = (version as String)
}

allprojects.forEach {
    it.tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
        args.add("--ignore-engines")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
