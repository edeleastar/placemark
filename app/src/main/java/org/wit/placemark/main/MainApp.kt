package org.wit.placemark.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.placemark.firebase.PlacemarkFireStore
import org.wit.placemark.models.PlacemarkMemStore
import org.wit.placemark.models.PlacemarkStore
import org.wit.placemark.room.PlacemarkStoreRoom

class MainApp : Application(), AnkoLogger {

  lateinit var placemarks: PlacemarkStore

  override fun onCreate() {
    super.onCreate()
   // placemarks = PlacemarkMemStore()
    //placemarks = PlacemarkStoreRoom (applicationContext)
    placemarks = PlacemarkFireStore(this)
    info("Placemark started")
  }
}