package com.example.play2plat_tpcm.room.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "games")
class Game(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "idGame") val idGame: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "coverImage") val coverImage: String,
    @ColumnInfo(name = "state") val state: String?,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
    @ColumnInfo(name = "userId") val userId: Int,
) : Parcelable