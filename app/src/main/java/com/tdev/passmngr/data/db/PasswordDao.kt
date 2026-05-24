package com.tdev.passmngr.data.db

import androidx.room.*
import com.tdev.passmngr.data.model.Password
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {

    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE accountName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun search(query: String): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE category = :category ORDER BY updatedAt DESC")
    fun getByCategory(category: String): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getById(id: Long): Password?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: Password): Long

    @Update
    suspend fun update(password: Password)

    @Delete
    suspend fun delete(password: Password)
}
