package org.wit.placemark.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.models.PlacemarkStore

class PlacemarkFireStore : PlacemarkStore, AnkoLogger {

  val placemarks = ArrayList<PlacemarkModel>()

  init {
    firebaseListenerInit()
  }

  fun getUserId(): String {
    return FirebaseAuth.getInstance().currentUser!!.uid
  }

  fun getDb(): DatabaseReference {
    return FirebaseDatabase.getInstance().reference
  }

  suspend override fun findAll(): List<PlacemarkModel> {
    return placemarks
  }

  suspend override fun findById(id: Long): PlacemarkModel? {
    val foundPlacemark: PlacemarkModel? = placemarks.find { p -> p.id == id }
    return foundPlacemark
  }

  override fun create(placemark: PlacemarkModel) {
    val key = getDb().child("users").child(getUserId()).child("placemarks").push().key
    placemark.fbId = key
    getDb().child("users").child(getUserId()).child("placemarks").child(key).setValue(placemark)
  }

  override fun update(placemark: PlacemarkModel) {
    getDb().child("users").child(getUserId()).child("placemarks").child(placemark.fbId).setValue(placemark)
  }

  override fun delete(placemark: PlacemarkModel) {
    getDb().child("users").child(getUserId()).child("placemarks").child(placemark.fbId).removeValue()
    placemarks.remove(placemark)
  }

  private fun firebaseListenerInit() {

    val childEventListener = object : ChildEventListener {

      override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
        val placemark = dataSnapshot!!.getValue(PlacemarkModel::class.java)
        placemarks.add (placemark!!)
      }

      override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
      }

      override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
      }

      override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
      }

      override fun onCancelled(databaseError: DatabaseError?) {
      }

    }
    getDb().child("users").child(getUserId()).child("placemarks").addChildEventListener(childEventListener)
  }

}