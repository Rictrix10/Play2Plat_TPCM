package com.example.play2plat_tpcm.room.repository

import androidx.lifecycle.LiveData
import com.example.play2plat_tpcm.room.dao.GameDao
import com.example.play2plat_tpcm.room.dao.UserDao
import com.example.play2plat_tpcm.room.entities.Game

class GameRepository(private  val gameDao: GameDao) {
    val readAllGames : LiveData<List<Game>> = gameDao.readAllGames()

    fun getGamesByIdUser(userId: Int): LiveData<List<Game>> {
        return gameDao.getGamesByUserId(userId)
    }

    fun getGamesByStateUserId(userId: Int, state: String): LiveData<List<Game>> {
        return gameDao.getGamesByStateUserId(userId, state)
    }

    fun getGamesByIsFavoriteUserId(userId: Int, isFavorite: Boolean): LiveData<List<Game>> {
        return gameDao.getGamesByIsFavoriteUserId(userId, isFavorite)
    }

    suspend fun addGame(game: Game){
        gameDao.addGame(game)
    }

    suspend fun updateGame(game: Game) {
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: Game) {
        gameDao.deleteGame(game)
    }
}