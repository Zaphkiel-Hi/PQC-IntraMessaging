package org.niklasunrau.pqcmessenger.domain.crypto.aes

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object AES {
    private val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    private val pbeFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    private val keyGenerator = KeyGenerator.getInstance("AES")
    private val CHARSET = Charsets.UTF_8

    private const val TAG_LENGTH = 128
    private const val IV_LENGTH = 12
    private const val SALT_LENGTH = 16
    private const val KEY_LENGTH = 256
    private const val ITERATIONS = 200000

    init {
        keyGenerator.init(128)
    }

    private fun getRandomNonce(len: Int): ByteArray {
        val nonce = ByteArray(len)
        SecureRandom().nextBytes(nonce)
        return nonce
    }

    private fun generateKeyFromPassword(password: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        return SecretKeySpec(pbeFactory.generateSecret(spec).encoded, "AES")
    }

    fun generateSymmetricKey(): SecretKey = keyGenerator.generateKey()
    fun encrypt(message: String, key: SecretKey): String {
        val iv = getRandomNonce(IV_LENGTH)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH, iv))
        val cipherText = cipher.doFinal(message.toByteArray(CHARSET))

        val cipherWithIv = iv + cipherText
        return Base64.encodeToString(cipherWithIv, Base64.DEFAULT)
    }

    fun encrypt(message: String, password: String): String {
        val salt = getRandomNonce(SALT_LENGTH)
        val secretKey = generateKeyFromPassword(password, salt)

        val iv = getRandomNonce(IV_LENGTH)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH, iv))
        val cipherText = cipher.doFinal(message.toByteArray(CHARSET))

        val cipherWithSaltIv = salt + iv + cipherText
        return Base64.encodeToString(cipherWithSaltIv, Base64.DEFAULT)
    }

    fun decrypt(cipherText: String, key: SecretKey): String {
        val bytes = Base64.decode(cipherText, Base64.DEFAULT)

        val iv = bytes.sliceArray(0..<IV_LENGTH)
        val content = bytes.sliceArray(IV_LENGTH..<bytes.size)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH, iv))
        return cipher.doFinal(content).toString(CHARSET)
    }


    fun decrypt(cipherText: String, password: String): String {
        val bytes = Base64.decode(cipherText, Base64.DEFAULT)

        val salt = bytes.sliceArray(0..<SALT_LENGTH)
        val secretKey = generateKeyFromPassword(password, salt)

        val iv = bytes.sliceArray(SALT_LENGTH..<SALT_LENGTH + IV_LENGTH)
        val content = bytes.sliceArray(SALT_LENGTH + IV_LENGTH..<bytes.size)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH, iv))
        return cipher.doFinal(content).toString(CHARSET)
    }


}