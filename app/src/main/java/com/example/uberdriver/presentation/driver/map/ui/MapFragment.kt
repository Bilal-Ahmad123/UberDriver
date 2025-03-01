package com.example.uberdriver.presentation.driver.map.ui

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.BitmapCreator
import com.example.uberdriver.core.common.ButtonAnimator
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    private var oldLocation: Location? = null
    private var mLastLocation: Location? = null
    private var binding: FragmentMapBinding? = null
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
        requestLocationPermission()
        driverRoomViewModel.getDriver()
        setUpGoogleMap()
        startRippleAnimation()
        onGoButtonClickListener()
        fetchCurrentLocation()
        connectToSocket()
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
        updateDriverMarker()
    }

    private fun fetchCurrentLocation() {
        checkLocationPermission {
            FetchLocation.getCurrentLocation(requireContext()) { location ->
                animateCameraToCurrentLocation(location)
            }
        }
    }

    private fun requestLocationPermission() {
        checkLocationPermission {
            fetchContinuousLocation()
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
        return BitmapCreator.bitmapDescriptorFromVector(vectorResId, requireContext())
    }

    private fun startRippleAnimation() {
        ButtonAnimator.startRippleAnimation(binding?.rippleView!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        googleMap = null
        oldLocation = null
        driverMarker = null
        mLastLocation = null
    }

    private fun onGoButtonClickListener() {
        binding?.goButton?.setOnClickListener {
            binding?.bottomSheet?.tvOffline?.text = "Going Online"
            ButtonAnimator.startHorizontalAnimation(
                binding!!.bottomSheet.linearLine,
                requireContext()
            )
            ButtonAnimator.stopRippleAnimation()
            binding?.goButton?.visibility = View.GONE
            hideLineView()
            fetchContinuousLocation()
        }
    }

    private fun hideLineView(){
        lifecycleScope.launch {
            delay(2000)
            binding?.bottomSheet?.tvOffline?.text = "You are Online"
            ButtonAnimator.stopAnimation()
            binding?.bottomSheet?.linearLine?.visibility = View.GONE
        }
    }

    private fun connectToSocket() {
        socketViewModel.connectSocket(Constants_Api.LOCATION_SOCKET_API)
    }
}