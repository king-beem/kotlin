// IGNORE_BACKEND_K2: JVM_IR

// FILE: test.kt
fun box(): String {
    val
            o
            =
        "O"


    val k = "K"

    return o + k
}

// EXPECTATIONS JVM_IR
// test.kt:8 box:
// test.kt:6 box:
// test.kt:11 box: o:java.lang.String="O":java.lang.String
// test.kt:13 box: o:java.lang.String="O":java.lang.String, k:java.lang.String="K":java.lang.String

// EXPECTATIONS JS_IR
// test.kt:8 box:
// test.kt:11 box: o="O":kotlin.String
// test.kt:13 box: o="O":kotlin.String, k="K":kotlin.String
