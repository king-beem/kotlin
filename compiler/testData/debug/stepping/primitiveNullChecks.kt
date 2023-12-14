
// FILE: test.kt

fun box(): String {
    42!!
    42.toLong()!!
    return "OK"!!
}

// EXPECTATIONS JVM JVM_IR
// test.kt:5 box
// test.kt:6 box
// test.kt:7 box

// EXPECTATIONS JS_IR
// test.kt:5 box
// test.kt:6 box
// test.kt:6 box
// test.kt:7 box

// EXPECTATIONS WASM
// test.kt:1 $box__JsExportAdapter
// test.kt:5 $box
// test.kt:6 $box
// test.kt:7 $box (12, 12, 12, 12, 4)
// String.kt:141 $kotlin.stringLiteral (17, 28, 17)
// Array.kt:59 $kotlin.Array.get (19, 26, 34, 8)
// ThrowHelpers.kt:29 $kotlin.wasm.internal.rangeCheck (6, 14, 6, 19, 28, 19, 6, 14, 6, 19, 28, 19)
// Array.kt:60 $kotlin.Array.get (15, 27, 23, 8)
// String.kt:142 $kotlin.stringLiteral
// String.kt:146 $kotlin.stringLiteral (47, 61, 16, 4)
// String.kt:147 $kotlin.stringLiteral (20, 20, 20, 20, 27, 33, 41, 20, 4)
// String.kt:148 $kotlin.stringLiteral (4, 15, 25, 4)
// Array.kt:74 $kotlin.Array.set (19, 26, 34, 8)
// Array.kt:75 $kotlin.Array.set (8, 20, 27, 16)
// String.kt:149 $kotlin.stringLiteral (11, 4)
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
// Runtime.kt:35 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (11, 22, 11, 11, 11, 11, 22, 11, 11, 11, 11, 22, 11, 11, 11)
// Runtime.kt:36 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (25, 34, 42, 38, 8, 25, 34, 42, 38, 8)
// Runtime.kt:37 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (8, 19, 8, 8, 8, 19, 8, 8)
// Runtime.kt:38 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (8, 8, 8, 8)
// Runtime.kt:123 $kotlin.wasm.internal.unsafeWasmCharArrayToRawMemory (41411, 41418, 41411, 41419, 41411, 41418, 41411, 41419)
// ExternalWrapper.kt:220 $kotlin.wasm.internal.kotlinToJsStringAdapter (36, 47, 62, 47, 77, 15, 8, 8)
// ExternalWrapper.kt:149 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter
// ExternalWrapper.kt:225 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter (26, 16, 15, 15, 31)
// ExternalWrapper.kt:153 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter
// ExternalWrapper.kt:151 $kotlin.wasm.internal.jsCheckIsNullOrUndefinedAdapter
// ExternalWrapper.kt:82 $kotlin.wasm.internal.kotlinToJsStringAdapter (14, 24)
// MemoryAllocation.kt:139 $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy (8, 20, 8)
// MemoryAllocation.kt:140 $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy
// ExternalWrapper.kt:84 $kotlin.wasm.internal.kotlinToJsStringAdapter (6, 16)
// ExternalWrapper.kt:83 $kotlin.wasm.internal.kotlinToJsStringAdapter
// ExternalWrapper.kt:80 $kotlin.wasm.internal.kotlinToJsStringAdapter
