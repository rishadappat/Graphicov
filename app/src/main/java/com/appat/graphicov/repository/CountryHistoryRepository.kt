package com.appat.graphicov.repository

import androidx.lifecycle.LiveData
import com.appat.graphicov.roomdb.entities.CountryHistoryEntity
import com.appat.graphicov.utilities.Utility

class CountryHistoryRepository {
    fun getAllCountryHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().countryHistoryDao().getAll())
    }

    fun getAllCountryCasesHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().countryHistoryDao().getAllCases("cases"))
    }

    fun getAllCountryDeathHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().countryHistoryDao().getAllCases("deaths"))
    }

    fun getAllCountryRecoveredHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().countryHistoryDao().getAllCases("recovered"))
    }

    suspend fun insertAllCountryHistory(allCountryHistoryEntity: List<CountryHistoryEntity>) = Utility.getDatabase().countryHistoryDao().insertAll(allCountryHistoryEntity)

    suspend fun deleteAll() = Utility.getDatabase().countryHistoryDao().deleteAll()
}