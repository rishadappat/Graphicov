package com.appat.graphicov.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appat.graphicov.roomdb.entities.GlobalHistoryEntity

@Dao
interface GlobalHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertAll(historyEntityData: List<GlobalHistoryEntity>)

    @Query("SELECT * FROM GlobalHistory order by date")
    fun getAll(): LiveData<List<GlobalHistoryEntity>>

    @Query("DELETE FROM GlobalHistory")
    suspend fun deleteAll()

    @Query("SELECT * FROM GlobalHistory where status = :status order by date")
    fun getAllCases(status: String): LiveData<List<GlobalHistoryEntity>>
}