
// FILE: test.kt

fun foo() {
    try {
        mightThrow()
    } catch (e: Throwable) {
        "OK"
    } finally {
        "FINALLY"
    }
    
    val t = try {
        mightThrow2()
    } catch (e: Throwable) {
        "OK2"
    } finally {
        "FINALLY2"
    }
}

var throw1 = false
var throw2 = false

fun mightThrow() {
    if (throw1) throw Exception()
}

fun mightThrow2() {
    if (throw2) throw Exception()
}

fun box() {
    foo()
    throw2 = true
    foo()
    throw1 = true
    foo()
}

// EXPECTATIONS JVM JVM_IR
// test.kt:34 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:26 mightThrow
// test.kt:27 mightThrow
// test.kt:10 foo
// test.kt:11 foo
// test.kt:13 foo
// test.kt:14 foo
// test.kt:30 mightThrow2
// test.kt:31 mightThrow2
// test.kt:14 foo
// test.kt:18 foo
// test.kt:19 foo
// test.kt:13 foo
// test.kt:20 foo
// test.kt:35 box
// test.kt:36 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:26 mightThrow
// test.kt:27 mightThrow
// test.kt:10 foo
// test.kt:11 foo
// test.kt:13 foo
// test.kt:14 foo
// test.kt:30 mightThrow2
// test.kt:15 foo
// test.kt:16 foo
// test.kt:18 foo
// test.kt:19 foo
// test.kt:13 foo
// test.kt:20 foo
// test.kt:37 box
// test.kt:38 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:26 mightThrow
// test.kt:7 foo
// test.kt:8 foo
// test.kt:10 foo
// test.kt:11 foo
// test.kt:13 foo
// test.kt:14 foo
// test.kt:30 mightThrow2
// test.kt:15 foo
// test.kt:16 foo
// test.kt:18 foo
// test.kt:19 foo
// test.kt:13 foo
// test.kt:20 foo
// test.kt:39 box

// EXPECTATIONS JS_IR
// test.kt:34 box
// test.kt:6 foo
// test.kt:26 mightThrow
// test.kt:27 mightThrow
// test.kt:14 foo
// test.kt:30 mightThrow2
// test.kt:31 mightThrow2
// test.kt:13 foo
// test.kt:20 foo
// test.kt:35 box
// test.kt:36 box
// test.kt:6 foo
// test.kt:26 mightThrow
// test.kt:27 mightThrow
// test.kt:14 foo
// test.kt:30 mightThrow2
// test.kt:30 mightThrow2
// test.kt:15 foo
// test.kt:16 foo
// test.kt:13 foo
// test.kt:20 foo
// test.kt:37 box
// test.kt:38 box
// test.kt:6 foo
// test.kt:26 mightThrow
// test.kt:26 mightThrow
// test.kt:7 foo
// test.kt:7 foo
// test.kt:14 foo
// test.kt:30 mightThrow2
// test.kt:30 mightThrow2
// test.kt:15 foo
// test.kt:16 foo
// test.kt:13 foo
// test.kt:20 foo
// test.kt:39 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:34 $box
// test.kt:6 $foo (8, 8, 8)
// test.kt:26 $mightThrow (8, 8, 8, 22, 22, 16)
// test.kt:5 $foo (4, 4, 4)
// test.kt:10 $foo (9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8, 8, 8, 8, 8, 8, 8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4, 11, 4, 11, 4, 11, 4)
// test.kt:14 $foo (8, 8, 8)
// test.kt:30 $mightThrow2 (8, 8, 22, 22, 16, 8, 22, 22, 16)
// test.kt:13 $foo (12, 12, 4, 12, 12, 4, 12, 12, 4)
// test.kt:18 $foo (9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9)
// test.kt:35 $box (13, 4)
// test.kt:36 $box
// String.kt:143 $kotlin.stringLiteral (15, 8, 15, 8, 15, 8, 15, 8, 15, 8)
// Exceptions.kt:16 $kotlin.Exception.<init> (34, 34, 4, 4, 34, 34, 4, 4, 34, 34, 4, 4)
// Throwable.kt:23 $kotlin.Throwable.<init> (32, 38, 27, 27, 32, 38, 27, 27, 32, 38, 27, 27)
// Throwable.kt:18 $kotlin.Throwable.<init> (28, 62, 28, 62, 28, 62)
// Throwable.kt:25 $kotlin.Throwable.<init> (50, 50, 50)
// ExternalWrapper.kt:149 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (34, 34, 34)
// ExternalWrapper.kt:225 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (26, 16, 15, 15, 31, 26, 16, 15, 15, 31, 26, 16, 15, 15, 31)
// ExternalWrapper.kt:153 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (16, 16, 16)
// ExternalWrapper.kt:151 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (16, 16, 16)
// Throwable.kt:27 $kotlin.Throwable.<init> (34, 34, 34)
// Throwable.kt:39 $kotlin.Throwable.<init> (69, 69, 69)
// test.kt:15 $foo (13, 13)
// test.kt:16 $foo (9, 9, 9, 9, 9, 9, 9, 9)
// test.kt:37 $box (13, 4)
// test.kt:38 $box
// test.kt:7 $foo
// test.kt:8 $foo (9, 9, 9, 9)
