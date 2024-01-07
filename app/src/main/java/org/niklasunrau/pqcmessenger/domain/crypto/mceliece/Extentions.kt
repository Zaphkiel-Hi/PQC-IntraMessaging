package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Ring
import com.google.common.math.IntMath
import org.jetbrains.kotlinx.multik.api.d2arrayIndices
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toLongArray
import cc.redberry.rings.poly.univar.UnivariatePolynomial as Poly
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64 as Element


operator fun <T> Poly<T>.plus(other: Poly<T>): Poly<T> = this.clone().add(other)

operator fun <T> Poly<T>.minus(other: Poly<T>): Poly<T> = this.clone().subtract(other)

operator fun <T> Poly<T>.times(other: Poly<T>): Poly<T> = this.clone().multiply(other)

operator fun <T> Poly<T>.unaryMinus(): Poly<T> = this.clone().negate()

inline fun <reified T> Poly<T>.split(): Pair<Poly<T>, Poly<T>> {
    var evenCoeffs = arrayOf<T>()
    var oddCoeffs = arrayOf<T>()
    val sqrtExpo = IntMath.pow(2, ring.perfectPowerExponent().toInt() - 1)

    for (i in 0..size()) {
        if (i % 2 == 0) evenCoeffs += ring.pow(get(i), sqrtExpo)
        else oddCoeffs += ring.pow(get(i), sqrtExpo)
    }

    val evenPoly = Poly.create(ring, *evenCoeffs)
    val oddPoly = Poly.create(ring, *oddCoeffs)

    return evenPoly to oddPoly

}

fun Element.toBinary(): String {
    return this.stream().toArray().joinToString(separator = "")
}



fun Element.toInt(): Int {
    var bin = ""
    for (i in degree() downTo 0) bin += get(i)
    return bin.toInt(2)
}
fun <T> Ring<T>.identity(): Poly<T> {
    return Poly.create(this, zero, one)
}

fun MultiArray<Long, D2>.nullspace(): Array<LongArray> {
    val newMatrix = this.transpose().copy()
    val (m, n) = newMatrix.shape
    val identity = mk.identity<Long>(m)
    val toSolve = newMatrix.append(identity, 1)
    val (inRREF, p) = toSolve.toArray().reducedRowEchelonForm(n)
    val nullspace = mk.ndarray(inRREF)[p..<inRREF.size, n..<inRREF[0].size]
    val (reducedNullspace, _) = nullspace.toArray().reducedRowEchelonForm()
    return reducedNullspace
}

fun Array<LongArray>.reducedRowEchelonForm(nCols: Int? = null): Pair<Array<LongArray>, Int> {
    val rowCount = this.size
    val colCount = this[0].size
    val numCols = nCols ?: colCount

    val newMatrix = mk.ndarray(this)
    var p = 0

    for (j in 0 until numCols) {
        var indexes = nonZero(newMatrix[p..<rowCount, j].toLongArray())
        if (indexes.isEmpty()) continue

        val i = p + indexes[0]

        val temp = newMatrix[i].copy()
        newMatrix[i] = newMatrix[p]
        newMatrix[p] = temp

        indexes = nonZero(newMatrix[0..<rowCount, j].toLongArray())
        indexes.remove(p)
        if (indexes.isNotEmpty()) {


            val firstVector = indexes.map { newMatrix[it, j] }
            val secondVector = newMatrix[p, 0..<colCount]
            val outer = mk.d2arrayIndices(firstVector.size, secondVector.size) { row, col ->
                firstVector[row] * secondVector[col]
            }
            for ((index, row) in indexes.withIndex()) {
                for (col in 0 until colCount)
                    newMatrix[row, col] = Math.floorMod(newMatrix[row, col] + outer[index, col], 2).toLong()
            }
        }
        p++
        if (p == rowCount)
            break
    }
    return newMatrix.toArray() to p
}