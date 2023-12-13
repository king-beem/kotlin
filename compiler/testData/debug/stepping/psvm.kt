// IGNORE_BACKEND: WASM
// FILE: test.kt

fun main(args: Array<String>) {
    args[0]
}

fun box() {
    main(arrayOf("OK"))
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:9 box
// test.kt:5 main
// test.kt:6 main
// test.kt:10 box

// EXPECTATIONS JS_IR
// test.kt:9 box
// test.kt:5 main
// test.kt:6 main
// test.kt:10 box