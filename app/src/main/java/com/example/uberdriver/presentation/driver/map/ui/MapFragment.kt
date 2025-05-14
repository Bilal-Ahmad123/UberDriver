package com.example.uberdriver.presentation.driver.map.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.BitmapCreator
import com.example.uberdriver.core.common.ButtonAnimator
import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.core.common.FetchLocation
import com.example.uberdriver.core.common.HRMarkerAnimation
import com.example.uberdriver.core.common.Helper
import com.example.uberdriver.core.common.PermissionManagers
import com.example.uberdriver.core.services.BackgroundLocationService
import com.example.uberdriver.core.services.LocationService
import com.example.uberdriver.databinding.FragmentMapBinding
import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import com.example.uberdriver.presentation.auth.register.viewmodels.VehicleViewModel
import com.example.uberdriver.presentation.driver.MainActivity
import com.example.uberdriver.presentation.driver.map.services.RouteNavigationService
import com.example.uberdriver.presentation.driver.map.utilities.RouteCreationHelper
import com.example.uberdriver.presentation.driver.map.viewmodel.DriverViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.GoogleViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.LocationViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.MapAndCardSharedViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.RideViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.SocketViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.TripViewModel
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


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
    private val googleViewModel: GoogleViewModel by viewModels<GoogleViewModel>()
    private val mapAndCardSharedViewModel: MapAndCardSharedViewModel by activityViewModels<MapAndCardSharedViewModel>()
    private val tripViewModel: TripViewModel by viewModels<TripViewModel>()
    private var isGoButtonClicked: Boolean = false
    private var rideRequestCardService: RideRequestCardService? = null
    private var routeCreationHelper: RouteCreationHelper? = null
    private var routeNavigationService: RouteNavigationService? = null

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
        initializeCardService()
        addObservers()
        navigateBtnClickListener()
        showPictureInPicture()
    }

    private fun addObservers() {
        observeRideRequests()
        observeAcceptRideBtnClicked()
        observeRideBtnClicked()
        observePickUpLocationReached()
        observeStartRideBtnClicked()
    }

    private fun initializeCardService() {
        rideRequestCardService = RideRequestCardService(
            locationViewModel,
            WeakReference(binding),
            this,
            rideViewModel,
            tripViewModel,
            requireContext(),
            mapAndCardSharedViewModel,
            driverViewModel
        )
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
        initializeRouteCreationHelper()
        initializeRouteNavigationService(googleMap)
        fetchCurrentLocation()
    }

    private fun initializeRouteNavigationService(googleMap: GoogleMap) {
        routeNavigationService = RouteNavigationService(
            WeakReference(googleMap),
            tripViewModel,
            mapAndCardSharedViewModel,
            locationViewModel,
            googleViewModel,
            driverViewModel,
            this,
            WeakReference(context)
        )


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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchContinuousLocation() {
        checkLocationPermission {
            viewLifecycleOwner.lifecycleScope.launch {
                Intent(requireContext(), BackgroundLocationService::class.java).apply {
                    action = BackgroundLocationService.ACTION_START
                    requireContext().startForegroundService(this)
                }
                FetchLocation.getLocationUpdates(requireContext()).collect {
                    locationViewModel.setDriverLocation(LatLng(it.latitude, it.longitude))
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

    private fun fetchLocationInBackground(){
        Intent(requireContext(), LocationService::class.java).apply {
            action = LocationService.ACTION_START
            requireContext().startService(this)
        }
    }

    private fun animateCameraToCurrentLocation(lastKnownLocation: Location?) {
        if (googleMap != null) {
            val userLatLng = lastKnownLocation?.let { LatLng(it.latitude, it.longitude) }
            viewLifecycleOwner.lifecycleScope.launch {
                googleViewModel.cameraZoomLevel.collectLatest {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng!!, it))
                }
            }
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
        rideRequestCardService = null
    }

    private fun onGoButtonClickListener() {
        binding?.goButton?.setOnClickListener {
            isGoButtonClicked = true
            binding?.frameLayout?.visibility = View.GONE
            viewLifecycleOwner.lifecycleScope.launch {
                mapAndCardSharedViewModel.setGoBtnClicked(true)
            }
        }
    }


    private fun connectToSocket() {
        socketViewModel.connectSocket(Constants_Api.LOCATION_SOCKET_API + "?riderId=${driverViewModel.driverId}")
        socketViewModel.observeConnectedToSocket()
    }

    private fun observeRideRequests() {
        rideViewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                rideRequests.collectLatest { it ->
                    it?.let {
                        rideRequestCardService?.showCard(it)
                        locationViewModel.location.value?.let { loc ->
                            routeCreationHelper?.createRoute(
                                LatLng(loc.latitude, loc.longitude),
                                LatLng(it.pickupLatitude, it.pickupLongitude),
                                LatLng(it.dropOffLatitude, it.dropOffLongitude)
                            )
                            hideCardAndRoute()
                        }
                    }
                }
            }
        }
    }


    private fun initializeRouteCreationHelper() {
        routeCreationHelper = RouteCreationHelper(
            googleViewModel,
            this,
            WeakReference(googleMap),
            WeakReference(requireContext())
        )
    }

    private var job: Job? = null
    private fun hideCardAndRoute() {
        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(10000)
            rideRequestCardService?.hideCardAndShowSheet()
            routeCreationHelper?.deleteEverythingOnMap()
            rideViewModel.rideRequests.emit(null)
        }
    }

    private fun observeAcceptRideBtnClicked() {
        viewLifecycleOwner.lifecycleScope.launch {
            mapAndCardSharedViewModel.acceptRideBtnClick.collectLatest {
                it?.let { t ->
                    if (t) {
                        hideCardAndOtherStuff()
                        rideViewModel.rideRequests.value?.let { a ->
                            routeNavigationService?.createRoute(
                                LatLng(
                                    a.pickupLatitude,
                                    a.pickupLongitude
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private var tripJob: Job? = null


    private fun cancelTripUpdates() {
        tripJob?.cancel(null)
    }

    private fun hideCardAndOtherStuff() {
        binding?.goButton?.visibility = View.GONE
        routeCreationHelper?.deleteEverythingOnMap()
        rideRequestCardService?.hideCard()
        job?.cancel(null)
    }


    private fun observeRideBtnClicked() {
        viewLifecycleOwner.lifecycleScope.launch {
            mapAndCardSharedViewModel.apply {
                startRideClick.collectLatest {
                    if (it) {
                        rideViewModel.rideRequests.value?.let { a ->
                            routeNavigationService?.createRoute(
                                LatLng(
                                    a.dropOffLatitude,
                                    a.dropOffLongitude
                                )
                            )
                            rideViewModel.rideRequests.emit(null)
                        }
                    }
                }
            }
        }
    }

    private fun observePickUpLocationReached() {
        viewLifecycleOwner.lifecycleScope.launch {
            mapAndCardSharedViewModel.apply {
                reachPickUpLocation.collectLatest {
                    if (it) {
                        binding?.bottomSheet?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeStartRideBtnClicked() {
        viewLifecycleOwner.lifecycleScope.launch {
            mapAndCardSharedViewModel.apply {
                startUberBtnClicked.collectLatest {
                    rideViewModel.rideRequests.value?.let { a ->
                        routeNavigationService?.cleanMap()
                        routeNavigationService?.createRoute(
                            LatLng(
                                a.dropOffLatitude,
                                a.dropOffLongitude
                            )
                        )
                    }
                }
            }
        }
    }

    private fun navigateBtnClickListener() {
        binding?.navigate?.setOnClickListener {
            rideViewModel.rideRequests.value?.let { a ->
                launchGoogleMap(LatLng(a.pickupLatitude, a.pickupLongitude))
            }
        }
    }

    private fun launchGoogleMap(destination: LatLng) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}" + "&mode=d")
        val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(intent)
    }

    private fun showPictureInPicture() {
        viewLifecycleOwner.lifecycleScope.launch {
            mapAndCardSharedViewModel.reachPickUpLocation.collectLatest {
                val it = Intent("intent.my.action")
                it.setComponent(
                    ComponentName(
                        requireContext().packageName,
                        MainActivity::class.java.getName()
                    )
                )
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                requireContext().applicationContext.startActivity(it)
                Log.d("PictureInPicture","PictureInPicture")
            }
        }
    }


}