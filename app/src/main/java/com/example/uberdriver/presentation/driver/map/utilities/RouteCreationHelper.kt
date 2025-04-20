package com.example.uberdriver.presentation.driver.map.utilities

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.directions.route.Routing
import com.directions.route.Routing.Builder
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.presentation.driver.map.viewmodel.GoogleViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.logicbeanzs.uberpolylineanimation.MapAnimator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class RouteCreationHelper(
    private val googleViewModel: GoogleViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val googleMap: WeakReference<GoogleMap>
) {

    private var foregroundPolyline: Polyline? = null
    private var backgroundPolyline: Polyline? = null
    private var routing: Builder? = null

    private fun initializeRouteInstance() {
        routing = Routing.Builder()
    }

    init {
        initializeRouteInstance()
        observeDirectionsResponse()
    }

    fun createRoute(current: LatLng, pickup: LatLng, dropOff: LatLng) {
        googleViewModel.getDirectionsResponse(current, dropOff)
    }

    fun deleteEverythingOnMap() {
        foregroundPolyline?.remove()
        backgroundPolyline?.remove()
        foregroundPolyline = null
        backgroundPolyline = null
    }

    private fun observeDirectionsResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            googleViewModel.apply {
                directionResponse.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            Log.i("Directions", it.data!!.routes.toString())
                            if (it.data!!.routes.isNotEmpty()) {
                                createPolyLine(it.data!!.routes[0].overview_polyline!!.points)
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun createPolyLine(line: String) {
        createAnimatedRoute(line)
    }

    private fun decodePolyLine(line: String): List<LatLng> {
        return PolyUtil.decode(line)
    }

    @SuppressLint("ResourceType")
    private fun createAnimatedRoute(line: String) {
        val (fg, bg) = CustomMapAnimator.animateRoute(googleMap.get()!!, decodePolyLine(line))
        foregroundPolyline = fg
        backgroundPolyline = bg
        MapAnimator.setPrimaryLineColor(Color.parseColor("#000000"))
        MapAnimator.setSecondaryLineColor(Color.parseColor("#ffffff"))
    }
}