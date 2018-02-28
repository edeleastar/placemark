package org.wit.placemark.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.placemark.R
import kotlinx.android.synthetic.main.activity_placemark_maps.*
import kotlinx.android.synthetic.main.content_placemark_maps.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.wit.placemark.main.MainApp

class PlacemarkMapsActivity : AppCompatActivity() {

  lateinit var map: GoogleMap
  lateinit var app: MainApp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark_maps)
    setSupportActionBar(toolbarMaps)
    mapView.onCreate(savedInstanceState);
    app = application as MainApp

    mapView.getMapAsync {
      map = it
      configureMap()
    }
  }

  fun configureMap() {
    map.uiSettings.setZoomControlsEnabled(true)
    async(UI) {
      app.placemarks.findAll().forEach {
        val loc = LatLng(it.lat, it.lng)
        val options = MarkerOptions().title(it.title).position(loc)
        map.addMarker(options).tag = it.id
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}
