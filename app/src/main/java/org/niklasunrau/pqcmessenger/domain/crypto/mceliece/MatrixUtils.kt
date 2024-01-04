package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

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

fun multiplyBinaryMatrices(
    vector: LongArray, matrix: Array<LongArray>
): LongArray {
    return multiplyBinaryMatrices(arrayOf(vector), matrix)[0]
}

fun multiplyBinaryMatrices(
    matrix1: Array<LongArray>, matrix2: Array<LongArray>
): Array<LongArray> {
    val row1 = matrix1.size
    val col1 = matrix1[0].size
    val col2 = matrix2[0].size
    val product = Array(row1) { LongArray(col2) { 0L } }

    for (i in 0 until row1) {
        for (j in 0 until col2) {
            for (k in 0 until col1) {
                product[i][j] = Math.floorMod(product[i][j] + matrix1[i][k] * matrix2[k][j], 2).toLong()
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
        if (detMod2(candidate) == 1) {
            return candidate
        }

    }
}

private fun detMod2(array: Array<LongArray>): Int {
    val n = array.size
    val matrix = Array(n) { array[it].clone() }
    for (i in matrix.indices) {
        var j = i
        while (j < n && matrix[j][i] == 0L) j += 1

        if (j == n) return 0

        if (i < j) {
            val temp = matrix[i]
            matrix[i] = matrix[j]
            matrix[j] = temp
        }

        for (k in (i + 1)..<n) {
            if (matrix[k][i] == 1L) {
                matrix[i].zip(matrix[k]).forEachIndexed { index, pair ->
                    matrix[k][index] = (pair.first + pair.second) % 2
                }
            }
        }
    }
    return 1
}

fun inverse(matrix: Array<LongArray>): Array<LongArray>{
    val aiMatrix = mk.ndarray(matrix).append(mk.identity(matrix.size), 1)
    val (rrefMatrix, _) = aiMatrix.toArray().reducedRowEchelonForm()
    return mk.ndarray(rrefMatrix)[matrix.indices, matrix.size..<rrefMatrix[0].size].toArray()
}

fun nonZero(array: LongArray): MutableList<Int>{
    val list = mutableListOf<Int>()
    for((index, elem) in array.withIndex()){
        if(elem != 0L) list.add(index)
    }
    return list
}