package com.griffith.user5
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.griffith.user5.R
import okhttp3.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


/**
 * This activity displays a map and allows the user to search for locations using an input field.
 * It uses the OpenStreetMap (OSM) library (osmdroid) for map rendering and OkHttp for HTTP requests.
 * The app performs a reverse geocoding search using the Nominatim API to find location names based on user input.
 * Users can also double-tap on the map to add a marker at a selected location.
 *
 * Libraries used:
 * - osmdroid: For rendering the map and handling map interactions.
 * - OkHttp: For making HTTP requests to Nominatim API to get location details.
 * - JSON: For parsing the response from Nominatim API.
 */
class Map : BaseActivity()  {
    private lateinit var mapView: MapView
    private var currentMarker: Marker? = null
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var gestureDetector: GestureDetector
    private lateinit var userRepository: UserRepository

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_view)
        // Setup navigation drawer
        setupDrawer(
            toolbarId = R.id.toolbar,
            drawerLayoutId = R.id.drawer_layout,
            navigationViewId = R.id.navigation_view
        )

        userRepository = UserRepository(this)
        val context = applicationContext
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Initializing the MapView
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Centering the map on Dublin's coordinates
        mapView.controller.setZoom(15.0)
      //  mapView.controller.setCenter(GeoPoint(53.349805, -6.26031))

        // Gesture detector for double-tap
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Get the geo coordinates from the screen tap position
                val projection = mapView.projection
                val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                // Log coordinates to check
                Log.d(TAG, "Double-tap at: ${geoPoint.latitude}, ${geoPoint.longitude}")
                runOnUiThread {
                    addMarkerAtLocation(geoPoint)
                }
                return true
            }
        })

        // Setting the touch listener for the map
        mapView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        // Search bar setup
        searchEditText = findViewById(R.id.search_edit_text)
        searchButton = findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchLocation(query)
            } else {
                Toast.makeText(this, "Please enter a location to search", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to search for a location and add a marker on the map
    private fun searchLocation(query: String) {
        val url = "https://nominatim.openstreetmap.org/search?format=json&q=$query"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body?.string()
                val jsonArray = JSONArray(jsonResponse)

                if (jsonArray.length() > 0) {
                    val result = jsonArray.getJSONObject(0)
                    val lat = result.getDouble("lat")
                    val lon = result.getDouble("lon")

                    val geoPoint = GeoPoint(lat, lon)

                    runOnUiThread {
                        addMarkerAtLocation(geoPoint)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@Map, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    // Function to add a marker at a specific GeoPoint
    private fun addMarkerAtLocation(geoPoint: GeoPoint) {
        runOnUiThread {
            // Remove previous marker
            currentMarker?.let { mapView.overlays.remove(it) }

            // Add new marker
            currentMarker = Marker(mapView).apply {
                position = geoPoint
                title = "Selected Location"
            }
            mapView.overlays.add(currentMarker)

            // Move camera to the new position
            mapView.controller.setCenter(geoPoint)
            mapView.invalidate()
            // Log and update database

            Log.d(TAG, "Marker updated at: ${geoPoint.latitude}, ${geoPoint.longitude}")
            val rad = 100.00
            userRepository.updateClockin(geoPoint.latitude, geoPoint.longitude, rad)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }
}
