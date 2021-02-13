package com.appat.graphicov.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appat.graphicov.roomdb.entities.CountryDataEntity

@Dao
interface AllCountriesDataDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertAll(allCountriesData: List<CountryDataEntity>)

    @Query("SELECT * FROM CountryDataEntity WHERE countryId = :countryId")
    fun getCountryDataByID(countryId: Int): LiveData<CountryDataEntity>

    @Query("SELECT * FROM CountryDataEntity WHERE iso2 = :iso")
    fun getCountryDataByISO(iso: String): LiveData<CountryDataEntity>

    @Query("SELECT * FROM CountryDataEntity order by country")
    fun getAll(): LiveData<List<CountryDataEntity>>

    @Query("SELECT * FROM CountryDataEntity order by cases DESC")
    fun getAllByCases(): LiveData<List<CountryDataEntity>>

    @Query("DELETE FROM CountryDataEntity")
    fun deleteAll()
}