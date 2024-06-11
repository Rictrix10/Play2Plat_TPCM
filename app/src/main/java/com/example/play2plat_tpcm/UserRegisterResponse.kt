package com.example.play2plat_tpcm

data class UserRegisterResponse(
    val id : Int,
    val username: String,
    val email: String,
    val password: String,
    val avatar: String?,
    val isDeleted: Boolean,
    val userTypeId: Int
)
