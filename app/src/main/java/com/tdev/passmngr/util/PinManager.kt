package com.tdev.passmngr.util

import android.content.Context
import androidx.core.content.edit
import java.security.MessageDigest

// PIN'i plaintext saklamak yerine SHA-256 hash'ini saklıyoruz.
// AndroidKeyStore burada fazladan — PIN kısa olduğu için ayrıca
// bcrypt kullanmak daha doğru olurdu ama bağımlılık eklemek istemedik.
object PinManager {

    private const val PREFS = "pin_prefs"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_AUTO_LOCK_MIN = "auto_lock_min"

    fun isSet(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_PIN_HASH, null) != null

    fun set(context: Context, pin: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString(KEY_PIN_HASH, hash(pin))
        }
    }

    fun check(context: Context, pin: String): Boolean {
        val stored = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_PIN_HASH, null) ?: return false
        return stored == hash(pin)
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            remove(KEY_PIN_HASH)
        }
    }

    fun setAutoLockMinutes(context: Context, minutes: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putInt(KEY_AUTO_LOCK_MIN, minutes)
        }
    }

    fun getAutoLockMillis(context: Context): Long {
        val min = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_AUTO_LOCK_MIN, 2)
        return min * 60_000L
    }

    private fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(pin.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
