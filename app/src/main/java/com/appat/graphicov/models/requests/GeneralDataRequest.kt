package com.appat.graphicov.models.requests

import com.google.gson.Gson

@Suppress("UNCHECKED_CAST")
data class GeneralDataRequest(
    val page: String,
    val twoDaysAgo: Boolean,
    val yesterday: Boolean
)
{
    fun toMap(): HashMap<String, Any>
    {
        return Gson().fromJson(Gson().toJson(this), HashMap::class.java) as HashMap<String, Any>
    }
}