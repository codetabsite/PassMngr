package com.tdev.passmngr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Category(val label: String) {
    SOCIAL("Sosyal Medya"),
    BANK("Banka"),
    GAME("Oyun"),
    EMAIL("E-posta"),
    WORK("İş"),
    OTHER("Diğer")
}

enum class SortOrder {
    NAME_ASC, DATE_DESC, LAST_USED
}

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountName: String,
    val username: String,
    val encryptedPassword: String,
    val category: Category = Category.OTHER,
    val note: String = "",
    val lastUsedAt: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "password_history")
data class PasswordHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val passwordId: Long,
    val encryptedPassword: String,
    val savedAt: Long = System.currentTimeMillis(),
)
