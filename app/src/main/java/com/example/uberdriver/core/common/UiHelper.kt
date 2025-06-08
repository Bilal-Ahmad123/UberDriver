package com.example.uberdriver.core.common

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UiHelper{
    fun animateCameraToFillRoute(routePoints: List<LatLng>,googleMap: GoogleMap) {
        val bounds: LatLngBounds.Builder = LatLngBounds.Builder()
        CoroutineScope(Dispatchers.Default).launch {
            routePoints.forEach {
                bounds.include(it)
            }
            withContext(Dispatchers.Main) {
                if (bounds != null) {
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds!!.build(),
                            100
                        )
                    )
                }
            }
        }
    }
}
