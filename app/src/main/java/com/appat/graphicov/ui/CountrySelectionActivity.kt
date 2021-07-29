package com.appat.graphicov.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appat.graphicov.adapters.CountryPickerAdapter
import com.appat.graphicov.databinding.ActivityCountrySelectionBinding
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.doAsync
import com.appat.graphicov.utilities.extensions.afterTextChanged
import com.appat.graphicov.utilities.sharedpreferences.DataStoreUtility
import com.appat.graphicov.utilities.sharedpreferences.SharedPrefUtility
import com.appat.graphicov.utilities.uiThread
import com.appat.graphicov.viewmodel.AllCountriesDataViewModel
import com.appat.graphicov.viewmodel.ViewModelFactory
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Status
import com.appat.graphicov.webservice.service.WebService
import com.appat.graphicov.webservice.serviceinterface.CovidService
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CountrySelectionActivity : MonetCompatActivity() {

    private val TAG = "CountrySelectionActivity"

    private lateinit var allCountriesDataViewModel: AllCountriesDataViewModel
    private var allCountryDataResponse = ArrayList<CountryDataEntity>()
    private val api = Api(WebService().getCovidService(CovidService::class.java))
    private lateinit var adapter: CountryPickerAdapter

    private lateinit var binding: ActivityCountrySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            binding = ActivityCountrySelectionBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            adapter = CountryPickerAdapter(this@CountrySelectionActivity)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(
                this@CountrySelectionActivity,
                RecyclerView.VERTICAL,
                false
            )
            setViewModel()

            binding.confirmButton.setOnClickListener {
                val selectedCountry = adapter.selectedCountryCode
                if(selectedCountry.isNotEmpty())
                {
                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            DataStoreUtility(this@CountrySelectionActivity).saveSelectedCountry(selectedCountry)
                            SharedPrefUtility.saveSelectedCountry(selectedCountry)
                            finish()
                        }
                    }
                }
            }

            binding.searchView.afterTextChanged { searchString->
                if(searchString == "")
                {
                    adapter.setData(allCountryDataResponse)
                }
                else {
                    val filteredData = allCountryDataResponse.filter {
                        it.countryName!!.contains(searchString, ignoreCase = true)
                    }
                    adapter.setData(filteredData)
                }
            }
        }
    }

    private fun setViewModel()
    {
        val factory = ViewModelFactory(api)
        allCountriesDataViewModel = ViewModelProvider(this, factory).get(AllCountriesDataViewModel::class.java)
        getAllCountriesData()
    }

    private fun getAllCountriesData() {
        getDataFromRoomDb()
    }

    private fun getDataFromRoomDb()
    {
        allCountriesDataViewModel.getAllCountriesData().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.observe(this, { allCountriesData->
                            doAsync {
                                allCountryDataResponse.clear()
                                allCountryDataResponse.addAll(allCountriesData)
                                uiThread {
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

    fun showFab()
    {
        binding.confirmButton.isEnabled = true
//        val set = AnimatorSet()
//
//        val translateAnimation = ObjectAnimator.ofFloat(binding.confirmButton, "translationY",(-50f).px)
//        val alphaAnimator = ObjectAnimator.ofFloat(binding.confirmButton, "alpha", 1f)
//
//        set.playTogether(translateAnimation, alphaAnimator)
//        set.duration = 500
//        set.start()
    }
}