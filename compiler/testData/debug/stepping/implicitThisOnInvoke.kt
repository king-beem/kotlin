
// FILE: test.kt

fun box() {
    test(B(A()))
}

class A

class B(val a: A) {
    operator fun A.invoke() {}
}

fun test(b: B) {
    with(b) {
        a()
    }
}

// EXPECTATIONS JVM JVM_IR
// test.kt:5 box
// test.kt:8 <init>
// test.kt:5 box
// test.kt:10 <init>
// test.kt:5 box
// test.kt:15 test

// test.kt:16 test
// test.kt:10 getA
// test.kt:16 test
// test.kt:11 invoke
// test.kt:17 test
// test.kt:15 test
// test.kt:18 test
// test.kt:6 box

// EXPECTATIONS JS_IR
// test.kt:5 box
// test.kt:8 <init>
// test.kt:5 box
// test.kt:10 <init>
// test.kt:10 <init>
// test.kt:5 box
// test.kt:11 invoke
// test.kt:18 test
// test.kt:6 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:5 $box (9, 11, 11, 9, 4)
// test.kt:10 $B.<init>
// test.kt:55 $test (1387, 1465)
// test.kt:16 $test
// test.kt:15 $test
