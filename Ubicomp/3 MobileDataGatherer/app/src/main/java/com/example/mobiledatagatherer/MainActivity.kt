package com.example.mobiledatagatherer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognitionClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestPermissionLauncher =
                registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                    }
                }
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE)

        findViewById<Button>(R.id.begin).setOnClickListener { startTracking() }
        findViewById<Button>(R.id.end).setOnClickListener { stopTracking() }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SomeChannelName"
            val descriptionText = "SomeChannelDescription"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SomeChannelId", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun startTracking() {
        val intent = Intent(this@MainActivity, BackgroundDetectedActivityService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopTracking() {
        val intent = Intent(this@MainActivity, BackgroundDetectedActivityService::class.java)
        stopService(intent)
    }
}