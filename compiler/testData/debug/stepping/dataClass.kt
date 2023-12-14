
// FILE: test.kt

data class D(val i: Int, val s: String)

data class E(val i: Int, val s: String) {
    override fun toString() = "OK"
    override fun equals(other: Any?) = false
    override fun hashCode() = 42
    fun copy() = E(i, s)
}

fun box() {
    val d = D(1, "a")
    d.equals(D(1, "a"))
    d.hashCode()
    d.toString()
    val (i, s) = d
    d.copy()
    val e = E(1, "a")
    e.equals(E(1, "a"))
    e.hashCode()
    e.toString()
    val (s2, i2) = e
    e.copy()
}

// EXPECTATIONS JVM JVM_IR
// test.kt:14 box
// test.kt:4 <init>
// test.kt:14 box
// test.kt:15 box
// test.kt:4 <init>
// test.kt:15 box
// test.kt:16 box
// test.kt:17 box
// test.kt:18 box
// test.kt:19 box
// test.kt:4 <init>
// test.kt:-1 copy
// test.kt:19 box
// test.kt:20 box
// test.kt:6 <init>
// test.kt:20 box
// test.kt:21 box
// test.kt:6 <init>
// test.kt:21 box
// test.kt:8 equals
// test.kt:21 box
// test.kt:22 box
// test.kt:9 hashCode
// test.kt:22 box
// test.kt:23 box
// test.kt:7 toString
// test.kt:23 box
// test.kt:24 box
// test.kt:25 box
// test.kt:10 copy
// test.kt:6 <init>
// test.kt:10 copy
// test.kt:25 box
// test.kt:26 box

// EXPECTATIONS JS_IR
// test.kt:14 box
// test.kt:4 <init>
// test.kt:4 <init>
// test.kt:4 <init>
// test.kt:15 box
// test.kt:4 <init>
// test.kt:4 <init>
// test.kt:4 <init>
// test.kt:15 box
// test.kt:1 equals
// test.kt:1 equals
// test.kt:1 equals
// test.kt:1 equals
// test.kt:1 equals
// test.kt:1 equals
// test.kt:16 box
// test.kt:1 hashCode
// test.kt:1 hashCode
// test.kt:17 box
// test.kt:1 toString
// test.kt:18 box
// test.kt:1 component1
// test.kt:18 box
// test.kt:1 component2
// test.kt:19 box
// test.kt:1 copy$default
// test.kt:1 copy$default
// test.kt:1 copy
// test.kt:4 <init>
// test.kt:4 <init>
// test.kt:4 <init>
// test.kt:20 box
// test.kt:6 <init>
// test.kt:6 <init>
// test.kt:6 <init>
// test.kt:21 box
// test.kt:6 <init>
// test.kt:6 <init>
// test.kt:6 <init>
// test.kt:21 box
// test.kt:8 equals
// test.kt:22 box
// test.kt:9 hashCode
// test.kt:23 box
// test.kt:7 toString
// test.kt:24 box
// test.kt:1 component1
// test.kt:24 box
// test.kt:1 component2
// test.kt:25 box
// test.kt:10 copy
// test.kt:6 <init>
// test.kt:6 <init>
// test.kt:6 <init>
// test.kt:26 box

