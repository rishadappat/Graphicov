package com.appat.graphicov.models.responses

data class GlobalDataResponse(
    val active: Long,
    val activePerOneMillion: Double,
    val affectedCountries: Long,
    val cases: Long,
    val casesPerOneMillion: Double,
    val critical: Long,
    val criticalPerOneMillion: Double,
    val deaths: Long,
    val deathsPerOneMillion: Double,
    val oneCasePerPeople: Double,
    val oneDeathPerPeople: Double,
    val oneTestPerPeople: Double,
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