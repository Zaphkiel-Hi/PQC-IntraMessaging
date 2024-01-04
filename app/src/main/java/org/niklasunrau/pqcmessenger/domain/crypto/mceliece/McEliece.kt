package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import com.google.common.math.IntMath.pow

data class McElieceSecretKey(
    val shuffleInvMatrix: Array<LongArray>, val permInvMatrix: Array<LongArray>, val goppaCode: GoppaCode
){
    constructor(): this(Array(0) { LongArray(0) }, Array(0) { LongArray(0) }, GoppaCode())

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
    var publicMatrix: Array<LongArray>,
) {
    constructor() : this(Array(0) { LongArray(0) })

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

object McEliece {
    private const val m = 8
    private const val t = 16
    private val n = pow(2, m)
    private val k = n - t * m

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