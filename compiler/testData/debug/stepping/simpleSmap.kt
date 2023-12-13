// IGNORE_BACKEND: WASM


// FILE: test.kt

inline fun inlineFun(s: () -> Unit) {
    s()
}

fun box() {
    inlineFun {
        "OK"
    }
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:11 box
// test.kt:7 box
// test.kt:12 box
// test.kt:13 box
// test.kt:7 box
// test.kt:8 box
// test.kt:14 box

// EXPECTATIONS JS_IR
// test.kt:14 box
