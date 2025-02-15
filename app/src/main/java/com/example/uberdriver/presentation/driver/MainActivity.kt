package com.example.uberdriver.presentation.driver

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.FetchLocation
import com.example.uberdriver.core.common.HRMarkerAnimation
import com.example.uberdriver.core.common.Helper
import com.example.uberdriver.core.common.PermissionManagers
import com.example.uberdriver.presentation.bottomsheet.GenericBottomSheet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
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
    private var bottomSheet: GenericBottomSheet? = null

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
        startRippleAnimation()
        showBottomSheet()
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
        updateDriverMarker()
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        checkLocationPermission {
            showUserLocation()
            fetchContinuousLocation()
        }
    }

    private fun showUserLocation() {
        checkLocationPermission {
            FetchLocation.getCurrentLocation(this) { location ->
                animateCameraToCurrentLocation(location)
            }
        }
    }

    private fun checkLocationPermission(onGranted: () -> Unit) {
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
                    .icon(bitmapDescriptorFromVector(R.drawable.driver_arrow1))
            )
        }
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        val drawable: Drawable? = ContextCompat.getDrawable(this, vectorResId)
        drawable?.let {
            val originalWidth = it.intrinsicWidth
            val originalHeight = it.intrinsicHeight

            val scaledWidth = (originalWidth * 0.24).toInt()
            val scaledHeight = (originalHeight * 0.24).toInt()

            val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
        return null
    }

    fun startRippleAnimation() {
        val rippleView = findViewById<View>(R.id.rippleView)

        val scaleX = ObjectAnimator.ofFloat(rippleView, View.SCALE_X, 1f, 1.5f)
        val scaleY = ObjectAnimator.ofFloat(rippleView, View.SCALE_Y, 1f, 1.5f)
        val alpha = ObjectAnimator.ofFloat(rippleView, View.ALPHA, 1f, 0f)

        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        alpha.repeatCount = ObjectAnimator.INFINITE

        scaleX.duration = 1000
        scaleY.duration = 1000
        alpha.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.start()
    }

    private fun showBottomSheet() {
        val customView =
            LayoutInflater.from(this).inflate(R.layout.you_are_offline_bottom_sheet_content, null)
        bottomSheet = GenericBottomSheet.newInstance(customView)
        bottomSheet?.isCancelable = false
        bottomSheet?.show(supportFragmentManager, bottomSheet?.tag)
    }


}