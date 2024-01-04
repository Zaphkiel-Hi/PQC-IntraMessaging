package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import com.google.common.math.IntMath.pow
import org.jetbrains.kotlinx.multik.api.linalg.inv
import org.jetbrains.kotlinx.multik.api.mk

data class McElieceSecretKey(
    val shuffleInvMatrix: Array<LongArray>, val permInvMatrix: Array<LongArray>, val goppaCode: GoppaCode
)

data class McEliecePublicKey(
    var publicMatrix: Array<LongArray>,
)

class McEliece(val m: Int, val t: Int) {
    val n = pow(2, m)
    val k = n - t * m

    fun generateKeyPair(): Pair<McElieceSecretKey, McEliecePublicKey> {
        val goppaCode = generateCode(n, m, t)
        val shuffleMatrix = generateShuffleMatrix(k)
        val permMatrix = generatePermMatrix(n)


        val sgMatrix = multiplyBinaryMatrices(shuffleMatrix, goppaCode.gMatrix)
        val publicMatrix = multiplyBinaryMatrices(sgMatrix, permMatrix)

        val shuffleInvMatrix = mk.linalg.inv(shuffleMatrix.toDoubleNDArray()).toGF2Array()
        val permInvMatrix = mk.linalg.inv(permMatrix.toDoubleNDArray()).toGF2Array()


        return Pair(
            McElieceSecretKey(shuffleInvMatrix, permInvMatrix, goppaCode), McEliecePublicKey(publicMatrix)
        )
    }

    fun encrypt(message: LongArray, publicKey: McEliecePublicKey): LongArray {
        val codeword = multiplyBinaryMatrices(message, publicKey.publicMatrix)

        val errorLocations = (0..<n).shuffled().slice(0..<t)

        for (loc in errorLocations) {
            codeword[loc] = (codeword[loc] + 1) % 2
        }

        return codeword
    }

    fun decrypt(cipher: LongArray, secretKey: McElieceSecretKey): LongArray {

        val noPermCipher = multiplyBinaryMatrices(cipher, secretKey.permInvMatrix)
        val decodedCipher = decode(noPermCipher, secretKey.goppaCode)
        return multiplyBinaryMatrices(decodedCipher, secretKey.shuffleInvMatrix)
    }

}