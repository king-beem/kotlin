// IGNORE_BACKEND: WASM
// FILE: test.kt

fun foo(n: Number) {
    if (!when (n) {
            is Float -> false
            else -> true
        }) {
    }
}

fun box() {
    foo(2.0f)
}

// EXPECTATIONS JVM JVM_IR

// test.kt:13 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:5 foo
// test.kt:10 foo
// test.kt:14 box

// EXPECTATIONS JS_IR
// test.kt:13 box
// test.kt:6 foo
// test.kt:6 foo
// test.kt:10 foo
// test.kt:14 box
