package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.poly.FiniteField
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import kotlin.random.Random

fun multiplyBinaryMatrices(
    vector: ByteArray, matrix: Array<ByteArray>
): ByteArray {
    return multiplyBinaryMatrices(arrayOf(vector), matrix)[0]
}

fun multiplyBinaryMatrices(
    matrix1: Array<ByteArray>, matrix2: Array<ByteArray>
): Array<ByteArray> {
    val row1 = matrix1.size
    val col1 = matrix1[0].size
    val col2 = matrix2[0].size
    val product = Array(row1) { ByteArray(col2) { 0 } }

    for (i in 0 until row1) {
        for (j in 0 until col2) {
            for (k in 0 until col1) {
                product[i][j] = Math.floorMod(product[i][j] + matrix1[i][k] * matrix2[k][j], 2).toByte()
            }
        }
    }
    return product
}

fun multiplyFieldMatrices(
    finiteField: FiniteField<Element>,
    matrix1: Array<Array<Element>>,
    matrix2: Array<Array<Element>>
): Array<Array<Element>> {
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

fun lJustZerosList(list: List<Byte>, length: Int): List<Byte> {
    var returnList = list
    if (list.size < length) {
        returnList = returnList + List(length - list.size) { 0 }
    }
    return returnList
}

fun generatePermMatrix(n: Int): Array<ByteArray> {
    val initialMatrix = Array(n) { ByteArray(n) { 0 } }
    val positions = (0..<n).shuffled()
    for ((row, pos) in positions.withIndex()) {
        initialMatrix[row][pos] = 1
    }
    return initialMatrix
}

fun generateShuffleMatrix(n: Int): Array<ByteArray> {
    while (true) {
        val candidate = Array(n) { ByteArray(n) { Random.nextBits(1).toByte() } }
        if (detMod2(candidate) == 1) {
            return candidate
        }

    }
}

private fun detMod2(array: Array<ByteArray>): Int {
    val n = array.size
    val matrix = Array(n) { array[it].clone() }
    val zeroByte = 0.toByte()
    val oneByte = 1.toByte()
    for (i in matrix.indices) {
        var j = i
        while (j < n && matrix[j][i] == zeroByte) j += 1

        if (j == n) return 0

        if (i < j) {
            val temp = matrix[i]
            matrix[i] = matrix[j]
            matrix[j] = temp
        }

        for (k in (i + 1)..<n) {

            if (matrix[k][i] == oneByte) {
                matrix[i].zip(matrix[k]).forEachIndexed { index, pair ->
                    matrix[k][index] = ((pair.first + pair.second) % 2).toByte()
                }
            }
        }
    }
    return 1
}

fun inverse(matrix: Array<ByteArray>): Array<ByteArray> {
    val aiMatrix = mk.ndarray(matrix).append(mk.identity(matrix.size), 1)
    val (rrefMatrix, _) = aiMatrix.reducedRowEchelonForm()
    return rrefMatrix[matrix.indices, matrix.size..<rrefMatrix[0].size].toArray()
}

fun nonZero(array: MultiArray<Byte, D1>): MutableList<Int> {
    val list = mutableListOf<Int>()
    val zeroByte = 0.toByte()
    for (index in array.indices) {
        if (array[index] != zeroByte) list.add(index)
    }
    return list
}