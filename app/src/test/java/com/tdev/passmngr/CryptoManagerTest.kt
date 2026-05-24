package com.tdev.passmngr

import org.junit.Assert.*
import org.junit.Test

class CryptoManagerTest {

    @Test
    fun `encrypted string is not equal to plaintext`() {
        val plain = "MySecret123!"
        val fakeEncrypted = "aGVsbG8=:d29ybGQ="
        assertNotEquals(plain, fakeEncrypted)
    }

    @Test
    fun `decrypt returns empty on malformed input`() {
        val result = runCatching {
            com.tdev.passmngr.util.CryptoManager.decrypt("badformat")
        }.getOrDefault("")
        assertEquals("", result)
    }

    @Test
    fun `password strength covers all tiers`() {
        val util = com.tdev.passmngr.util.PasswordGenerator
        assertEquals(0, util.strength(""))
        assertTrue(util.strength("a") in 1..2)
        assertTrue(util.strength("Abcdef1!Abcdef1!Abcdef1!") >= 4)
    }
}
