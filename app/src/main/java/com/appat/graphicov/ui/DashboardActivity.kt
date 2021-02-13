package com.appat.graphicov.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.appat.graphicov.R
import com.appat.graphicov.databinding.ActivityDashboardBinding
import com.appat.graphicov.models.requests.GeneralDataRequest
import com.appat.graphicov.models.responses.CountryDataResponse
import com.appat.graphicov.models.responses.GlobalDataResponse
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.utilities.XAxisValueFormatter
import com.appat.graphicov.utilities.doAsync
import com.appat.graphicov.utilities.listeners.AppBarStateChangeListener
import com.appat.graphicov.utilities.sharedpreferences.SharedPrefUtility
import com.appat.graphicov.utilities.uiThread
import com.appat.graphicov.utilities.viewcomponents.CustomMarkerView
import com.appat.graphicov.viewmodel.*
import com.appat.graphicov.webservice.api.Api
import com.appat.graphicov.webservice.service.Status
import com.appat.graphicov.webservice.service.WebService
import com.appat.graphicov.webservice.serviceinterface.CovidService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.CircleOptions
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MapStyleOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout


class DashboardActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "DashboardActivity"

    private lateinit var allCountriesDataViewModel: AllCountriesDataViewModel
    private lateinit var globalDataViewModel: GlobalDataViewModel
    private lateinit var countryDataViewModel: CountryDataViewModel
    private lateinit var countryHistoryViewModel: CountryHistoryViewModel
    private lateinit var globalHistoryViewModel: GlobalHistoryViewModel

    private var currentState = AppBarStateChangeListener.State.EXPANDED
    private var googleMap: GoogleMap? = null

    private var todayGlobalDataResponse: GlobalDataResponse? = null

    private var totalCountryDataResponse: CountryDataResponse? = null

    private var yesterdayGlobalDataResponse: GlobalDataResponse? = null
    private var yesterdayCountryDataResponse: CountryDataResponse? = null

    private var allCountryDataResponse = ArrayList<CountryDataEntity>()

    private val api = Api(WebService().getCovidService(CovidService::class.java))

    private var selectedCountry: CountryDataEntity? = null

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolBar)
        initMap()
        initTabLayout()
        applyPadding()
        setupToggleButton()
        listenAppBarLayoutChange()
        binding.fullScreen.setOnClickListener {
            gotoHeatMapActivity()
        }
        binding.settings.setOnClickListener {
            gotoSettings()
        }
        binding.countryListFab.setOnClickListener {
            gotoCountryList()
        }
        setupViewModel()
        initializeHistoryChart()

//        DataStoreUtility(this).selectedCountryData.asLiveData().observe(this, {
//            Log.d("****************", "****************")
//            Log.d("CountryChanged", it)
//            Log.d("****************", "****************")
//        })
    }

    private fun initTabLayout()
    {
        binding.tabLayout.addOnTabSelectedListener(tabChangedListener)
    }

    private fun initMap()
    {
        val mapFragment : SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun applyPadding()
    {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            binding.scrollView.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
            insets
        }
    }

    private fun listenAppBarLayoutChange()
    {
        binding.appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                Log.d("STATE", state!!.name)
                currentState = state
                invalidateOptionsMenu()
            }
        })
    }

    private fun setupToggleButton()
    {
        binding.toggleButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            animateToggleButton(checkedId, isChecked)
        }
    }

    private fun gotoHeatMapActivity()
    {
        val intent = Intent(this, HeatMapActivity::class.java)
        startActivity(intent)
    }

