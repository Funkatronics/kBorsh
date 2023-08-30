/*
 * Base64
 * kBorsh
 * 
 * Created by Funkatronics on 11/15/2022
 */

package com.funkatronics.base64

internal interface Base64Encoder {
    fun decode(src: ByteArray): ByteArray
    fun encode(src: ByteArray): ByteArray

    fun decode(src: String): ByteArray = decode(src.encodeToByteArray())
    fun encodeToString(src: ByteArray): String {
        val encoded = encode(src)
        return buildString(encoded.size) {
            encoded.forEach { append(it.toInt().toChar()) }
        }
    }
}

internal expect object Base64Factory {
    fun createEncoder(): Base64Encoder
}