package io.legado.validator

import io.legado.validator.model.BookSource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HeaderTest {
    @Test
    fun `plain JSON header parses correctly`() {
        val source = BookSource(header = """{"User-Agent": "CustomBot"}""")
        assertEquals("CustomBot", source.getHeaderMap()["User-Agent"])
    }

    @Test
    fun `js prefix header executes and parses`() {
        val source = BookSource(header = """@js:({"X-Test": "val"})""")
        assertEquals("val", source.getHeaderMap()["X-Test"])
    }

    @Test
    fun `js tag header executes and parses`() {
        val source = BookSource(header = """<js>({"X-Test": "val"})</js>""")
        assertEquals("val", source.getHeaderMap()["X-Test"])
    }
}
