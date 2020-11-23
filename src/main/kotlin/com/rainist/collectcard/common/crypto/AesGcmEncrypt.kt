package com.rainist.collectcard.common.crypto

import com.rainist.collectcard.common.exception.CipherException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesGcmEncrypt {
    private const val ALGORITHM_AES_GCM_NOPADDING = "AES/GCM/NoPadding"
    private const val ALGORITHM_AES = "AES"
    const val GCM_TAG_LENGTH = 16

    fun encryptStringBase64(secret: String?, iv: String?, payload: String): String? {
        return Base64Util.encode(
                encrypt(Base64Util.decode(secret),
                Base64Util.decode(iv),
                payload.toByteArray(StandardCharsets.UTF_8)
            )
        )
    }

    fun decryptStringBase64(secret: String?, iv: String?, encrypted: String?): String? {
        return String(
                decrypt(
                    Base64Util.decode(secret),
                    Base64Util.decode(iv),
                    Base64Util.decode(encrypted)
                )
        )
    }

    fun encrypt(secret: ByteArray?, iv: ByteArray?, payload: ByteArray?): ByteArray {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM_AES_GCM_NOPADDING)
            val mode = Cipher.ENCRYPT_MODE
            val key = SecretKeySpec(secret, ALGORITHM_AES)
            val algorithmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)

            cipher.init(mode, key, algorithmParameterSpec)
            cipher.doFinal(payload)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException,
                is InvalidKeyException,
                is InvalidAlgorithmParameterException,
                is IllegalBlockSizeException,
                is BadPaddingException -> {
                    throw CipherException("encrypt fail.", e)
                }
                else -> throw e
            }
        }
    }

    fun decrypt(secret: ByteArray?, iv: ByteArray?, encrypted: ByteArray?): ByteArray {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM_AES_GCM_NOPADDING)
            val mode = Cipher.DECRYPT_MODE
            val key = SecretKeySpec(secret, ALGORITHM_AES)
            val algorithmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)

            cipher.init(mode, key, algorithmParameterSpec)
            cipher.doFinal(encrypted)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException,
                is InvalidKeyException,
                is InvalidAlgorithmParameterException,
                is IllegalBlockSizeException,
                is BadPaddingException -> {
                    throw CipherException("encrypt fail.", e)
                }
                else -> throw e
            }
        }
    }
}
