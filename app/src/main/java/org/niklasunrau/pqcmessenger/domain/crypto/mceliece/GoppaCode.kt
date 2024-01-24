package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.poly.univar.IrreduciblePolynomials.randomIrreduciblePolynomial
import cc.redberry.rings.poly.univar.UnivariateFactorization
import org.apache.commons.math3.random.MersenneTwister
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece.ff2m
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece.k
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece.support
import kotlin.experimental.xor

fun generateCode(n: Int, m: Int, t: Int): GoppaCode {
    val gPoly = randomIrreduciblePolynomial(ff2m, t, MersenneTwister())

    val xMatrix = Array(t) { Array(t) { ff2m.zero } }
    val yMatrix = Array(t) { Array(n) { ff2m.zero } }
    val zMatrix = Array(n) { Array(n) { ff2m.zero } }

    for (row in 0 until n) {
        for (col in 0 until n) {
            if (row < t) {
                if (col < t) {
                    // X has dimension t x t
                    if (row - col in 0 until t) {
                        xMatrix[row][col] = gPoly[t - (row - col)]
                    }
                }
                // Y has dimension t x n
                yMatrix[row][col] = ff2m.pow(support[col], row)
            }
            // Z has dimension n x n
            if (row == col) {
                zMatrix[row][col] = ff2m.pow(gPoly.evaluate(support[row]), -1)
            }
        }
    }

    val xyMatrix = multiplyFieldMatrices(ff2m, xMatrix, yMatrix)
    val hMatrix = multiplyFieldMatrices(ff2m, xyMatrix, zMatrix)

    // convert a single row of H to binary rep.
    fun rowToBinary(row: Int): NDArray<Byte, D2> {

        // initialize with first element
        var rowMatrix = hMatrix[row][0].toBinaryColumn(m)

        // afterwards only append
        for (col in 1 until n) {
            val coeffsColumn = hMatrix[row][col].toBinaryColumn(m)
            rowMatrix = rowMatrix.append(coeffsColumn, 1)
        }
        return rowMatrix
    }

    // repeat process for evry row
    var hBinMatrix = rowToBinary(0)
    for (row in 1 until t) {
        hBinMatrix = hBinMatrix.append(rowToBinary(row), 0)
    }

    val gMatrix = hBinMatrix.nullspace()
    return GoppaCode(gMatrix, gPoly)
}

private fun pattersonAlgorithm(cipher: ByteArray, goppaCode: GoppaCode): ByteArray {
    val gPoly = goppaCode.gPoly

    val oneByte = 1.toByte()
    var syndrome = Poly.zero(ff2m)
    for (i in cipher.indices) {
        if (cipher[i] != oneByte) {
            continue
        }
        syndrome += inverseModPoly(
            Poly.create(ff2m, support[i].negate(), ff2m.one),
            gPoly
        )
    }
    val syndromeInverse = inverseModPoly(syndrome, gPoly)

    val sigma: Poly = if (syndromeInverse == ff2m.identity()) {
        ff2m.identity()
    } else {
        val s = sqrtModPoly(syndromeInverse - ff2m.identity(), gPoly)
        val (alpha, beta) = latticeBasisReduction(s, gPoly)


        (alpha * alpha) + (ff2m.identity() * (beta * beta))
    }

    val factors = UnivariateFactorization.FactorInGF(sigma).factors

    for (factor in factors) {
        val loc = factor.cc().toInt()
        cipher[loc] = (cipher[loc] xor 1)
    }

    return cipher


}

fun decode(cipher: ByteArray, goppaCode: GoppaCode): ByteArray {
    val fixedMessage = pattersonAlgorithm(cipher, goppaCode)
    val systemToSolve =
        mk.ndarray(goppaCode.gMatrix).transpose().append(mk.ndarray(fixedMessage).reshape(fixedMessage.size, 1), 1)
    val (solution, _) = systemToSolve.reducedRowEchelonForm(k)
    return ByteArray(k) { ind -> solution[0..<k, k][ind] }
}

