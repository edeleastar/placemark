package org.wit.placemark.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.wit.placemark.R
import kotlinx.android.synthetic.main.activity_placemark_maps.*

class PlacemarkMapsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark_maps)
    setSupportActionBar(toolbarMaps)
  }
}
