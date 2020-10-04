package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    val EXTRA_URL = "EXTRA_URL"

    private var downloadID: Long = 0

    private val NOTIFICATION_ID = 0

    private val BASE_URL = "https://github.com/"

    private lateinit var action: NotificationCompat.Action

    private lateinit var radioGroup: RadioGroup

    private lateinit var custom_button: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button = findViewById(R.id.custom_button)

        custom_button.visibility = View.INVISIBLE

        radioGroup = findViewById(R.id.urlRadioGroup)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            custom_button.visibility = View.VISIBLE
            hint_textview.visibility = View.INVISIBLE

            when (checkedId) {
                R.id.radioButton1 -> URL = BASE_URL + "bumptech/glide"
                R.id.radioButton2 -> URL =
                    BASE_URL + "nd940-c3-advanced-android-programming-project-starter"
                R.id.radioButton3 -> URL = BASE_URL + "square/retrofit"
            }

            URL = URL + "/archive/master.zip"
        }



        custom_button.setOnClickListener {
            val selectedURL = radioGroup.checkedRadioButtonId
            if (selectedURL != -1) {
                download()
            } else {
                Toast.makeText(this, "Please select a URL", Toast.LENGTH_SHORT).show()
            }
        }

        radioGroup = findViewById(R.id.urlRadioGroup)

        createChannel(
            getString(R.string.loading_notification_channel_id),
            getString(R.string.loading_notification_channel_name)
        )

        mainMotionLayout.transitionToEnd()
    }


    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.details_notification_channel_description)

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                URL + "has been downloaded",
                applicationContext
            )

            if (downloadID == id) {
                Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    }


    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
        // Create the content intent for the notification, which launches
        // this activity

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(
            applicationContext,
            DetailActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        resultIntent.putExtra(EXTRA_URL, URL);

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        action =
            NotificationCompat.Action.Builder(
                R.drawable.ic_assistant_black_24dp,
                applicationContext.getString(R.string.action_open_details),
                resultPendingIntent
            )
                .build()

        // Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.loading_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(
                applicationContext
                    .getString(R.string.notification_title)
            )
            .setContentText(messageBody)

            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

            // Adding details action
            .addAction(
                action
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notify(NOTIFICATION_ID, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
