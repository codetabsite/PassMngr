package com.tdev.passmngr.data.repository

import com.tdev.passmngr.data.db.PasswordDao
import com.tdev.passmngr.data.db.PasswordHistoryDao
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.model.PasswordHistory
import com.tdev.passmngr.data.model.SortOrder
import com.tdev.passmngr.util.CryptoManager
import kotlinx.coroutines.flow.Flow

class PasswordRepository(
    private val dao: PasswordDao,
    private val historyDao: PasswordHistoryDao,
) {
    fun getAll(sort: SortOrder): Flow<List<Password>> = when (sort) {
        SortOrder.NAME_ASC   -> dao.getAllByName()
        SortOrder.DATE_DESC  -> dao.getAllByDate()
        SortOrder.LAST_USED  -> dao.getAllByLastUsed()
    }

    suspend fun getById(id: Long): Password? = dao.getById(id)

    suspend fun save(password: Password): Long {
        val encrypted = password.copy(
            encryptedPassword = CryptoManager.encrypt(password.encryptedPassword)
        )
        return if (password.id == 0L) {
            dao.insert(encrypted)
        } else {
            // Güncelleme öncesi geçmişe kaydet
            dao.getById(password.id)?.let { old ->
                historyDao.insert(PasswordHistory(passwordId = old.id, encryptedPassword = old.encryptedPassword))
                historyDao.pruneOldHistory(old.id)
            }
            dao.update(encrypted.copy(updatedAt = System.currentTimeMillis()))
            password.id
        }
    }

    suspend fun delete(password: Password) = dao.delete(password)

    suspend fun markUsed(id: Long) = dao.updateLastUsed(id, System.currentTimeMillis())

    fun decrypt(password: Password): String = CryptoManager.decrypt(password.encryptedPassword)

    suspend fun getHistory(id: Long): List<String> =
        historyDao.getHistory(id).map { CryptoManager.decrypt(it.encryptedPassword) }
}
