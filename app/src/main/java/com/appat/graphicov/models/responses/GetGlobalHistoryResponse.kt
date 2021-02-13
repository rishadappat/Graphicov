package com.appat.graphicov.models.responses

data class GetGlobalHistoryResponse(
    val cases: HashMap<String, Long>,
    val deaths: HashMap<String, Long>,
    val recovered: HashMap<String, Long>
)