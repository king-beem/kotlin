
// FILE: test.kt

fun foo(shouldThrow: Boolean) {
    try {
        if (shouldThrow) throw Exception()
    } catch (e: Exception) {
        "OK"
    }
    "OK"
}

fun box() {
    foo(false)
    foo(true)
}

// EXPECTATIONS JVM JVM_IR
// test.kt:14 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:10 foo
// test.kt:11 foo
// test.kt:15 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:7 foo
// test.kt:8 foo
// test.kt:10 foo
// test.kt:11 foo
// test.kt:16 box

// EXPECTATIONS JS_IR
// test.kt:14 box
// test.kt:6 foo
// test.kt:11 foo
// test.kt:15 box
// test.kt:6 foo
// test.kt:6 foo
// test.kt:7 foo
// test.kt:7 foo
// test.kt:11 foo
// test.kt:16 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:14 $box (8, 4)
// test.kt:6 $foo (12, 12, 31, 31, 25)
// test.kt:10 $foo (5, 5, 5, 5, 5, 5, 5, 5)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4)
// test.kt:15 $box (8, 4)
// Exceptions.kt:16 $kotlin.Exception.<init> (34, 34, 4, 4)
// Throwable.kt:23 $kotlin.Throwable.<init> (32, 38, 27, 27)
// Throwable.kt:18 $kotlin.Throwable.<init> (28, 62)
// Throwable.kt:25 $kotlin.Throwable.<init>
// ExternalWrapper.kt:149 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter
// ExternalWrapper.kt:225 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (26, 16, 15, 15, 31)
// ExternalWrapper.kt:153 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter
// ExternalWrapper.kt:151 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter
// Throwable.kt:27 $kotlin.Throwable.<init>
// Throwable.kt:39 $kotlin.Throwable.<init>
// test.kt:5 $foo
// test.kt:7 $foo (27, 13)
// test.kt:8 $foo (9, 9, 9, 9)
// String.kt:143 $kotlin.stringLiteral (15, 8, 15, 8)
