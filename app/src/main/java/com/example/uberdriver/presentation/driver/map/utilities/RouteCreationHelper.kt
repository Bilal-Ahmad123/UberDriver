package com.example.uberdriver.presentation.driver.map.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.presentation.driver.map.viewmodel.GoogleViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.logicbeanzs.uberpolylineanimation.MapAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class RouteCreationHelper(
    private val googleViewModel: GoogleViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val googleMap: WeakReference<GoogleMap>,
    private val context: WeakReference<Context>
) {

    private var foregroundPolyline: Polyline? = null
    private var backgroundPolyline: Polyline? = null
    private var pickUpMarker: Marker? = null
    private var dropOffMarker: Marker? = null
    private var pickUp: LatLng? = null
    private var dropOff: LatLng? = null

    private fun initializeRouteInstance() {
    }

    init {
        initializeRouteInstance()
        observeDirectionsResponse()
    }

    fun createRoute(current: LatLng, pickup: LatLng, dropOff: LatLng) {
        pickUp = pickup
        this.dropOff = dropOff
        googleViewModel.getDirectionsResponse(current, dropOff, pickup)
    }

    fun deleteEverythingOnMap() {
        foregroundPolyline?.remove()
        backgroundPolyline?.remove()
        pickUpMarker?.remove()
        dropOffMarker?.remove()
        foregroundPolyline = null
        backgroundPolyline = null
        pickUpMarker = null
        dropOffMarker =  null
        pickUp = null
        dropOff = null
    }

    private fun observeDirectionsResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            googleViewModel.apply {
                directionResponse.collectLatest {
                    when (it) {
                        is Resource.Success -> {
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
        if (line.isNotEmpty()) {
            val decodedRoutes = decodePolyLine(line)
            val (fg, bg) = CustomMapAnimator.animateRoute(googleMap.get()!!, decodedRoutes)
            foregroundPolyline = fg
            backgroundPolyline = bg
            MapAnimator.setPrimaryLineColor(Color.parseColor("#000000"))
            MapAnimator.setSecondaryLineColor(Color.parseColor("#ffffff"))
            pickUpMarker()
            dropOffMarker()
            Log.d("DecodedRoute",decodedRoutes.toString())
            animateCameraToFillRoute(decodedRoutes)
        }

    }

    private fun pickUpMarker() {
        pickUp?.let {
            pickUpMarker = googleMap.get()?.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    )
                    .icon(bitmapDescriptorFromVector(context.get()!!, R.drawable.untitled_2))
            )
        }

    }

    private fun dropOffMarker() {
        dropOff?.let {
            dropOffMarker =googleMap.get()?.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    )
                    .icon(bitmapDescriptorFromVector(context.get()!!, R.drawable.untitled_1))
            )
        }


    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            ?: throw IllegalArgumentException("Drawable not found")

        val density = context.resources.displayMetrics.density
        val desiredDpSize = 80
        val scale = desiredDpSize * density

        val originalWidth = vectorDrawable.intrinsicWidth
        val originalHeight = vectorDrawable.intrinsicHeight

        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

        val width: Int
        val height: Int

        if (aspectRatio >= 1) {
            width = scale.toInt()
            height = (scale / aspectRatio).toInt()
        } else {
            height = scale.toInt()
            width = (scale * aspectRatio).toInt()
        }

        vectorDrawable.setBounds(0, 0, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private var bounds: LatLngBounds.Builder? = LatLngBounds.Builder()

    private fun animateCameraToFillRoute(routePoints: List<LatLng>) {
        bounds = LatLngBounds.Builder()
        CoroutineScope(Dispatchers.Default).launch {
            routePoints.forEach {
                bounds!!.include(it)
            }
            withContext(Dispatchers.Main) {
                if (bounds != null) {
                    googleMap.get()?.animateCamera(
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