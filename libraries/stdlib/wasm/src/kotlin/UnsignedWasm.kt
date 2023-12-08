/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin

import kotlin.internal.InlineOnly
import kotlin.wasm.internal.*
import kotlin.wasm.internal.WasmOp
import kotlin.wasm.internal.implementedAsIntrinsic
import kotlin.wasm.internal.wasm_u32_compareTo

@PublishedApi
@WasmOp(WasmOp.I32_REM_U)
internal actual fun uintRemainder(v1: UInt, v2: UInt): UInt = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I32_DIV_U)
internal actual fun uintDivide(v1: UInt, v2: UInt): UInt = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I64_REM_U)
internal actual fun ulongRemainder(v1: ULong, v2: ULong): ULong = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I64_DIV_U)
internal actual fun ulongDivide(v1: ULong, v2: ULong): ULong = implementedAsIntrinsic

@PublishedApi
@InlineOnly
internal actual inline fun uintCompare(v1: Int, v2: Int): Int = wasm_u32_compareTo(v1, v2)

@PublishedApi
@InlineOnly
internal actual inline fun ulongCompare(v1: Long, v2: Long): Int = wasm_u64_compareTo(v1, v2)

@PublishedApi
@WasmOp(WasmOp.I64_EXTEND_I32_U)
internal actual fun uintToULong(value: Int): ULong = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I64_EXTEND_I32_U)
internal actual fun uintToLong(value: Int): Long = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.F32_CONVERT_I32_U)
internal actual fun uintToFloat(value: Int): Float = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I32_TRUNC_SAT_F32_U)
internal actual fun floatToUInt(value: Float): UInt = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.F64_CONVERT_I32_U)
internal actual fun uintToDouble(value: Int): Double = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I32_TRUNC_SAT_F64_U)
internal actual fun doubleToUInt(value: Double): UInt = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.F32_CONVERT_I64_U)
internal actual fun ulongToFloat(value: Long): Float = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I64_TRUNC_SAT_F32_U)
internal actual fun floatToULong(value: Float): ULong = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.F64_CONVERT_I64_U)
internal actual fun ulongToDouble(value: Long): Double = implementedAsIntrinsic

@PublishedApi
@WasmOp(WasmOp.I64_TRUNC_SAT_F64_U)
internal actual fun doubleToULong(value: Double): ULong = implementedAsIntrinsic

@InlineOnly
internal actual inline fun uintToString(value: Int): String = utoa32(value.toUInt())

internal actual fun uintToString(value: Int, base: Int): String = numberToString(
    value = value,
    size = UInt.SIZE_BITS - 1,
    base = base,
    mod = { this % it },
    div = { this / it },
    contains = { it in this }
)

@InlineOnly
internal actual inline fun ulongToString(value: Long): String = utoa64(value.toULong())

internal actual fun ulongToString(value: Long, base: Int): String = numberToString(
    value = value,
    size = ULong.SIZE_BITS - 1,
    base = base,
    mod = { this % it },
    div = { this / it },
    contains = { it in this }
)

private inline fun <T : Number> numberToString(
    value: T,
    size: Int,
    base: Int,
    mod: T.(Int) -> T,
    div: T.(Int) -> T,
    contains: IntRange.(T) -> Boolean
): String {
    checkRadix(base)

    var unsignedValue = value

    if (base == 10) return unsignedValue.toString()
    if ((0 until base).contains(value)) return value.getChar().toString()

    val buffer = WasmCharArray(size)

    val radix = base
    var currentBufferIndex = size

    while (unsignedValue != 0) {
        buffer.set(currentBufferIndex, (unsignedValue.mod(radix)).getChar())
        unsignedValue = unsignedValue.div(radix)
        currentBufferIndex--
    }

    return buffer.createStringStartingFrom(currentBufferIndex + 1)
}