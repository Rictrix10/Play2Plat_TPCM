package com.example.play2plat_tpcm.api

import GeoNamesService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoNamesServiceBuilder {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://api.geonames.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(GeoNamesService::class.java)
}