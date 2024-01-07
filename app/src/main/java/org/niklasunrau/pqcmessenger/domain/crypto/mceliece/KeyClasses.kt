package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Rings
import cc.redberry.rings.poly.univar.UnivariatePolynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricPublicKey
import org.niklasunrau.pqcmessenger.domain.crypto.AsymmetricSecretKey

@Serializable
@SerialName("McEliece")
data class McElieceSecretKey(
    @Serializable(MatrixSerializer::class) val shuffleInvMatrix: Array<LongArray>,
    @Serializable(PermMatrixSerializer::class) val permInvMatrix: Array<LongArray>,
    val goppaCode: GoppaCode
) : AsymmetricSecretKey() {
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
@SerialName("McEliece")
data class McEliecePublicKey(
    @Serializable(MatrixSerializer::class) var publicMatrix: Array<LongArray>,
) : AsymmetricPublicKey() {
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


@Serializable
data class GoppaCode(
    @Serializable(MatrixSerializer::class) val gMatrix: Array<LongArray>,
    val support: List<@Serializable(ElementSerializer::class) UnivariatePolynomialZp64>,
    val gPoly: @Serializable(PolySerializer::class) UnivariatePolynomial<@Serializable(ElementSerializer::class) UnivariatePolynomialZp64>,
) {
    constructor() : this(
        Array(0) { LongArray(0) },
        listOf<UnivariatePolynomialZp64>(),
        UnivariatePolynomial.create(Rings.GF(2, 1), UnivariatePolynomialZp64.zero(2))
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GoppaCode

        if (!gMatrix.contentDeepEquals(other.gMatrix)) return false
        if (support != other.support) return false
        return gPoly == other.gPoly
    }

    override fun hashCode(): Int {
        var result = gMatrix.contentDeepHashCode()
        result = 31 * result + support.hashCode()
        result = 31 * result + gPoly.hashCode()
        return result
    }
}
