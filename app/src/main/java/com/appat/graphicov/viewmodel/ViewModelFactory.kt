package com.appat.graphicov.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appat.graphicov.repository.AllCountriesDataRepository
import com.appat.graphicov.repository.WebServiceRepository
import com.appat.graphicov.webservice.api.Api

class ViewModelFactory(private val api: Api) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AllCountriesDataViewModel::class.java) -> {
                AllCountriesDataViewModel(AllCountriesDataRepository()) as T
            }
            modelClass.isAssignableFrom(GlobalDataViewModel::class.java) -> {
                GlobalDataViewModel(WebServiceRepository(api)) as T
            }
            modelClass.isAssignableFrom(CountryDataViewModel::class.java) -> {
                CountryDataViewModel(WebServiceRepository(api)) as T
            }
            modelClass.isAssignableFrom(CountryHistoryViewModel::class.java) -> {
                CountryHistoryViewModel() as T
            }
            modelClass.isAssignableFrom(GlobalHistoryViewModel::class.java) -> {
                GlobalHistoryViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}