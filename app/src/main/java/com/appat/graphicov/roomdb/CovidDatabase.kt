package com.appat.graphicov.roomdb

import android.content.Context
import androidx.room.*
import com.appat.graphicov.roomdb.dao.AllCountriesDataDao
import com.appat.graphicov.roomdb.dao.CountryHistoryDao
import com.appat.graphicov.roomdb.dao.GlobalHistoryDao
import com.appat.graphicov.roomdb.entities.CountryDataEntity
import com.appat.graphicov.roomdb.entities.CountryHistoryEntity
import com.appat.graphicov.roomdb.entities.GlobalHistoryEntity
import com.appat.graphicov.utilities.TimestampConverter

@Database(
    entities = [CountryDataEntity::class, CountryHistoryEntity::class, GlobalHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class)
abstract class CovidGraphDatabase: RoomDatabase() {

    abstract fun allCountriesDataDao(): AllCountriesDataDao
    abstract fun countryHistoryDao(): CountryHistoryDao
    abstract fun globalHistoryDao(): GlobalHistoryDao

    companion object {

        @Volatile private var instance: CovidGraphDatabase? = null

        private const val DATABASE_NAME = "covidgraph_db"

        fun getInstance(context: Context): CovidGraphDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, CovidGraphDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as CovidGraphDatabase
        }
    }
}