package com.example.play2plat_tpcm.api

data class PatchMessage (
    val message: String,
    val userOneId: Int,
    val userTwoId: Int,
    val date: String?
)