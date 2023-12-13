// IGNORE_BACKEND: WASM

// FILE: test.kt

fun box() {
    lookAtMe {
        val c = "c"
    }
}

inline fun lookAtMe(f: (String) -> Unit) {
    val a = "a"
    f(a)
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:6 box
// test.kt:12 box
// test.kt:13 box
// test.kt:7 box
// test.kt:8 box
// test.kt:13 box
// test.kt:14 box
// test.kt:9 box

// EXPECTATIONS JS_IR
// test.kt:12 box
// test.kt:7 box
// test.kt:9 box
