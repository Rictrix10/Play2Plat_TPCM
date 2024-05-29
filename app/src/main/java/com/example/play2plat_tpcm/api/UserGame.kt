package com.example.play2plat_tpcm.api

data class UserGame (
    val userId: Int,
    val gameId: Int,
    val state: String
)