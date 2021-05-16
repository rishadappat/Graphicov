package com.appat.graphicov.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.repository.WebServiceRepository
import com.appat.graphicov.webservice.service.Resource
import kotlinx.coroutines.Dispatchers

class GlobalDataViewModel(private val webServiceRepository: WebServiceRepository) : ViewModel() {
    fun getGlobalData(params: GeneralDataRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = webServiceRepository.getGlobalData(params)
            if(response == null)
            {
                emit(Resource.error(data = null, message = "Error Occurred!"))
            }
            else {
                emit(Resource.success(data = response))
            }
        } catch (exception: Exception) {
            Log.e("getGlobalData", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}