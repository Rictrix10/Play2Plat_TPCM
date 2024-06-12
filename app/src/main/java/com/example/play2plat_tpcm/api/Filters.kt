package com.example.play2plat_tpcm.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filters(
    val genres: List<String>? = null,
    val platforms: List<String>? = null,
    val companies: List<String>? = null,
    val sequences: List<String>? = null,
    val free: Boolean?,
    val isAscending: Boolean,
    val orderType: String
) : Parcelable
