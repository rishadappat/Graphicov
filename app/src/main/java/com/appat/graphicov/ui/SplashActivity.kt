package com.appat.graphicov.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.appat.graphicov.R
import com.appat.graphicov.databinding.ActivitySplashBinding
import com.appat.graphicov.utilities.Utility
import com.appat.graphicov.utilities.base.GraphicovActivity
import com.appat.graphicov.utilities.sharedpreferences.DataStoreUtility
import com.appat.graphicov.utilities.sharedpreferences.SharedPrefUtility
import kotlinx.coroutines.*

class SplashActivity : GraphicovActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContentView(R.layout.activity_splash)
            withContext(Dispatchers.Main) {
                delay(500)
                getCurrentCountry()
            }
        }
    }

    private suspend fun getCurrentCountry()
    {
        val selectedCountry = SharedPrefUtility.getSelectedCountry().value
        if (selectedCountry.isNullOrEmpty()) {
            val currentCountryISO = Utility.getCurrentCountryISO(this@SplashActivity)
            if(currentCountryISO.isNotEmpty())
            {
                SharedPrefUtility.saveSelectedCountry(currentCountryISO)
                DataStoreUtility(this@SplashActivity).saveSelectedCountry(currentCountryISO)
            }
            gotoWelcomeScreen()
        } else  {
            gotoDashboard()
        }
    }

    private fun gotoDashboard()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun gotoWelcomeScreen()
    {
        val intent = Intent(this, WelcomeScreenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}