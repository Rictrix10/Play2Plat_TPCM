package com.example.play2plat_tpcm.api

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val avatar: String?,
    val userTypeId: Int,
    val platforms: List<String>?
)
