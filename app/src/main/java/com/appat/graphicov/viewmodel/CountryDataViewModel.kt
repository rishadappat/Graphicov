package com.appat.graphicov.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.repository.WebServiceRepository
import com.appat.graphicov.webservice.service.Resource
import kotlinx.coroutines.Dispatchers

class CountryDataViewModel(private val webServiceRepository: WebServiceRepository) : ViewModel() {
    fun getCountryData(params: GeneralDataRequest, country: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            if(webServiceRepository.getCountryData(params, country) == null)
            {
                emit(Resource.error(data = null, message = "Error Occurred!"))
            }
            else {
                emit(Resource.success(data = webServiceRepository.getCountryData(params, country)))
            }
        } catch (exception: Exception) {
            Log.e("getCountryData", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}