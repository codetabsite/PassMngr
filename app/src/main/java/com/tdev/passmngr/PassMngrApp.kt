package com.tdev.passmngr

import android.app.Application
import com.tdev.passmngr.data.db.PassDatabase
import com.tdev.passmngr.data.repository.PasswordRepository

class PassMngrApp : Application() {

    val repository: PasswordRepository by lazy {
        val db = PassDatabase.getInstance(this)
        PasswordRepository(db.passwordDao(), db.historyDao())
    }

    // Uygulamanın arka plana geçtiği zaman — otomatik kilit için
    var backgroundedAt: Long = 0L
    var isUnlocked: Boolean = false
}
