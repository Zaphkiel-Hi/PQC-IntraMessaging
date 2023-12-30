package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import android.util.Log
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


class GoppaCode(
    private var n: Int, private val m: Int, private val t: Int
) {
    val gMatrix: Array<LongArray>
    val ff2m: FiniteField<UnivariatePolynomialZp64> = GF(2, m)
    val support: List<UnivariatePolynomialZp64> = ff2m.iterator().asSequence().toList()
    val gPoly: UnivariatePolynomial<UnivariatePolynomialZp64> =
        randomIrreduciblePolynomial(ff2m, t, MersenneTwister())

    init {
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

        fun multiplyMatrices(
            finiteField: FiniteField<UnivariatePolynomialZp64>,
            matrix1: Array<Array<UnivariatePolynomialZp64>>,
            matrix2: Array<Array<UnivariatePolynomialZp64>>
        ): Array<Array<UnivariatePolynomialZp64>> {
            val row1 = matrix1.size
            val col1 = matrix1[0].size
            val col2 = matrix2[0].size
            val product = Array(row1) { Array(col2) { finiteField.zero } }

            for (i in 0 until row1) {
                for (j in 0 until col2) {
                    for (k in 0 until col1) {
                        product[i][j] = finiteField.add(
                            product[i][j],
                            finiteField.multiply(matrix1[i][k], matrix2[k][j])
                        )
                    }
                }
            }

            return product
        }

        val xyMatrix = multiplyMatrices(ff2m, xMatrix, yMatrix)
        val hMatrix = multiplyMatrices(ff2m, xyMatrix, zMatrix)

        fun lJustZerosList(list: List<Long>, length: Int): List<Long> {
            var returnList = list
            if (list.size < length) {
                returnList = returnList + List(length - list.size) { 0L }
            }
            return returnList
        }

        var hBinMatrix = mk.zeros<Long>(1, 1)
        for (row in 0 until t) {
            var rowMatrix = mk.zeros<Long>(1, 1)
            for (col in 0 until n) {
                val coeffs =
                    mk.ndarray(listOf(lJustZerosList(hMatrix[row][col].stream().toList(), m)))
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
        Log.d("MCELIECE", "TEST")

        val m = hBinMatrix.shape[0]
        val lhsArray = hBinMatrix.toArray()
        val rhsArray = LongArray(m) { 0 }

        LinearSolver.rowEchelonForm(
            IntegersZp64(2),
            lhsArray,
            rhsArray,
            true,
            false
        )
        gMatrix = nullspace(lhsArray)
    }
}