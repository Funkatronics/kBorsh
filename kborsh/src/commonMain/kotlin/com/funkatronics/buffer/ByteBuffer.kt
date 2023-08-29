/*
 * ByteBuffer
 * kBorsh
 *
 * Created by Funkatronics on 11/15/2022
 */

package com.funkatronics.buffer

interface ByteBuffer {
    val capacity: Int
    val order: ByteOrder

    fun order(order: ByteOrder): ByteBuffer
    fun put(byte: Byte): ByteBuffer
    fun get(): Byte

    fun put(bytes: ByteArray): ByteBuffer = this.also {
        bytes.forEach { byte -> put(byte) }
    }

    fun get(dst: ByteArray) {
        dst.indices.forEach {
            dst[it] = get()
        }
    }

    fun getShort(): Short = readIntoLong(Short.SIZE_BYTES, order).toShort()
    fun getInt(): Int = readIntoLong(Int.SIZE_BYTES, order).toInt()
    fun getLong(): Long = readIntoLong(Long.SIZE_BYTES, order)
    fun getFloat(): Float = Float.fromBits(readIntoLong(Int.SIZE_BYTES, order).toInt())
    fun getDouble(): Double = Double.fromBits(readIntoLong(Long.SIZE_BYTES, order))

    fun putShort(value: Short): ByteBuffer = put(value.toByteArray(Short.SIZE_BYTES))
    fun putInt(value: Int): ByteBuffer = put(value.toByteArray(Int.SIZE_BYTES))
    fun putLong(value: Long): ByteBuffer = put(value.toByteArray(Long.SIZE_BYTES))
    fun putFloat(value: Float): ByteBuffer = put(value.toByteArray(Float.SIZE_BYTES))
    fun putDouble(value: Double): ByteBuffer = put(value.toByteArray(Double.SIZE_BYTES))

    fun array(): ByteArray

    private fun readIntoLong(count: Int, order: ByteOrder): Long =
        ByteArray(count).run { get(this); toLong(order)}

    private infix fun Long.sho(bits: Int): Long =
        if (order == ByteOrder.LITTLE_ENDIAN) this shl bits else this shr bits

    private fun ByteArray.toLong(order: ByteOrder): Long {
        if (order == ByteOrder.BIG_ENDIAN) this.reverse()
        var result = 0L
        indices.forEach { b -> result = result or (this[b].toLong() shl 8 * b) }
        return result
    }

    private fun Number.toByteArray(sizeBytes: Int): ByteArray =
        ByteArray(sizeBytes).apply {
            indices.forEach { b ->
                this[b] = ((this@toByteArray.toLong() sho 8 * b) and 0xFF).toByte()
            }
        }

    companion object
}

expect fun ByteBuffer.Companion.allocate(capacity: Int): ByteBuffer
expect fun ByteBuffer.Companion.wrap(array: ByteArray): ByteBuffer