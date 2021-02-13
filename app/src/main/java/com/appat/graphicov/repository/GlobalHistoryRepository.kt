package com.appat.graphicov.repository

import androidx.lifecycle.LiveData
import com.appat.graphicov.roomdb.entities.GlobalHistoryEntity
import com.appat.graphicov.utilities.Utility

class GlobalHistoryRepository {

    fun getAllGlobalHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().globalHistoryDao().getAll())
    }

    fun getAllGlobalCasesHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().globalHistoryDao().getAllCases("cases"))
    }

    fun getAllGlobalDeathHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().globalHistoryDao().getAllCases("deaths"))
    }

    fun getAllGlobalRecoveredHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        successListener(Utility.getDatabase().globalHistoryDao().getAllCases("recovered"))
    }

    suspend fun insertAllGlobalHistory(allCountryHistoryEntity: List<GlobalHistoryEntity>) = Utility.getDatabase().globalHistoryDao().insertAll(allCountryHistoryEntity)

    suspend fun deleteAll() = Utility.getDatabase().globalHistoryDao().deleteAll()
}