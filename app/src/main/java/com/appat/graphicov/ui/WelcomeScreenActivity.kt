package com.appat.graphicov.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.appat.graphicov.databinding.ActivityWelcomeScreenBinding
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.utilities.MonetColors
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.utilities.base.GraphicovActivity
import com.appat.graphicov.utilities.extensions.isDarkThemeOn
import com.appat.graphicov.utilities.sharedpreferences.SharedPrefUtility
import com.appat.graphicov.viewmodel.AllCountriesDataViewModel
import com.appat.graphicov.viewmodel.ViewModelFactory
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Status
import com.appat.graphicov.webservice.service.WebService
import com.appat.graphicov.webservice.serviceinterface.CovidService
import com.bumptech.glide.Glide
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.kieronquinn.monetcompat.core.MonetCompat

class WelcomeScreenActivity : GraphicovActivity<ActivityWelcomeScreenBinding>(ActivityWelcomeScreenBinding::inflate) {

    private lateinit var allCountriesDataViewModel: AllCountriesDataViewModel

    private val api = Api(WebService().getCovidService(CovidService::class.java))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()

            Log.d("CurrentCountry", Utility.getCurrentCountryISO(this@WelcomeScreenActivity))
            setViewModel()
            binding.changeButton.setOnClickListener {
                gotoCountrySelection()
            }
            binding.proceedButton.setOnClickListener {
                gotoDashboard()
            }
            binding.selectCountryButton.setOnClickListener {
                gotoCountrySelection()
            }
        }
    }

    private fun setViewModel()
    {
        val factory = ViewModelFactory(api)
        allCountriesDataViewModel =
            ViewModelProvider(this, factory).get(AllCountriesDataViewModel::class.java)
        getAllCountriesData()
    }

    private fun getAllCountriesData() {
        val request = GeneralDataRequest("active", twoDaysAgo = false, yesterday = false)
        allCountriesDataViewModel.getAllCountriesDataWeb(request, api).observe(this, { resource ->
            resource.let { resourceData ->
                when (resourceData.status) {
                    Status.SUCCESS -> {
                        getCurrentCountry()
                    }
                    Status.LOADING -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                        binding.selectCountryContainer.visibility = View.GONE
                        binding.countrySelectorView.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        binding.progressIndicator.visibility = View.GONE
                        binding.selectCountryContainer.visibility = View.VISIBLE
                        binding.countrySelectorView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun getCurrentCountry()
    {
        SharedPrefUtility.getSelectedCountry().observe(this, {
            Log.d("SplashActivity", it.toString())
            if (!it.isNullOrEmpty()) {
                allCountriesDataViewModel.getCountryDataByISO(it).observe(this@WelcomeScreenActivity, { resource ->
                    resource.let { resourceData ->
                        when (resourceData.status) {
                            Status.SUCCESS -> {
                                resourceData.data?.observe(
                                    this@WelcomeScreenActivity,
                                    { country ->
                                        if (country != null) {
                                            binding.progressIndicator.visibility = View.GONE
                                            binding.selectCountryContainer.visibility = View.GONE
                                            binding.countrySelectorView.visibility = View.VISIBLE
                                            binding.proceedButton.isEnabled = true
                                            binding.countryName.text = country.country
                                            Glide.with(this@WelcomeScreenActivity)
                                                .load(country.flag)
                                                .into(binding.flagImage)
                                        }
                                        else {
                                            binding.progressIndicator.visibility = View.GONE
                                            binding.selectCountryContainer.visibility = View.VISIBLE
                                            binding.countrySelectorView.visibility = View.GONE
                                        }
                                    })
                            }
                            Status.LOADING -> {
                                binding.progressIndicator.visibility = View.VISIBLE
                                binding.selectCountryContainer.visibility = View.GONE
                                binding.countrySelectorView.visibility = View.GONE
                            }
                            Status.ERROR -> {
                                binding.progressIndicator.visibility = View.GONE
                                binding.selectCountryContainer.visibility = View.VISIBLE
                                binding.countrySelectorView.visibility = View.GONE
                            }
                        }
                    }
                })
            }
            else {
                binding.progressIndicator.visibility = View.GONE
                binding.selectCountryContainer.visibility = View.VISIBLE
                binding.countrySelectorView.visibility = View.GONE
            }
        })
    }

    private fun gotoDashboard()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun gotoCountrySelection()
    {
        val intent = Intent(this, CountrySelectionActivity::class.java)
        startActivity(intent)
    }
}