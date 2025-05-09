package com.example.uberdriver.presentation.driver.map.services

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.Distance
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.Duration
import com.example.uberdriver.core.common.PolyUtilExtension
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.data.remote.api.backend.socket.trip.model.ReachedRider
import com.example.uberdriver.presentation.driver.map.viewmodel.DriverViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.GoogleViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.LocationViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.MapAndCardSharedViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.TripViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class RouteNavigationService(
    private val googleMap: WeakReference<GoogleMap>,
    private val tripViewModel: TripViewModel,
    private val mapAndCardSharedViewModel: MapAndCardSharedViewModel,
    private val locationViewModel: LocationViewModel,
    private val googleViewModel: GoogleViewModel,
    private val driverViewModel: DriverViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val context: WeakReference<Context>
) {
    private var polylineOptions: PolylineOptions? = null
    private var routePoints: MutableList<LatLng>? = null
    private var polyline: Polyline? = null
    private var location: LatLng? = null
    private var duration: Duration? = null
    private var distance: Distance? = null

    fun createRoute(
        location: LatLng,
    ) {
        this.location = location
        locationViewModel.location.value?.let {
            tripViewModel.directionsRequest(location, it)
            observeDirectionsResponse()
            observeLocationChanges()
            observeDistanceMatrixResponse()
            registerDistanceHandler()
        }
    }


    private fun removeTravelledPolyLine(loc: LatLng) {
        routePoints?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                val (index, closestPoint) = PolyUtilExtension.getNearestPointOnRoute(
                    loc, it
                )
                val trimmedPoints = routePoints?.subList(0, index)

                trimmedPoints?.let { b ->
                    polyline?.points = b
                }
                trimmedPoints?.let { b ->
                    if (b.size <= 2) {
                        driverReachedToLocation(loc)
                    }
                }

            }

        }
    }

    private fun observeDirectionsResponse() {
        tripViewModel?.run {
            viewLifecycleOwner.lifecycleScope.launch {
                directions.collectLatest {
                    Log.d("DirectionsResponse", it?.data.toString())
                    if (it?.data!!.routes.isNotEmpty()) {
                        createRoute(
                            it.data!!.routes[0].overview_polyline!!.points,
                            it.data.routes[0].legs[0].duration,
                            it.data.routes[0].legs[0].distance
                        )
                    }
                }

            }
        }
    }

    private fun createRoute(
        line: String,
        duration: Duration,
        distance: com.example.uber.data.remote.api.googleMaps.models.directionsResponse.Distance
    ) {
        val routePoints: MutableList<LatLng> = PolyUtil.decode(line)
        if (routePoints.size > 1) {
            this.routePoints = routePoints
            polylineOptions = PolylineOptions()
                .width(15f)
                .color(Color.DKGRAY)

            polyline?.remove()
            polylineOptions?.let {
                it.addAll(routePoints)
                polyline = googleMap.get()?.addPolyline(it)
            }
            updateMapZoomLevel()
            this.distance = distance
            this.duration = duration
        }
    }

    private fun updateMapZoomLevel() {
        viewLifecycleOwner.lifecycleScope.launch {
            googleViewModel.setCameraZoomLevel(17.0f)
            withContext(Dispatchers.Main) {
                googleMap?.get()?.moveCamera(CameraUpdateFactory.zoomTo(17.0f))
            }

        }
    }

    private fun observeLocationChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.location.collectLatest {
                it?.let { a ->
                    checkIfDriverLocationOnRoute(a)
                    removeTravelledPolyLine(a)
                    sendTripLocationUpdates(a)
                }
            }
        }
    }

    private fun sendTripLocationUpdates(value: LatLng) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (distance != null && duration != null) {
                tripViewModel.ride.value?.let {ri->
                    tripViewModel.sendTripLocation(
                        TripLocation(
                            ri.rideId,
                            driverViewModel.driverId!!,
                            ri.riderId,
                            value.latitude,
                            value.longitude,
                            distance!!.value,
                            duration!!.value
                        )
                    )
                }
            }
        }
    }

    private fun checkIfDriverLocationOnRoute(trip: LatLng) {

        if (routePoints != null && !PolyUtil.isLocationOnPath(
                trip,
                routePoints,
                true,
                50.0
            )
        ) {
            tripViewModel.directionsRequest(
                location!!,
                trip
            )
        }
    }

    private fun driverReachedToLocation(value: LatLng) {
        routePoints?.let {
                driverViewModel?.driverId?.let { a ->
                    cleanMap()
                    tripViewModel.ride.value?.let {trip->
                        tripViewModel.reachedRiderPickUpSpot(
                            ReachedRider(
                                trip.riderId,
                                a,
                                trip.rideId,
                                true,
                            )
                        )
                    }
                }
            }
    }

    private fun getDistanceMatrix(destination: LatLng, origin: LatLng) {
        viewLifecycleOwner.lifecycleScope.launch {
            googleViewModel.getDistanceMatrix(destination, origin)
        }
    }

    private fun observeDistanceMatrixResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            googleViewModel.distanceMatrix.value?.let { res ->
                distance =res.data?.rows?.get(0)?.elements?.get(0)?.distance
                duration = res.data?.rows?.get(0)?.elements?.get(0)?.duration
            }
        }
    }


    private fun cleanMap() {
        polyline?.remove()
        routePoints?.clear()
        handler?.removeCallbacks(runnable)
        handler = null
        routePoints = null
        distance = null
        duration = null
    }

    var handler:Handler? = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            getDistanceMatrix(locationViewModel.location.value!!, location!!)
        }
    }

    private fun registerDistanceHandler(){
        handler?.postDelayed(runnable,10000)
    }
}