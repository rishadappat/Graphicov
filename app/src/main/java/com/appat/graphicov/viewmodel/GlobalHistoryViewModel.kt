package com.appat.graphicov.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appat.graphicov.repository.GlobalHistoryRepository
import com.appat.graphicov.repository.WebServiceRepository
import com.appat.graphicov.roomdb.entities.GlobalHistoryEntity
import com.appat.graphicov.webservice.api.Api
import kotlinx.coroutines.launch

class GlobalHistoryViewModel: ViewModel() {
    fun getGlobalHistoryFromWeb(lastdays: String, api: Api){
        viewModelScope.launch {
            WebServiceRepository(api).getGlobalHistory(lastdays = lastdays)
        }
    }

    fun getGlobalHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        GlobalHistoryRepository().getAllGlobalHistory(successListener)
    }

    fun getAllGlobalCasesHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        GlobalHistoryRepository().getAllGlobalCasesHistory(successListener)
    }

    fun getAllGlobalDeathHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        GlobalHistoryRepository().getAllGlobalDeathHistory(successListener)
    }

    fun getAllGlobalRecoveredHistory(successListener: (LiveData<List<GlobalHistoryEntity>>)->Unit) {
        GlobalHistoryRepository().getAllGlobalRecoveredHistory(successListener)
    }
}