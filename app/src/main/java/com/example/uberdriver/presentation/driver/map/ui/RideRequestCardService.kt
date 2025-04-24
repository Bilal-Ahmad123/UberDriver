package com.example.uberdriver.presentation.driver.map.ui

import android.content.Context
import android.location.Location
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.FetchLocation
import com.example.uberdriver.core.common.Helper
import com.example.uberdriver.core.common.SoundHelper
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.databinding.FragmentMapBinding
import com.example.uberdriver.presentation.driver.map.utilities.RouteCreationHelper
import com.example.uberdriver.presentation.driver.map.viewmodel.LocationViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.MapAndCardSharedViewModel
import com.example.uberdriver.presentation.driver.map.viewmodel.RideViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale

class RideRequestCardService(
    private val locationViewModel: LocationViewModel,
    private val binding: WeakReference<FragmentMapBinding>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val rideViewModel: RideViewModel,
    private val context: Context,
    private val mapAndCardSharedModel : MapAndCardSharedViewModel
) {


    init {
        startObservingNearbyRideRequests()
        setRideAcceptListener()
    }

    private fun startObservingNearbyRideRequests() {
        rideViewModel.startObservingNearbyRideRequests()
        rideViewModel.observeRideRequests()
    }


    suspend fun showCard(it: NearbyRideRequests) {
        if (it != null) {
            binding.get()?.bottomSheet?.mcSheet?.visibility = View.GONE
            binding.get()?.cardView?.visibility = View.VISIBLE
            binding.get()?.pickupLocationName?.text =
                getLocationName(it.pickupLatitude, it.pickupLongitude)
            val pickUpDistance = getUserDistance(it.pickupLatitude, it.pickupLongitude)
            binding.get()?.pickupTime?.text =
                Helper.getUserFetchTime(pickUpDistance.toDouble()).toString() + " mins "
            binding.get()?.pickpDistance?.text = "(${pickUpDistance} mil) away"
            val dropOffDistance =
                getUserDistance(it.dropOffLatitude, it.dropOffLongitude)
            binding.get()?.dropOffLocationName?.text =
                getLocationName(it.dropOffLatitude, it.dropOffLongitude)
            binding.get()?.dropOffTime?.text =
                Helper.getUserFetchTime(dropOffDistance.toDouble())
                    .toString() + " mins "
            binding.get()?.dropOffDistance?.text = "(${dropOffDistance} mil) trip"
            SoundHelper.startSound(context, R.raw.uberx_request_tone)
            binding.get()?.content?.startRippleAnimation()
        }
    }

    private suspend fun getLocationName(latitude: Double, longitude: Double): String {
        return FetchLocation.getLocation(
            latitude = latitude,
            longitude = longitude, context
        )
    }


    private fun getUserDistance(lat: Double, lng: Double): String {
        return String.format(Locale.US, "%.1f", FetchLocation.getDistance(Location("").apply {
            latitude = lat
            longitude = lng
        }, Location("").apply {
            latitude = locationViewModel.location.value!!.latitude
            longitude = locationViewModel.location.value!!.longitude
        }))
    }


    fun hideCardAndShowSheet() {
        binding.get()?.content?.stopRippleAnimation()
        binding.get()?.cardView?.visibility = View.GONE
        binding.get()?.bottomSheet?.mcSheet?.visibility = View.VISIBLE
        SoundHelper.destroySoundInstance()
    }

    private fun setRideAcceptListener(){
        binding.get()?.acceptRide?.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mapAndCardSharedModel.setRideBtnClicked(true)
            }
        }
    }


}