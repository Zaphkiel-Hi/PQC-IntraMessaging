package org.niklasunrau.pqcmessenger.domain.crypto

import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


fun  String.toBitArray(): ByteArray {
    return this.map { it.toString().toByte() }.toByteArray()
}
fun  String.toBitArrayLong(): LongArray {
    return this.map { it.toString().toLong() }.toLongArray()
}

fun SecretKey.toBitArray(): ByteArray {

    var longArray = byteArrayOf()
    for (byte in encoded) {
        longArray += byte.toUByte().toString(2).padStart(8, '0').toBitArray()
    }
    return longArray
}

fun ByteArray.toSecretKey(): SecretKey {
    var byteArray = byteArrayOf()
    for (byteStart in indices step 8)
        byteArray += this.slice(byteStart until (byteStart + 8)).joinToString("").toUByte(2).toByte()

    return SecretKeySpec(byteArray, "AES")
}