
// FILE: test.kt

class Foo {
    var a: String

    init {
        a = x()
    }
}

class Bar {
    init {
        val a = 5
    }

    init {
        val b = 2
    }
}

class Boo {
    init {
        val a = 5
    }

    val x = x()

    init {
        val b = 2
    }
}

class Zoo {
    init { val a = 5 }

    init { val b = 6 }

    init {
        val c = 7
    }

    init { val d = 8 }
}

fun x() = ""

fun box() {
    Foo()
    Bar()
    Boo()
    Zoo()
}

// JVM_IR has an extra step back to the line of the class
// declaration for the return in the constructor.

// EXPECTATIONS JVM JVM_IR
// test.kt:49 box
// test.kt:4 <init>
// test.kt:7 <init>
// test.kt:8 <init>
// test.kt:46 x
// test.kt:8 <init>
// test.kt:9 <init>
// EXPECTATIONS JVM_IR
// test.kt:4 <init>
// EXPECTATIONS JVM JVM_IR
// test.kt:49 box
// test.kt:50 box
// test.kt:12 <init>
// test.kt:13 <init>
// test.kt:14 <init>
// test.kt:15 <init>
// test.kt:17 <init>
// test.kt:18 <init>
// test.kt:19 <init>
// EXPECTATIONS JVM_IR
// test.kt:12 <init>
// EXPECTATIONS JVM JVM_IR
// test.kt:50 box
// test.kt:51 box
// test.kt:22 <init>
// test.kt:23 <init>
// test.kt:24 <init>
// test.kt:25 <init>
// test.kt:27 <init>
// test.kt:46 x
// test.kt:27 <init>
// test.kt:29 <init>
// test.kt:30 <init>
// test.kt:31 <init>
// EXPECTATIONS JVM_IR
// test.kt:22 <init>
// EXPECTATIONS JVM JVM_IR
// test.kt:51 box
// test.kt:52 box
// test.kt:34 <init>
// test.kt:35 <init>
// test.kt:37 <init>
// test.kt:39 <init>
// test.kt:40 <init>
// test.kt:41 <init>
// test.kt:43 <init>
// EXPECTATIONS JVM_IR
// test.kt:34 <init>
// EXPECTATIONS JVM JVM_IR
// test.kt:52 box
// test.kt:53 box

// EXPECTATIONS JS_IR
// test.kt:49 box
// test.kt:8 <init>
// test.kt:46 x
// test.kt:4 <init>
// test.kt:50 box
// test.kt:14 <init>
// test.kt:18 <init>
// test.kt:12 <init>
// test.kt:51 box
// test.kt:24 <init>
// test.kt:27 <init>
// test.kt:46 x
// test.kt:30 <init>
// test.kt:22 <init>
// test.kt:52 box
// test.kt:35 <init>
// test.kt:37 <init>
// test.kt:40 <init>
// test.kt:43 <init>
// test.kt:34 <init>
// test.kt:53 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:49 $box (4, 4, 4)
// test.kt:8 $Foo.<init> (8, 12, 8)
// test.kt:46 $x (10, 10, 10, 10, 12, 10, 10, 10, 10, 12)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4)
// test.kt:50 $box (4, 4, 4)
// test.kt:14 $Bar.<init> (16, 8)
// test.kt:18 $Bar.<init> (16, 8)
// test.kt:51 $box (4, 4, 4)
// test.kt:24 $Boo.<init> (16, 8)
// test.kt:27 $Boo.<init>
// String.kt:143 $kotlin.stringLiteral (15, 8)
// test.kt:30 $Boo.<init> (16, 8)
// test.kt:52 $box (4, 4, 4)
// test.kt:35 $Zoo.<init> (19, 11)
// test.kt:37 $Zoo.<init> (19, 11)
// test.kt:40 $Zoo.<init> (16, 8)
// test.kt:43 $Zoo.<init> (19, 11)
