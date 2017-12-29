package org.wit.placemark.room

import android.arch.persistence.room.Room
import android.content.Context
import org.jetbrains.anko.coroutines.experimental.bg
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.models.PlacemarkStore

class PlacemarkStoreRoom(val context: Context) : PlacemarkStore {

  var dao: PlacemarkDao

  init {
    val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
        .fallbackToDestructiveMigration()
        .build()
    dao = database.placemarkDao()
  }

  override suspend fun findAll(): List<PlacemarkModel> {
    val deferredPlacemarks = bg {
      dao.findAll()
    }
    val placemarks = deferredPlacemarks.await()
    return placemarks
  }

  override fun create(placemark: PlacemarkModel) {
    bg {
      dao.create(placemark)
    }
  }

  override fun update(placemark: PlacemarkModel) {
    bg {
      dao.update(placemark)
    }
  }
}