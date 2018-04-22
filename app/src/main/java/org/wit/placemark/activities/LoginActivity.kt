package org.wit.placemark.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.firebase.PlacemarkFireStore
import org.wit.placemark.main.MainApp

class LoginActivity : AppCompatActivity(), AnkoLogger {

  lateinit var auth: FirebaseAuth
  var fireStore: PlacemarkFireStore? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    progressBar.visibility = View.GONE
    auth = FirebaseAuth.getInstance()

    var app = application as MainApp
    if (app.placemarks is PlacemarkFireStore) {
      fireStore = app.placemarks as PlacemarkFireStore
    }

    signUpBtn.setOnClickListener {
      progressBar.visibility = View.VISIBLE
      val email = field_email.text.toString()
      val password = field_password.text.toString()
      if (email == "" || password == "") {
        toast("Please provide email + password")
      }
      else {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
          progressBar.visibility = View.GONE
          if (task.isSuccessful) {
            info("Login success")
            startActivity(intentFor<PlacemarkListActivity>())
          }
          else {
            toast("Sign Up Failed: ${task.exception?.message}")
          }
        }
      }
    }

    signInBtn.setOnClickListener {
      progressBar.visibility = View.VISIBLE
      val email = field_email.text.toString()
      val password = field_password.text.toString()
      if (email == "" || password == "") {
        toast("Please provide email + password")
      }
      else {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
          progressBar.visibility = View.GONE
          if (task.isSuccessful) {
            if (fireStore != null) {
              fireStore!!.fetchPlacemarks {
                startActivity(intentFor<PlacemarkListActivity>())
              }
            }
            else {
              startActivity(intentFor<PlacemarkListActivity>())
            }
          }
          else {
            toast("Sign In Failed")
          }
        }
      }
    }
  }
}