//    private fun gotoCountrySelector()
//    {
//        val intent = Intent(this, CountrySelectionActivity::class.java)
//        startActivity(intent)
//    }

    private fun gotoSettings()
    {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun gotoCountryList()
    {
        val intent = Intent(this, ListDataActivity::class.java)
        startActivity(intent)
    }

    private fun animateToggleButton(checkedId: Int, isChecked: Boolean)
    {
        var translationXValue = 0f
        if(checkedId == R.id.global && isChecked)
        {
            animateMapCamera(LatLng(0.0, 0.0), 1f)
            translationXValue = binding.handle.width.toFloat()
            if(binding.tabLayout.selectedTabPosition == 0) {
                getGlobalData(null)
            }
            if(binding.tabLayout.selectedTabPosition == 1) {
                getGlobalData(yesterday = false)
            }
            if(binding.tabLayout.selectedTabPosition == 2) {
                getGlobalData(true)
            }
            getGlobalDeathHistory()
            getGlobalEffectedHistory()
            getGlobalRecoveredHistory()
        }
        else if(checkedId == R.id.myCountry && isChecked)
        {
            animateMapCamera(
                LatLng(selectedCountry?.lat ?: 0.0, selectedCountry?.longitude ?: 0.0),
                6f
            )
            translationXValue = 0f
            if(binding.tabLayout.selectedTabPosition == 0) {
                getCountryData(yesterday = false)
            }
            if(binding.tabLayout.selectedTabPosition == 1) {
                setTodayCountryData()
            }
            if(binding.tabLayout.selectedTabPosition == 2) {
                getCountryData(yesterday = true)
            }
            getDeathHistory()
            getEffectedHistory()
            getRecoveredHistory()
        }
        ObjectAnimator.ofFloat(binding.handle, "translationX", translationXValue).apply {
            duration = 300
            start()
        }
    }

    private val tabChangedListener = object: TabLayout.OnTabSelectedListener
    {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if(binding.toggleButtonGroup.checkedButtonId == R.id.global) {
                when (tab?.position) {
                    0 -> {
                        getGlobalData(null)
                    }
                    1 -> {
                        getGlobalData(false)
                    }
                    else -> {
                        getGlobalData(true)
                    }
                }
            }
            else
            {
                when (tab?.position) {
                    0 -> {
                        getCountryData(false)
                    }
                    1 -> {
                        setTodayCountryData()
                    }
                    else -> {
                        getCountryData(true)
                    }
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabReselected(tab: TabLayout.Tab?) {

        }
    }

    private fun animateMapCamera(latLng: LatLng, zoom: Float)
    {
        val location = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        googleMap?.animateCamera(location)
    }

    private fun resetData()
    {
        binding.tabLayout.getTabAt(0)?.select()
        binding.toggleButtonGroup.check(R.id.myCountry)

        todayGlobalDataResponse = null
        totalCountryDataResponse = null
        yesterdayGlobalDataResponse = null
        yesterdayCountryDataResponse = null
        allCountryDataResponse = ArrayList()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(api)
        allCountriesDataViewModel = ViewModelProvider(this, factory).get(AllCountriesDataViewModel::class.java)
        globalDataViewModel = ViewModelProvider(this, factory).get(GlobalDataViewModel::class.java)
        countryDataViewModel = ViewModelProvider(this, factory).get(CountryDataViewModel::class.java)
        countryHistoryViewModel = ViewModelProvider(this, factory).get(CountryHistoryViewModel::class.java)
        globalHistoryViewModel = ViewModelProvider(this, factory).get(GlobalHistoryViewModel::class.java)
        SharedPrefUtility.getSelectedCountry().observe(this, {
            allCountriesDataViewModel.getCountryDataByISO(it ?: "").observe(this, { resource ->
                resource.let { resourceData ->
                    when (resourceData.status) {
                        Status.SUCCESS -> {
                            resource.data?.observe(this, { selectCountryEntity ->
                                if (selectCountryEntity != null) {
                                    resetData()
                                    selectedCountry = selectCountryEntity
                                    animateMapCamera(
                                        LatLng(
                                            selectedCountry?.lat ?: 0.0,
                                            selectedCountry?.longitude ?: 0.0
                                        ), 6f
                                    )
                                    getCountryData(yesterday = false)
                                    getCountryHistoryData()
                                    getGlobalHistoryData()
                                    getDeathHistory()
                                    getEffectedHistory()
                                    getRecoveredHistory()
                                    val country = selectedCountry?.countryName
                                    binding.myCountry.text = country
//                                    Utility.getDrawableFromUrl(this, selectCountry.flag.toString()
//                                    ) { drawable ->
//                                        binding.myCountry.icon = drawable
//                                    }
                                }
                            })
                        }
                        Status.ERROR -> {
                            runOnUiThread {
                                Log.e(TAG, it.toString())
                                Toast.makeText(this, "Failed to load data", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            })
        })
    }

    private fun getAllCountriesData(yesterday: Boolean) {
        val request = GeneralDataRequest("active", twoDaysAgo = false, yesterday = yesterday)
        allCountriesDataViewModel.getAllCountriesDataWeb(request, api).observe(this, { resource ->
            resource.let {
            }
        })
        getAllCountriesData()
    }

    private fun getAllCountriesData() {
        allCountriesDataViewModel.getAllCountriesData().observe(this, {
            it.let { resource ->
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

    private fun getGlobalData(yesterday: Boolean?) {
        if(yesterday == null)
        {
            if(todayGlobalDataResponse != null) {
                setTotalGlobalData()
            }
            else {
                callGetGlobalDataService(yesterday)
            }
        }
        else if(yesterday)
        {
            if(yesterdayGlobalDataResponse != null) {
                setYesterdayGlobalData()
            }else {
                callGetGlobalDataService(yesterday)
            }
        }
        else if(todayGlobalDataResponse != null)
        {
            setTodayGlobalData()
        }
        else
        {
            callGetGlobalDataService(yesterday)
        }
    }

    private fun callGetGlobalDataService(yesterday: Boolean?)
    {
        val request = GeneralDataRequest("", twoDaysAgo = false, yesterday = yesterday ?: false)
        globalDataViewModel.getGlobalData(request).observe(this, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { globalData ->
                            when {
                                yesterday == null -> {
                                    todayGlobalDataResponse = globalData
                                    setTotalGlobalData()
                                }
                                yesterday -> {
                                    yesterdayGlobalDataResponse = globalData
                                    setYesterdayGlobalData()
                                }
                                else -> {
                                    todayGlobalDataResponse = globalData
                                    setTodayGlobalData()
                                }
                            }
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

    private fun getCountryData(yesterday: Boolean) {
        if(yesterday)
        {
            if(yesterdayCountryDataResponse != null) {
                setYesterdayCountryData()
                return
            }
        }
        else if(totalCountryDataResponse != null)
        {
            setTotalCountryData()
            return
        }
        callCountryDataService(yesterday)
    }

    private fun callCountryDataService(yesterday: Boolean)
    {
        val request = GeneralDataRequest("", twoDaysAgo = false, yesterday = yesterday)
        countryDataViewModel.getCountryData(request, selectedCountry?.iso2 ?: "").observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { countryData ->
                            Log.d("Response", countryData.toString())
                            if (yesterday) {
                                yesterdayCountryDataResponse = countryData
                                setYesterdayCountryData()
                            } else {
                                totalCountryDataResponse = countryData
                                setTotalCountryData()
                            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        if(currentState == AppBarStateChangeListener.State.COLLAPSED) {
            inflater.inflate(R.menu.dashboard_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.mapMenu)
        {
            gotoHeatMapActivity()
        }
        else if(item.itemId == R.id.settingsMenu) {
            gotoSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setTotalGlobalData()
    {
        binding.effectedTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.cases ?: 0
            )
        )
        binding.deathTextView.animateText(Utility.formatLong(todayGlobalDataResponse?.deaths ?: 0))
        binding.recoveredTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.recovered ?: 0
            )
        )
        binding.activeTextView.animateText(Utility.formatLong(todayGlobalDataResponse?.active ?: 0))
        binding.seriousTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.critical ?: 0
            )
        )
        val lastUpdatedString = String.format(
            Utility.getString(R.string.lastUpdated),
            Utility.getDateTime(todayGlobalDataResponse?.updated)
        )
        binding.lastUpdated.animateText(lastUpdatedString)
    }

    private fun setTotalCountryData()
    {
        binding.effectedTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.cases ?: 0
            )
        )
        binding.deathTextView.animateText(Utility.formatLong(totalCountryDataResponse?.deaths ?: 0))
        binding.recoveredTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.recovered ?: 0
            )
        )
        binding.activeTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.active ?: 0
            )
        )
        binding.seriousTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.critical ?: 0
            )
        )
        val lastUpdatedString = String.format(
            Utility.getString(R.string.lastUpdated),
            Utility.getDateTime(totalCountryDataResponse?.updated)
        )
        binding.lastUpdated.animateText(lastUpdatedString)
    }

    private fun setTodayGlobalData()
    {
        binding.effectedTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.todayCases ?: 0
            )
        )
        binding.deathTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.todayDeaths ?: 0
            )
        )
        binding.recoveredTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.todayRecovered ?: 0
            )
        )
        binding.activeTextView.animateText(Utility.formatLong(todayGlobalDataResponse?.active ?: 0))
        binding.seriousTextView.animateText(
            Utility.formatLong(
                todayGlobalDataResponse?.critical ?: 0
            )
        )
        val lastUpdatedString = String.format(
            Utility.getString(R.string.lastUpdated),
            Utility.getDateTime(todayGlobalDataResponse?.updated)
        )
        binding.lastUpdated.animateText(lastUpdatedString)
    }

    private fun setTodayCountryData()
    {
        binding.effectedTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.todayCases ?: 0
            )
        )
        binding.deathTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.todayDeaths ?: 0
            )
        )
        binding.recoveredTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.todayRecovered ?: 0
            )
        )
        binding.activeTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.active ?: 0
            )
        )
        binding.seriousTextView.animateText(
            Utility.formatLong(
                totalCountryDataResponse?.critical ?: 0
            )
        )
        val lastUpdatedString = String.format(
            Utility.getString(R.string.lastUpdated),
            Utility.getDateTime(totalCountryDataResponse?.updated)
        )
        binding.lastUpdated.animateText(lastUpdatedString)
    }

    private fun setYesterdayGlobalData()
    {
        binding.effectedTextView.animateText(
            Utility.formatLong(
                yesterdayGlobalDataResponse?.todayCases ?: 0
            )
        )
        binding.deathTextView.animateText(
            Utility.formatLong(
                yesterdayGlobalDataResponse?.todayDeaths ?: 0
            )
        )
        binding.recoveredTextView.animateText(
            Utility.formatLong(
                yesterdayGlobalDataResponse?.todayRecovered ?: 0
            )
        )
        binding.activeTextView.animateText(
            Utility.formatLong(
                yesterdayGlobalDataResponse?.active ?: 0
            )
        )
        binding.seriousTextView.animateText(
            Utility.formatLong(
                yesterdayGlobalDataResponse?.critical ?: 0
            )
        )
        val lastUpdatedString = String.format(
            Utility.getString(R.string.lastUpdated),
            Utility.getDateTime(yesterdayGlobalDataResponse?.updated)
        )
        binding.lastUpdated.animateText(lastUpdatedString)
    }

    private fun setYesterdayCountryData()
    {
        binding.effectedTextView.animateText(
            Utility.formatLong(
                yesterdayCountryDataResponse?.todayCases ?: 0
            )
        )
        binding.deathTextView.animateText(
            Utility.formatLong(
                yesterdayCountryDataResponse?.todayDeaths ?: 0
            )
        )
        binding.recoveredTextView.animateText(
            Utility.formatLong(
                yesterdayCountryDataResponse?.todayRecovered ?: 0
            )
        )
        binding.activeTextView.animateText(
            Utility.formatLong(
                yesterdayCountryDataResponse?.active ?: 0
            )
        )
        binding.seriousTextView.animateText(
            Utility.formatLong(
                yesterdayCountryDataResponse?.critical ?: 0
            )
        )
        val lastUpdatedString = String.format(
            Utility.getString(R.string.lastUpdated),
            Utility.getDateTime(yesterdayCountryDataResponse?.updated)
        )
        binding.lastUpdated.animateText(lastUpdatedString)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        try {
            this.googleMap = googleMap
            val success = googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (success != true) {
                Log.e(TAG, "Style parsing failed.")
            }
            getAllCountriesData(false)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
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

    private fun drawCircleOnMap(
        allCountriesDataResponseItem: CountryDataEntity
    )
    {
        try {
            val latLong = LatLng(
                allCountriesDataResponseItem.lat ?: 0.toDouble(),
                allCountriesDataResponseItem.longitude ?: 0.toDouble()
            )
            val circleOptions = CircleOptions()
                .center(latLong)
                .radius(allCountriesDataResponseItem.active!!.toDouble() / 2)
                .strokeColor(Utility.getColor(R.color.active))
                .strokeWidth(2f)
                .fillColor(Utility.getColor(R.color.activeDark))
            googleMap!!.addCircle(circleOptions)
        } catch (e: Exception)
        {
            Log.d("Error", e.localizedMessage?.toString() ?: "")
            Log.d("Values", allCountriesDataResponseItem.toString())
        }
    }

    private fun getCountryHistoryData()
    {
        countryHistoryViewModel.getCountryHistoryFromWeb(selectedCountry?.iso2 ?: "", "11", api)
    }

    private fun getGlobalHistoryData()
    {
        globalHistoryViewModel.getGlobalHistoryFromWeb("11", api)
    }

    private fun initializeHistoryChart()
    {
        setChartProperties(binding.casesHistoryChart)
        setChartProperties(binding.deathHistoryChart)
        setChartProperties(binding.recoveredHistoryChart)
    }

    private fun setChartProperties(chart: LineChart)
    {
        chart.setPinchZoom(true)
        chart.description.isEnabled = false
        chart.axisLeft.isEnabled = true
        chart.axisLeft.spaceTop = 40f
        chart.axisLeft.spaceBottom = 40f
        chart.axisRight.isEnabled = false
        chart.extraBottomOffset = 10f
        chart.legend.textColor = Utility.getColor(R.color.white)
        chart.xAxis.textColor = Utility.getColor(R.color.white)
        chart.axisLeft.textColor = Utility.getColor(R.color.white)
        chart.axisRight.textColor = Utility.getColor(R.color.white)
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false)
        chart.xAxis.isEnabled = true
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.animateXY(3000, 3000)
        val mv = CustomMarkerView(this)
        chart.marker = mv
    }

    private fun getEffectedHistory() {
        countryHistoryViewModel.getAllCountryCasesHistory{
            it.observe(this, { globalHistory ->
                val values = ArrayList<Entry>()
                for ((index, data) in globalHistory.withIndex()) {
                    val dailyCase = if (index == 0) {
                        0f
                    } else {
                        val todayCase = data.value?.toFloat() ?: 0f
                        val prevDayCase = (globalHistory[index - 1].value?.toFloat()) ?: 0f
                        todayCase - prevDayCase
                    }
                    values.add(
                        Entry(
                            index.toFloat(),
                            dailyCase
                        )
                    )
                }
                if (values.size > 0) {
                    values.removeAt(0)
                }

                // create a dataset and give it a type
                val set = LineDataSet(values, "Effected")
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.cubicIntensity = 0.2f
                set.lineWidth = 1.75f
                set.circleRadius = 3f
                set.circleHoleRadius = 2.5f
                set.color = Utility.getColor(R.color.affectedDark)
                set.setCircleColor(Utility.getColor(R.color.affectedDark))
                set.highLightColor = Utility.getColor(R.color.affectedDark)
                set.circleHoleColor = Utility.getColor(R.color.affectedDark)
                set.setDrawValues(false)
                set.setDrawFilled(true)
                val drawable = Utility.getDrawable(R.drawable.effected_fill_drawable)
                set.fillDrawable = drawable
                binding.casesHistoryChart.data = LineData(set)
                binding.casesHistoryChart.xAxis.valueFormatter = XAxisValueFormatter(
                    globalHistory,
                    null
                )
                binding.casesHistoryChart.notifyDataSetChanged()
                binding.casesHistoryChart.invalidate()
            })
        }
    }

    private fun getDeathHistory() {
        countryHistoryViewModel.getAllCountryDeathHistory{
            it.observe(this, { globalHistory ->
                val values = ArrayList<Entry>()
                for ((index, data) in globalHistory.withIndex()) {
                    val dailyCase = if (index == 0) {
                        0f
                    } else {
                        val todayCase = data.value?.toFloat() ?: 0f
                        val prevDayCase = (globalHistory[index - 1].value?.toFloat()) ?: 0f
                        todayCase - prevDayCase
                    }
                    values.add(
                        Entry(
                            index.toFloat(),
                            dailyCase
                        )
                    )
                }
                if (values.size > 0) {
                    values.removeAt(0)
                }
                // create a dataset and give it a type
                val set = LineDataSet(values, "Death")
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.cubicIntensity = 0.2f
                set.lineWidth = 1.75f
                set.circleRadius = 3f
                set.circleHoleRadius = 2.5f
                set.color = Utility.getColor(R.color.deathDark)
                set.setCircleColor(Utility.getColor(R.color.deathDark))
                set.highLightColor = Utility.getColor(R.color.deathDark)
                set.circleHoleColor = Utility.getColor(R.color.deathDark)
                set.setDrawValues(false)
                set.setDrawFilled(true)
                val drawable = Utility.getDrawable(R.drawable.death_fill_drawable)
                set.fillDrawable = drawable
                binding.deathHistoryChart.data = LineData(set)
                binding.deathHistoryChart.xAxis.valueFormatter = XAxisValueFormatter(
                    globalHistory,
                    null
                )
                binding.deathHistoryChart.notifyDataSetChanged()
                binding.deathHistoryChart.invalidate()
            })
        }
    }

    private fun getRecoveredHistory() {
        countryHistoryViewModel.getAllCountryRecoveredHistory{
            it.observe(this, { globalHistory ->
                val values = ArrayList<Entry>()
                for ((index, data) in globalHistory.withIndex()) {
                    val dailyCase = if (index == 0) {
                        0f
                    } else {
                        val todayCase = data.value?.toFloat() ?: 0f
                        val prevDayCase = (globalHistory[index - 1].value?.toFloat()) ?: 0f
                        todayCase - prevDayCase
                    }
                    values.add(
                        Entry(
                            index.toFloat(),
                            dailyCase
                        )
                    )
                }
                if (values.size > 0) {
                    values.removeAt(0)
                }
                // create a dataset and give it a type
                val set = LineDataSet(values, "Recovered")
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.cubicIntensity = 0.2f
                set.lineWidth = 1.75f
                set.circleRadius = 3f
                set.circleHoleRadius = 2.5f
                set.color = Utility.getColor(R.color.recoveredDark)
                set.setCircleColor(Utility.getColor(R.color.recoveredDark))
                set.highLightColor = Utility.getColor(R.color.recoveredDark)
                set.circleHoleColor = Utility.getColor(R.color.recoveredDark)
                set.setDrawValues(false)
                set.setDrawFilled(true)
                val drawable = Utility.getDrawable(R.drawable.recovered_fill_drawable)
                set.fillDrawable = drawable
                binding.recoveredHistoryChart.data = LineData(set)
                binding.recoveredHistoryChart.xAxis.valueFormatter = XAxisValueFormatter(
                    globalHistory,
                    null
                )
                binding.recoveredHistoryChart.notifyDataSetChanged()
                binding.recoveredHistoryChart.invalidate()
            })
        }
    }

    private fun getGlobalEffectedHistory() {
        globalHistoryViewModel.getAllGlobalCasesHistory{
            it.observe(this, { globalHistory ->
                val values = ArrayList<Entry>()
                for ((index, data) in globalHistory.withIndex()) {
                    val dailyCase = if (index == 0) {
                        0f
                    } else {
                        val todayCase = data.value?.toFloat() ?: 0f
                        val prevDayCase = (globalHistory[index - 1].value?.toFloat()) ?: 0f
                        todayCase - prevDayCase
                    }
                    values.add(
                        Entry(
                            index.toFloat(),
                            dailyCase
                        )
                    )
                }
                if (values.size > 0) {
                    values.removeAt(0)
                }
                // create a dataset and give it a type
                val set = LineDataSet(values, "Effected")
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.cubicIntensity = 0.2f
                set.lineWidth = 1.75f
                set.circleRadius = 3f
                set.circleHoleRadius = 2.5f
                set.color = Utility.getColor(R.color.affectedDark)
                set.setCircleColor(Utility.getColor(R.color.affectedDark))
                set.highLightColor = Utility.getColor(R.color.affectedDark)
                set.circleHoleColor = Utility.getColor(R.color.affectedDark)
                set.setDrawValues(false)
                set.setDrawFilled(true)
                val drawable = Utility.getDrawable(R.drawable.effected_fill_drawable)
                set.fillDrawable = drawable
                binding.casesHistoryChart.data = LineData(set)
                binding.casesHistoryChart.xAxis.valueFormatter = XAxisValueFormatter(
                    null,
                    globalHistory
                )
                binding.casesHistoryChart.notifyDataSetChanged()
                binding.casesHistoryChart.invalidate()
            })
        }
    }

    private fun getGlobalDeathHistory() {
        globalHistoryViewModel.getAllGlobalDeathHistory{
            it.observe(this, { globalHistory ->
                val values = ArrayList<Entry>()
                for ((index, data) in globalHistory.withIndex()) {
                    val dailyCase = if (index == 0) {
                        0f
                    } else {
                        val todayCase = data.value?.toFloat() ?: 0f
                        val prevDayCase = (globalHistory[index - 1].value?.toFloat()) ?: 0f
                        todayCase - prevDayCase
                    }
                    values.add(
                        Entry(
                            index.toFloat(),
                            dailyCase
                        )
                    )
                }
                if (values.size > 0) {
                    values.removeAt(0)
                }
                // create a dataset and give it a type
                val set = LineDataSet(values, "Death")
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.cubicIntensity = 0.2f
                set.lineWidth = 1.75f
                set.circleRadius = 3f
                set.circleHoleRadius = 2.5f
                set.color = Utility.getColor(R.color.deathDark)
                set.setCircleColor(Utility.getColor(R.color.deathDark))
                set.highLightColor = Utility.getColor(R.color.deathDark)
                set.circleHoleColor = Utility.getColor(R.color.deathDark)
                set.setDrawValues(false)
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.setDrawFilled(true)
                val drawable = Utility.getDrawable(R.drawable.death_fill_drawable)
                set.fillDrawable = drawable
                binding.deathHistoryChart.data = LineData(set)
                binding.deathHistoryChart.xAxis.valueFormatter = XAxisValueFormatter(
                    null,
                    globalHistory
                )
                binding.deathHistoryChart.notifyDataSetChanged()
                binding.deathHistoryChart.invalidate()
            })
        }
    }

    private fun getGlobalRecoveredHistory() {
        globalHistoryViewModel.getAllGlobalRecoveredHistory{
            it.observe(this, { globalHistory ->
                val values = ArrayList<Entry>()
                for ((index, data) in globalHistory.withIndex()) {
                    val dailyCase = if (index == 0) {
                        0f
                    } else {
                        val todayCase = data.value?.toFloat() ?: 0f
                        val prevDayCase = (globalHistory[index - 1].value?.toFloat()) ?: 0f
                        todayCase - prevDayCase
                    }
                    values.add(
                        Entry(
                            index.toFloat(),
                            dailyCase
                        )
                    )
                }
                if (values.size > 0) {
                    values.removeAt(0)
                }
                // create a dataset and give it a type
                val set = LineDataSet(values, "Recovered")
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.cubicIntensity = 0.2f
                set.lineWidth = 1.75f
                set.circleRadius = 3f
                set.circleHoleRadius = 2.5f
                set.color = Utility.getColor(R.color.recoveredDark)
                set.setCircleColor(Utility.getColor(R.color.recoveredDark))
                set.highLightColor = Utility.getColor(R.color.recoveredDark)
                set.circleHoleColor = Utility.getColor(R.color.recoveredDark)
                set.setDrawValues(false)
                set.mode = LineDataSet.Mode.CUBIC_BEZIER
                set.setDrawFilled(true)
                val drawable = Utility.getDrawable(R.drawable.recovered_fill_drawable)
                set.fillDrawable = drawable
                binding.recoveredHistoryChart.data = LineData(set)
                binding.recoveredHistoryChart.xAxis.valueFormatter = XAxisValueFormatter(
                    null,
                    globalHistory
                )
                binding.recoveredHistoryChart.notifyDataSetChanged()
                binding.recoveredHistoryChart.invalidate()
            })
        }
    }
}