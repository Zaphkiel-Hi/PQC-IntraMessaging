package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Rings.GF
import cc.redberry.rings.poly.univar.IrreduciblePolynomials
import cc.redberry.rings.poly.univar.UnivariateFactorization
import org.apache.commons.math3.random.Well19937c
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toLongArray
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece.ff2m
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece.support
import kotlin.streams.toList
import cc.redberry.rings.poly.univar.UnivariatePolynomial as Poly
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64 as Element

fun generateCode(n: Int, m: Int, t: Int): GoppaCode {
    val gPoly = IrreduciblePolynomials.randomIrreduciblePolynomial(ff2m, t, Well19937c())

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
            val coeffs =
                mk.ndarray(listOf(lJustZerosList(hMatrix[row][col].stream().toList(), m).reversed())).transpose()
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

    val gMatrix = hBinMatrix.nullspace()

    return GoppaCode(gMatrix, gPoly)
}

private fun pattersonAlgorithm(cipher: LongArray, goppaCode: GoppaCode): LongArray {
    val inversePolys = mutableListOf<Poly<Element>>()
    val ff2m = GF(2, McEliece.m)
    val gPoly = goppaCode.gPoly


    for (i in cipher.indices) {
        if (cipher[i] == 1L) {
            inversePolys.add(
                inverseModPoly(
                    Poly.create(
                        ff2m, support[i].negate(), ff2m.one
                    ), gPoly

                )
            )
        }
    }
    var syndrome = Poly.zero(ff2m)
    for (poly in inversePolys) {
        syndrome += poly
    }

    val syndromeInverse = inverseModPoly(syndrome, gPoly)
    val s = sqrtModPoly(syndromeInverse - ff2m.identity(), gPoly)

    val (alpha, beta) = latticeBasisReduction(s, gPoly)


    val sigma = (alpha * alpha) + (ff2m.identity() * (beta * beta))
    val factors = UnivariateFactorization.FactorInGF(sigma).factors

    for (factor in factors) {
        val loc = factor.cc().toInt()
        cipher[loc] = (cipher[loc] + 1) % 2
    }

    return cipher


}

fun decode(cipher: LongArray, goppaCode: GoppaCode): LongArray {
    val k = goppaCode.gMatrix.size
    val fixedMessage = pattersonAlgorithm(cipher, goppaCode)
    val systemToSolve =
        mk.ndarray(goppaCode.gMatrix).transpose().append(mk.ndarray(fixedMessage).reshape(fixedMessage.size, 1), 1)
            .toArray()
    val (solution, _) = systemToSolve.reducedRowEchelonForm(k)
    return mk.ndarray(solution)[0..<k, k].toLongArray()

}

