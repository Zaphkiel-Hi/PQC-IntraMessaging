package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Rings.GF
import cc.redberry.rings.Rings.UnivariateRing
import cc.redberry.rings.poly.FiniteField
import cc.redberry.rings.poly.UnivariateRing
import com.google.common.math.IntMath.pow
import kotlinx.serialization.Serializable
import cc.redberry.rings.poly.univar.UnivariatePolynomial as Poly
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64 as Element

@Serializable
data class McElieceSecretKey(
    @Serializable(MatrixSerializer::class) val shuffleInvMatrix: Array<LongArray>,
    @Serializable(PermMatrixSerializer::class) val permInvMatrix: Array<LongArray>,
    val goppaCode: GoppaCode
) {
    constructor() : this(Array(0) { LongArray(0) }, Array(0) { LongArray(0) }, GoppaCode())

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

@Serializable
data class McEliecePublicKey(
    @Serializable(MatrixSerializer::class) var publicMatrix: Array<LongArray>,
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
    const val m = 8
    private const val t = 16
    val ff2m: FiniteField<Element> = GF(2, m)
    val coeffRing: UnivariateRing<Poly<Element>> = UnivariateRing(ff2m)

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