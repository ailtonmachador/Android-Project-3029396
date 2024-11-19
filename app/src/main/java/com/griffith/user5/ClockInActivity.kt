package com.griffith.user5

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ClockInActivity : AppCompatActivity() {
    // Declare variables to handle location and UI elements
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTextView: TextView
    private lateinit var checkInButton: Button

    // Variables to define the allowed location for check-in (Griffith College campus in this case)
    private val allowedLatitude = 53.330556 // Latitude of the allowed location
    private val allowedLongitude = -6.278333 // Longitude of the allowed location
    private val allowedRadius = 100 // Radius in meters for the allowed check-in area

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_in)

        // Initialize UI elements (TextView and Button)
        locationTextView = findViewById(R.id.locationTextView)
        checkInButton = findViewById(R.id.checkInButton)

        // Initialize the location client to get location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set click listener on the check-in button
        checkInButton.setOnClickListener {
            // Check if the app has the necessary location permission
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // If permission is not granted, request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                // If permission is granted, proceed to get the location
                getLastLocation()
            }
        }
    }

    // Method to get the last known location of the device
    private fun getLastLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Get the last known location using the fused location provider
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location ->
                // Check if the location was successfully obtained
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Check if the location is within the allowed area
                    if (isWithinAllowedArea(latitude, longitude)) {
                        // If within the allowed area, show success message
                        locationTextView.text = "Check-in done in the permited location"
                        Toast.makeText(this, "Check-in done!", Toast.LENGTH_SHORT).show()
                    } else {
                        // If outside the allowed area, show error message
                        locationTextView.text = "You are not in the permited location for check-in!"
                        Toast.makeText(
                            this,
                            "You need to be in the permited location for check-in.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // If the location could not be obtained, show an error
                    Toast.makeText(this, "location is not available", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Method to check if the current location is within the allowed area
    private fun isWithinAllowedArea(currentLat: Double, currentLng: Double): Boolean {
        val results = FloatArray(1)
        // Calculate the distance between the current location and the allowed location
        android.location.Location.distanceBetween(
            currentLat, currentLng,
            allowedLatitude, allowedLongitude,
            results
        )
        // Return true if the distance is within the allowed radius
        return results[0] <= allowedRadius
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if the permission request was granted
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If granted, proceed to get the location
                getLastLocation()
            } else {
                // If denied, show a permission denial message
                Toast.makeText(this, "permition for get location denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
