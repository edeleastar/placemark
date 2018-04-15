package org.wit.placemark.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.placemark.R

class LoginActivity : AppCompatActivity(), AnkoLogger {

  lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    auth = FirebaseAuth.getInstance()

    signUpBtn.setOnClickListener {
      val email = field_email.text.toString()
      val password = field_password.text.toString()
      if (email == "" || password == "") {
        toast("Please provide email + password")
      }
      else {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
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
      val email = field_email.text.toString()
      val password = field_password.text.toString()
      if (email == "" || password == "") {
        toast("Please provide email + password")
      }
      else {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
          if (task.isSuccessful) {
            startActivity(intentFor<PlacemarkListActivity>())
          }
          else {
            toast("Sign In Failed")
          }
        }
      }
    }
  }
}