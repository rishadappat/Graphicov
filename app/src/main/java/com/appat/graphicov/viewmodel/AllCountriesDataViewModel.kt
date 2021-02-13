package com.appat.graphicov.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.repository.AllCountriesDataRepository
import com.appat.graphicov.repository.WebServiceRepository
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Resource
import kotlinx.coroutines.Dispatchers

class AllCountriesDataViewModel (private val allCountriesDataRepository: AllCountriesDataRepository) : ViewModel() {
    fun getAllCountriesData() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = allCountriesDataRepository.getAllCountriesData()))
        } catch (exception: Exception) {
            Log.e("getAllCountriesData", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAllCountriesDataByCases() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = allCountriesDataRepository.getAllCountriesDataByCases()))
        } catch (exception: Exception) {
            Log.e("getAllCountriesData", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAllCountriesDataWeb(request: GeneralDataRequest, api: Api) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = WebServiceRepository(api).getAllCountriesData(request)))
        } catch (exception: Exception) {
            Log.e("getAllCountriesDataWeb", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getCountryDataByID(countryId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = allCountriesDataRepository.getCountryDataByID(countryId)))
        } catch (exception: Exception) {
            Log.e("getCountryDataByID", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getCountryDataByISO(iso: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = allCountriesDataRepository.getCountryDataByISO(iso)))
        } catch (exception: Exception) {
            Log.e("getCountryDataByISO", exception.message.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}