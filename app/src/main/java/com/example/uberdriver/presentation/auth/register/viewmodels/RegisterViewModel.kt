package com.example.uberdriver.presentation.auth.register.viewmodels

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.authentication.model.request.CreateDriverRequest
import com.example.uberdriver.domain.remote.authentication.model.response.CreateDriver
import com.example.uberdriver.domain.remote.authentication.usecase.CreateDriverUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val createDriverUseCase: CreateDriverUseCase
) : BaseViewModel(dispatcher) {
    val _user = MutableStateFlow<Resource<CreateDriver>?>(null)
    val user get() = _user

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }
        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun createDriver(driver: CreateDriverRequest) {
        launchOnBack {
            _user.emit(Resource.Loading())
            val result = createDriverUseCase(driver)
            _user.emit(handleResponse(result))
        }
    }
}