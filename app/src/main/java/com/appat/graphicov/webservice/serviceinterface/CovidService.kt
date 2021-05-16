package com.appat.graphicov.webservice.serviceinterface

import com.appat.graphicov.models.responses.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface CovidService {
    @GET("countries")
    suspend fun getAllCountriesData(@QueryMap params: HashMap<String, Any>): AllCountriesDataResponse

    @GET("countries/{country}?strict=true")
    suspend fun getCountryData(@Path("country") country: String, @QueryMap params: HashMap<String, Any>): CountryDataResponse

    @GET("all")
    suspend fun getGlobalData(@QueryMap params: HashMap<String, Any>): GlobalDataResponse

    @GET("historical/{country}")
    suspend fun getCountryHistory(@Path("country") country: String, @Query("lastdays") lastdays: String): GetCountryHistoryResponse

    @GET("historical/all")
    suspend fun getGlobalHistory(@Query("lastdays") lastdays: String): GetGlobalHistoryResponse

    @GET("gov")
    suspend fun getGovntCountries(): Array<String>

    @GET("gov/{country}")
    suspend fun getGovntCountryData(@Path("country") country: String): GovntCountryDataResponse
}