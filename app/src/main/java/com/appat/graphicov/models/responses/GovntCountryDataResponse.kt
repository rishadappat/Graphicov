package com.appat.graphicov.models.responses

data class GovntCountryDataResponse(
    val states: List<State>,
    val total: Total,
    val updated: Long
)

data class State(
    val active: Long,
    val cases: Long,
    val deaths: Long,
    val recovered: Long,
    val state: String,
    val todayActive: Long,
    val todayCases: Long,
    val todayDeaths: Long,
    val todayRecovered: Long
)

data class Total(
    val active: Long,
    val cases: Long,
    val deaths: Long,
    val recovered: Long,
    val todayActive: Long,
    val todayCases: Long,
    val todayDeaths: Long,
    val todayRecovered: Long
)