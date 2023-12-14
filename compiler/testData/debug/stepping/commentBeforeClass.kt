
// FILE: test.kt

fun box() {
    A()
}

// Some comment
class A {

}

// EXPECTATIONS JVM JVM_IR
// test.kt:5 box
// test.kt:9 <init>
// test.kt:5 box
// test.kt:6 box

// EXPECTATIONS JS_IR
// test.kt:5 box
// test.kt:9 <init>
// test.kt:6 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:5 $box (4, 4, 4)
