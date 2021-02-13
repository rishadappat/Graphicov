package com.appat.graphicov.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appat.graphicov.repository.CountryHistoryRepository
import com.appat.graphicov.repository.WebServiceRepository
import com.appat.graphicov.roomdb.entities.CountryHistoryEntity
import com.appat.graphicov.webservice.api.Api
import kotlinx.coroutines.launch

class CountryHistoryViewModel(): ViewModel() {
    fun getCountryHistoryFromWeb(country: String, lastdays: String, api: Api){
        viewModelScope.launch {
            WebServiceRepository(api).getCountryHistory(country, lastdays)
        }
    }

    fun getAllCountryCasesHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        CountryHistoryRepository().getAllCountryCasesHistory(successListener)
    }

    fun getAllCountryDeathHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        CountryHistoryRepository().getAllCountryDeathHistory(successListener)
    }

    fun getAllCountryRecoveredHistory(successListener: (LiveData<List<CountryHistoryEntity>>)->Unit) {
        CountryHistoryRepository().getAllCountryRecoveredHistory(successListener)
    }
}