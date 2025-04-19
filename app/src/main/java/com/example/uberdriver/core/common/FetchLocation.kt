package com.example.uberdriver.core.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.Locale

object FetchLocation {
    private val mCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var locationCallback: LocationCallback? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val continuousLocation = MutableStateFlow<Location?>(null)
    private var locationRequest: LocationRequest? = null
    private var locationCallback2: LocationCallback? = null
    suspend fun getLocation(latitude: Double, longitude: Double, context: Context): String {
        var addresses: List<Address> = emptyList()
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )!!

        } catch (e: Exception) {
            Log.e("getLocation", e.message.toString())
        }
        return if (addresses.isNotEmpty()) addresses[0].getAddressLine(0).split(",")[1] else ""
    }

    fun getCurrentLocation(
        context: Context,
        dispatcher: (Location?) -> Unit
        ) {
        lazyInitializeLocationEngine(context)
        mCoroutineScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val locationRequest = LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 2000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            fusedLocationClient?.removeLocationUpdates(this)
                            dispatcher.invoke(location)
                        }
                    }
                }
                checkLocationPermission(context) {
                    fusedLocationClient?.requestLocationUpdates(
                        locationRequest,
                        locationCallback!!,
                        Looper.getMainLooper()
                    )
                    fusedLocationClient!!.lastLocation.addOnSuccessListener {
                        runCatching {
                            if (it != null) {
                                dispatcher.invoke(Location("Custom").apply {
                                    this.latitude = it.latitude
                                    this.longitude = it.longitude
                                })
                            }
                        }
                    }.addOnFailureListener { exception ->
                        exception.printStackTrace()

                    }
                }
            } catch (it: Exception) {
            }


        }
    }

    private fun lazyInitializeLocationEngine(context: Context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }


    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun cleanResources() {
        fusedLocationClient = null;
    }

    fun customLocationMapper(latitude: Double, longitude: Double): Location {
        val locationMapper = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        return locationMapper
    }

    fun getContinuousLocation(context: Context) {
        val continuousLocation = LocationRequest.create().apply {
            setInterval(5000)
            setFastestInterval(5000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
            fusedLocationClient?.requestLocationUpdates(
                continuousLocation,
                locationCallback2!!, Looper.getMainLooper()
            )

    }

    fun getLocationUpdates(context: Context): Flow<Location> = callbackFlow {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.create().apply {
            setInterval(5000)
            setFastestInterval(5000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }


    private fun checkLocationPermission(context: Context, onGranted: () -> Unit) {
        PermissionManagers.requestPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (it) {
                onGranted.invoke()
            }
        }
    }

    fun getDistance(startPoint : Location,endDistance:Location):Double{
        return startPoint.distanceTo(endDistance) / 1609.344
    }

}
