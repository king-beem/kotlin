// IGNORE_BACKEND: WASM
// FILE: test.kt

fun foo(x: Int) {
   when (val y =
           when {
             x == 0 -> 1
             x == 1 -> 2
             else -> 0
         }) {
       0 -> 3
       1 -> 4
   }
}

fun box() {
    foo(0)
    foo(1)
    foo(2)
}

// The JVM_IR backend optimizes the inner when to a switch and therefore goes directly to the
// right case without stepping through evaluation of each condition.

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:17 box
// test.kt:6 foo
// test.kt:7 foo
// test.kt:5 foo
// test.kt:12 foo
// test.kt:14 foo
// test.kt:18 box
// test.kt:6 foo
// EXPECTATIONS JVM
// test.kt:7 foo
// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:8 foo
// test.kt:5 foo
// test.kt:14 foo
// test.kt:19 box
// test.kt:6 foo
// EXPECTATIONS JVM
// test.kt:7 foo
// test.kt:8 foo
// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:9 foo
// test.kt:5 foo
// test.kt:11 foo
// test.kt:14 foo
// test.kt:20 box

// EXPECTATIONS JS_IR
// test.kt:17 box
// test.kt:6 foo
// test.kt:7 foo
// test.kt:14 foo
// test.kt:18 box
// test.kt:6 foo
// test.kt:8 foo
// test.kt:14 foo
// test.kt:19 box
// test.kt:6 foo
// test.kt:9 foo
// test.kt:14 foo
// test.kt:20 box
