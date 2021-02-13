package com.appat.graphicov.webservice.api

import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.webservice.serviceinterface.CovidService

class Api(private val covidService: CovidService) {

    suspend fun getAllCountriesData(params: GeneralDataRequest) = covidService.getAllCountriesData(params.toMap())

    suspend fun getCountryData(params: GeneralDataRequest, country: String) = covidService.getCountryData(country, params.toMap())

    suspend fun getGlobalData(params: GeneralDataRequest) = covidService.getGlobalData(params.toMap())

    suspend fun getCountryHistory(country: String, lastdays: String) = covidService.getCountryHistory(country, lastdays)

    suspend fun getGlobalHistory(lastdays: String) = covidService.getGlobalHistory(lastdays)
}