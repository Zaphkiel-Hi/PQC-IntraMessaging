package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import kotlin.math.abs


fun rref(matrix: Array<LongArray>, pivot: MutableList<Int>): Array<LongArray> {
    val rref = mutableListOf<LongArray>()
    for (i in matrix.indices) rref.add(matrix[i].copyOf(matrix[i].size))
    var row = 0
    var col = 0
    while (col < rref[0].size && row < rref.size) {
        var j = row
        for (i in row + 1 until rref.size)
            if (abs(rref[i][col]) > abs(rref[j][col]))
                j = i
        if (abs(rref[j][col]) < 0.00001) {
            col++
            continue
        }

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
    return rref.toTypedArray()
}

fun kernel(aMatrix: Array<LongArray>): Array<LongArray> {
    // https://stackoverflow.com/a/50437982
    val n = aMatrix[0].size
    val pivot = mutableListOf<Int>()
    val rMatrix = rref(aMatrix, pivot)
    val r = pivot.size
    val nopiv = HashMap<Int, Int>()

    for (i in 0 until n) {
        nopiv[i] = i + 1
    }

    for (e in pivot) {
        if (nopiv.containsValue(e + 1)) nopiv.remove(e, e + 1)
    }

    for (j in 0 until r) {
        val index = pivot[j]
        if (nopiv.containsValue(index)) nopiv.remove(index)
    }
    val zMatrix = Array(n) {
        LongArray(
            n - r
        )
    }

    //Add 1(s) in the main diagonal
    if (n > r) {
        val eye = Array(n - r) {
            LongArray(
                n - r
            )
        }
        for (i in eye.indices) {
            for (j in eye[i].indices) {
                if (j == i) eye[i][j] = 1L else eye[i][j] = 0L
            }
        }
        //Add eye in Z
        val loc = nopiv.values.toTypedArray<Int>()
        for (i in loc.indices) {
            val index = loc[i]
            for (j in zMatrix[0].indices) {
                zMatrix[index - 1][j] = eye[i][j]
            }
        }
        if (r > 0) {
            for (i in 0 until r) {
                val indexi = pivot[i]
                for (j in loc.indices) {
                    val indexd = loc[j] - 1
                    zMatrix[indexi][j] = -rMatrix[i][indexd]
                }
            }
        }
    }
    return zMatrix
}