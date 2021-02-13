package com.appat.graphicov.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appat.graphicov.roomdb.entities.CountryHistoryEntity

@Dao
interface CountryHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertAll(historyEntityData: List<CountryHistoryEntity>)

    @Query("SELECT * FROM CountryHistory order by date")
    fun getAll(): LiveData<List<CountryHistoryEntity>>

    @Query("DELETE FROM CountryHistory")
    suspend fun deleteAll()

    @Query("SELECT * FROM CountryHistory where status = :status order by date")
    fun getAllCases(status: String): LiveData<List<CountryHistoryEntity>>
}