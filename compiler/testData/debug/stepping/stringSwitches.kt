
// FILE: test.kt

fun stringSwitch(x: String) {
    val l = when {
        x == "x" -> 1
        x == "xy" -> 2
        x == "xyz" -> 3
        else -> -1
    }

    val l2 = when (x) {
        "x" -> 1
        "xy" -> 2
        "xyz" -> 3
        else -> -1
    }

    val l3 = when
        (x)
    {
        "x" -> 1
        "xy" -> 2
        "xyz" -> 3
        else -> -1
    }
}

fun box() {
    stringSwitch("x")
    stringSwitch("xy")
    stringSwitch("xyz")
    stringSwitch("nope")
}

// JVM_IR uses the line number of the start of the `when` as the line number
// for the lookup/table switch. Therefore when the subject and the when is
// on separate lines the first step is on the subject, then steop to the when,
// then to the right branch.

// JVM_IR uses optimized lookup/table switches for all these cases. JVM
// does not. So on JVM there are steps on each condition evaluation for
// the first `when`.

// EXPECTATIONS JVM JVM_IR
// test.kt:30 box
// test.kt:5 stringSwitch
// test.kt:6 stringSwitch
// test.kt:5 stringSwitch
// test.kt:12 stringSwitch
// test.kt:13 stringSwitch
// test.kt:12 stringSwitch
// test.kt:20 stringSwitch
// EXPECTATIONS JVM_IR
// test.kt:19 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:22 stringSwitch
// test.kt:19 stringSwitch
// test.kt:27 stringSwitch
// test.kt:31 box
// test.kt:5 stringSwitch
// EXPECTATIONS JVM
// test.kt:6 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:7 stringSwitch
// test.kt:5 stringSwitch
// test.kt:12 stringSwitch
// test.kt:14 stringSwitch
// test.kt:12 stringSwitch
// test.kt:20 stringSwitch
// EXPECTATIONS JVM_IR
// test.kt:19 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:23 stringSwitch
// test.kt:19 stringSwitch
// test.kt:27 stringSwitch
// test.kt:32 box
// test.kt:5 stringSwitch
// EXPECTATIONS JVM
// test.kt:6 stringSwitch
// test.kt:7 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:8 stringSwitch
// test.kt:5 stringSwitch
// test.kt:12 stringSwitch
// test.kt:15 stringSwitch
// test.kt:12 stringSwitch
// test.kt:20 stringSwitch
// EXPECTATIONS JVM_IR
// test.kt:19 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:24 stringSwitch
// test.kt:19 stringSwitch
// test.kt:27 stringSwitch
// test.kt:33 box
// test.kt:5 stringSwitch
// EXPECTATIONS JVM
// test.kt:6 stringSwitch
// test.kt:7 stringSwitch
// test.kt:8 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:9 stringSwitch
// test.kt:5 stringSwitch
// test.kt:12 stringSwitch
// test.kt:16 stringSwitch
// test.kt:12 stringSwitch
// test.kt:20 stringSwitch
// EXPECTATIONS JVM_IR
// test.kt:19 stringSwitch
// EXPECTATIONS JVM JVM_IR
// test.kt:25 stringSwitch
// test.kt:19 stringSwitch
// test.kt:27 stringSwitch
// test.kt:34 box

