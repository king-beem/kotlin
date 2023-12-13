// IGNORE_BACKEND: WASM
// FILE: test.kt

fun box() {
    foo(
            1 + 1
    )
}

fun foo(i: Int) {
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:6 box
// test.kt:5 box
// test.kt:11 foo
// test.kt:8 box

// EXPECTATIONS JS_IR
// test.kt:5 box
// test.kt:11 foo
// test.kt:8 box