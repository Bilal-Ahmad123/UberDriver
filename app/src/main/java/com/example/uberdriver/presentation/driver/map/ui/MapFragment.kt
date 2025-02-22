package com.example.uberdriver.presentation.driver.map.ui

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.core.common.FetchLocation
import com.example.uberdriver.core.common.HRMarkerAnimation
import com.example.uberdriver.core.common.Helper
import com.example.uberdriver.core.common.PermissionManagers
import com.example.uberdriver.databinding.FragmentMapBinding
import com.example.uberdriver.domain.remote.location.model.UpdateLocation
import com.example.uberdriver.presentation.driver.map.viewmodel.SocketViewModel
import com.example.uberdriver.presentation.splash.viewmodel.DriverRoomViewModel
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    private var oldLocation: Location? = null
    private var mLastLocation: Location? = null
    private var binding :FragmentMapBinding? = null
    private val socketViewModel: SocketViewModel by viewModels<SocketViewModel>()
    private val driverRoomViewModel: DriverRoomViewModel by activityViewModels<DriverRoomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        driverRoomViewModel.getDriver()
        setUpGoogleMap()
        startRippleAnimation()
    }

    private fun getCurrentMapStyle(): Int =
        if (Helper.isDarkMode(requireContext())) R.raw.night_map else R.raw.day_map

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                getCurrentMapStyle()
            )
        )
        socketViewModel.connectSocket(Constants_Api.LOCATION_SOCKET_API)
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
            FetchLocation.getCurrentLocation(requireContext()) { location ->
                animateCameraToCurrentLocation(location)
            }
        }
    }

    private fun checkLocationPermission(onGranted: () -> Unit) {
        PermissionManagers.requestPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (it) {
                onGranted.invoke()
            }
        }
    }

    private fun setUpGoogleMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun fetchContinuousLocation() {
        checkLocationPermission {
            requireActivity().lifecycleScope.launch {
                FetchLocation.getLocationUpdates(requireContext()).collect {
                    updateLocation(it)
                    driverRoomViewModel.driver.value.let { dri ->
                        Log.d("MapFragment", "DriverId: ${dri?.driverId}")
                        socketViewModel.sendDriverLocation(
                            UpdateLocation(
                                dri?.driverId!!,
                                it.longitude,
                                it.latitude
                            )
                        )
                    }
                }
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
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), vectorResId)
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

    private fun startRippleAnimation() {
        val rippleView = binding?.rippleView

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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        googleMap = null
        oldLocation = null
        driverMarker = null
        mLastLocation = null
    }
}