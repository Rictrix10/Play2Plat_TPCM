package com.example.play2plat_tpcm.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.play2plat_tpcm.room.entities.Game

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGame(game: Game)

    @Update
    suspend fun updateGame(game: Game)

    @Query("SELECT * FROM games WHERE userId = :userId")
    fun getGamesByUserId(userId: Int): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE userId = :userId AND state = :state")
    fun getGamesByStateUserId(userId: Int, state: String): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE userId = :userId AND isFavorite = :isFavorite")
    fun getGamesByIsFavoriteUserId(userId: Int, isFavorite: Boolean): LiveData<List<Game>>

    @Query("SELECT * FROM games ORDER BY id DESC")
    fun readAllGames() : LiveData<List<Game>>

    @Delete
    suspend fun deleteGame(game: Game)
}