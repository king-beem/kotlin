plugins {
    kotlin("multiplatform").version("<pluginMarkerVersion>")
}

repositories {
    mavenLocal()
    mavenCentral()
}

with(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.apply(rootProject)) {
    // canary nodejs that supports recent Wasm GC changes
    nodeVersion = "21.0.0-v8-canary202309167e82ab1fa2"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

with(org.jetbrains.kotlin.gradle.targets.js.d8.D8RootPlugin.apply(rootProject)) {
    // Test that we can set the version and it is a String.
    // But use the default version since update this place every time anyway.
    version = (version as String)
}

allprojects.forEach {
    it.tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
        args.add("--ignore-engines")
    }
}

tasks.named<org.jetbrains.kotlin.gradle.targets.js.npm.LockCopyTask>("kotlinStorePackageLock") {
    //A little hacky way to make yarn results
    inputFile.value(projectDir.resolve("packageLockStub"))
}

kotlin {
    wasmJs {
        <JsEngine> {
            testTask {
                filter.apply {
                    excludeTest("WasmTest", "testShouldBeExcluded")
                }
            }
        }
        <ApplyBinaryen>
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
            }
        }
    }
}
