package com.appat.graphicov.roomdb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "CountryHistory")
data class CountryHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long?,

    @ColumnInfo(name = "status")
    var status: String?,

    @ColumnInfo(name = "date")
    var date: Date?,

    @ColumnInfo(name = "value")
    var value: Long?,

    @ColumnInfo(name = "countryName")
    var countryName: String?,
)