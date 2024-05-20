package com.example.play2plat_tpcm.api

data class Game(
    val id: Int?,
    val name: String,
    val description: String,
    val isFree: Boolean,
    val releaseDate: String,
    val pegiInfo: Int,
    val coverImage: String,
    val sequenceId: Int,
    val companyId: Int
)
