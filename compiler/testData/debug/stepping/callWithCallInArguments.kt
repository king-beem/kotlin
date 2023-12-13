// IGNORE_BACKEND: WASM
// FILE: test.kt

class A

fun bar(a: A) = A()

fun box() {
    val a = A()
    bar(
            bar(
                    bar(a)
            )
    )
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:9 box
// test.kt:4 <init>
// test.kt:9 box
// test.kt:12 box
// test.kt:6 bar
// test.kt:4 <init>
// test.kt:6 bar
// test.kt:11 box
// test.kt:6 bar
// test.kt:4 <init>
// test.kt:6 bar
// test.kt:10 box
// test.kt:6 bar
// test.kt:4 <init>
// test.kt:6 bar
// test.kt:10 box
// test.kt:15 box

// EXPECTATIONS JS_IR
// test.kt:9 box
// test.kt:4 <init>
// test.kt:10 box
// test.kt:6 bar
// test.kt:4 <init>
// test.kt:11 box
// test.kt:6 bar
// test.kt:4 <init>
// test.kt:10 box
// test.kt:6 bar
// test.kt:4 <init>
// test.kt:15 box