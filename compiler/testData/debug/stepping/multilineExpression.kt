// IGNORE_BACKEND: WASM
// FILE: test.kt
// KT-17753

fun box() {
    test(true, true, true)
}

fun test(a: Boolean, b: Boolean, c: Boolean): Boolean {
    return a
            && b
            && c
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:6 box
// EXPECTATIONS JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:10 test
// test.kt:11 test
// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:12 test
// test.kt:10 test
// test.kt:6 box
// test.kt:7 box

// EXPECTATIONS JS_IR
// test.kt:6 box
// test.kt:10 test
// test.kt:7 box