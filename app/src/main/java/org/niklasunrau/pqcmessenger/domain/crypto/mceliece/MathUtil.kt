package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import android.util.Log
import cc.redberry.rings.poly.FiniteField
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.rand
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray

fun <T> multiplyBinaryMatrices(
    matrix1: Array<Array<T>>,
    matrix2: Array<Array<T>>
): Array<LongArray> {
    val row1 = matrix1.size
    val col1 = matrix1[0].size
    val col2 = matrix2[0].size
    val product = Array(row1) { LongArray(col2) {0L} }

    for (i in 0 until row1) {
        for (j in 0 until col2) {
            for (k in 0 until col1) {
                product[i][j] = (product[i][j] + matrix1[i][k] * matrix2[k][j]) % 2
            }
        }
    }
    return product
}

fun multiplyFieldMatrices(
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
                    product[i][j], finiteField.multiply(matrix1[i][k], matrix2[k][j])
                )
            }
        }
    }

    return product
}


fun lJustZerosList(list: List<Long>, length: Int): List<Long> {
    var returnList = list
    if (list.size < length) {
        returnList = returnList + List(length - list.size) { 0L }
    }
    return returnList
}

fun swapColumns(matrix: Array<LongArray>, a: Int, b: Int) {
    for (i in matrix.indices) {
        // Swap two numbers
        val temp = matrix[i][a]
        matrix[i][a] = matrix[i][b]
        matrix[i][b] = temp
    }
}

fun nullspace(matrix: Array<LongArray>): Array<LongArray> {
    val m = matrix.size
    val n = matrix[0].size

    for (row in matrix.indices) {
        if (matrix[row][row] != 1L) {
            var col = row + 1
            while (matrix[row][col] != 1L) col++

            swapColumns(matrix, row, col)

        }
    }

    val rhs = mk.ndarray(matrix)[0..<m, m..<n]
    val nsp = rhs.append(mk.identity(n - m), 0).transpose()
    return nsp.toArray()
}

fun generatePermMatrix(n: Int): Array<LongArray> {
    val initialMatrix = Array(n) { LongArray(n) { 0 } }
    val positions = (0..<n).shuffled()
    for ((row, pos) in positions.withIndex()) {
        initialMatrix[row][pos] = 1
    }
    return initialMatrix
}

fun generateShuffleMatrix(n: Int): Array<LongArray> {
    while (true) {
        val candidate = mk.rand<Long, D2>(0, 2, IntArray(2) { n }).toArray()
        if (detMod2(candidate) == 1){
            return candidate
        }

    }
}
fun Array<LongArray>.deepCopy() = Array(size) { get(it).clone() }

private fun detMod2(array: Array<LongArray>): Int {
    val n = array.size
    val matrix = array.deepCopy()
    for (i in matrix.indices) {
        var j = i
        while (j < n && matrix[j][i] == 0L)
            j += 1

        if (j == n)
            return 0

        if (i < j) {
            val temp = matrix[i]
            matrix[i] = matrix[j]
            matrix[j] = temp
        }

        for (k in (i + 1)..<n) {
            if (matrix[k][i] == 1L) {
                matrix[i].zip(matrix[k]).forEachIndexed() { index, pair ->
                    matrix[k][index] = (pair.first + pair.second) % 2
                }
            }
        }
    }
    return 1
}

fun inverseModPoly(poly: UnivariatePolynomialZp64, mod: UnivariatePolynomialZp64): UnivariatePolynomialZp64 {

}

fun splitPoly(poly: UnivariatePolynomialZp64): Pair<UnivariatePolynomialZp64, UnivariatePolynomialZp64> {

}

fun sqrtModPoly(poly: UnivariatePolynomialZp64, mod: UnivariatePolynomialZp64){

}

fun norm(aPoly: UnivariatePolynomialZp64, bPoly: UnivariatePolynomialZp64): Int{

}

fun latticeBasisReuction(poly: UnivariatePolynomialZp64, mod: UnivariatePolynomialZp64)