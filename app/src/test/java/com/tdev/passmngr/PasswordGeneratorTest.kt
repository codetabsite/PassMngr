package com.tdev.passmngr

import com.tdev.passmngr.util.PasswordGenerator
import org.junit.Assert.*
import org.junit.Test

class PasswordGeneratorTest {

    @Test
    fun `generated password has correct length`() {
        val pw = PasswordGenerator.generate(16)
        assertEquals(16, pw.length)
    }

    @Test
    fun `generated password contains lowercase`() {
        val pw = PasswordGenerator.generate(32)
        assertTrue(pw.any { it.isLowerCase() })
    }

    @Test
    fun `generated password contains uppercase`() {
        val pw = PasswordGenerator.generate(32)
        assertTrue(pw.any { it.isUpperCase() })
    }

    @Test
    fun `generated password contains digit`() {
        val pw = PasswordGenerator.generate(32)
        assertTrue(pw.any { it.isDigit() })
    }

    @Test
    fun `generated password contains symbol`() {
        val pw = PasswordGenerator.generate(32)
        assertTrue(pw.any { !it.isLetterOrDigit() })
    }

    @Test
    fun `empty password has zero strength`() {
        assertEquals(0, PasswordGenerator.strength(""))
    }

    @Test
    fun `short simple password is weak`() {
        assertEquals(1, PasswordGenerator.strength("abc"))
    }

    @Test
    fun `strong password scores high`() {
        val strength = PasswordGenerator.strength("Tr0ub4dor&3XkP9!")
        assertTrue(strength >= 4)
    }

    @Test
    fun `two generated passwords are not identical`() {
        val a = PasswordGenerator.generate()
        val b = PasswordGenerator.generate()
        assertNotEquals(a, b)
    }
}
