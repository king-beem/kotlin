
// FILE: test.kt

fun box() {
    var x: String
    var y: Int
    var z: Boolean
    z = false
    y = 42
    if (!z) {
        x = y.toString()
    }
}

// The JVM IR backend does generate line number information for the
// declaration of local variables without initializer.
// Stepping through those is useful for breakpoinnts.
// The JVM backend does generate these line numbers as well.

// EXPECTATIONS JVM JVM_IR
// test.kt:5 box
// test.kt:6 box
// test.kt:7 box
// test.kt:8 box
// test.kt:9 box
// test.kt:10 box
// test.kt:11 box
// test.kt:13 box

// EXPECTATIONS JS_IR
// test.kt:8 box
// test.kt:9 box
// test.kt:10 box
// test.kt:11 box
// test.kt:13 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:8 $box (8, 4)
// test.kt:9 $box (8, 4)
// test.kt:10 $box (9, 8)
// test.kt:11 $box (12, 14, 8)
// Primitives.kt:1359 $kotlin.Int__toString-impl (21, 8, 24)
// Number2String.kt:199 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:200 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:201 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:202 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:203 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:204 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:207 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 4, 4, 4)
// Number2String.kt:92 $kotlin.wasm.internal.<init properties Number2String.kt>
// Number2String.kt:220 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 4, 4, 4, 35, 66, 97, 4, 4)
// Number2String.kt:429 $kotlin.wasm.internal.<init properties Number2String.kt> (6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10, 6, 10)
// ULong.kt:17 $kotlin.<ULong__<get-data>-impl> (125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125, 125)
// Number2String.kt:221 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:222 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:223 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:224 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:225 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:226 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:227 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:228 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:229 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:230 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:231 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:232 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:233 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:234 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:235 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:236 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:237 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:238 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:239 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:240 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66, 97)
// Number2String.kt:241 $kotlin.wasm.internal.<init properties Number2String.kt> (4, 35, 66)
// Number2String.kt:75 $kotlin.wasm.internal.<init properties Number2String.kt> (0, 8)
// Number2String.kt:47 $kotlin.wasm.internal.itoa32 (8, 16, 8, 21, 29, 21)
// Number2String.kt:50 $kotlin.wasm.internal.itoa32 (8, 17, 8, 8)
// Number2String.kt:53 $kotlin.wasm.internal.itoa32 (8, 22, 8)
// Number2String.kt:55 $kotlin.wasm.internal.itoa32 (8, 26, 8)
// Number2String.kt:57 $kotlin.wasm.internal.itoa32 (15, 31, 15)
// Number2String.kt:58 $kotlin.wasm.internal.itoa32 (11, 19, 11, 24, 32, 24, 4)
// Assertions.kt:14 $kotlin.assert (11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4)
// Assertions.kt:21 $kotlin.assert (9, 8, 9, 8, 9, 8, 9, 8, 9, 8, 9, 8)
// Number2String.kt:59 $kotlin.wasm.internal.itoa32 (23, 31, 23, 51)
// Number2String.kt:61 $kotlin.wasm.internal.itoa32 (34, 19, 46, 19)
// Number2String.kt:107 $kotlin.wasm.internal.decimalCount32 (8, 16, 8)
// Number2String.kt:108 $kotlin.wasm.internal.decimalCount32 (12, 20, 12)
// Number2String.kt:109 $kotlin.wasm.internal.decimalCount32 (19, 24, 33, 24, 19, 12)
// Number2String.kt:62 $kotlin.wasm.internal.itoa32 (28, 14)
// Number2String.kt:63 $kotlin.wasm.internal.itoa32 (18, 23, 33, 4)
// Number2String.kt:71 $kotlin.wasm.internal.utoaDecSimple (11, 23, 11, 11, 4)
// Number2String.kt:72 $kotlin.wasm.internal.utoaDecSimple (11, 18, 26, 11, 4)
// Number2String.kt:73 $kotlin.wasm.internal.utoaDecSimple (11, 25, 11, 30, 45, 52, 30, 4)
// Number2String.kt:75 $kotlin.wasm.internal.utoaDecSimple (14, 4)
// Number2String.kt:76 $kotlin.wasm.internal.utoaDecSimple (17, 4)
// Number2String.kt:78 $kotlin.wasm.internal.utoaDecSimple (16, 22, 16, 8, 16, 22, 16, 8)
// Primitives.kt:1066 $kotlin.Int__div-impl (24, 12, 69, 96, 24, 12, 69, 96)
// Number2String.kt:79 $kotlin.wasm.internal.utoaDecSimple (16, 22, 16, 8, 16, 22, 16, 8)
// Number2String.kt:80 $kotlin.wasm.internal.utoaDecSimple (14, 8, 14, 8)
// Number2String.kt:81 $kotlin.wasm.internal.utoaDecSimple (8, 8)
// Primitives.kt:1159 $kotlin.Int__dec-impl (15, 8, 16, 15, 8, 16)
// Number2String.kt:82 $kotlin.wasm.internal.utoaDecSimple (8, 19, 39, 27, 15, 8, 19, 39, 27, 15)
// Number2String.kt:41 $kotlin.wasm.internal.digitToChar (20, 11, 23, 11, 4, 20, 11, 23, 11, 4)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8, 8, 8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4, 11, 4, 11, 4, 11, 4, 11, 4)
// Number2String.kt:9 $kotlin.wasm.internal.CharCodes_initEntries
// Enum.kt:9 $kotlin.Enum.<init> (4, 4, 4, 4, 4)
// Enum.kt:11 $kotlin.Enum.<init> (4, 4, 4, 4, 4)
// Number2String.kt:7 $kotlin.wasm.internal.CharCodes.<init> (29, 29, 29, 29, 29)
// Number2String.kt:10 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:11 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:12 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:32 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:42 $kotlin.wasm.internal.digitToChar (25, 32, 12, 39, 4, 25, 32, 12, 39, 4)
// Primitives.kt:1306 $kotlin.Int__toChar-impl (18, 9, 45, 18, 9, 45)
// Number2String.kt:83 $kotlin.wasm.internal.utoaDecSimple (13, 19, 13, 13, 13, 19, 13, 13)
// Number2String.kt:64 $kotlin.wasm.internal.itoa32 (8, 16, 8)
// Number2String.kt:173 $kotlin.wasm.internal.itoa32 (10, 10, 10, 10, 17, 23, 28, 35, 10)
// Number2String.kt:174 $kotlin.wasm.internal.itoa32
// Number2String.kt:67 $kotlin.wasm.internal.itoa32
