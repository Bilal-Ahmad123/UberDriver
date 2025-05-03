package com.example.uberdriver.presentation.driver.map.services

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.core.common.PolyUtilExtension
import com.example.uberdriver.presentation.driver.map.viewmodel.LocationViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.MapAndCardSharedViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.TripViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class RouteNavigationService(
    private val googleMap: WeakReference<GoogleMap>,
    private val tripViewModel: TripViewModel,
    private val mapAndCardSharedViewModel: MapAndCardSharedViewModel,
    private val locationViewModel: LocationViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val context: WeakReference<Context>
) {
    private var polylineOptions: PolylineOptions? = null
    private var routePoints: List<LatLng>? = null
    private var polyline: Polyline? = null
    private var riderPickUpLocation: LatLng? = null
    private var isPickUpRouting = true

    fun createRoute(
        pickUpLocation: LatLng,
    ) {
        riderPickUpLocation = pickUpLocation
        locationViewModel.location.value?.let {
            tripViewModel.directionsRequest(pickUpLocation, it)
            observeDirectionsResponse()
            observeLocationChanges()
        }
    }


    private fun removeTravelledPolyLine(loc:LatLng) {
        routePoints?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                val (index, closestPoint) = PolyUtilExtension.getNearestPointOnRoute(
                    loc, it
                )
                val trimmedPoints = routePoints?.subList(0, index)

                trimmedPoints?.let { b ->
                    polyline?.points = b
                }
            }

        }
    }

    private fun observeDirectionsResponse() {
        tripViewModel?.run {
            viewLifecycleOwner.lifecycleScope.launch {
                directions.collectLatest {
                    if (it?.data!!.routes.isNotEmpty()) {
                        createRoute(it.data!!.routes[0].overview_polyline!!.points)
                    }
                }

            }
        }
    }

    private fun createRoute(line: String) {
        val routePoints: List<LatLng> = PolyUtil.decode(line)
        if (routePoints.size > 1) {
            this.routePoints = routePoints
            polylineOptions = PolylineOptions()
                .width(10f)
                .color(Color.DKGRAY)

            polylineOptions?.let {
                it.addAll(routePoints)
                polyline = googleMap.get()?.addPolyline(it)
            }
        }
    }

    private fun observeLocationChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.location.collectLatest {
                it?.let { a->
                    removeTravelledPolyLine(a)
                }
            }
        }
    }


}