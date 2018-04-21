package org.wit.placemark.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_placemark_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.*
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.PlacemarkModel

class PlacemarkListActivity : AppCompatActivity(), PlacemarkListener {

  lateinit var app: MainApp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark_list)
    app = application as MainApp

    val user = FirebaseAuth.getInstance().currentUser
    var appTitle = "${title.toString()}: ${user!!.email}"
    toolbarMain.title = appTitle
    setSupportActionBar(toolbarMain)

    var layoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    loadPlacemarks()
  }

  private fun loadPlacemarks() {
    async(UI) {
      showPlacemarks(app.placemarks.findAll())
    }
  }

  fun showPlacemarks(placemarks: List<PlacemarkModel>) {
    recyclerView.adapter = PlacemarkAdapter(placemarks, this)
    recyclerView.adapter.notifyDataSetChanged()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    loadPlacemarks()
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.item_add -> startActivityForResult<PlacemarkActivity>(200)
      R.id.item_map -> startActivity<PlacemarkMapsActivity>()
      R.id.item_logout -> {
        app.placemarks.clear()
        FirebaseAuth.getInstance().signOut()
        startActivity<LoginActivity>()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onPlacemarkClick(placemark: PlacemarkModel) {
    startActivityForResult(intentFor<PlacemarkActivity>().putExtra("placemark_edit", placemark), 201)
  }

  override fun onPlacemarkLongClick(placemark: PlacemarkModel) {
    val title = ctx.getString(R.string.dialog_title_delete)
    val message = ctx.getString(R.string.dialog_desc_delete)

    alert(message, title) {
      positiveButton(ctx.getString(android.R.string.ok)) {
        app.placemarks.delete(placemark)
        loadPlacemarks()
      }
      negativeButton(ctx.getString(android.R.string.no)) { }
    }.show()
  }
}