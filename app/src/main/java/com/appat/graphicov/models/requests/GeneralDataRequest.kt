package com.appat.graphicov.models.requests

import com.google.gson.Gson

data class GeneralDataRequest(
    val sort: String,
    val twoDaysAgo: Boolean,
    val yesterday: Boolean
)
{
    fun toMap(): HashMap<String, Any>
    {
        return Gson().fromJson(Gson().toJson(this), HashMap::class.java) as HashMap<String, Any>
    }
}