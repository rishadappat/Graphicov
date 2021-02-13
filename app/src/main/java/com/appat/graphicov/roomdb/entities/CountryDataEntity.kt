package com.appat.graphicov.roomdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CountryDataEntity")
data class CountryDataEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long?,

    @ColumnInfo(name = "countryId")
    var countryId: Int?,

    @ColumnInfo(name = "countryName")
    var countryName: String?,

    @ColumnInfo(name = "longitude")
    var longitude: Double?,

    @ColumnInfo(name = "lat")
    var lat: Double?,

    @ColumnInfo(name = "flag")
    var flag: String?,

    @ColumnInfo(name = "iso2")
    var iso2: String?,

    @ColumnInfo(name = "iso3")
    var iso3: String?,

    @ColumnInfo(name = "active")
    var active: Long?,

    @ColumnInfo(name = "activePerOneMillion")
    var activePerOneMillion: Double?,

    @ColumnInfo(name = "cases")
    var cases: Long?,

    @ColumnInfo(name = "casesPerOneMillion")
    var casesPerOneMillion: Double?,

    @ColumnInfo(name = "continent")
    var continent: String?,

    @ColumnInfo(name = "country")
    var country: String?,

    @ColumnInfo(name = "critical")
    var critical: Long?,

    @ColumnInfo(name = "criticalPerOneMillion")
    var criticalPerOneMillion: Double?,

    @ColumnInfo(name = "deaths")
    var deaths: Long?,

    @ColumnInfo(name = "deathsPerOneMillion")
    var deathsPerOneMillion: Double?,

    @ColumnInfo(name = "oneCasePerPeople")
    var oneCasePerPeople: Double?,

    @ColumnInfo(name = "oneDeathPerPeople")
    var oneDeathPerPeople: Double?,

    @ColumnInfo(name = "oneTestPerPeople")
    var oneTestPerPeople: Double?,

    @ColumnInfo(name = "population")
    var population: Long?,

    @ColumnInfo(name = "recovered")
    var recovered: Long?,

    @ColumnInfo(name = "recoveredPerOneMillion")
    var recoveredPerOneMillion: Double?,

    @ColumnInfo(name = "tests")
    var tests: Long?,

    @ColumnInfo(name = "testsPerOneMillion")
    var testsPerOneMillion: Double?,

    @ColumnInfo(name = "todayCases")
    var todayCases: Long?,

    @ColumnInfo(name = "todayDeaths")
    var todayDeaths: Long?,

    @ColumnInfo(name = "todayRecovered")
    var todayRecovered: Long?,

    @ColumnInfo(name = "updated")
    var updated: Long?
)
{
    init {

    }
}