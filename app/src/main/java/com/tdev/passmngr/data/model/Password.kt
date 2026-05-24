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

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountName: String,
    val username: String,
    val encryptedPassword: String,
    val category: Category = Category.OTHER,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
