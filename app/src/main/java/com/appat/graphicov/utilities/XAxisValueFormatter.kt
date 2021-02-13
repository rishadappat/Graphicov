package com.appat.graphicov.utilities

import com.appat.graphicov.roomdb.entities.CountryHistoryEntity
import com.appat.graphicov.roomdb.entities.GlobalHistoryEntity
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class XAxisValueFormatter(private val countryHistoryValues: List<CountryHistoryEntity>?, private val globalHistoryValues: List<GlobalHistoryEntity>?) : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return try {
            if (globalHistoryValues == null) {
                Utility.dateToString(countryHistoryValues!![value.toInt()].date, Constants.ddMMM)
            } else {
                Utility.dateToString(globalHistoryValues[value.toInt()].date, Constants.ddMMM)
            }
        } catch (e: Exception) {
            ""
        }
    }
}