package com.example.uberdriver.domain.remote.socket.ride.repository

import com.example.uberdriver.core.common.SocketMethods
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.example.uberdriver.domain.remote.socket.ride.model.AcceptRideRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class RideRepository @Inject constructor(private val broker: SocketBroker) : RideRepository {
    private val _rideRequests = MutableSharedFlow<NearbyRideRequests>()
    private val rideRequests = _rideRequests.asSharedFlow()

    override fun startObservingNearbyRideRequests() {
        runCatching {
            broker.apply {
                if (getHubConnection() != null) {
                    getHubConnection()?.on(
                        SocketMethods.SEND_RIDE_REQUEST_TO_DRIVERS,
                        { userId: String, pickupLongitude: Double, pickupLatitude: Double, dropOffLongitude: Double, dropOffLatitude: Double,rideId: String ->
                            CoroutineScope(Dispatchers.IO).launch {
                                _rideRequests.emit(
                                    NearbyRideRequests(
                                        UUID.fromString(userId),
                                        pickupLongitude,
                                        pickupLatitude,
                                        dropOffLongitude,
                                        dropOffLatitude,
                                        rideId = UUID.fromString(rideId)
                                    )
                                )
                            }
                        },
                        String::class.java,
                        Double::class.java,
                        Double::class.java,
                        Double::class.java,
                        Double::class.java,
                        String::class.java
                        )
                }
            }
        }
    }

    override fun observeRideRequest(): Flow<NearbyRideRequests> = rideRequests

    override fun acceptRide(
        ride:AcceptRideRequest
    ) {
        broker.send<AcceptRideRequest>(ride,SocketMethods.ACCEPT_RIDE)
    }
}