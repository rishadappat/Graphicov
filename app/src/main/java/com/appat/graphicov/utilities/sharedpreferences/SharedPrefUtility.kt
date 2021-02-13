package com.appat.graphicov.utilities.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.appat.graphicov.utilities.Utility

object SharedPrefUtility {

    private fun getAppPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(Utility.getContext())
    }

    fun getAppPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveSelectedCountry(selectedCountry: String)
    {
        if(selectedCountry.isNotEmpty()) {
            getAppPreferences().edit().putString("SelectedCountry", selectedCountry).apply()
        }
    }

    fun getSelectedCountry(): LiveData<String?>
    {
        return getAppPreferences().liveData("SelectedCountry", "")
    }

    fun saveAnalytics(enabled: Boolean)
    {
        getAppPreferences().edit().putBoolean("Analytics", enabled).apply()
    }

    fun getAnalytics(): LiveData<Boolean>
    {
        return getAppPreferences().liveData("Analytics", true)
    }
}