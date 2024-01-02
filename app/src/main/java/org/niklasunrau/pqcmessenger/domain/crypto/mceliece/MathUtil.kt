package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Ring
import cc.redberry.rings.Rings.UnivariateRing
import cc.redberry.rings.poly.FiniteField
import com.google.common.math.IntMath.pow
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.rand
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import cc.redberry.rings.poly.univar.UnivariatePolynomial as Polynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64 as FieldElement

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
                product[i][j] = (product[i][j] + matrix1[i][k] * matrix2[k][j]) % 2
            }
        }
    }
    return product
}


fun multiplyFieldMatrices(
    finiteField: FiniteField<FieldElement>, matrix1: Array<Array<FieldElement>>, matrix2: Array<Array<FieldElement>>
): Array<Array<FieldElement>> {
    val row1 = matrix1.size
    val col1 = matrix1[0].size
    val col2 = matrix2[0].size
    val product = Array(row1) { Array(col2) { finiteField.zero } }

    for (i in 0 until row1) {
        for (j in 0 until col2) {
            for (k in 0 until col1) {
                product[i][j] = finiteField.add(
                    product[i][j], finiteField.multiply(matrix1[i][k].multiply(), matrix2[k][j])
                )
            }
        }
    }

    return product
}

fun Array<DoubleArray>.toLongArray(): Array<LongArray> {
    return Array(this.size) { row -> LongArray(this[0].size) { col -> this[row][col].toLong() } }
}

fun Array<LongArray>.toDoubleArray(): Array<DoubleArray> {
    return Array(this.size) { row -> DoubleArray(this[0].size) { col -> this[row][col].toDouble() } }
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
                matrix[i].zip(matrix[k]).forEachIndexed() { index, pair ->
                    matrix[k][index] = (pair.first + pair.second) % 2
                }
            }
        }
    }
    return 1
}

fun inverseModPoly(poly: Polynomial<FieldElement>, mod: Polynomial<FieldElement>): Polynomial<FieldElement> {
    val polyRing = UnivariateRing(poly.ring)
    return polyRing.extendedGCD(poly, mod)[1]
}

private fun splitPoly(poly: Polynomial<FieldElement>): Pair<Polynomial<FieldElement>, Polynomial<FieldElement>> {
    var evenCoeffs = arrayOf<FieldElement>()
    var oddCoeffs = arrayOf<FieldElement>()
    for (i in 0..poly.size()) {
        if (i % 2 == 0) evenCoeffs += poly[i]
        else oddCoeffs += poly[i]
    }

    val evenPoly = Polynomial.create(poly.ring, *evenCoeffs)
    val oddPoly = Polynomial.create(poly.ring, *oddCoeffs)

    return evenPoly to oddPoly

}


fun sqrtModPoly(poly: Polynomial<FieldElement>, mod: Polynomial<FieldElement>): Polynomial<FieldElement> {
    val (g0, g1) = splitPoly(mod)
    val (t0, t1) = splitPoly(poly)
    val polyRing = UnivariateRing(poly.ring)
    return polyRing.remainder(t0.add(g0.multiply(inverseModPoly(g1, mod)).multiply(t1)), mod)
}

fun identity(ring: Ring<FieldElement>): Polynomial<FieldElement> {
    return Polynomial.create(ring, ring.zero, ring.one)
}

private fun norm(aPoly: Polynomial<FieldElement>, bPoly: Polynomial<FieldElement>): Int {
    return pow(2, (aPoly.multiply(aPoly).add(identity(aPoly.ring).multiply(bPoly.multiply(bPoly)))).degree())
}

fun latticeBasisReduction(
    poly: Polynomial<FieldElement>, mod: Polynomial<FieldElement>
): Pair<Polynomial<FieldElement>, Polynomial<FieldElement>> {
    val t = mod.degree()
    val a = mutableListOf<Polynomial<FieldElement>>()
    val b = mutableListOf<Polynomial<FieldElement>>()
    val ring = poly.ring
    val polyRing = UnivariateRing(ring)

    var divmod = polyRing.divideAndRemainder(mod, poly)
    var q = divmod[0]
    var r: Polynomial<FieldElement>
    a.add(mod.subtract(q.multiply(poly)))
    b.add(q.negate())

    if (norm(a[0], b[0]) > pow(2, t)) {
        divmod = polyRing.divideAndRemainder(poly, a[0])
        q = divmod[0]
        r = divmod[1]
        a.add(r)
        b.add(polyRing.one.subtract(q.multiply(b[0])))
    } else {
        return a[0] to b[0]
    }

    var i = 1
    while (norm(a[i], b[i]) > pow(2, t)) {
        divmod = polyRing.divideAndRemainder(a[i - 1], a[i])
        q = divmod[0]
        r = divmod[1]
        a.add(r)
        b.add(b[i - 1].subtract(q.multiply(b[i])))
        i++
    }

    return a[i] to b[i]
}