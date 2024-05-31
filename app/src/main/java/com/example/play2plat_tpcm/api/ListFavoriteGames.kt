package com.example.play2plat_tpcm.api

data class ListFavoriteGames (
    val userId: Int,
    val gameId: Int,
    val game: GameFavorite
)