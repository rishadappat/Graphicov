package com.appat.graphicov.repository

import androidx.lifecycle.LiveData
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.Utility

class AllCountriesDataRepository {

    fun getAllCountriesData() = Utility.getDatabase().allCountriesDataDao().getAll()

    fun getAllCountriesDataByCases() = Utility.getDatabase().allCountriesDataDao().getAllByCases()

    suspend fun insertAllCountriesData(allCountriesData: List<CountryDataEntity>) = Utility.getDatabase().allCountriesDataDao().insertAll(allCountriesData)

    fun getCountryDataByID(countryId: Int): LiveData<CountryDataEntity> = Utility.getDatabase().allCountriesDataDao().getCountryDataByID(countryId)

    fun getCountryDataByISO(iso: String): LiveData<CountryDataEntity> = Utility.getDatabase().allCountriesDataDao().getCountryDataByISO(iso)

    fun deleteAll() = Utility.getDatabase().allCountriesDataDao().deleteAll()
}