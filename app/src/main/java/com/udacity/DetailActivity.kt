package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    val EXTRA_URL = "EXTRA_URL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);


        var strURL: String? = intent.getStringExtra(EXTRA_URL)

        Toast.makeText(this, strURL, Toast.LENGTH_SHORT).show()

        url_tv.text = strURL

        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()

        motionLayout.transitionToEnd()
        //motionLayout.transitionToStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        motionLayout.transitionToStart()
    }
}
