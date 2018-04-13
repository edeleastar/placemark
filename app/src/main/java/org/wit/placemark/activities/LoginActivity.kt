package org.wit.placemark.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.wit.placemark.R

class LoginActivity : AppCompatActivity(), AnkoLogger {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    signUpBtn.setOnClickListener {
      startActivity(intentFor<PlacemarkListActivity>())
    }

    signInBtn.setOnClickListener {
      startActivity(intentFor<PlacemarkListActivity>())
    }
  }
}