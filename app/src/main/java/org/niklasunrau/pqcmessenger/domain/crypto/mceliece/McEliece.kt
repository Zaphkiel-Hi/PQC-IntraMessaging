package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import com.google.common.math.IntMath.pow
import org.jetbrains.kotlinx.multik.api.linalg.inv
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toListD2

data class McElieceSecretkey(
    val shuffleInvMatrix: Array<DoubleArray>,
    val permInvMatrix: Array<DoubleArray>,
    val goppaCode: GoppaCode
)

data class McEliecePublicKey(
    val publicMatrix: Array<LongArray>,
)

class McEliece(val m: Int, val t: Int) {
    val n = pow(2, m)
    val k = n - t * m

    fun generateKeyPair(): Pair<McElieceSecretkey, McEliecePublicKey> {
        val goppaCode = generateCode(n, m, t)
        val shuffleMatrix = generateShuffleMatrix(k)
        val permMatrix = generatePermMatrix(n)

        val sgMatrix = multiplyBinaryMatrices(shuffleMatrix, goppaCode.gMatrix)
        val publicMatrix = multiplyBinaryMatrices(sgMatrix, permMatrix)

        val shuffleInvMatrix = mk.linalg.inv(mk.ndarray(shuffleMatrix)).toArray()
        val permInvMatrix = mk.linalg.inv(mk.ndarray(permMatrix)).toArray()


        return Pair(
            McElieceSecretkey(shuffleInvMatrix, permInvMatrix, goppaCode),
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

    fun decrypt(cipher: LongArray, secretKey: McElieceSecretkey): LongArray {
        val cipherAsMatrix = Array(1) { cipher }
        val unshuffledCipher = multiplyBinaryMatrices(cipherAsMatrix, secretKey.shuffleInvMatrix)[0]
        val decodedCipher = decode(unshuffledCipher, secretKey.goppaCode)
        val message = multiplyBinaryMatrices(decodedCipher, secretKey.permInvMatrix)[0]

        return message
    }

}