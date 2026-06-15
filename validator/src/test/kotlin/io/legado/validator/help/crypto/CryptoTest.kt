package io.legado.validator.help.crypto

import io.legado.validator.help.JsExtensions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.Base64

class CryptoTest {

    private val js = object : JsExtensions {
        override fun getSource(): Any? = null
    }

    // ── digestHex ──

    @Test
    fun `digestHex MD5`() {
        assertEquals("5d41402abc4b2a76b9719d911017c592", js.digestHex("hello", "MD5"))
    }

    @Test
    fun `digestHex SHA-256`() {
        assertEquals(
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
            js.digestHex("hello", "SHA-256")
        )
    }

    @Test
    fun `digestHex SHA-1`() {
        assertEquals("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d", js.digestHex("hello", "SHA-1"))
    }

    // ── digestBase64Str ──

    @Test
    fun `digestBase64Str MD5`() {
        assertEquals("XUFAKrxLKna5cZ2REBfFkg==", js.digestBase64Str("hello", "MD5"))
    }

    @Test
    fun `digestBase64Str SHA-256`() {
        assertEquals(
            "LPJNul+wow4m6DsqxbninhsWHlwfp0JecwQzYpOLmCQ=",
            js.digestBase64Str("hello", "SHA-256")
        )
    }

    // ── HMacHex ──

    @Test
    fun `HMacHex HMAC-SHA256`() {
        val result = js.HMacHex("message", "HmacSHA256", "key")
        assertEquals(64, result.length)
        assertTrue(result.matches(Regex("[0-9a-f]+")))
    }

    @Test
    fun `HMacHex deterministic`() {
        val r1 = js.HMacHex("test", "HmacSHA256", "mykey")
        val r2 = js.HMacHex("test", "HmacSHA256", "mykey")
        assertEquals(r1, r2)
    }

    @Test
    fun `HMacHex different keys produce different results`() {
        val r1 = js.HMacHex("test", "HmacSHA256", "key1")
        val r2 = js.HMacHex("test", "HmacSHA256", "key2")
        assertNotEquals(r1, r2)
    }

    // ── HMacBase64 ──

    @Test
    fun `HMacBase64 deterministic`() {
        val r1 = js.HMacBase64("test", "HmacSHA256", "mykey")
        val r2 = js.HMacBase64("test", "HmacSHA256", "mykey")
        assertEquals(r1, r2)
    }

    @Test
    fun `HMacBase64 is valid base64`() {
        val result = js.HMacBase64("test", "HmacSHA256", "mykey")
        assertDoesNotThrow { Base64.getDecoder().decode(result) }
    }

    // ── SymmetricCrypto (AES) ──

    @Test
    fun `AES-128-ECB encrypt and decrypt`() {
        val key = "1234567890abcdef"
        val crypto = SymmetricCrypto("AES/ECB/PKCS5Padding", key.toByteArray())
        val encrypted = crypto.encrypt("hello world")
        val decrypted = crypto.decryptStr(encrypted)
        assertEquals("hello world", decrypted)
    }

    @Test
    fun `AES-256-CBC encrypt and decrypt`() {
        val key = "1234567890abcdef1234567890abcdef"
        val iv = "abcdef1234567890"
        val crypto = SymmetricCrypto("AES/CBC/PKCS5Padding", key.toByteArray())
        crypto.setIv(iv.toByteArray())
        val encrypted = crypto.encrypt("hello world")
        val decrypted = crypto.decryptStr(encrypted)
        assertEquals("hello world", decrypted)
    }

    @Test
    fun `AES encryptBase64`() {
        val key = "1234567890abcdef"
        val crypto = SymmetricCrypto("AES/ECB/PKCS5Padding", key.toByteArray())
        val b64 = crypto.encryptBase64("hello world")
        assertDoesNotThrow { Base64.getDecoder().decode(b64) }
        val decrypted = crypto.decryptStr(b64)
        assertEquals("hello world", decrypted)
    }

    @Test
    fun `AES encryptHex`() {
        val key = "1234567890abcdef"
        val crypto = SymmetricCrypto("AES/ECB/PKCS5Padding", key.toByteArray())
        val hex = crypto.encryptHex("hello world")
        assertTrue(hex.matches(Regex("[0-9a-f]+")))
    }

    @Test
    fun `DES encrypt and decrypt`() {
        val key = "12345678"
        val crypto = SymmetricCrypto("DES/ECB/PKCS5Padding", key.toByteArray())
        val encrypted = crypto.encrypt("hello")
        val decrypted = crypto.decryptStr(encrypted)
        assertEquals("hello", decrypted)
    }

    @Test
    fun `3DES encrypt and decrypt`() {
        val key = "1234567890123456ABCDEFGH"
        val crypto = SymmetricCrypto("DESede/ECB/PKCS5Padding", key.toByteArray())
        val encrypted = crypto.encrypt("hello")
        val decrypted = crypto.decryptStr(encrypted)
        assertEquals("hello", decrypted)
    }

    @Test
    fun `SymmetricCrypto ByteArray decrypt`() {
        val key = "1234567890abcdef"
        val crypto = SymmetricCrypto("AES/ECB/PKCS5Padding", key.toByteArray())
        val data = "test data".toByteArray()
        val encrypted = crypto.encrypt(data)
        val decrypted = crypto.decrypt(encrypted)
        assertArrayEquals(data, decrypted)
    }
}
