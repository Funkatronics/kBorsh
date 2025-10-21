/*
 * BorshSerializationTests
 * kBorsh
 *
 * Created by Funkatronics on 7/25/2022
 */

package com.funkatronics.kborsh

import com.funkatronics.buffer.ByteBuffer
import com.funkatronics.buffer.ByteOrder
import com.funkatronics.buffer.allocate
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals

class BorshTests {

    //region ENCODING
    @Test
    fun testBooleanEncodesAsByte() {
        // given
        val expectedBytesTrue = byteArrayOf(1)
        val expectedBytesFalse = byteArrayOf(0)

        // when
        val encodedBytesTrue = Borsh.encodeToByteArray(true)
        val encodedBytesFalse = Borsh.encodeToByteArray(false)

        //then
        assertContentEquals(expectedBytesTrue, encodedBytesTrue)
        assertContentEquals(expectedBytesFalse, encodedBytesFalse)
    }

    @Test
    fun testIntegerEncodesAsLittleEndianBytes() {
        // given
        val value = UInt.MAX_VALUE.toInt()
        val expectedBytes = ByteBuffer.allocate(Int.SIZE_BYTES).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(value)
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(value)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testStringEncodesAsByteArray() {
        // given
        val valueString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

        val valueBytes = valueString.encodeToByteArray()
        val expectedBytes = ByteBuffer.allocate(Int.SIZE_BYTES + valueBytes.size).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(valueBytes.size)
            put(valueBytes)
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(valueString)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testDoubleEncodesAsLittleEndianBytes() {
        // given
        val value = Double.MAX_VALUE
        val expectedBytes = ByteBuffer.allocate(Double.SIZE_BYTES).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putDouble(value)
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(value)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testBorshEncodeFloatNaNThrowsError() {
        // given
        val testDouble = Float.NaN

        // when
        val actualError = runCatching {
            Borsh.encodeToByteArray(testDouble)
        }.exceptionOrNull()

        // then
        assertTrue(actualError is SerializationException)
    }

    @Test
    fun testBorshEncodeDoubleNaNThrowsError() {
        // given
        val testDouble = Double.NaN

        // when
        val actualError = runCatching {
            Borsh.encodeToByteArray(testDouble)
        }.exceptionOrNull()

        // then
        assertTrue(actualError is SerializationException)
    }

    @Test
    fun testListEncodesAsListOfValues() {
        // given
        val list = listOf(1, 3, 2, 4)
        val sizeBytes = Int.SIZE_BYTES + Int.SIZE_BYTES*list.size
        val expectedBytes = ByteBuffer.allocate(sizeBytes).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(list.size)
            list.forEach { putInt(it) }
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(list)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testMapEncodesAsSortedListOfKV() {
        // given
        val map = mapOf(1 to 1, 3 to 3, 2 to 2, 4 to 4)
        val sizeBytes = Int.SIZE_BYTES + Int.SIZE_BYTES*2*map.size
        val expectedBytes = ByteBuffer.allocate(sizeBytes).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(map.size)
            map.keys.sorted().forEach { key -> putInt(key); putInt(map[key]!!) }
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(map)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testHashSetEncodesAsSortedListOfValues() {
        // given
        val values = hashSetOf(1, 3, 2, 4)
        val sizeBytes = Int.SIZE_BYTES + Int.SIZE_BYTES*values.size
        val expectedBytes = ByteBuffer.allocate(sizeBytes).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(values.size)
            values.sorted().forEach { putInt(it) }
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(values)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testHashMapEncodesAsSortedListOfKV() {
        // given
        val valueMap = hashMapOf(1 to 1, 3 to 3, 2 to 2, 4 to 5)
        val sizeBytes = Int.SIZE_BYTES + Int.SIZE_BYTES*2*valueMap.size
        val expectedBytes = ByteBuffer.allocate(sizeBytes).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(valueMap.size)
            valueMap.keys.sorted().forEach { key ->
                putInt(key); putInt(valueMap[key]!!)
            }
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(valueMap)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testEnumEncodesAsOrdinalInteger() {
        // given
        val value = BorshTestEnum.ENUM2
        val expectedBytes = byteArrayOf(value.ordinal.toByte())

        // when
        val encodedBytes = Borsh.encodeToByteArray(value)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testNonNullOptionalEncodesWithNullMarkByte() {
        // given
        val value: Int? = 1234
        val expectedBytes = ByteBuffer.allocate(Int.SIZE_BYTES + 1).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put(1)
            putInt(value!!)
        }.array()

        // when
        val encodedBytes = Borsh.encodeToByteArray(value)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun testNullOptionalEncodesOnlyNullMarkByte() {
        // given
        val value: Int? = null
        val expectedBytes = byteArrayOf(0)

        // when
        val encodedBytes = Borsh.encodeToByteArray(value)

        //then
        assertContentEquals(expectedBytes, encodedBytes)
    }
    //endregion

    //region DECODING
    @Test
    fun testDecodeStringEncodedAsByteArray() {
        // given
        val expectedString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

        val utf8Bytes = expectedString.encodeToByteArray()
        val encodedBytes = ByteBuffer.allocate(Int.SIZE_BYTES + utf8Bytes.size).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(utf8Bytes.size)
            put(utf8Bytes)
        }.array()

        // when
        val decodedString = Borsh.decodeFromByteArray<String>(encodedBytes)

        //then
        assertEquals(expectedString, decodedString)
    }

    @Test
    fun testBorshDecodeObject() {
        // given
        val testClass = BorshTestClass(
            200u, 200u, 200u, 200u,
            -100, -100, -100, -100,
            123.4f, 123.4,
            "borsh test string", BorshTestEnum.ENUM2,
            "optional string", 1234,
            listOf(1, 2, 3, 4), mapOf("map1" to 1, "map2" to 2, "map3" to 3),
            hashMapOf("hash1" to 1, "hash2" to 2, "hash3" to 3), hashSetOf(1, 2, 3),
            BorshTestSubclass("subclass", 1234)
        )

        // when
        val encodedBorsh = Borsh.encodeToByteArray(testClass)
        val decodedObject = Borsh.decodeFromByteArray<BorshTestClass>(encodedBorsh)

        // then
        assertBorshEquals(testClass, decodedObject)
    }

    @Test
    fun testBorshDecodeObjectWithOptionals() {
        // given
        val testClass = BorshTestClass(
            200u, 200u, 200u, 200u,
            -100, -100, -100, -100,
            123.4f, 123.4,
            "borsh test string", BorshTestEnum.ENUM2,
            null, null,
            listOf(1, 2, 3, 4), mapOf("map1" to 1, "map2" to 2, "map3" to 3),
            hashMapOf("hash1" to 1, "hash2" to 2, "hash3" to 3), hashSetOf(1, 2, 3),
            BorshTestSubclass("subclass", 1234)
        )

        // when
        val encodedBorsh = Borsh.encodeToByteArray(testClass)
        val decodedObject = Borsh.decodeFromByteArray<BorshTestClass>(encodedBorsh)

        // then
        assertBorshEquals(testClass, decodedObject)
    }
    //endregion

    private fun assertBorshEquals(expected: BorshTestClass, actual: BorshTestClass) {
        assertEquals(expected.u8, actual.u8)
        assertEquals(expected.u16, actual.u16)
        assertEquals(expected.u32, actual.u32)
        assertEquals(expected.u64, actual.u64)

        assertEquals(expected.i8, actual.i8)
        assertEquals(expected.i16, actual.i16)
        assertEquals(expected.i32, actual.i32)
        assertEquals(expected.i64, actual.i64)

        // Floats don’t stringify the same on JS vs JVM (e.g. 123.4 vs 123.4000015),
        // so we compare fields individually with a tolerance instead of relying on
        // the data class equals() which would fail assertion here on JS.
        assertEquals(expected.f32, actual.f32, 0.0001f)
        assertEquals(expected.f64, actual.f64, 1e-9)

        assertEquals(expected.string, actual.string)
        assertEquals(expected.enum, actual.enum)

        assertEquals(expected.optionalString, actual.optionalString)
        assertEquals(expected.optionalNum, actual.optionalNum)

        assertEquals(expected.list, actual.list)
        assertEquals(expected.map, actual.map)
        assertEquals(expected.hashMap, actual.hashMap)
        assertEquals(expected.hashSet, actual.hashSet)

        assertEquals(expected.struct, actual.struct)
        assertEquals(expected.struct.name, actual.struct.name)
        assertEquals(expected.struct.id, actual.struct.id)
    }
}