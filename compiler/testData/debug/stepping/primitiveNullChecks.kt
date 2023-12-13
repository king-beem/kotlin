// IGNORE_BACKEND: WASM
// FILE: test.kt

fun box(): String {
    42!!
    42.toLong()!!
    return "OK"!!
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:5 box
// test.kt:6 box
// test.kt:7 box

// EXPECTATIONS JS_IR
// test.kt:5 box
// test.kt:6 box
// test.kt:6 box
// test.kt:7 box
