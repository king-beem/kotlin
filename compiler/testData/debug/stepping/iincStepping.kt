
// IGNORE_BACKEND: JVM
// FILE: test.kt
fun box() {
    var i = 0
    ++i
    i += 1
    i +=
        1
    i -= 1
    i -=
        1
    i = i + 1
    i =
        i + 1
    i =
        i +
            1
}

// The current backend has strange stepping behavior for assignments.
// It generates the line number for the assignment first, and then
// the evaluation of the right hand side with line numbers.
// That leads to the line number with the assignment typically
// not being hit at all as it has no instructions. Also, stepping
// through the evaluation of the right hand side and then hitting
// the line number for the actual assignment makes more sense as
// that is the actual evaluation order.

// EXPECTATIONS JVM JVM_IR
// test.kt:5 box
// test.kt:6 box
// test.kt:7 box
// test.kt:8 box
// test.kt:9 box
// test.kt:8 box
// test.kt:10 box
// test.kt:11 box
// test.kt:12 box
// test.kt:11 box
// test.kt:13 box
// test.kt:15 box
// test.kt:14 box
// test.kt:17 box
// test.kt:18 box
// test.kt:17 box
// test.kt:16 box
// test.kt:19 box

// EXPECTATIONS JS_IR
// test.kt:5 box
// test.kt:6 box
// test.kt:7 box
// test.kt:8 box
// test.kt:10 box
// test.kt:11 box
// test.kt:13 box
// test.kt:15 box
// test.kt:17 box
// test.kt:19 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:5 $box (12, 4)
// test.kt:6 $box (6, 6)
// test.kt:79 $box (43357, 43364, 43357, 43365)
// test.kt:7 $box (4, 9, 4, 4)
// test.kt:8 $box (4, 4, 4)
// test.kt:9 $box
// test.kt:10 $box (4, 9, 4, 4)
// test.kt:11 $box (4, 4, 4)
// test.kt:12 $box
// test.kt:13 $box (8, 12, 8, 4)
// test.kt:15 $box (8, 12, 8)
// test.kt:14 $box
// test.kt:17 $box (8, 8)
// test.kt:18 $box
// test.kt:16 $box
