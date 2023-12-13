// IGNORE_BACKEND: WASM
// FILE: test.kt

class AWithCompanion {
    companion object {
        //Comment before
        val compPropVal = 1
    }
}

fun box() {
    AWithCompanion.compPropVal
}

// EXPECTATIONS JVM JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:12 box
// test.kt:7 <clinit>
// test.kt:7 getCompPropVal
// test.kt:7 getCompPropVal
// test.kt:12 box
// test.kt:13 box

// EXPECTATIONS JS_IR
// test.kt:12 box
// test.kt:7 <init>
// test.kt:5 <init>
// test.kt:13 box
