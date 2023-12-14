
// FILE: test.kt
fun box() {
    var x = false
    f {
        x = true
    }
}

fun f(block: () -> Unit) {
    block()
}

// EXPECTATIONS JVM JVM_IR
// test.kt:4 box
// test.kt:5 box
// test.kt:11 f
// test.kt:6 invoke
// test.kt:7 invoke
// test.kt:11 f
// test.kt:12 f
// test.kt:8 box

// EXPECTATIONS JS_IR
// test.kt:4 box
// test.kt:5 box
// test.kt:5 box$lambda
// test.kt:5 box
// test.kt:11 f
// test.kt:6 box$lambda$lambda
// test.kt:7 box$lambda$lambda
// test.kt:12 f
// test.kt:8 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:4 $box (12, 4)
// test.kt:5 $box (6, 6, 4)
// test.kt:11 $f
// test.kt:6 $box$lambda.invoke (8, 12, 12, 12, 12, 8)
