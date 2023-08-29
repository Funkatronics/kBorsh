/*
 * Multiplatform Base64
 * kBorsh
 *
 * Created by Funkatronics on 08/29/2023
 */

package com.funkatronics.base64

import com.funkatronics.encoders.Base64

actual object Base64Factory {
    actual fun createEncoder(): Base64Encoder = MultiMultBase64Encoder()
}

class MultiMultBase64Encoder : Base64Encoder {
    override fun decode(src: ByteArray): ByteArray = Base64.decode(src.decodeToString())
    override fun encode(src: ByteArray): ByteArray = Base64.encode(src)
}