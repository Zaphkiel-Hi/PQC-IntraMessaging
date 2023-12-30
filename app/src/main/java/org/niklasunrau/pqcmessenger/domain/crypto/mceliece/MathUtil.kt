package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import kotlin.math.abs


fun rref(matrix: Array<LongArray>, pivot: MutableList<Int>): Array<LongArray> {
    val rref = Array(matrix.size) { LongArray(0) }
    for (i in matrix.indices) rref[i] = matrix[i].copyOf(matrix[i].size)

    var row = 0
    var col = 0
    while (col < rref[0].size && row < rref.size) {
        var j = row
        for (i in row + 1 until rref.size) if (abs(rref[i][col]) > abs(rref[j][col])) j = i
        if (abs(rref[j][col]) < 0.00001) {
            col++
            continue
        }

        //Remember where we pivoted
        pivot.add(j)

        val temp = rref[j]
        rref[j] = rref[row]
        rref[row] = temp
        val s = 1L / rref[row][col]
        j = 0
        while (j < rref[0].size) {
            rref[row][j] *= s
            j++
        }
        for (i in rref.indices) {
            if (i != row) {
                val t = rref[i][col]
                j = 0
                while (j < rref[0].size) {
                    rref[i][j] -= t * rref[row][j]
                    j++
                }
            }
        }
        row++
        col++
    }
    return rref
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