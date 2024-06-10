package com.example.play2plat_tpcm.api

data class Filters(
    val genres: List<String>? = null,
    val platforms: List<String>? = null,
    val companies: List<String>? = null,
    val sequences: List<String>? = null,
    val free: Boolean? = null,
    val isAscending: Boolean? = null,
    val orderType: String? = null
)