
// FILE: test.kt

fun foo() {
    try {
        mightThrow()
    } finally {
        "FINALLY"
    }
    
    val t = try {
        mightThrow2()
    } finally {
        "FINALLY2"
    }
}

var throw1 = false
var throw2 = false

fun mightThrow() {
    if (throw1) throw Exception()
}

fun mightThrow2() {
    if (throw2) throw Exception()
}

fun box() {
    foo()
    throw2 = true
    foo()
    // Never gets here.
    throw1 = true
    foo()
}

// The JVM backend steps back to line 11 when leaving the
// `mightThrow2` call. The JVM_IR backend does not. The
// JVM_IR behavior is consistent with what happens for the
// try-finally where the value is discarded which seems good.

// EXPECTATIONS JVM JVM_IR
// test.kt:30 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:22 mightThrow
// test.kt:23 mightThrow
// test.kt:8 foo
// test.kt:9 foo
// test.kt:11 foo
// test.kt:12 foo
// test.kt:26 mightThrow2
// test.kt:27 mightThrow2
// EXPECTATIONS JVM
// test.kt:12 foo
// EXPECTATIONS JVM JVM_IR
// test.kt:14 foo
// test.kt:15 foo
// test.kt:11 foo
// test.kt:16 foo
// test.kt:31 box
// test.kt:32 box
// test.kt:5 foo
// test.kt:6 foo
// test.kt:22 mightThrow
// test.kt:23 mightThrow
// test.kt:8 foo
// test.kt:9 foo
// test.kt:11 foo
// test.kt:12 foo
// test.kt:26 mightThrow2
// EXPECTATIONS JVM
// test.kt:15 foo
// test.kt:11 foo
// EXPECTATIONS JVM_IR
// test.kt:14 foo

