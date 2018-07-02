package com.novumlogic.firebasedemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.novumlogic.firebasedemo.helpers.AppConstants

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Thread.sleep(2000)

        if (getSharedPreferences(AppConstants.PREF_FIREBASE, 0).getString(AppConstants.PREF_FIREBASE_USER_CONTEXT, "").isNullOrEmpty()) {
            startActivity(Intent(this, FirebaseSignUpActivity::class.java))
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}