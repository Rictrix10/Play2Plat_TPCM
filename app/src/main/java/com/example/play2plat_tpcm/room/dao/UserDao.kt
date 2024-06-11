package com.example.play2plat_tpcm.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.play2plat_tpcm.room.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE idUser = :idUser LIMIT 1")
    fun getUserByIdUser(idUser: Int): LiveData<User>

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun readAllUsers() : LiveData<List<User>>

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    fun getUserByEmailAndPassword(email: String, password: String): LiveData<User?>



    @Delete
    suspend fun deleteUser(user: User)
}