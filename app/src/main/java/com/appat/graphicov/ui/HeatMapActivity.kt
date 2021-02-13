package com.appat.graphicov.ui

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.appat.graphicov.R
import com.appat.graphicov.databinding.ActivityHeatMapBinding
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.utilities.doAsync
import com.appat.graphicov.utilities.uiThread
import com.appat.graphicov.viewmodel.AllCountriesDataViewModel
import com.appat.graphicov.viewmodel.ViewModelFactory
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Status
import com.appat.graphicov.webservice.service.WebService
import com.appat.graphicov.webservice.serviceinterface.CovidService
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.CircleOptions
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MapStyleOptions

class HeatMapActivity : AppCompatActivity(), OnMapReadyCallback {

    val TAG = "HeatMapActivity"

    private var allCountryDataResponse = ArrayList<CountryDataEntity>()

    private lateinit var allCountriesDataViewModel: AllCountriesDataViewModel

    private var googleMap: GoogleMap? = null

    val api = Api(WebService().getCovidService(CovidService::class.java))

    private lateinit var binding: ActivityHeatMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeatMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initMap()
        setupViewModel()

        binding.statusSelectionGroup.addOnButtonCheckedListener { _, _, _ ->
            addDataToGoogleMap()
        }
    }

    private fun initMap()
    {
        val mapFragment : SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            this.googleMap = map
            val success: Boolean = map!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
            getAllCountriesData(false)
            this.googleMap?.setOnCircleClickListener {
                Log.d("CircleClicked", it.tag.toString())
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(api)
        allCountriesDataViewModel = ViewModelProvider(this, factory).get(AllCountriesDataViewModel::class.java)
    }

    private fun getAllCountriesData(yesterday: Boolean) {
        allCountriesDataViewModel.getAllCountriesData().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.observe(this, { allCountriesData ->
                            doAsync {
                                allCountryDataResponse.clear()
                                allCountryDataResponse.addAll(allCountriesData)
                                uiThread {
                                    addDataToGoogleMap()
                                }
                            }
                        })
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

    private fun addDataToGoogleMap()
    {
        googleMap?.clear()
        for (countryData in allCountryDataResponse.iterator())
        {
            addCircle(countryData)
        }
    }

    private fun addCircle(allCountriesDataResponseItem: CountryDataEntity)
    {
        drawCircleOnMap(
            allCountriesDataResponseItem = allCountriesDataResponseItem
        )
    }

    private fun drawCircleOnMap(allCountriesDataResponseItem: CountryDataEntity)
    {
        var strokeColor = Utility.getColor(R.color.transparent)
        var fillColor = Utility.getColor(R.color.transparent)
        var radius = 0L
        when (binding.statusSelectionGroup.checkedButtonId) {
            R.id.activeButton -> {
                strokeColor = Utility.getColor(R.color.active)
                fillColor = Utility.getColor(R.color.activeDark)
                radius = allCountriesDataResponseItem.active!! / 2
            }
            R.id.effectedButton -> {
                strokeColor = Utility.getColor(R.color.affected)
                fillColor = Utility.getColor(R.color.affectedDark)
                radius = allCountriesDataResponseItem.cases!! / 6
            }
            R.id.deathButton -> {
                strokeColor = Utility.getColor(R.color.death)
                fillColor = Utility.getColor(R.color.deathDark)
                radius = allCountriesDataResponseItem.deaths!! * 2
            }
            R.id.recoveredButton -> {
                strokeColor = Utility.getColor(R.color.recovered)
                fillColor = Utility.getColor(R.color.recoveredDark)
                radius = allCountriesDataResponseItem.recovered!! / 6
            }
        }
        try {
            val latLong = LatLng(
                allCountriesDataResponseItem.lat ?: 0.toDouble(),
                allCountriesDataResponseItem.longitude ?: 0.toDouble()
            )
            val circleOptions = CircleOptions()
                .center(latLong)
                .radius(radius.toDouble())
                .strokeColor(strokeColor)
                .strokeWidth(5f)
                .fillColor(fillColor)
                .clickable(true)
            val circle = googleMap!!.addCircle(circleOptions)
            circle.tag = allCountriesDataResponseItem.countryId
        } catch (e: Exception)
        {
            Log.d("Error", e.localizedMessage ?: "")
            Log.d("Values", allCountriesDataResponseItem.toString())
        }

    }
}