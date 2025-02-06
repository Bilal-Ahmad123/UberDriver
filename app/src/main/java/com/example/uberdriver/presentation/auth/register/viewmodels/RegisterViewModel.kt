package com.example.uberdriver.presentation.auth.register.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.authentication.model.request.CreateDriverRequest
import com.example.uberdriver.domain.backend.authentication.model.response.CreateDriver
import com.example.uberdriver.domain.usecase.CreateDriverUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val createDriverUseCase: CreateDriverUseCase
) : BaseViewModel(dispatcher) {
    private val _user = MutableLiveData<Resource<CreateDriver>>()
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
            _user.postValue(Resource.Loading())
            val result = createDriverUseCase(driver)
            _user.postValue(handleResponse(result))
        }
    }
}