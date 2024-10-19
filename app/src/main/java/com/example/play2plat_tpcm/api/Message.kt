package com.example.play2plat_tpcm.api

data class Message(
    val id: Int,
    val message: String,
    val image: String?,
    val isAnswer: Boolean?,
    val userOneId: Int,
    val userTwoId: Int,
    val date: String?

)