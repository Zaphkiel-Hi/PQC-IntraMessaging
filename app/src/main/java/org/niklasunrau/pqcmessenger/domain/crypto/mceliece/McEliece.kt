package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Rings.GF
import cc.redberry.rings.Rings.UnivariateRing
import cc.redberry.rings.poly.FiniteField
import cc.redberry.rings.poly.UnivariateRing
import cc.redberry.rings.poly.univar.UnivariatePolynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64
import com.google.common.math.IntMath.pow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricAlgorithm
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey
import kotlin.experimental.xor

typealias Element = UnivariatePolynomialZp64
typealias Poly = UnivariatePolynomial<Element>

object McEliece : AsymmetricAlgorithm<McElieceSecretKey, McEliecePublicKey>() {
    private const val m = 8
    private const val t = 16
    val ff2m: FiniteField<Element> = GF(2, m)
    val support = ff2m.iterator().asSequence().toList()
    val coeffRing: UnivariateRing<Poly> = UnivariateRing(ff2m)

    private val n = pow(2, m)
    val k = n - t * m

    override suspend fun generateKeyPair(): Pair<McElieceSecretKey, McEliecePublicKey> = withContext(Dispatchers.Default) {
        val goppaCode = generateCode(n, m, t)
        val shuffleMatrix = generateShuffleMatrix(k)
        val permMatrix = generatePermMatrix(n)

        val sgMatrix = multiplyBinaryMatrices(shuffleMatrix, goppaCode.gMatrix)
        val publicMatrix = multiplyBinaryMatrices(sgMatrix, permMatrix)

        val shuffleInvMatrix = inverse(shuffleMatrix)
        val permInvMatrix = inverse(permMatrix)

        return@withContext Pair(
            McElieceSecretKey(shuffleInvMatrix, permInvMatrix, goppaCode), McEliecePublicKey(publicMatrix)
        )
    }


    override fun encrypt(message: ByteArray, publicKey: AsymmetricPublicKey): ByteArray {
        publicKey as McEliecePublicKey
        val codeword = multiplyBinaryMatrices(message, publicKey.publicMatrix)
        val errorLocations = (0..<n).shuffled().slice(0..<t)
        for (loc in errorLocations) {
            codeword[loc] = (codeword[loc] xor 1)
        }
        return codeword
    }

    override fun decrypt(cipher: ByteArray, secretKey: AsymmetricSecretKey): ByteArray {
        secretKey as McElieceSecretKey
        val noPermCipher = multiplyBinaryMatrices(cipher, secretKey.permInvMatrix)
        val decodedCipher = decode(noPermCipher, secretKey.goppaCode)
        return multiplyBinaryMatrices(decodedCipher, secretKey.shuffleInvMatrix)
    }

}