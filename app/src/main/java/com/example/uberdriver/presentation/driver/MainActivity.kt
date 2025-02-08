package com.example.uberdriver.presentation.driver

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.FetchLocation
import com.example.uberdriver.core.common.HRMarkerAnimation
import com.example.uberdriver.core.common.Helper
import com.example.uberdriver.core.common.PermissionManagers
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    private var oldLocation: Location? = null
    private var mLastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navBarHeight = systemBarsInsets.bottom
            view.setPadding(0, 0, 0, navBarHeight)
            insets
        }
        setUpGoogleMap()
    }

    private fun getCurrentMapStyle(): Int =
        if (Helper.isDarkMode(this)) R.raw.night_map else R.raw.day_map

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                getCurrentMapStyle()
            )
        )
        requestLocationPermission()
        updateDriverMarker()
    }

    private fun requestLocationPermission() {
        checkLocationPermission("Need Access to Location") {
            showUserLocation()
            fetchContinuousLocation()
        }
    }

    private fun showUserLocation() {
        checkLocationPermission(null) {
            googleMap?.isMyLocationEnabled = true
            FetchLocation.getCurrentLocation(this) { location ->
                animateCameraToCurrentLocation(location)
            }
        }
    }

    private fun checkLocationPermission(rationale: String?, onGranted: () -> Unit) {
        PermissionManagers.requestPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (it) {
                onGranted.invoke()
            }
        }
    }

    private fun setUpGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    private fun fetchContinuousLocation() {
        lifecycleScope.launch {
            FetchLocation.getLocationUpdates(this@MainActivity).collect {
                updateLocation(it)
            }
        }
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        if (googleMap != null) {
            val userLatLng = lastKnownLocation?.let { LatLng(it.latitude, it.longitude) }
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng!!, 13.0f))
        }
    }

    private fun animateMarker() {
        HRMarkerAnimation(
            googleMap, 1000
        ) { updatedLocation -> oldLocation = updatedLocation }.animateMarker(
            mLastLocation,
            oldLocation,
            driverMarker
        )
    }

    private fun updateLocation(newLocation: Location?) {
        if (newLocation != null) {
            mLastLocation = newLocation
            animateMarker()
        }
    }

    private fun updateDriverMarker() {
        if (driverMarker == null) {
            driverMarker = googleMap?.addMarker(
                MarkerOptions().position(LatLng(33.591293, 73.122300))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .alpha(0f)
            )
        }
    }
}