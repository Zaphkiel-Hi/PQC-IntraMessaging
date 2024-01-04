package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import android.util.Log
import com.google.common.math.IntMath.pow

data class McElieceSecretKey(
    val shuffleInvMatrix: Array<LongArray>, val permInvMatrix: Array<LongArray>, val goppaCode: GoppaCode
)

data class McEliecePublicKey(
    var publicMatrix: Array<LongArray>,
)

fun logging(str: String) {
    Log.d("MCEL", str)
}

fun logging(matrix: Array<LongArray>) {
    logging(matrix.toPrettyString())
}

fun logging(any: Any?) {
    logging(any.toString())
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

        val shuffleInvMatrix = inverse(shuffleMatrix)
        val permInvMatrix = inverse(permMatrix)

        return Pair(
            McElieceSecretKey(shuffleInvMatrix, permInvMatrix, goppaCode),
            McEliecePublicKey(publicMatrix)
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