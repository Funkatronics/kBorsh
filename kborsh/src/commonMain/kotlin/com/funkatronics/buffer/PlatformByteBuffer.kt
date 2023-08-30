/*
 * Multiplatform ByteBuffer
 * kBorsh
 *
 * Created by Funkatronics on 08/29/2023
 */

package com.funkatronics.buffer

import com.ditchoom.buffer.AllocationZone
import com.ditchoom.buffer.PlatformBuffer
import com.ditchoom.buffer.allocate
import com.ditchoom.buffer.wrap

internal class PlatformByteBuffer(private var buffer: PlatformBuffer) : ByteBuffer {

    override val capacity: Int get() = buffer.capacity
    override val order: ByteOrder get() = when (buffer.byteOrder) {
        com.ditchoom.buffer.ByteOrder.LITTLE_ENDIAN -> ByteOrder.LITTLE_ENDIAN
        com.ditchoom.buffer.ByteOrder.BIG_ENDIAN -> ByteOrder.BIG_ENDIAN
    }

    override fun order(order: ByteOrder): ByteBuffer {
        buffer = PlatformBuffer.wrap(buffer.readByteArray(buffer.capacity), when (order) {
            ByteOrder.BIG_ENDIAN -> com.ditchoom.buffer.ByteOrder.BIG_ENDIAN
            ByteOrder.LITTLE_ENDIAN -> com.ditchoom.buffer.ByteOrder.LITTLE_ENDIAN
        })

        return this
    }

    override fun get(): Byte = buffer.readByte()
    override fun getShort(): Short = buffer.readShort()
    override fun getInt(): Int = buffer.readInt()
    override fun getLong(): Long = buffer.readLong()
    override fun getFloat(): Float = buffer.readFloat()
    override fun getDouble(): Double = buffer.readDouble()

    override fun put(byte: Byte): ByteBuffer = this.also { buffer.writeByte(byte) }
    override fun putShort(value: Short): ByteBuffer = this.also { buffer.writeShort(value) }
    override fun putInt(value: Int): ByteBuffer = this.also { buffer.writeInt(value) }
    override fun putLong(value: Long): ByteBuffer = this.also { buffer.writeLong(value) }
    override fun putFloat(value: Float): ByteBuffer = this.also { buffer.writeFloat(value) }
    override fun putDouble(value: Double): ByteBuffer = this.also { buffer.writeDouble(value) }

    override fun array(): ByteArray =
        ByteArray(buffer.capacity).apply {
            buffer.position(0)
            get(this)
        }
}

internal actual fun ByteBuffer.Companion.allocate(capacity: Int): ByteBuffer = PlatformByteBuffer(
    PlatformBuffer.allocate(capacity,
        zone = AllocationZone.Direct,
        byteOrder = com.ditchoom.buffer.ByteOrder.BIG_ENDIAN)
)

internal actual fun ByteBuffer.Companion.wrap(array: ByteArray): ByteBuffer = PlatformByteBuffer(
    PlatformBuffer.wrap(array, byteOrder = com.ditchoom.buffer.ByteOrder.BIG_ENDIAN)
)