package com.appat.graphicov.utilities.sharedpreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class DataStoreUtility(private val context: Context) {

    val selectedCountryData: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            val selectedCountryData = preference[KEY_SELECTED_COUNTRY] ?: ""
            selectedCountryData
        }

    suspend fun saveSelectedCountry(selectedCountry: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_COUNTRY] = selectedCountry
        }
    }

    companion object {
        val KEY_SELECTED_COUNTRY = stringPreferencesKey("key_selected_country")
    }
}