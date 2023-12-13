// IGNORE_BACKEND: WASM
// FILE: test.kt
// KT-22488

fun box() {
    test()
}

fun test(): Long {
    if (1 == 1 &&
        2 == 2) {
        return 0
    }

    return 1
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:6 box
// EXPECTATIONS JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:10 test
// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:11 test
// test.kt:12 test
// test.kt:6 box
// test.kt:7 box

// EXPECTATIONS JS_IR
// test.kt:6 box
// test.kt:12 test
// test.kt:7 box