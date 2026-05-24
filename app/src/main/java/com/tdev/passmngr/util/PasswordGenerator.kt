package com.tdev.passmngr.util

import kotlin.math.ln
import kotlin.math.log2

object PasswordGenerator {

    private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#\$%^&*()_+-=[]{}|;:,.<>?"

    fun generate(length: Int = 16): String {
        val pool = LOWER + UPPER + DIGITS + SYMBOLS
        val result = StringBuilder()
        result.append(LOWER.random())
        result.append(UPPER.random())
        result.append(DIGITS.random())
        result.append(SYMBOLS.random())
        repeat(length - 4) { result.append(pool.random()) }
        return result.toList().shuffled().joinToString("")
    }

    fun strength(password: String): Int {
        if (password.isEmpty()) return 0
        var poolSize = 0
        if (password.any { it.isLowerCase() }) poolSize += 26
        if (password.any { it.isUpperCase() }) poolSize += 26
        if (password.any { it.isDigit() }) poolSize += 10
        if (password.any { !it.isLetterOrDigit() }) poolSize += 32
        val entropy = password.length * log2(poolSize.toDouble())
        return when {
            entropy < 28 -> 1
            entropy < 36 -> 2
            entropy < 60 -> 3
            entropy < 128 -> 4
            else -> 5
        }
    }
}
