package com.griffith.user5

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.pow
import com.airbnb.lottie.LottieAnimationView  //animation

class ClockInActivity : BaseActivity() {
    // Declare variables to handle location and UI elements
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTextView: TextView
    private lateinit var checkInButton: Button

    // Variables to define the allowed location for check-in (Griffith College campus in this case)
    private var allowedLatitude = 0.0 // Latitude of the allowed location
    private var allowedLongitude = 0.0// Longitude of the allowed location
    private var allowedRadius = 0.0 // Radius in meters for the allowed check-in area
    private lateinit var userRepository: UserRepository
    private lateinit var animationViewFindingLocation: LottieAnimationView //for animation
    private lateinit var animationViewLocationPositive: LottieAnimationView
    private lateinit var animationViewFindingLocationDenied: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_in)

        //setupDrawer  for the navagationview
        setupDrawer(
            toolbarId = R.id.toolbar,
            drawerLayoutId = R.id.drawer_layout,
            navigationViewId = R.id.navigation_view
        )

        userRepository = UserRepository(this)
        //list to retrive location from DB and set to the variable lat, lon,rad
        val myMutableList: List<Triple<Double, Double, Double>> = userRepository.getClockinLocations()
        if (myMutableList.isNotEmpty()) {
             allowedLatitude = myMutableList[0].first  // Lat
            Log.d(TAG, "---> LAT: $$allowedLatitude ")
             allowedLongitude = myMutableList[0].second // Long
            Log.d(TAG, "---> LOG: $$allowedLongitude ")
             allowedRadius =  myMutableList[0].third // Rad
            Log.d(TAG, "---> RAD: $$allowedRadius ")
        } else {
            println("No locations found")
        }



        // Initialize UI elements (TextView and Button)
        locationTextView = findViewById(R.id.locationTextView)
        checkInButton = findViewById(R.id.checkInButton)
        animationViewFindingLocation = findViewById(R.id.lottieAnimationViewLocation) // Initialize the animation view
        animationViewLocationPositive = findViewById(R.id.LocationPositive) // Initialize the animation view
        animationViewFindingLocationDenied = findViewById(R.id.LocationDenied) // Initialize the animation view

        // Initialize the location client to get location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //animation for location start invisible
        animationViewLocationPositive.visibility = LottieAnimationView.GONE
        animationViewFindingLocationDenied.visibility = LottieAnimationView.GONE

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
                // Start the animation
//                animationView.playAnimation()
                // If permission is granted, proceed to get the location
                getLastLocation()
            }
        }
    }

    // Method to get the last known location of the device
    @SuppressLint("SetTextI18n")
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
                    //hide finding location animation
                    animationViewFindingLocation .visibility = LottieAnimationView.GONE

                    // Check if the location is within the allowed area
                    if (isWithinAllowedArea(latitude, longitude)) {
                        // If within the allowed area, show success message and show positive animation

                        locationTextView.text = "Check-in done in the permited location $latitude $longitude \n"
                        Toast.makeText(this, "Check-in done!", Toast.LENGTH_SHORT).show()
                        animationViewLocationPositive.visibility = LottieAnimationView.VISIBLE
                    } else {
                        // If outside the allowed area, show error message
                        locationTextView.text = "You are not in the permited location for check-in!\n" +
                                "$latitude, $longitude \n" +
                                "you should be in $allowedLatitude, $allowedLongitude"
                        animationViewFindingLocationDenied.visibility = LottieAnimationView.VISIBLE

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

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the Earth in kilometers
        val lat1Rad = Math.toRadians(lat1) // Convert latitude of point 1 to radians
        val lon1Rad = Math.toRadians(lon1) // Convert longitude of point 1 to radians
        val lat2Rad = Math.toRadians(lat2) // Convert latitude of point 2 to radians
        val lon2Rad = Math.toRadians(lon2) // Convert longitude of point 2 to radians

        // Calculate the differences in latitude and longitude between the two points
        val deltaLat = lat2Rad - lat1Rad
        val deltaLon = lon2Rad - lon1Rad

        // Apply the Haversine formula to calculate the central angle between the two points
        val a = Math.sin(deltaLat / 2).pow(2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        // Distance in kilometers multiplied by 1000 to convert to meters
        return R * c * 1000
    }

    // Method to check if the current location is within the allowed area
    private fun isWithinAllowedArea(currentLat: Double, currentLng: Double): Boolean {
        // Calculate the distance between the current location and the allowed location
        val distance = haversineDistance(currentLat, currentLng, allowedLatitude, allowedLongitude)

        // Log the distance for debugging
        Log.d(TAG, "Distance between current location and allowed location: $distance meters")

        // Log the coordinates for debugging purposes
        Log.d(TAG, "Current location: $currentLat, $currentLng  Allowed location: $allowedLatitude, $allowedLongitude")

        // Debug log for the calculated distance
        val d = distance
        Log.d(TAG, "isWithinAllowedArea: distance $d")

        // Return true if the calculated distance is within the allowed radius
        return distance <= allowedRadius
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