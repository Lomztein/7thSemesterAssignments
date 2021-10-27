package com.example.mobiledatagatherer

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class DetectedActivityIntentService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        var result : ActivityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
        var activities : List<DetectedActivity> = result.probableActivities;

        Log.i(TAG, "Detected activities..");
        for (activity in activities) {
            Log.i(TAG, getActivityString(activity.type) + ": C = " + activity.confidence);
        }

        storeActivities(activities);
    }

    fun storeActivities(activities: List<DetectedActivity>) {
        var builder : StringBuilder = java.lang.StringBuilder();
        var date : Date = Calendar.getInstance().time;

        builder.append(date.toString());
        for (activity in activities) {
            builder.append("," + getActivityString(activity.type) + ":" + activity.confidence)
        }
        builder.append("\n")
        Log.i(TAG, builder.toString());

        try {
            var external: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            var path : String = external.absolutePath + "/ActivityData.csv";
            var stream: FileOutputStream =
                FileOutputStream(path, true)
            stream.write(builder.toString().toByteArray());
            stream.close()
            Log.i(
                TAG,
                "Data succesfully written to " + path
            );
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace();
        }
    }

    fun getActivityString(detectedActivityType: Int): String? {
        return when (detectedActivityType) {
            DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
            DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
            DetectedActivity.ON_FOOT -> "ON_FOOT"
            DetectedActivity.RUNNING -> "RUNNING"
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.TILTING -> "TILTING"
            DetectedActivity.UNKNOWN -> "UNKNOWN"
            DetectedActivity.WALKING -> "WALKING"
            else -> "UNIDENTIFIABLE"
        }
    }

    companion object {
        var TAG : String = "DetectedActivityIS";
    }
}