package com.example.mobiledatagatherer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.DetectedActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class BackgroundDetectedActivityService : Service() {

    lateinit var client : ActivityRecognitionClient;
    private var notification : Notification? = null;

    internal var mBinder: IBinder = LocalBinder()
    inner class LocalBinder : Binder() {
        val serverInstance: BackgroundDetectedActivityService
            get() = this@BackgroundDetectedActivityService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder;
    }

    override fun onCreate() {
        super.onCreate()
        client = ActivityRecognitionClient(this);

        beginUpdates()

        Log.i("FUCK YOU", "startForeground is being called NOW YOU FUCK JESUS CHRIST")
        startForeground(25565, notification)
    }

    fun beginUpdates () {
        client.requestActivityUpdates(60000, getActivityDetectionPendingIntent());
        Log.i("BackgroundService", "Requesting activity updates..")
        var toast = Toast.makeText(this, "Activity background service started.", Toast.LENGTH_SHORT)
        toast.show()

        notification = setNotification("Service is alive!")
        startForeground(25565, notification)
    }

    fun endUpdates () {
        client.removeActivityUpdates(getActivityDetectionPendingIntent())
        Log.i("BackgroundService", "Requesting end of activity updates..")
        var toast = Toast.makeText(this, "Activity background service ended.", Toast.LENGTH_SHORT)
        toast.show()

        setNotification("Service is dead :(")
    }

    fun setNotification (content : String) : Notification {
        val pendingIntent: PendingIntent =
                Intent(this, BackgroundDetectedActivityService::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }

        var builder = NotificationCompat.Builder(this, "SomeChannelId")
                .setSmallIcon(R.drawable.googleg_standard_color_18)
                .setContentTitle("Activity update")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        var notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(25565, notification)
        }

        return notification
    }

    fun getActivityDetectionPendingIntent (): PendingIntent? {
        var intent : Intent = Intent(this, DetectedActivityIntentService::class.java);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        endUpdates()
    }
}