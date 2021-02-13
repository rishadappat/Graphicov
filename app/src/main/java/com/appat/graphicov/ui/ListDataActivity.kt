package com.appat.graphicov.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appat.graphicov.adapters.ListDataAdapter
import com.appat.graphicov.databinding.ActivityListDataBinding
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.doAsync
import com.appat.graphicov.utilities.uiThread
import com.appat.graphicov.viewmodel.AllCountriesDataViewModel
import com.appat.graphicov.viewmodel.GlobalDataViewModel
import com.appat.graphicov.viewmodel.ViewModelFactory
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Status
import com.appat.graphicov.webservice.service.WebService
import com.appat.graphicov.webservice.serviceinterface.CovidService

class ListDataActivity : AppCompatActivity() {

    private val TAG = "ListDataActivity"

    private lateinit var allCountriesDataViewModel: AllCountriesDataViewModel
    private lateinit var globalDataViewModel: GlobalDataViewModel

    private var allCountryDataResponse = ArrayList<CountryDataEntity>()

    private val api = Api(WebService().getCovidService(CovidService::class.java))

    private lateinit var adapter: ListDataAdapter

    private lateinit var binding: ActivityListDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = ListDataAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this@ListDataActivity,
            RecyclerView.VERTICAL,
            false
        )
        setViewModel()
    }

    private fun setViewModel()
    {
        val factory = ViewModelFactory(api)
        allCountriesDataViewModel = ViewModelProvider(this, factory).get(AllCountriesDataViewModel::class.java)
        globalDataViewModel = ViewModelProvider(this, factory).get(GlobalDataViewModel::class.java)
        getAllCountriesData()
        callGetGlobalDataService()
    }

    private fun getAllCountriesData() {
        getDataFromRoomDb()
    }

    private fun getDataFromRoomDb()
    {
        allCountriesDataViewModel.getAllCountriesDataByCases().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.observe(this, { allCountriesData->
                            doAsync {
                                allCountryDataResponse.clear()
                                allCountryDataResponse.addAll(allCountriesData)
                                uiThread {
                                    binding.recyclerView.adapter = adapter
                                    adapter.setData(allCountryDataResponse)
                                }
                            }
                        })
                    }
                    Status.ERROR -> {
                        runOnUiThread {
                            Log.e(TAG, it.message.toString())
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private fun callGetGlobalDataService()
    {
        val request = GeneralDataRequest("", twoDaysAgo = false, yesterday = false)
        globalDataViewModel.getGlobalData(request).observe(this, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { globalData ->
                            adapter.setGlobalData(globalData)
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }
}