package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Ring
import com.google.common.math.IntMath
import org.jetbrains.kotlinx.multik.api.d2arrayIndices
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import org.jetbrains.kotlinx.multik.ndarray.operations.toArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toLongArray
import kotlin.collections.set
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
    var bin = ""
    for (i in degree() downTo 0) bin += get(i)
    return bin
}

fun Poly<Element>.toStringWithPower(): String {
    val elemPrefix = "a^"
    val varString = "x"
    val invLut = generatePowerLUT(this.ring)
    val lut = invLut.reversed()
    if (isConstant) return elemPrefix + lut[cc()].toString()

    var result = ""
    for ((index, coeff) in this.dataReferenceUnsafe.withIndex()) {

        if (ring.isZero(coeff)) continue

        val cfString = if (!ring.isOne(coeff)) elemPrefix + lut[coeff].toString() else ""

        if (result.isNotEmpty()) result += " + "
        result += (cfString)

        if (index == 0) continue

        if (cfString.isNotEmpty()) result += "*"

        result += varString

        if (index > 1) result += "^$index"
    }
    return result
}

fun <T> Ring<T>.identity(): Poly<T> {
    return Poly.create(this, zero, one)
}

fun <K, V> Map<K, V>.reversed() = HashMap<V, K>().also { newMap ->
    entries.forEach { newMap[it.value] = it.key }
}


fun NDArray<Double, D2>.toGF2Array(): Array<LongArray> {
    return this.map { Math.floorMod(it.toLong(), 2).toLong() }.toArray()
}

fun Array<LongArray>.toDoubleNDArray(): NDArray<Double, D2> {
    return mk.d2arrayIndices(this.size, this[0].size) { i, j -> this[i][j].toDouble() }
}

fun Array<IntArray>.toLong(): Array<LongArray> {
    return Array(this.size) { row -> LongArray(this[0].size) { col -> this[row][col].toLong() } }
}

fun Array<LongArray>.copy() = Array(size) { get(it).clone() }


fun Array<LongArray>.toPrettyString(): String {
    var result = ""
    for (row in this) {
        result += row.contentToString() + "\n"
    }
    return result

}

fun Array<Array<Element>>.toPrettyString(ring: Ring<Element>): String {
    val elemPrefix = "a^"
    val invLut = generatePowerLUT(ring)
    val lut = invLut.reversed()
    var result = ""
    for (row in this.indices) {
        result += "[ "
        for (col in this[row].indices) {
            val elem = this[row][col]
            var str = if (elem.isZero) "0  " else if (elem.isOne) "1  " else elemPrefix + lut[elem].toString()
            result += "$str "
        }

        result += "]\n"
    }

    return result
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
        val firstVector = indexes.map { newMatrix[it, j] }
        val secondVector = newMatrix[p, 0..<colCount]
        val outer = mk.d2arrayIndices(firstVector.size, secondVector.size) { row, col ->
            firstVector[row] * secondVector[col]
        }

        for ((index, row) in indexes.withIndex()) {
            for(col in 0 until colCount)
                newMatrix[row, col] = Math.floorMod(newMatrix[row, col] + outer[index, col], 2).toLong()
        }

        p++
        if (p == rowCount)
            break
    }
    return newMatrix.toArray() to p
}