package com.example.play2plat_tpcm.api

data class Comment(
    val comments: String,
    val image: String?,
    val isAnswer: Int?,
    val userId: Int,
    val gameId: Int,
    val latitude: Double?,
    val longitude: Double?,
    val location: String?
)