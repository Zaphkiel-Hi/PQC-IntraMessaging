package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import com.google.common.math.IntMath.pow
import org.jetbrains.kotlinx.multik.api.linalg.inv
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray

data class McElieceSecretKey(
    val shuffleInvMatrix: Array<LongArray>,
    val permInvMatrix: Array<LongArray>,
    val goppaCode: GoppaCode
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as McElieceSecretKey

        if (!shuffleInvMatrix.contentDeepEquals(other.shuffleInvMatrix)) return false
        if (!permInvMatrix.contentDeepEquals(other.permInvMatrix)) return false
        return goppaCode == other.goppaCode
    }

    override fun hashCode(): Int {
        var result = shuffleInvMatrix.contentDeepHashCode()
        result = 31 * result + permInvMatrix.contentDeepHashCode()
        result = 31 * result + goppaCode.hashCode()
        return result
    }
}

data class McEliecePublicKey(
    val publicMatrix: Array<LongArray>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as McEliecePublicKey

        return publicMatrix.contentDeepEquals(other.publicMatrix)
    }

    override fun hashCode(): Int {
        return publicMatrix.contentDeepHashCode()
    }
}

class McEliece(val m: Int, val t: Int) {
     val n = pow(2, m)
    val k = n - t * m

    fun generateKeyPair(): Pair<McElieceSecretKey, McEliecePublicKey> {
        val goppaCode = generateCode(n, m, t)
        val shuffleMatrix = generateShuffleMatrix(k)
        val permMatrix = generatePermMatrix(n)

        val sgMatrix = multiplyBinaryMatrices(shuffleMatrix, goppaCode.gMatrix)
        val publicMatrix = multiplyBinaryMatrices(sgMatrix, permMatrix)

        val shuffleInvMatrix = mk.linalg.inv(mk.ndarray(shuffleMatrix.toDoubleArray())).toArray().toLongArray()
        val permInvMatrix = mk.linalg.inv(mk.ndarray(permMatrix.toDoubleArray())).toArray().toLongArray()


        return Pair(
            McElieceSecretKey(shuffleInvMatrix, permInvMatrix, goppaCode),
            McEliecePublicKey(publicMatrix)
        )
    }

    fun encrypt(message: LongArray, publicKey: McEliecePublicKey): LongArray {
        val messageAsMatrix = Array(1) { message }
        val codeword = multiplyBinaryMatrices(messageAsMatrix, publicKey.publicMatrix)[0]

        val errorLocations = (0..<n).shuffled().slice(0..t)
        for (loc in errorLocations) {
            codeword[loc] = (codeword[loc] + 1) % 2
        }

        return codeword
    }

    fun decrypt(cipher: LongArray, secretKey: McElieceSecretKey): LongArray {
        val cipherAsMatrix = Array(1) { cipher }
        val noPermCipher = multiplyBinaryMatrices(cipherAsMatrix, secretKey.permInvMatrix)[0]
        val decodedCipher = decode(noPermCipher, secretKey.goppaCode)
        return multiplyBinaryMatrices(decodedCipher, secretKey.permInvMatrix)[0]
    }

}