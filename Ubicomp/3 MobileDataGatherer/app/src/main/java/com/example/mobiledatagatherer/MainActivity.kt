package com.example.mobiledatagatherer

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.ActivityRecognitionClient

class MainActivity : AppCompatActivity() {

    lateinit var client : ActivityRecognitionClient;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client = ActivityRecognitionClient(this);

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    beginUpdates()
                } else {
                    // idk
                }
            }

        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun beginUpdates () {
        client.requestActivityUpdates(60000, getActivityDetectionPendingIntent());
    }

    fun endUpdates () {
        client.removeActivityUpdates(getActivityDetectionPendingIntent())
    }

    fun getActivityDetectionPendingIntent (): PendingIntent? {
        var intent : Intent = Intent(this, DetectedActivityIntentService::class.java);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    override fun onDestroy() {
        super.onDestroy()
        endUpdates()
    }
}