// EXPECTATIONS JS_IR
// test.kt:30 box
// test.kt:5 stringSwitch
// test.kt:6 stringSwitch
// test.kt:12 stringSwitch
// test.kt:13 stringSwitch
// test.kt:19 stringSwitch
// test.kt:22 stringSwitch
// test.kt:27 stringSwitch
// test.kt:31 box
// test.kt:5 stringSwitch
// test.kt:7 stringSwitch
// test.kt:12 stringSwitch
// test.kt:14 stringSwitch
// test.kt:19 stringSwitch
// test.kt:23 stringSwitch
// test.kt:27 stringSwitch
// test.kt:32 box
// test.kt:5 stringSwitch
// test.kt:8 stringSwitch
// test.kt:12 stringSwitch
// test.kt:15 stringSwitch
// test.kt:19 stringSwitch
// test.kt:24 stringSwitch
// test.kt:27 stringSwitch
// test.kt:33 box
// test.kt:5 stringSwitch
// test.kt:9 stringSwitch
// test.kt:12 stringSwitch
// test.kt:16 stringSwitch
// test.kt:19 stringSwitch
// test.kt:25 stringSwitch
// test.kt:27 stringSwitch
// test.kt:34 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:30 $box (18, 18, 18, 18, 4)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4, 11, 4, 11, 4, 11, 4)
// test.kt:6 $stringSwitch (8, 14, 14, 14, 14, 8, 20, 8, 8, 8)
// String.kt:122 $kotlin.String.hashCode (12, 25, 12, 12, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 12, 25, 12, 12, 35, 28, 12, 25, 12, 12, 35, 28)
// String.kt:123 $kotlin.String.hashCode (30, 30, 30, 30)
// String.kt:124 $kotlin.String.hashCode (12, 26, 12, 12, 26, 12, 12, 26, 12, 12, 26, 12)
// String.kt:63 $kotlin.String.hashCode (12, 12, 12, 12)
// String.kt:66 $kotlin.String.hashCode (15, 8, 15, 8, 15, 8, 15, 8)
// String.kt:126 $kotlin.String.hashCode (8, 8, 8, 8)
// String.kt:127 $kotlin.String.hashCode (19, 8, 19, 8, 19, 8, 19, 8)
// String.kt:146 $kotlin.String.hashCode (37, 37, 37, 37)
// String.kt:147 $kotlin.String.hashCode (21, 7, 29, 7, 21, 12, 21, 7, 29, 7, 7, 21, 7, 29, 7, 21, 12, 21, 7, 29, 7, 7, 21, 12, 21, 7, 29, 7, 7, 21, 7, 29, 7, 21, 12, 21, 7, 29, 7, 7, 21, 12, 21, 7, 29, 7, 7, 21, 12, 21, 7, 29, 7, 7, 21, 7, 29, 7, 21, 12, 21, 7, 29, 7, 7, 21, 12, 21, 7, 29, 7, 7, 21, 12, 21, 7, 29, 7, 7, 21, 12, 21, 7, 29, 7, 7)
// String.kt:129 $kotlin.String.hashCode (20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12, 20, 29, 20, 34, 19, 41, 55, 51, 19, 12)
// String.kt:49 $kotlin.String.hashCode (43, 43, 43, 43, 43, 43, 43, 43, 43, 43)
// String.kt:50 $kotlin.String.hashCode (9, 9, 9, 9, 9, 9, 9, 9, 9, 9)
// String.kt:128 $kotlin.String.hashCode (8, 8, 8, 8)
// String.kt:131 $kotlin.String.hashCode (20, 8, 20, 8, 20, 8, 20, 8)
// String.kt:132 $kotlin.String.hashCode (15, 8, 15, 8, 15, 8, 15, 8)
// String.kt:143 $kotlin.stringLiteral (15, 8, 15, 8, 15, 8, 15, 8, 15, 8, 15, 8, 15, 8, 15, 8, 15, 8)
// String.kt:98 $kotlin.String.equals (12, 12, 12, 12, 12, 12, 12, 12, 12)
// String.kt:99 $kotlin.String.equals (12, 35, 28, 12, 35, 28, 12, 35, 28, 12, 35, 28, 12, 35, 28, 12, 35, 28, 12, 35, 28, 12, 35, 28, 12, 35, 28)
// test.kt:1 $stringSwitch (0, 0, 0, 0, 0, 0, 0, 0, 0)
// test.kt:5 $stringSwitch (4, 4, 4, 4)
// test.kt:12 $stringSwitch (19, 4, 19, 4, 19, 4, 19, 4)
// test.kt:13 $stringSwitch (8, 9, 9, 9, 9, 8, 15, 8, 8, 8)
// test.kt:20 $stringSwitch (9, 9, 9, 9)
// test.kt:22 $stringSwitch (8, 9, 9, 9, 9, 8, 15, 8, 8, 8)
// test.kt:19 $stringSwitch (4, 4, 4, 4)
// test.kt:31 $box (18, 18, 18, 18, 4)
// test.kt:7 $stringSwitch (8, 14, 14, 14, 14, 8, 21)
// test.kt:14 $stringSwitch (8, 9, 9, 9, 9, 8, 16)
// test.kt:23 $stringSwitch (8, 9, 9, 9, 9, 8, 16)
// test.kt:32 $box (18, 18, 18, 18, 4)
// test.kt:8 $stringSwitch (8, 14, 14, 14, 14, 8, 22)
// test.kt:15 $stringSwitch (8, 9, 9, 9, 9, 8, 17)
// test.kt:24 $stringSwitch (8, 9, 9, 9, 9, 8, 17)
// test.kt:33 $box (18, 18, 18, 18, 4)
// test.kt:9 $stringSwitch
// test.kt:16 $stringSwitch
// test.kt:25 $stringSwitch
