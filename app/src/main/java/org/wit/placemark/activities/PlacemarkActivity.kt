package org.wit.placemark.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_placemark.*
import org.jetbrains.anko.*
import org.wit.placemark.R
import org.wit.placemark.helpers.*
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel

class PlacemarkActivity : AppCompatActivity(), AnkoLogger {

  var placemark = PlacemarkModel()
  lateinit var app: MainApp
  lateinit var map: GoogleMap
  var edit = false
  val IMAGE_REQUEST = 1
  val LOCATION_REQUEST = 2
  val defaultLocation = Location(52.245696, -7.139102, 15f)
  val locationRequest = createDefaultLocationRequest()

  private lateinit var locationService: FusedLocationProviderClient

  var locationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult?) {
      if (locationResult != null && locationResult.locations != null) {
        val l = locationResult.locations.last()
        info ("Location Update ${l.latitude} ${l.longitude}")
        lat.setText(l.latitude.toString())
        lng.setText(l.longitude.toString())
        placemark.lat = l.latitude
        placemark.lng = l.longitude
        configureMap()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark)
    mapView.onCreate(savedInstanceState);
    app = application as MainApp
    locationService = LocationServices.getFusedLocationProviderClient(this)

    toolbarAdd.title = title
    setSupportActionBar(toolbarAdd)

    btnHere.isEnabled = false

    mapView.getMapAsync {
      map = it
      configureMap()
    }

    if (intent.hasExtra("placemark_edit")) {
      edit = true
      placemark = intent.extras.getParcelable<PlacemarkModel>("placemark_edit")
      placemarkTitle.setText(placemark.title)
      description.setText(placemark.description)
      placemarkImage.setImageBitmap(readImageFromPath(this, placemark.image))
      if (placemark.image != null) {
        chooseImage.setText(R.string.change_placemark_image)
      }
    } else {
      placemark.lat = defaultLocation.lat
      placemark.lng = defaultLocation.lng
      placemark.zoom = defaultLocation.zoom
    }

    chooseImage.setOnClickListener {
      showImagePicker(this, IMAGE_REQUEST)
    }

    placemarkLocation.setOnClickListener {
      if (placemark.zoom != 0f) {
        defaultLocation.lat = placemark.lat
        defaultLocation.lng = placemark.lng
        defaultLocation.zoom = placemark.zoom
      }
      startActivityForResult(intentFor<MapsActivity>().putExtra("location", defaultLocation), LOCATION_REQUEST)
    }

    btnHere.setOnClickListener {
      setCurrentLocation()
    }
  }

  fun configureMap() {
    map.uiSettings.setZoomControlsEnabled(true)
    val loc = LatLng(placemark.lat, placemark.lng)
    val options = MarkerOptions().title(placemark.title).position(loc)
    map.addMarker(options)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, placemark.zoom))
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_placemark, menu)
    return super.onCreateOptionsMenu(menu)
  }

  fun save() {
    placemark.title = placemarkTitle.text.toString()
    placemark.description = description.text.toString()

    if (edit) {
      app.placemarks.update(placemark.copy())
      setResult(201)
      finish()
    } else {
      if (placemark.title.isNotEmpty()) {
        app.placemarks.create(placemark.copy())
        setResult(200)
        finish()
      } else {
        toast(R.string.enter_placemark_title)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.item_save -> {
        save()
      }
      R.id.item_cancel -> {
        setResult(RESULT_CANCELED)
        finish()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      IMAGE_REQUEST -> {
        if (data != null) {
          placemark.image = data.getData().toString()
          placemarkImage.setImageBitmap(readImage(this, resultCode, data))
          chooseImage.setText(R.string.change_placemark_image)
        }
      }
      LOCATION_REQUEST -> {
        if (data != null) {
          val location = data.extras.getParcelable<Location>("location")
          map.clear()
          placemark.lat = location.lat
          placemark.lng = location.lng
          placemark.zoom = location.zoom
          configureMap()
        }
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun setCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      defaultLocation.lat = it.latitude
      defaultLocation.lng = it.longitude
      placemark.lat = it.latitude
      placemark.lng = it.longitude
      configureMap()
    }
  }

  @SuppressLint("MissingPermission")
  private fun startLocationUpdates() {
    locationService.requestLocationUpdates(locationRequest, locationCallback, null)
  }


  override fun onStart() {
    super.onStart()
    if (checkLocationPermissions(this)) {
      btnHere.isEnabled = true
    }
  }

  @SuppressLint("MissingPermission")
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      btnHere.isEnabled = true
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
    startLocationUpdates()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}
