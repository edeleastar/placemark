package org.wit.placemark.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import org.wit.placemark.models.PlacemarkModel

@Database(entities = arrayOf(PlacemarkModel::class), version = 1)
abstract class Database : RoomDatabase() {

  abstract fun placemarkDao(): PlacemarkDao
}