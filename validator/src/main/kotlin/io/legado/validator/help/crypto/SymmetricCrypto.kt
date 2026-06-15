package io.legado.validator.help.crypto

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SymmetricCrypto(
    private val transformation: String,
    key: ByteArray
) {
    private val keySpec = SecretKeySpec(key, transformation.substringBefore('/'))
    private var ivSpec: IvParameterSpec? = null

    fun setIv(iv: ByteArray): SymmetricCrypto {
        ivSpec = IvParameterSpec(iv)
        return this
    }

    private fun cipher(mode: Int): Cipher {
        val c = Cipher.getInstance(transformation)
        if (ivSpec != null) {
            c.init(mode, keySpec, ivSpec)
        } else {
            c.init(mode, keySpec)
        }
        return c
    }

    fun encrypt(data: ByteArray): ByteArray = cipher(Cipher.ENCRYPT_MODE).doFinal(data)

    fun encrypt(data: String, charset: String = "UTF-8"): ByteArray =
        encrypt(data.toByteArray(charset(charset)))

    fun encryptBase64(data: ByteArray): String =
        Base64.getEncoder().encodeToString(encrypt(data))

    fun encryptBase64(data: String, charset: String = "UTF-8"): String =
        encryptBase64(data.toByteArray(charset(charset)))

    fun encryptHex(data: ByteArray): String =
        encrypt(data).joinToString("") { "%02x".format(it) }

    fun encryptHex(data: String, charset: String = "UTF-8"): String =
        encryptHex(data.toByteArray(charset(charset)))

    fun decrypt(data: ByteArray): ByteArray = cipher(Cipher.DECRYPT_MODE).doFinal(data)

    fun decrypt(data: String): ByteArray = decrypt(Base64.getDecoder().decode(data))

    fun decryptStr(data: ByteArray): String = String(decrypt(data))

    fun decryptStr(data: String): String = decryptStr(Base64.getDecoder().decode(data))
}
