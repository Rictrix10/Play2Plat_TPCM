package com.example.play2plat_tpcm.api

data class PatchComment(
    val comments: String,
    val userId: Int,
    val gameId: Int,
    val latitude: Double?,
    val longitude: Double?,
    val location: String?
)