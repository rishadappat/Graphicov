package com.appat.graphicov.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.appat.graphicov.BuildConfig
import com.appat.graphicov.R
import com.appat.graphicov.databinding.ActivitySettingsBinding
import com.appat.graphicov.databinding.BottomSheetPersistentBinding
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.utilities.sharedpreferences.SharedPrefUtility
import com.appat.graphicov.viewmodel.AllCountriesDataViewModel
import com.appat.graphicov.viewmodel.ViewModelFactory
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Status
import com.appat.graphicov.webservice.service.WebService
import com.appat.graphicov.webservice.serviceinterface.CovidService
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*

import com.google.firebase.analytics.FirebaseAnalytics

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var allCountriesDataViewModel: AllCountriesDataViewModel

    private val api = Api(WebService().getCovidService(CovidService::class.java))

    private var statusBarHeight: Int = 0

    private var bottomsheet: BottomSheetPersistentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolBar)
        setViewModel()

        bottomsheet = binding.aboutBottomSheet

        bottomsheet?.packageNameTextView?.text = BuildConfig.APPLICATION_ID
        bottomsheet?.versionTextView?.text = BuildConfig.VERSION_NAME

        bottomSheetBehavior = from(bottomsheet!!.root)
        bottomsheet?.openButton?.setOnClickListener {
            if(bottomSheetBehavior.state == STATE_COLLAPSED) {
                bottomSheetBehavior.state = STATE_EXPANDED
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomsheet!!.aboutBottomSheetRoot) { _, insets ->
            statusBarHeight = insets.systemWindowInsetBottom
            insets
        }

        binding.changeButton.setOnClickListener {
            gotoCountrySelection()
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        bottomsheet?.linkedInButton?.setOnClickListener {
            Utility.openUrlInCustomTab(getString(R.string.linked_in_url), this)
        }

        bottomsheet?.gitHubButton?.setOnClickListener {
            Utility.openUrlInCustomTab(getString(R.string.gitHub_url), this)
        }

        bottomsheet?.stackOverFlowButton?.setOnClickListener {
            Utility.openUrlInCustomTab(getString(R.string.stackOverFlow_url), this)
        }

        bottomsheet?.emailButton?.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:" + getString(R.string.email_address))
            }
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
        }

        bottomsheet?.rateButton?.setOnClickListener {
            Utility.openInAppReview(this)
        }

        bottomsheet?.shareButton?.setOnClickListener {
            Utility.openShare(this)
        }

        SharedPrefUtility.getAnalytics().observe(this, {
            bottomsheet?.analyticsSwitch?.isChecked = it
        })

        bottomsheet?.analyticsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefUtility.saveAnalytics(isChecked)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(isChecked)
        }

        bottomsheet?.apiSourceButton?.setOnClickListener {
            Utility.openUrlInCustomTab(getString(R.string.apiSourceUrl), this)
        }

        bottomsheet?.openInStore?.setOnClickListener {
            Utility.openPlayStoreUrl(this)
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback()
    {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if(bottomSheetBehavior.state == STATE_HALF_EXPANDED) {
                bottomSheetBehavior.state = STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
//            //change translationY and padding of container
//            val totalPadding = statusBarHeight + 20.px
//            bottomsheet!!.aboutBottomSheetRoot.setPadding(0, (slideOffset * totalPadding).toInt(), 0, 0)
        }
    }

    override fun onBackPressed() {
        if(bottomSheetBehavior.state == STATE_COLLAPSED) {
            super.onBackPressed()
        }
        else {
            bottomSheetBehavior.state = STATE_COLLAPSED
        }
    }

    private fun setViewModel()
    {
        val factory = ViewModelFactory(api)
        allCountriesDataViewModel = ViewModelProvider(this, factory).get(AllCountriesDataViewModel::class.java)
        getCurrentCountry()
    }

    private fun getCurrentCountry()
    {
        SharedPrefUtility.getSelectedCountry().observe(this, {
            Log.d("SplashActivity", it.toString())
            if (it.isNullOrEmpty()) {
                SharedPrefUtility.saveSelectedCountry(Utility.getCurrentCountryISO(this@SettingsActivity))
            } else {
                allCountriesDataViewModel.getCountryDataByISO(it).observe(this@SettingsActivity, { resource ->
                    resource.let { resourceData ->
                        when (resourceData.status) {
                            Status.SUCCESS -> {
                                resourceData.data?.observe(
                                    this@SettingsActivity,
                                    { country ->
                                        if (country != null) {
                                            binding.countryName.text = country.country
                                            Glide.with(this@SettingsActivity)
                                                .load(country.flag)
                                                .into(binding.flagImage)
                                        }
                                    })
                            }
                            else -> {

                            }
                        }
                    }
                })
            }
        })
    }

    private fun gotoCountrySelection()
    {
        val intent = Intent(this, CountrySelectionActivity::class.java)
        startActivity(intent)
    }
}