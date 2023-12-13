// IGNORE_BACKEND: WASM
// FILE: test.kt
fun box() {
    for (i in 1..3) {
        foo(i)
    }
}

inline fun foo(n: Int) {}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:4 box
// test.kt:5 box
// test.kt:9 box
// test.kt:4 box
// test.kt:5 box
// test.kt:9 box
// test.kt:4 box
// test.kt:5 box
// test.kt:9 box
// test.kt:4 box
// test.kt:7 box

// EXPECTATIONS JS_IR
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:4 box
// test.kt:7 box
