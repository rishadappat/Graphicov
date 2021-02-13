package com.appat.graphicov.models.responses

data class GetCountryHistoryResponse(
    val country: String,
    val province: List<String>,
    val timeline: Timeline
)