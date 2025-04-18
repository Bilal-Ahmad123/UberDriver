package com.example.uberdriver.presentation.driver.map.ui

import android.Manifest
import android.graphics.Color
import android.location.Location
import android.os.Bundle
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
import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import com.example.uberdriver.presentation.auth.register.viewmodels.VehicleViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.DriverViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.LocationViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.RideViewModel
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    private var oldLocation: Location? = null
    private var mLastLocation: Location? = null
    private var binding: FragmentMapBinding? = null
    private val socketViewModel: SocketViewModel by viewModels<SocketViewModel>()
    private val rideViewModel: RideViewModel by viewModels<RideViewModel>()
    private val locationViewModel: LocationViewModel by activityViewModels<LocationViewModel>()
    private val driverRoomViewModel: DriverRoomViewModel by activityViewModels<DriverRoomViewModel>()
    private val driverViewModel: DriverViewModel by activityViewModels<DriverViewModel>()
    private val vehicleViewModel: VehicleViewModel by activityViewModels<VehicleViewModel>()
    private var isGoButtonClicked: Boolean = false
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
        initialCalls()
        setUpGoogleMap()
        startRippleAnimation()
        onGoButtonClickListener()
        connectToSocket()
        fetchContinuousLocation()
        startObservingNearbyRideRequests()
        observeRideRequests()
    }

    private fun initialCalls() {
        viewLifecycleOwner.lifecycleScope.launch {
            val job = driverRoomViewModel.getDriver()
            job.join()
            getVehicleDetails()
        }
    }

    private fun getVehicleDetails() {
        vehicleViewModel.getVehicleDetails(driverRoomViewModel.driver.value!!.driverId)
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
            viewLifecycleOwner.lifecycleScope.launch {
                FetchLocation.getLocationUpdates(requireContext()).collect {
                    locationViewModel.setDriverLocation(LatLng(it.latitude,it.longitude))
                    animateCameraToCurrentLocation(it)
                    updateLocation(it)
                    driverRoomViewModel.driver.value.let { dri ->
                        with(socketViewModel) {
                            if (socketConnected.value && isGoButtonClicked) {
                                locationViewModel.sendDriverLocation(
                                    UpdateLocation(
                                        dri?.driverId!!,
                                        it.longitude,
                                        it.latitude,
                                        vehicleViewModel.vehicleDetails.value!!.data!!.type
                                    )
                                )
                            }
                        }

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
            isGoButtonClicked = true
            binding?.bottomSheet?.tvOffline?.text = "Going Online"
            ButtonAnimator.startHorizontalAnimation(
                binding!!.bottomSheet.linearLine,
                requireContext()
            )
            binding?.frameLayout?.visibility = View.GONE
            hideLineView()
        }
    }

    private fun hideLineView() {
        lifecycleScope.launch {
            delay(2000)
            binding?.bottomSheet?.tvOffline?.text = "You are Online"
            ButtonAnimator.stopAnimation()
            binding?.bottomSheet?.linearLine?.visibility = View.GONE
        }
    }

    private fun connectToSocket() {
        socketViewModel.connectSocket(Constants_Api.LOCATION_SOCKET_API + "?riderId=${driverViewModel.driverId}")
        socketViewModel.observeConnectedToSocket()
    }

    private fun startObservingNearbyRideRequests() {
        rideViewModel.startObservingNearbyRideRequests()
        rideViewModel.observeRideRequests()
    }

    private fun observeRideRequests() {
        rideViewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                rideRequests.collectLatest {
                    if (it != null) {
                        binding?.bottomSheet?.mcSheet?.visibility = View.GONE
                        binding?.cardView?.visibility = View.VISIBLE
                        binding?.pickupLocationName?.text = getLocationName(it.pickupLatitude,it.pickupLongitude)
                        val pickUpDistance = getUserDistance(it.pickupLatitude,it.pickupLongitude)
                        binding?.pickupTime?.text = Helper.getUserFetchTime(pickUpDistance.toDouble()).toString() + " mins "
                        binding?.pickpDistance?.text = "(${pickUpDistance} mil) away"
                        val dropOffDistance = getUserDistance(it.dropOffLatitude,it.dropOffLongitude)
                        binding?.dropOffLocationName?.text = getLocationName(it.dropOffLatitude,it.dropOffLongitude)
                        binding?.dropOffTime?.text = Helper.getUserFetchTime(dropOffDistance.toDouble()).toString() + " mins "
                        binding?.dropOffDistance?.text = "(${dropOffDistance} mil) trip"
                    }
                }
            }
        }
    }

    private suspend fun getLocationName(latitude : Double, longitude : Double):String{
            return FetchLocation.getLocation(
                latitude = latitude,
                longitude = longitude, requireContext()
            )
    }


    private fun getUserDistance(lat: Double,lng: Double):String{
        return String.format(Locale.US, "%.1f", FetchLocation.getDistance(Location("").apply {
            latitude = lat
            longitude = lng
        },Location("").apply {
            latitude = locationViewModel.location.value!!.latitude
            longitude = locationViewModel.location.value!!.longitude
        }))
    }

}