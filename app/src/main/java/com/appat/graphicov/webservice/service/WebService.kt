package com.appat.graphicov.webservice.service

import com.appat.graphicov.R
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.webservice.serviceinterface.CovidService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WebService {
    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Utility.getString(R.string.baseUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getService(serviceClass: Class<Any>): Any {
        return initRetrofit().create(serviceClass)
    }

    fun getCovidService(serviceClass: Class<CovidService>): CovidService
    {
        return getService(serviceClass as Class<Any>) as CovidService
    }
}