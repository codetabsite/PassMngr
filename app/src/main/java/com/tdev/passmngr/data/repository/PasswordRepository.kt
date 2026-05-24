package com.tdev.passmngr.data.repository

import com.tdev.passmngr.data.db.PasswordDao
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.util.CryptoManager
import kotlinx.coroutines.flow.Flow

class PasswordRepository(private val dao: PasswordDao) {

    fun getAll(): Flow<List<Password>> = dao.getAll()

    fun search(query: String): Flow<List<Password>> =
        if (query.isBlank()) dao.getAll() else dao.search(query)

    fun getByCategory(category: Category): Flow<List<Password>> =
        dao.getByCategory(category.name)

    suspend fun getById(id: Long): Password? = dao.getById(id)

    suspend fun save(accountName: String, username: String, plainPassword: String, category: Category): Long {
        val encrypted = CryptoManager.encrypt(plainPassword)
        return dao.insert(Password(accountName = accountName, username = username, encryptedPassword = encrypted, category = category))
    }

    suspend fun update(existing: Password, newAccountName: String, newUsername: String, newPlainPassword: String, newCategory: Category) {
        val encrypted = CryptoManager.encrypt(newPlainPassword)
        dao.update(existing.copy(accountName = newAccountName, username = newUsername, encryptedPassword = encrypted, category = newCategory, updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(password: Password) = dao.delete(password)

    fun decryptPassword(password: Password): String = CryptoManager.decrypt(password.encryptedPassword)
}
