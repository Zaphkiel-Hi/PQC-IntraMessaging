package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.IntegersZp64
import cc.redberry.rings.Rings.GF
import cc.redberry.rings.linear.LinearSolver
import cc.redberry.rings.poly.FiniteField
import cc.redberry.rings.poly.univar.IrreduciblePolynomials.randomIrreduciblePolynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64
import org.apache.commons.math3.random.MersenneTwister
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import kotlin.streams.toList

data class GoppaCode(
    val gMatrix: Array<LongArray>,
    val ff2m: FiniteField<UnivariatePolynomialZp64>,
    val support: List<UnivariatePolynomialZp64>,
    val gPoly: UnivariatePolynomial<UnivariatePolynomialZp64>,
)

fun generateCode(n: Int, m: Int, t: Int): GoppaCode {
    val ff2m = GF(2, m)
    val support = ff2m.iterator().asSequence().toList()
    val gPoly = randomIrreduciblePolynomial(ff2m, t, MersenneTwister())
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
    val rhsArray = LongArray(hBinMatrix.shape[0]) { 0 }

    LinearSolver.rowEchelonForm(
        IntegersZp64(2), lhsArray, rhsArray, true, false
    )
    return GoppaCode(nullspace(lhsArray), ff2m, support, gPoly)
}

private fun pattersonAlgorithm(unshuffledCipher: LongArray, goppaCode: GoppaCode): LongArray {
    val inversePolys = listOf<UnivariatePolynomialZp64>()


}

fun decode(unshuffledCipher: LongArray, goppaCode: GoppaCode): Array<LongArray> {
    val k = goppaCode.gMatrix.size
    val fixedMessage = pattersonAlgorithm(unshuffledCipher, )

    val
    val systemToSolve =
}

