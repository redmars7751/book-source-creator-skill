package io.legado.validator.web

import java.util.concurrent.ConcurrentHashMap

object CookieStore {
    private val cookies = ConcurrentHashMap<String, String>()

    fun setCookie(domain: String, cookie: String) {
        cookies[domain.lowercase()] = cookie
    }

    fun getCookie(domain: String): String? = cookies[domain.lowercase()]

    fun clearCookie(domain: String) {
        cookies.remove(domain.lowercase())
    }

    fun clearAll() {
        cookies.clear()
    }

    fun getAll(): Map<String, String> = cookies.toMap()

    fun hasCookies(): Boolean = cookies.isNotEmpty()
}
