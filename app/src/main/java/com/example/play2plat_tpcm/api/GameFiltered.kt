package com.example.play2plat_tpcm.api

data class GameFiltered(
    val id: Int,
    val name: String,
    val description: String,
    val isFree: Boolean,
    val releaseDate: String,
    val pegiInfo: Int,
    val coverImage: String,
    val isDeleted: Boolean,
    val averageStars: Float,
    val companyId: Int,
    val company: CompanyFiltered,
    val sequence: SequenceFiltered?,
    val genres: List<String>,
    val platforms: List<String>,
    val avaliations: List<AvaliationFiltered>
)