// EXPECTATIONS JS_IR
// test.kt:30 box
// test.kt:6 foo
// test.kt:22 mightThrow
// test.kt:23 mightThrow
// test.kt:12 foo
// test.kt:26 mightThrow2
// test.kt:27 mightThrow2
// test.kt:11 foo
// test.kt:16 foo
// test.kt:31 box
// test.kt:32 box
// test.kt:6 foo
// test.kt:22 mightThrow
// test.kt:23 mightThrow
// test.kt:12 foo
// test.kt:26 mightThrow2
// test.kt:26 mightThrow2

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:30 $box
// test.kt:6 $foo (8, 8)
// test.kt:22 $mightThrow (8, 8)
// test.kt:5 $foo (4, 4)
// test.kt:8 $foo (9, 9, 9, 9, 9, 9, 9, 9)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17, 17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8, 15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral (8, 8, 8, 8, 8)
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4, 47, 61, 16, 4, 47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4, 20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4, 4, 15, 25, 4, 4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8, 19, 26, 34, 8, 19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16, 8, 20, 27, 16, 8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4, 11, 4, 11, 4)
// test.kt:12 $foo (8, 8)
// test.kt:26 $mightThrow2 (8, 8, 22, 22, 16)
// test.kt:11 $foo (12, 4, 12)
// test.kt:14 $foo (9, 9, 9, 9, 9, 9, 9, 9)
// test.kt:31 $box (13, 4)
// test.kt:32 $box
// String.kt:143 $kotlin.stringLiteral (15, 8, 15, 8)
// Exceptions.kt:16 $kotlin.Exception.<init> (34, 34, 4, 4)
// Throwable.kt:23 $kotlin.Throwable.<init> (32, 38, 27, 27)
// Throwable.kt:18 $kotlin.Throwable.<init> (28, 62)
// Throwable.kt:25 $kotlin.Throwable.<init>
// ExternalWrapper.kt:149 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (34, 34)
// ExternalWrapper.kt:225 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (26, 16, 15, 15, 31, 26, 16, 15, 15, 31)
// ExternalWrapper.kt:153 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (16, 16)
// ExternalWrapper.kt:151 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (16, 16)
// Throwable.kt:27 $kotlin.Throwable.<init>
// Throwable.kt:39 $kotlin.Throwable.<init>
// ExceptionHelpers.kt:20 $kotlin.wasm.internal.throwAsJsException (17, 19, 19, 19, 42, 44, 28, 55, 57, 4)
// Throwable.kt:18 $kotlin.Throwable.<get-message>
// TypeInfo.kt:34 $kotlin.wasm.internal.getSimpleName
// TypeInfo.kt:35 $kotlin.wasm.internal.getSimpleName
// TypeInfo.kt:36 $kotlin.wasm.internal.getSimpleName
// TypeInfo.kt:37 $kotlin.wasm.internal.getSimpleName
// TypeInfo.kt:33 $kotlin.wasm.internal.getSimpleName
// TypeInfo.kt:48 $kotlin.wasm.internal.getString (31, 45, 31, 17, 4)
// TypeInfo.kt:49 $kotlin.wasm.internal.getString (27, 41, 27, 13, 4)
// TypeInfo.kt:50 $kotlin.wasm.internal.getString (28, 42, 28, 14, 4)
// TypeInfo.kt:51 $kotlin.wasm.internal.getString (25, 29, 34, 11, 4)
// TypeInfo.kt:38 $kotlin.wasm.internal.getSimpleName
// ExternalWrapper.kt:200 $kotlin.wasm.internal.kotlinToJsStringAdapter
// ExternalWrapper.kt:356 $kotlin.wasm.internal.kotlinToJsStringAdapter (60, 60)
// String.kt:18 $kotlin.String.<get-length>
// ExternalWrapper.kt:357 $kotlin.wasm.internal.kotlinToJsStringAdapter (9, 10)
// ExternalWrapper.kt:85 $kotlin.wasm.internal.kotlinToJsStringAdapter
// ExternalWrapper.kt:89 $kotlin.wasm.internal.kotlinToJsStringAdapter (56, 49)
// ExternalWrapper.kt:204 $kotlin.wasm.internal.kotlinToJsStringAdapter (23, 32, 4)
// ExternalWrapper.kt:205 $kotlin.wasm.internal.kotlinToJsStringAdapter (26, 4)
// ExternalWrapper.kt:76 $kotlin.wasm.internal.kotlinToJsStringAdapter
// ExternalWrapper.kt:78 $kotlin.wasm.internal.kotlinToJsStringAdapter (41, 25)
// MemoryAllocation.kt:69 $kotlin.wasm.unsafe.createAllocatorInTheNewScope (20, 38, 20)
// MemoryAllocation.kt:70 $kotlin.wasm.unsafe.createAllocatorInTheNewScope (8, 30, 68, 8)
// MemoryAllocation.kt:88 $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init> (4, 4)
// MemoryAllocation.kt:86 $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init>
// MemoryAllocation.kt:90 $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init>
// MemoryAllocation.kt:93 $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init>
// MemoryAllocation.kt:96 $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init>
// MemoryAllocation.kt:71 $kotlin.wasm.unsafe.createAllocatorInTheNewScope (23, 4)
// MemoryAllocation.kt:72 $kotlin.wasm.unsafe.createAllocatorInTheNewScope (11, 4)
// ExternalWrapper.kt:209 $kotlin.wasm.internal.kotlinToJsStringAdapter (24, 43, 69, 56, 88, 43, 24, 34, 34, 34, 105, 8)
// _Ranges.kt:1321 $kotlin.ranges.coerceAtMost (15, 22, 15, 54, 4)
// MemoryAllocation.kt:99 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (15, 14, 8)
// MemoryAllocation.kt:23 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (8, 8, 8, 8)
// MemoryAllocation.kt:27 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (6, 5, 6, 5, 6, 5, 6, 5)
// MemoryAllocation.kt:100 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (15, 14, 8)
// MemoryAllocation.kt:104 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (20, 8)
// MemoryAllocation.kt:105 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (22, 41, 22, 49, 22, 57, 65, 57, 21, 8)
// MemoryAllocation.kt:160 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (44656, 44665, 44661, 44668, 12576, 12571, 12581)
// MemoryAllocation.kt:106 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (14, 23, 14, 28, 37, 28, 46, 28, 8)
// MemoryAllocation.kt:108 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (16, 28, 12, 47, 12)
// MemoryAllocation.kt:112 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (27, 36, 27, 8)
// MemoryAllocation.kt:114 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (29, 48, 29, 8)
// MemoryAllocation.kt:115 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (12, 32, 12)
// MemoryAllocation.kt:125 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (14, 33, 52, 33, 14, 8)
// MemoryAllocation.kt:61 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate
// UInt.kt:17 $kotlin.<UInt__<init>-impl>
// MemoryAllocation.kt:127 $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (15, 8)
// MemoryAccess.kt:16 $kotlin.wasm.unsafe.<Pointer__<init>-impl>
// MemoryAccess.kt:16 $kotlin.wasm.unsafe.<Pointer__<get-address>-impl>
// ExternalWrapper.kt:410 $kotlin.wasm.internal.kotlinToJsStringAdapter (1204, 1208)
// UInt.kt:17 $kotlin.<UInt__<get-data>-impl>
// ExternalWrapper.kt:211 $kotlin.wasm.internal.kotlinToJsStringAdapter (45, 8)
// ExternalWrapper.kt:212 $kotlin.wasm.internal.kotlinToJsStringAdapter (28, 8)
// ExternalWrapper.kt:213 $kotlin.wasm.internal.kotlinToJsStringAdapter (15, 31, 46, 31, 15, 15, 15)
// ExternalWrapper.kt:219 $kotlin.wasm.internal.kotlinToJsStringAdapter (39, 49, 64, 79, 64, 94, 8)
// Runtime.kt:32 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (18, 4)
// Runtime.kt:33 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (23, 35, 23, 4)
// Runtime.kt:34 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (19, 4)
// Runtime.kt:35 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11)
// Runtime.kt:36 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8, 25, 34, 42, 38, 8)
// Runtime.kt:37 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8, 8, 19, 8, 8)
// Runtime.kt:38 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8)
// Runtime.kt:123 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419)
// ExternalWrapper.kt:220 $kotlin.wasm.internal.kotlinToJsStringAdapter (36, 47, 62, 47, 77, 15, 8, 8)
// ExternalWrapper.kt:82 $kotlin.wasm.internal.kotlinToJsStringAdapter (14, 24)
// MemoryAllocation.kt:139 $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy (8, 20, 8)
// MemoryAllocation.kt:140 $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy
// ExternalWrapper.kt:84 $kotlin.wasm.internal.kotlinToJsStringAdapter (6, 16)
// ExternalWrapper.kt:83 $kotlin.wasm.internal.kotlinToJsStringAdapter
// ExternalWrapper.kt:80 $kotlin.wasm.internal.kotlinToJsStringAdapter
