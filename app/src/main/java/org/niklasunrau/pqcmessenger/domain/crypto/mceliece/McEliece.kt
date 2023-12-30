package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import com.google.common.math.IntMath.pow

class McEliece(val m: Int, val t: Int) {
    val n = pow(2, m)
    val k = n - t * m

    fun generateKeyPair(){
        val goppaCode = generateCode(n, m, t)
        val shuffleMatrix = generateShuffleMatrix(k)
        val permMatrix = generatePermMatrix(n)


    }

}