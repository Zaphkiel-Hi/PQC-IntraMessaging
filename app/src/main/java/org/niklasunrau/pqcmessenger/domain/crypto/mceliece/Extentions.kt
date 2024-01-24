package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.Ring
import cc.redberry.rings.poly.univar.UnivariatePolynomial
import com.google.common.math.IntMath
import org.jetbrains.kotlinx.multik.api.d2arrayIndices
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.asDNArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import kotlin.streams.toList


operator fun Poly.plus(other: Poly): Poly = this.clone().add(other)

operator fun Poly.minus(other: Poly): Poly = this.clone().subtract(other)

operator fun Poly.times(other: Poly): Poly = this.clone().multiply(other)

operator fun Poly.unaryMinus(): Poly = this.clone().negate()

fun Poly.split(): Pair<Poly, Poly> {
    var evenCoeffs = arrayOf<Element>()
    var oddCoeffs = arrayOf<Element>()
    val sqrtExpo = IntMath.pow(2, ring.perfectPowerExponent().toInt() - 1)

    for (i in 0..size()) {
        if (i % 2 == 0) evenCoeffs += ring.pow(get(i), sqrtExpo)
        else oddCoeffs += ring.pow(get(i), sqrtExpo)
    }

    val evenPoly = UnivariatePolynomial.create(ring, *evenCoeffs)
    val oddPoly = UnivariatePolynomial.create(ring, *oddCoeffs)

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

fun Element.toBinaryColumn(width: Int): NDArray<Byte, D2> {
    return mk.ndarray(listOf(lJustZerosList(this.stream().toList().map { it.toByte() }, width))).transpose()
}

fun Ring<Element>.identity(): Poly {
    return UnivariatePolynomial.create(this, zero, one)
}

fun MultiArray<Byte, D2>.nullspace(): Array<ByteArray> {
    // Finding the left kernel, so we transpose first to get the right one
    val newMatrix = this.transpose().copy()
    val (m, n) = newMatrix.shape

    // Append identy so we have [ A | I ]
    val identity = mk.identity<Byte>(m)
    val toSolve = newMatrix.append(identity, 1)

    // Apply gaussian elimination to get [I | A^-1]
    val (inRREF, p) = toSolve.reducedRowEchelonForm(n)

    // Row reduce the left null space so that it begins with an I
    val nullspace = inRREF[p..<inRREF.shape[0], n..<inRREF.shape[1]].asDNArray().asD2Array()
    val (reducedNullspace, _) = nullspace.reducedRowEchelonForm()

    return reducedNullspace.toArray()
}

fun MultiArray<Byte, D2>.toArray(): Array<ByteArray> =
    Array(shape[0]) { row ->
        ByteArray(shape[1]) { col -> this[row][col] }
    }


fun NDArray<Byte, D2>.reducedRowEchelonForm(nCols: Int? = null): Pair<NDArray<Byte, D2>, Int> {
    val (rowCount, colCount) = this.shape
    val numCols = nCols ?: colCount

    val newMatrix = this.copy()
    var p = 0

    for (j in 0 until numCols) {
        var indexes = nonZero(newMatrix[p..<rowCount, j])
        if (indexes.isEmpty()) continue

        val i = p + indexes[0]

        val temp = newMatrix[i].copy()
        newMatrix[i] = newMatrix[p]
        newMatrix[p] = temp

        indexes = nonZero(newMatrix[0..<rowCount, j])
        indexes.remove(p)
        if (indexes.isNotEmpty()) {


            val firstVector = indexes.map { newMatrix[it, j] }
            val secondVector = newMatrix[p, 0..<colCount]
            val outer = mk.d2arrayIndices(firstVector.size, secondVector.size) { row, col ->
                firstVector[row] * secondVector[col]
            }
            for ((index, row) in indexes.withIndex()) {
                for (col in 0 until colCount)
                    newMatrix[row, col] = Math.floorMod(newMatrix[row, col] + outer[index, col], 2).toByte()
            }
        }
        p++
        if (p == rowCount)
            break
    }
    return newMatrix to p
}