// EXPECTATIONS WASM
// test.kt:14 $box (12, 14, 18, 18, 18, 18, 12)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4, 11, 4, 11, 4, 11, 4, 11, 4, 11, 4, 11, 4, 11, 4, 11, 4, 11, 4)
// test.kt:4 $D.<init> (13, 25, 13, 25, 13, 25)
// test.kt:15 $box (4, 13, 15, 19, 19, 19, 19, 13, 6, 6)
// String.kt:143 $kotlin.stringLiteral (15, 8, 15, 8, 15, 8)
// String.kt:98 $kotlin.String.equals
// String.kt:99 $kotlin.String.equals (12, 35, 28)
// test.kt:16 $box (4, 6, 6)
// Primitives.kt:1366 $kotlin.Int__hashCode-impl
// String.kt:122 $kotlin.String.hashCode (12, 25, 12, 12)
// String.kt:123 $kotlin.String.hashCode
// String.kt:124 $kotlin.String.hashCode (12, 26, 12)
// String.kt:63 $kotlin.String.hashCode
// String.kt:66 $kotlin.String.hashCode (15, 8)
// String.kt:126 $kotlin.String.hashCode
// String.kt:127 $kotlin.String.hashCode (19, 8)
// String.kt:146 $kotlin.String.hashCode
// String.kt:147 $kotlin.String.hashCode (21, 7, 29, 7, 21, 12, 21, 7, 29, 7, 7)
// String.kt:129 $kotlin.String.hashCode (20, 29, 20, 34, 19, 41, 55, 51, 19, 12)
// String.kt:49 $kotlin.String.hashCode
// String.kt:50 $kotlin.String.hashCode
// String.kt:128 $kotlin.String.hashCode
// String.kt:131 $kotlin.String.hashCode (20, 8)
// String.kt:132 $kotlin.String.hashCode (15, 8)
// test.kt:17 $box (4, 6, 6)
// StringBuilder.kt:17 $kotlin.text.StringBuilder.<init> (39, 34, 34)
// StringBuilder.kt:20 $kotlin.text.StringBuilder.<init> (52, 62, 52, 47, 47)
// Arrays.kt:95 $kotlin.CharArray.<init> (12, 19, 12, 12, 19, 12)
// Arrays.kt:96 $kotlin.CharArray.<init> (8, 32, 18, 8, 8, 32, 18, 8)
// StringBuilder.kt:14 $kotlin.text.StringBuilder.<init>
// StringBuilder.kt:33 $kotlin.text.StringBuilder.<init>
// StringBuilder.kt:143 $kotlin.text.StringBuilder.append (59, 66, 72, 59, 83, 59, 66, 72, 59, 83, 59, 66, 72, 59, 83, 59, 66, 72, 59, 83)
// Library.kt:19 $kotlin.toString (37, 37, 43, 43, 43, 37, 37, 63, 37, 37, 43, 43, 43, 37, 37, 63, 37, 37, 43, 43, 43, 37, 37, 63, 37, 37, 43, 43, 43, 37, 37, 63)
// String.kt:119 $kotlin.String.toString (49, 49, 49, 49)
// StringBuilder.kt:225 $kotlin.text.StringBuilder.append (23, 23, 8, 23, 23, 8, 23, 23, 8, 23, 23, 8)
// StringBuilder.kt:226 $kotlin.text.StringBuilder.append (8, 28, 37, 8, 8, 28, 37, 8, 8, 28, 37, 8, 8, 28, 37, 8)
// StringBuilder.kt:696 $kotlin.text.StringBuilder.ensureExtraCapacity (8, 31, 41, 31, 8, 8, 31, 41, 31, 8, 8, 31, 41, 31, 8, 8, 31, 41, 31, 8, 8, 31, 41, 31, 8)
// StringBuilder.kt:700 $kotlin.text.StringBuilder.ensureCapacityInternal (12, 26, 12, 12, 26, 12, 12, 26, 12, 12, 26, 12, 12, 26, 12)
// StringBuilder.kt:701 $kotlin.text.StringBuilder.ensureCapacityInternal (12, 26, 32, 12, 12, 26, 32, 12, 12, 26, 32, 12, 12, 26, 32, 12, 12, 26, 32, 12)
// Arrays.kt:135 $kotlin.CharArray.<get-size> (16, 24, 29, 16, 24, 29, 16, 24, 29, 16, 24, 29, 16, 24, 29, 16, 24, 29, 16, 24, 29, 16, 24, 29, 16, 24, 29)
// StringBuilder.kt:227 $kotlin.text.StringBuilder.append (8, 32, 39, 48, 19, 8, 8, 8, 32, 39, 48, 19, 8, 8, 8, 32, 39, 48, 19, 8, 8, 8, 32, 39, 48, 19, 8, 8)
// StringBuilder.kt:915 $kotlin.text.insertString (21, 28, 35, 42, 45, 51, 8, 58, 21, 28, 35, 42, 45, 51, 8, 58, 21, 28, 35, 42, 45, 51, 8, 58, 21, 28, 35, 42, 45, 51, 8, 58)
// StringBuilderWasm.kt:53 $kotlin.text.insertString (7, 7, 7, 7, 7)
// StringBuilderWasm.kt:54 $kotlin.text.insertString (43, 36, 43, 36, 43, 36, 43, 36, 43, 36)
// StringBuilderWasm.kt:41 $kotlin.text.insertString (31, 37, 4, 31, 37, 4, 31, 37, 4, 31, 37, 4, 31, 37, 4)
// StringBuilderWasm.kt:57 $kotlin.text.insertString (199, 212, 230, 238, 251, 168, 199, 212, 230, 238, 251, 168, 199, 212, 230, 238, 251, 168, 199, 212, 230, 238, 251, 168, 199, 212, 230, 238, 251, 168)
// StringBuilderWasm.kt:42 $kotlin.text.insertString (11, 4, 11, 4, 11, 4, 11, 4, 11, 4)
// StringBuilder.kt:228 $kotlin.text.StringBuilder.append (15, 8, 15, 8, 15, 8, 15, 8)
// StringBuilder.kt:178 $kotlin.text.StringBuilder.append (8, 28, 8)
// StringBuilder.kt:702 $kotlin.text.StringBuilder.ensureCapacityInternal (51, 57, 63, 39, 12)
// AbstractList.kt:141 $kotlin.collections.Companion.newCapacity (30, 45, 61, 45, 30)
// AbstractList.kt:142 $kotlin.collections.Companion.newCapacity (16, 30, 16, 44, 16)
// AbstractList.kt:144 $kotlin.collections.Companion.newCapacity (16, 30, 16, 45, 16)
// AbstractList.kt:146 $kotlin.collections.Companion.newCapacity (19, 12)
// StringBuilder.kt:703 $kotlin.text.StringBuilder.ensureCapacityInternal (12, 20, 33, 26, 12)
// _ArraysWasm.kt:1417 $kotlin.collections.copyOf (11, 44, 16, 4)
// _ArraysWasm.kt:1781 $kotlin.collections.copyOfUninitializedElements (11, 39, 42, 11, 4)
// _ArraysWasm.kt:1694 $kotlin.collections.copyOfUninitializedElements (18, 28, 18)
// _ArraysWasm.kt:1695 $kotlin.collections.copyOfUninitializedElements (8, 18, 8)
// _ArraysWasm.kt:1698 $kotlin.collections.copyOfUninitializedElements (17, 27, 17, 4)
// _ArraysWasm.kt:1699 $kotlin.collections.copyOfUninitializedElements (4, 18, 26, 29, 40, 61, 48, 9, 9)
// _Ranges.kt:1321 $kotlin.ranges.coerceAtMost (15, 22, 15, 36, 4)
// _ArraysWasm.kt:1228 $kotlin.collections.copyInto (35, 47, 57, 62, 17)
// AbstractList.kt:119 $kotlin.collections.Companion.checkRangeIndexes (16, 28, 16, 33, 43, 33, 16, 28, 16, 33, 43, 33)
// AbstractList.kt:122 $kotlin.collections.Companion.checkRangeIndexes (16, 28, 16, 16, 28, 16)
// _ArraysWasm.kt:1229 $kotlin.collections.copyInto (20, 31, 20, 4)
// _ArraysWasm.kt:1230 $kotlin.collections.copyInto (35, 54, 74, 54, 85, 97, 17)
// _ArraysWasm.kt:1231 $kotlin.collections.copyInto (39, 44, 53, 65, 25)
// _ArraysWasm.kt:81 $kotlin.collections.copyInto (4, 17)
// _ArraysWasm.kt:82 $kotlin.collections.copyInto (7, 15, 28)
// _ArraysWasm.kt:79 $kotlin.collections.copyInto
// _ArraysWasm.kt:1232 $kotlin.collections.copyInto (11, 4)
// _ArraysWasm.kt:1700 $kotlin.collections.copyOfUninitializedElements (11, 4)
// StringBuilder.kt:179 $kotlin.text.StringBuilder.append (8, 29, 36, 45, 19, 8, 8)
// StringBuilderWasm.kt:52 $kotlin.text.insertInt (22, 28)
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
// Assertions.kt:14 $kotlin.assert (11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4, 11, 18, 18, 4)
// Assertions.kt:21 $kotlin.assert (9, 8, 9, 8, 9, 8, 9, 8, 9, 8)
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
// Number2String.kt:78 $kotlin.wasm.internal.utoaDecSimple (16, 22, 16, 8)
// Primitives.kt:1066 $kotlin.Int__div-impl (24, 12, 69, 96)
// Number2String.kt:79 $kotlin.wasm.internal.utoaDecSimple (16, 22, 16, 8)
// Number2String.kt:80 $kotlin.wasm.internal.utoaDecSimple (14, 8)
// Number2String.kt:81 $kotlin.wasm.internal.utoaDecSimple
// Primitives.kt:1159 $kotlin.Int__dec-impl (15, 8, 16)
// Number2String.kt:82 $kotlin.wasm.internal.utoaDecSimple (8, 19, 39, 27, 15)
// Number2String.kt:41 $kotlin.wasm.internal.digitToChar (20, 11, 23, 11, 4)
// Number2String.kt:9 $kotlin.wasm.internal.CharCodes_initEntries
// Enum.kt:9 $kotlin.Enum.<init> (4, 4, 4, 4, 4)
// Enum.kt:11 $kotlin.Enum.<init> (4, 4, 4, 4, 4)
// Number2String.kt:7 $kotlin.wasm.internal.CharCodes.<init> (29, 29, 29, 29, 29)
// Number2String.kt:10 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:11 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:12 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:32 $kotlin.wasm.internal.CharCodes_initEntries
// Number2String.kt:42 $kotlin.wasm.internal.digitToChar (25, 32, 12, 39, 4)
// Primitives.kt:1306 $kotlin.Int__toChar-impl (18, 9, 45)
// Number2String.kt:83 $kotlin.wasm.internal.utoaDecSimple (13, 19, 13, 13)
// Number2String.kt:64 $kotlin.wasm.internal.itoa32 (8, 16, 8)
// Number2String.kt:173 $kotlin.wasm.internal.itoa32 (10, 10, 10, 10, 17, 23, 28, 35, 10)
// Number2String.kt:174 $kotlin.wasm.internal.itoa32
// Number2String.kt:67 $kotlin.wasm.internal.itoa32
// StringBuilderWasm.kt:53 $kotlin.text.insertInt (17, 29, 4)
// StringBuilderWasm.kt:54 $kotlin.text.insertInt (17, 24, 31, 44, 47, 4, 4)
// StringBuilderWasm.kt:55 $kotlin.text.insertInt (11, 4)
// StringBuilder.kt:180 $kotlin.text.StringBuilder.append (15, 8)
// StringBuilder.kt:499 $kotlin.text.StringBuilder.toString (64, 71, 74, 38, 82)
// StringBuilderWasm.kt:46 $kotlin.text.unsafeStringFromCharArray (29, 15, 4)
// StringBuilderWasm.kt:47 $kotlin.text.unsafeStringFromCharArray (18, 24, 46, 4)
// StringBuilderWasm.kt:57 $kotlin.text.unsafeStringFromCharArray (199, 230, 238, 251, 168, 2348, 2348, 2348, 2348, 2355, 2361, 2366, 2373, 2348, 2378)
// StringBuilderWasm.kt:48 $kotlin.text.unsafeStringFromCharArray
// test.kt:18 $box (17, 9, 9, 17, 12, 12)
// test.kt:1 $D.component1
// test.kt:19 $box (4, 6, 6, 6, 6, 6, 6)
// test.kt:20 $box (12, 14, 18, 18, 18, 18, 12)
// test.kt:6 $E.<init> (13, 25, 13, 25, 13, 25)
// test.kt:21 $box (4, 13, 15, 19, 19, 19, 19, 13, 6, 6)
// test.kt:8 $E.equals (39, 44)
// test.kt:22 $box (4, 6, 6)
// test.kt:9 $E.hashCode (30, 32)
// test.kt:23 $box (4, 6, 6)
// test.kt:7 $E.toString (31, 31, 31, 31, 33)
// test.kt:24 $box (19, 9, 9, 19, 13, 13)
// test.kt:1 $E.component1
// test.kt:25 $box (4, 6, 6)
// test.kt:10 $E.copy (17, 19, 22, 17, 24)
