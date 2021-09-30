package com.example.mobiledatabutbetter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var view : TextView;
    lateinit var sensor : TextView;
    lateinit var button : Button;

    lateinit var fusedLocationClient : FusedLocationProviderClient;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById(R.id.data);
        button = findViewById(R.id.button);
        sensor = findViewById(R.id.data2);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        sensor.also { sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL) }

        val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {

                        try {
                            fusedLocationClient.lastLocation.addOnCompleteListener {
                                var text = "";
                                text += "Latitude: " + it.result.latitude;
                                text += "\nLongtitude: " + it.result.longitude;
                                text += "\nAccuracy: " + it.result.accuracy;
                                text += "\nBearing: " + it.result.bearing;

                                view.text = text;

                            }
                        }catch (exc : SecurityException) { view.text = "aw :(" }
                    } else {
                        view.text = "well fuck.";
                    }
                }

        button.setOnClickListener { requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION); }

        view.text = "ass";
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            sensor.text = "GRAVITY LOL:\n" +
                    "X: " + event.values[0] + "\n" +
                    "Y: " + event.values[1] + "\n" +
                    "Z: " + event.values[2];
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}