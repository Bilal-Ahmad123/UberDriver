package com.example.uberdriver.presentation.driver.map.ui

import android.Manifest
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
import com.example.uberdriver.presentation.driver.map.services.RouteNavigationService
import com.example.uberdriver.presentation.driver.map.utilities.RouteCreationHelper
import com.example.uberdriver.presentation.driver.map.viewmodel.DriverViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.GoogleViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.LocationViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.MapAndCardSharedViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.RideViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.SocketViewModel
import com.example.uberdriver.presentation.splash.viewmodel.DriverRoomViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.navigation.SupportNavigationFragment
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
        observeRideRequests()
        observeAcceptRideBtnClicked()
        observeRideBtnClicked()
        observePickUpLocationReached()
    }

    private fun initializeCardService() {
        rideRequestCardService = RideRequestCardService(
            locationViewModel,
            WeakReference(binding),
            this,
            rideViewModel,
            requireContext(),
            mapAndCardSharedViewModel
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
            requireActivity(),
            childFragmentManager, rideViewModel,
            mapAndCardSharedViewModel,
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
            childFragmentManager.findFragmentById(R.id.google_map) as SupportNavigationFragment
        mapFragment.getMapAsync(this)
    }


    private fun fetchContinuousLocation() {
        checkLocationPermission {
            viewLifecycleOwner.lifecycleScope.launch {
                FetchLocation.getLocationUpdates(requireContext()).collect {
                    locationViewModel.setDriverLocation(LatLng(it.latitude, it.longitude))
//                    animateCameraToCurrentLocation(it)
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
        rideRequestCardService = null
    }

    private fun onGoButtonClickListener() {
        binding?.goButton?.setOnClickListener {
            isGoButtonClicked = true
            binding?.tvOffline?.text = "Going Online"
            ButtonAnimator.startHorizontalAnimation(
                binding!!.linearLine,
                requireContext()
            )
            binding?.frameLayout?.visibility = View.GONE
            hideLineView()
        }
    }

    private fun hideLineView() {
        lifecycleScope.launch {
            delay(2000)
            binding?.tvOffline?.text = "You are Online"
            ButtonAnimator.stopAnimation()
            binding?.linearLine?.visibility = View.GONE
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
                            routeNavigationService?.navigateToPlace(
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

    private fun hideCardAndOtherStuff() {
        binding?.goButton?.visibility = View.GONE
        driverMarker?.isVisible = false
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
                            routeNavigationService?.navigateToPlace(
                                LatLng(
                                    a.dropOffLatitude,
                                    a.dropOffLongitude
                                )
                            )
                            rideViewModel.rideRequests.emit(null)
                            mapAndCardSharedViewModel.apply {
                                setPickUpLocationReached(false)
                            }
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
                        mapAndCardSharedViewModel.setPickUpLocationReached(false)
                        binding?.bottomSheet?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeDropOffLocationReached(){

    }
}