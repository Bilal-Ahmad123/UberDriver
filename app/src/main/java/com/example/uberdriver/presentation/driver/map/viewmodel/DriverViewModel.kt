package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.driver.details.model.DriverDetails
import com.example.uberdriver.domain.remote.driver.usecase.GetDriverDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val getDriverDetailsUseCase: GetDriverDetailsUseCase
) : BaseViewModel(dispatcher) {
    private var _driverId: UUID? = null
    val driverId get() = _driverId

    private var _driverDetails = MutableStateFlow<Resource<DriverDetails>?>(null)
    val driverDetails get() = _driverDetails.asStateFlow()

    fun setDriverId(driverId: UUID) {
        _driverId = driverId
    }

    fun getDriverDetails() {
        launchOnBack {
            _driverDetails.emit(Resource.Loading())
            val result = getDriverDetailsUseCase(driverId!!)
            _driverDetails.emit(handleResponse(result))
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