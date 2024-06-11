package com.example.play2plat_tpcm.room.repository

import androidx.lifecycle.LiveData
import com.example.play2plat_tpcm.room.dao.UserDao
import com.example.play2plat_tpcm.room.entities.User

class UserRepository(private  val userDao: UserDao) {
    val readAllUsers : LiveData<List<User>> = userDao.readAllUsers()

    fun getUserByIdUser(idUser: Int): LiveData<User> {
        return userDao.getUserByIdUser(idUser)
    }

    fun getUserByEmailAndPassword(email: String, password: String): LiveData<User?> {
        return userDao.getUserByEmailAndPassword(email, password)
    }


    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}