package com.appat.graphicov.repository

import android.util.Log
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.models.responses.CountryDataResponse
import com.appat.graphicov.models.responses.GlobalDataResponse
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.roomdb.entities.CountryHistoryEntity
import com.appat.graphicov.roomdb.entities.GlobalHistoryEntity
import com.appat.graphicov.utilities.Constants
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.webservice.api.Api
import com.google.gson.Gson

class WebServiceRepository(private val api: Api) {

    suspend fun getAllCountriesData(params: GeneralDataRequest) {
        try {
            val allCountriesDataResponse = api.getAllCountriesData(params)
            val allCountriesDataArray = ArrayList<CountryDataEntity>()
            for (allCountriesData in allCountriesDataResponse) {
                val json = Gson().toJson(allCountriesData)
                val countriesData = Gson().fromJson(json, CountryDataEntity::class.java)
                countriesData.countryId = allCountriesData.countryInfo._id
                countriesData.longitude = allCountriesData.countryInfo.long
                countriesData.lat = allCountriesData.countryInfo.lat
                countriesData.countryName = allCountriesData.country
                countriesData.iso2 = allCountriesData.countryInfo.iso2
                countriesData.iso3 = allCountriesData.countryInfo.iso3
                countriesData.flag = allCountriesData.countryInfo.flag
                allCountriesDataArray.add(countriesData)
            }
            AllCountriesDataRepository().deleteAll()
            AllCountriesDataRepository().insertAllCountriesData(allCountriesDataArray)
            Log.d("getAllCountriesData", allCountriesDataArray.toString())
        } catch (e: Exception)
        {
            Log.e("getAllCountriesData", e.message.toString())
            Log.e("getAllCountriesData", e.toString())
        }
    }

    suspend fun getCountryData(params: GeneralDataRequest, country: String): CountryDataResponse? {
        return try {
            api.getCountryData(params, country)
        } catch (e: Exception)
        {
            Log.e("getCountryData", e.toString())
            return null
        }
    }

    suspend fun getGlobalData(params: GeneralDataRequest): GlobalDataResponse? {
        return try {
            api.getGlobalData(params)
        }catch (e: Exception)
        {
            Log.e("getGlobalData", e.toString())
            null
        }
    }

    suspend fun getCountryHistory(country: String, lastdays: String) {
        try {
            val countryHistory = api.getCountryHistory(country, lastdays)
            val allCountryHistory = ArrayList<CountryHistoryEntity>()

            for (key in countryHistory.timeline.cases.keys) {
                val caseObject = countryHistory.timeline.cases[key]
                val countryHistoryData = CountryHistoryEntity(
                    null,
                    "cases",
                    Utility.fromDateString(key, Constants.Mddyy),
                    caseObject,
                    countryHistory.country
                )
                allCountryHistory.add(countryHistoryData)
            }

            for (key in countryHistory.timeline.deaths.keys) {
                val deathObject = countryHistory.timeline.deaths[key]
                val countryHistoryData = CountryHistoryEntity(
                    null,
                    "deaths",
                    Utility.fromDateString(key, Constants.Mddyy),
                    deathObject,
                    countryHistory.country
                )
                allCountryHistory.add(countryHistoryData)
            }

            for (key in countryHistory.timeline.recovered.keys) {
                val recoveredObject = countryHistory.timeline.recovered[key]
                val countryHistoryData = CountryHistoryEntity(
                    null,
                    "recovered",
                    Utility.fromDateString(key, Constants.Mddyy),
                    recoveredObject,
                    countryHistory.country
                )
                allCountryHistory.add(countryHistoryData)
            }
            CountryHistoryRepository().deleteAll()
            CountryHistoryRepository().insertAllCountryHistory(allCountryHistory)
        }catch (e: Exception)
        {
            Log.e("getCountryHistory", e.toString())
        }
    }

    suspend fun getGlobalHistory(lastdays: String) {
        try {
            val globalHistory = api.getGlobalHistory(lastdays)
            val allGlobalHistory = ArrayList<GlobalHistoryEntity>()

            for (key in globalHistory.cases.keys) {
                val caseObject = globalHistory.cases[key]
                val globalHistoryData = GlobalHistoryEntity(
                    null,
                    "cases",
                    Utility.fromDateString(key, Constants.Mddyy),
                    caseObject
                )
                allGlobalHistory.add(globalHistoryData)
            }

            for (key in globalHistory.deaths.keys) {
                val deathObject = globalHistory.deaths[key]
                val globalHistoryData = GlobalHistoryEntity(
                    null,
                    "deaths",
                    Utility.fromDateString(key, Constants.Mddyy),
                    deathObject
                )
                allGlobalHistory.add(globalHistoryData)
            }

            for (key in globalHistory.recovered.keys) {
                val recoveredObject = globalHistory.recovered[key]
                val globalHistoryData = GlobalHistoryEntity(
                    null,
                    "recovered",
                    Utility.fromDateString(key, Constants.Mddyy),
                    recoveredObject
                )
                allGlobalHistory.add(globalHistoryData)
            }
            Log.e("getGlobalHistory", "Saving  DB GlobalHistoryRepository" + allGlobalHistory.size)
            GlobalHistoryRepository().deleteAll()
            GlobalHistoryRepository().insertAllGlobalHistory(allGlobalHistory)
        }catch (e: Exception)
        {
            Log.e("getGlobalHistory", e.toString())
        }
    }
}