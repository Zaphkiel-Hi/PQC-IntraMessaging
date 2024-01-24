package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import kotlin.collections.component1
import kotlin.collections.component2

fun inverseModPoly(poly: Poly, mod: Poly): Poly {
    return McEliece.coeffRing.extendedGCD(poly, mod)[1]
}

fun sqrtModPoly(poly: Poly, mod: Poly): Poly {
    val (g0, g1) = mod.split()
    val (t0, t1) = poly.split()
    return McEliece.coeffRing.remainder(
        t0 + (g0 * inverseModPoly(g1, mod) * t1),
        mod
    )
}

private fun normExpo(aPoly: Poly, bPoly: Poly): Int {
    return ((aPoly * aPoly) + (McEliece.ff2m.identity() * (bPoly * bPoly))).degree()
}

fun latticeBasisReduction(
    poly: Poly, mod: Poly
): Pair<Poly, Poly> {
    val t = mod.degree()
    val a = mutableListOf<Poly>()
    val b = mutableListOf<Poly>()

    val (q0) = McEliece.coeffRing.divideAndRemainder(mod, poly)
    a.add(mod - (q0 * poly))
    b.add(-q0)

    if (normExpo(a[0], b[0]) > t) {
        val (q, r) = McEliece.coeffRing.divideAndRemainder(poly, a[0])
        a.add(r)
        b.add(McEliece.coeffRing.one - (q * b[0]))
    } else {
        return a[0] to b[0]
    }

    var i = 1
    while (normExpo(a[i], b[i]) > t) {
        val (q, r) = McEliece.coeffRing.divideAndRemainder(a[i - 1], a[i])
        a.add(r)
        b.add(b[i - 1] - (q * b[i]))
        i++
    }

    return a[i] to b[i]
}