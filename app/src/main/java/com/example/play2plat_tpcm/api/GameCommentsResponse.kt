package com.example.play2plat_tpcm.api

data class GameCommentsResponse(
    val id: Int,
    val comments: String,
    val image: String,
    val isAnswer: Boolean?,
    val userId: Int,
    val gameId: Int,
    val latitude: Double,
    val longitude: Double,
    val user: UserComment,
    val game: GameComment
)