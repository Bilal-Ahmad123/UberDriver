package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.rider.model.RiderDetails
import com.example.uberdriver.domain.remote.driver.usecase.GetDriverDetailsUseCase
import com.example.uberdriver.domain.remote.rider.usecase.GetRiderDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(
    private val dispatchers: IDispatchers,
    private val getRiderDetailsUseCase: GetRiderDetails
):BaseViewModel(dispatchers) {
    private val riderDetails = MutableStateFlow<Resource<RiderDetails>?>(null);
    val riderDetailsState get() = riderDetails.asStateFlow()

    fun getRiderDetails(riderId: UUID){
        launchOnBack {
            riderDetails.emit(Resource.Loading())
            val response = getRiderDetailsUseCase(riderId)
            riderDetails.emit(handleResponse(response))
        }
    }

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }
        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }
}