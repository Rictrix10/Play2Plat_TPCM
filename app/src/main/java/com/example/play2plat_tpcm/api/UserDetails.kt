package com.example.play2plat_tpcm.api

data class UserDetails(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val avatar: String?,
    val userTypeId: Int,
    val isDeleted: Boolean,
    val platforms: List<String>?
)
