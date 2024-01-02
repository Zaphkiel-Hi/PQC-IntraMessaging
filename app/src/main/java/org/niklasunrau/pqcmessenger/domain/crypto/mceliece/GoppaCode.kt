package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import android.util.Log
import cc.redberry.rings.IntegersZp64
import cc.redberry.rings.Rings.GF
import cc.redberry.rings.linear.LinearSolver
import cc.redberry.rings.poly.FiniteField
import cc.redberry.rings.poly.univar.IrreduciblePolynomials
import cc.redberry.rings.poly.univar.UnivariateFactorization.FactorInGF
import org.apache.commons.math3.random.MersenneTwister
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import kotlin.streams.toList
import cc.redberry.rings.poly.univar.UnivariatePolynomial as Polynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64 as FieldElement

data class GoppaCode(
    val gMatrix: Array<LongArray>,
    val ff2m: FiniteField<FieldElement>,
    val support: List<FieldElement>,
    val gPoly: Polynomial<FieldElement>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GoppaCode

        if (!gMatrix.contentDeepEquals(other.gMatrix)) return false
        if (ff2m != other.ff2m) return false
        if (support != other.support) return false
        return gPoly == other.gPoly
    }

    override fun hashCode(): Int {
        var result = gMatrix.contentDeepHashCode()
        result = 31 * result + ff2m.hashCode()
        result = 31 * result + support.hashCode()
        result = 31 * result + gPoly.hashCode()
        return result
    }
}

fun generateCode(n: Int, m: Int, t: Int): GoppaCode {
    val ff2m = GF(2, m)
    val support = ff2m.iterator().asSequence().toList()
    val gPoly = IrreduciblePolynomials.randomIrreduciblePolynomial(ff2m, t, MersenneTwister())

    val xMatrix = Array(t) { Array(t) { ff2m.zero } }
    for (row in 0 until t) {
        for (col in 0 until t) {
            if (row - col in 0 until t) {
                xMatrix[row][col] = gPoly[t - (row - col)]
            }
        }
    }

    val yMatrix = Array(t) { Array(n) { ff2m.zero } }
    for (row in 0 until t) {
        for (col in 0 until n) {
            yMatrix[row][col] = ff2m.pow(support[col], row)
        }
    }

    val zMatrix = Array(n) { Array(n) { ff2m.zero } }
    for (row in 0 until n) {
        for (col in 0 until n) {
            if (row == col) {
                zMatrix[row][col] = ff2m.pow(gPoly.evaluate(support[row]), -1)
            }
        }
    }


    val xyMatrix = multiplyFieldMatrices(ff2m, xMatrix, yMatrix)
    val hMatrix = multiplyFieldMatrices(ff2m, xyMatrix, zMatrix)


    var hBinMatrix = mk.zeros<Long>(1, 1)
    for (row in 0 until t) {
        var rowMatrix = mk.zeros<Long>(1, 1)
        for (col in 0 until n) {
            val coeffs = mk.ndarray(listOf(lJustZerosList(hMatrix[row][col].stream().toList(), m)))
                .transpose()
            rowMatrix = if (col == 0) {
                coeffs
            } else {
                rowMatrix.append(coeffs, 1)
            }
        }
        hBinMatrix = if (row == 0) {
            rowMatrix
        } else {
            hBinMatrix.append(rowMatrix, 0)
        }
    }

    val lhsArray = hBinMatrix.toArray()
    val rhsArray = LongArray(hBinMatrix.shape[0]) { 0L }

    LinearSolver.rowEchelonForm(
        IntegersZp64(2), lhsArray, rhsArray, true, false
    )
    return GoppaCode(nullspace(lhsArray), ff2m, support, gPoly)
}

private fun pattersonAlgorithm(cipher: LongArray, goppaCode: GoppaCode): LongArray {
    val inversePolys = mutableListOf<Polynomial<FieldElement>>()
    val ff2m = goppaCode.ff2m
    val gPoly = goppaCode.gPoly


    for (i in cipher.indices) {
        if (cipher[i] == 1L) {
            inversePolys.add(
                inverseModPoly(
                    Polynomial.create(
                        ff2m,
                        goppaCode.support[i].negate(),
                        ff2m.one
                    ), gPoly
                )
            )
        }
    }

    var syndrome = Polynomial.zero(ff2m)
    for (poly in inversePolys) {
        syndrome = syndrome.add(poly)
    }

    val syndromeInverse = inverseModPoly(syndrome, gPoly)
    val s = sqrtModPoly(syndromeInverse.add(identity(ff2m)), gPoly)

    val (alpha, beta) = latticeBasisReduction(s, gPoly)

    val sigma = alpha.multiply(alpha).add(identity(ff2m).multiply(beta.multiply(beta)))
    val errorLocations = FactorInGF(sigma)
    //TODO(find factors)

    Log.d("McEliece", sigma.isOverFiniteField.toString())
    Log.d("McEliece", sigma.ring.toString())


//    for (loc in errorLocations) {
//        unshuffledCipher[loc] = (unshuffledCipher[loc] + 1) % 2
//    }

    return cipher


}

fun decode(cipher: LongArray, goppaCode: GoppaCode): Array<LongArray> {
    val k = goppaCode.gMatrix.size
    val fixedMessage = pattersonAlgorithm(cipher, goppaCode)
    //TODO(finish this)
    return Array(1) { fixedMessage }
}

