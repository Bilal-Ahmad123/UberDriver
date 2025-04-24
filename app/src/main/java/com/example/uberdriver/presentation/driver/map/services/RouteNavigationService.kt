package com.example.uberdriver.presentation.driver.map.services

import android.annotation.SuppressLint
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.uberdriver.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.ArrivalEvent
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationApi.NavigatorListener
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.Navigator.RouteStatus
import com.google.android.libraries.navigation.RoutingOptions
import com.google.android.libraries.navigation.SupportNavigationFragment
import com.google.android.libraries.navigation.Waypoint
import java.lang.ref.WeakReference

class RouteNavigationService(
    private val googleMap: WeakReference<GoogleMap>,
    private val fragmentActivity: FragmentActivity,
    private val childFragmentManager: FragmentManager
) {
    private var navigator: Navigator? = null
    private var routingOptions: RoutingOptions? = null
    private lateinit var navFragment: SupportNavigationFragment
    private var navInfoDisplayFragment: Fragment? = null


    init {
        initialize()
    }


    fun initialize() {
        navFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportNavigationFragment
        fragmentActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initializeNavigationApi()
    }

    private fun registerNavigationListeners() {
        navigator?.addArrivalListener(arrivalListener)
        navigator?.addRouteChangedListener(routeChangedListener)
    }

    private val routeChangedListener = object : Navigator.RouteChangedListener {
        override fun onRouteChanged() {
        }
    }

    private val arrivalListener = object : Navigator.ArrivalListener {
        override fun onArrival(p0: ArrivalEvent?) {
            navigator?.stopGuidance()
        }

    }

    private fun initializeNavigationApi() {
        NavigationApi.getNavigator(
            fragmentActivity,
            object : NavigatorListener {
                @SuppressLint("MissingPermission")
                override fun onNavigatorReady(navigator: Navigator) {
                    this@RouteNavigationService.navigator = navigator
                    registerNavigationListeners()
                    navFragment.setReportIncidentButtonEnabled(false)
                    navFragment.setRecenterButtonEnabled(false)
                    navFragment.setTrafficPromptsEnabled(false)
                    navFragment.setEtaCardEnabled(false)
                    navFragment.setSpeedLimitIconEnabled(false)
                    navFragment.setSpeedometerEnabled(false)
                    navFragment.setTrafficIncidentCardsEnabled(false)
                    navFragment.setTripProgressBarEnabled(false)
                    googleMap.get()?.apply {
                        followMyLocation(GoogleMap.CameraPerspective.TOP_DOWN_HEADING_UP)
                    }
                    googleMap.get()?.isMyLocationEnabled = false
                    routingOptions = RoutingOptions()
                    routingOptions?.travelMode(RoutingOptions.TravelMode.DRIVING)

                }

                override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
                    when (errorCode) {
                        NavigationApi.ErrorCode.NOT_AUTHORIZED -> {
                            Log.e("Un", "sdsd")
                        }

                        NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> {
                            Log.e("zxsz", "sdsd")

                        }

                        else -> {

                        }
                    }
                }
            },
        )
    }


    fun navigateToPlace(position: LatLng) {
        val waypoint: Waypoint =
            Waypoint.Builder().setLatLng(position.latitude, position.longitude).build()
        val pendingRoute = navigator?.setDestination(waypoint)

        pendingRoute?.setOnResultListener { code ->
            when (code) {
                RouteStatus.OK -> {

                    navigator?.startGuidance()
                }

                RouteStatus.ROUTE_CANCELED -> {
                }

                RouteStatus.NO_ROUTE_FOUND,
                RouteStatus.NETWORK_ERROR -> {
                }

                else -> {

                }
            }
        }

    }

}