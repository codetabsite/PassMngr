package com.tdev.passmngr

import android.app.Application
import com.tdev.passmngr.data.db.PassDatabase
import com.tdev.passmngr.data.repository.PasswordRepository

class PassMngrApp : Application() {

    val database by lazy { PassDatabase.getInstance(this) }
    val repository by lazy { PasswordRepository(database.passwordDao()) }
}
