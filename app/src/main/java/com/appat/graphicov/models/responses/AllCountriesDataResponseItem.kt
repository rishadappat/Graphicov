package com.appat.graphicov.models.responses

data class AllCountriesDataResponseItem(
    val active: Long,
    val activePerOneMillion: Double,
    val cases: Long,
    val casesPerOneMillion: Double,
    val continent: String,
    val country: String,
    val countryInfo: CountryInfo,
    val critical: Long,
    val criticalPerOneMillion: Double,
    val deaths: Long,
    val deathsPerOneMillion: Double,
    val oneCasePerPeople: Long,
    val oneDeathPerPeople: Long,
    val oneTestPerPeople: Long,
    val population: Long,
    val recovered: Long,
    val recoveredPerOneMillion: Double,
    val tests: Long,
    val testsPerOneMillion: Double,
    val todayCases: Long,
    val todayDeaths: Long,
    val todayRecovered: Long,
    val updated: Long
)