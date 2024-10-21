package com.example.play2plat_tpcm.api

data class UserDetails(
    val id: Int,
    val username: String,
    val avatar: String?,
    val isDeleted: Boolean
)
