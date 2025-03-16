package com.example.uberdriver.presentation.auth.register.viewmodels

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicleRequest
import com.example.uberdriver.domain.remote.vehicle.model.response.CheckVehicleExists
import com.example.uberdriver.domain.remote.vehicle.model.response.CreateVehicle
import com.example.uberdriver.domain.remote.vehicle.usecase.CheckVehicleExistsUseCase
import com.example.uberdriver.domain.remote.vehicle.usecase.CreateVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val useCase: CreateVehicleUseCase,
    private val checkVehicleExistsUseCase: CheckVehicleExistsUseCase
):BaseViewModel(dispatcher){
    private val _vehicleExists = MutableStateFlow<Resource<CheckVehicleExists>?>(null)
    val vehicleExists get() = _vehicleExists
    private val _createVehicle = MutableStateFlow<Resource<CreateVehicle>?>(null)
    val createVehicle get() = _createVehicle

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }
        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun getVehicleTypes():ArrayList<String>{
        return arrayListOf(
            "UberX",
            "UberXL",
            "Uber Lux"
        )
    }

    fun createVehicle(vehicleRequest: CreateVehicleRequest) {
        launchOnBack {
            _createVehicle.emit(Resource.Loading())
            val result = useCase(vehicleRequest)
            _createVehicle.emit(handleResponse(result))
        }
    }
    fun checkVehicleExists(driverId: UUID) {
        launchOnBack {
            _vehicleExists.emit(Resource.Loading())
            val result = checkVehicleExistsUseCase(driverId)
            _vehicleExists.emit(handleResponse(result))
        }
    }




}