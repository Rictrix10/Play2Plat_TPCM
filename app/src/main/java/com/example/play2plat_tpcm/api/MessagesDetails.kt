package com.example.play2plat_tpcm.api

data class MessagesDetails(
    val id: Int,
    val message: String,
    val image: String?,
    val isAnswer: Int?,
    val userOneId: Int,
    val userTwoId: Int,
    val date: String?,
    val userOne: UserDetails,
    val userTwo: UserDetails

)