package com.example.uberdriver.presentation.auth.login.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.backend.authentication.model.response.DriverExists
import com.example.uberdriver.domain.usecase.CheckDriverExists
import com.example.uberdriver.domain.usecase.SignInUseCase
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val checkDriverExistsUseCase: CheckDriverExists,
    private val signInUseCase: SignInUseCase,
) : BaseViewModel(dispatcher) {
    private val _user = MutableLiveData<Resource<FirebaseUser>>()
    val user get() = _user
    private val _userExists = MutableLiveData<Resource<DriverExists>>()
    val userExists get() = _userExists

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }
        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun checkIfUserExists(email: String) {
        launchOnBack {
            _userExists.postValue(Resource.Loading())
            val result = checkDriverExistsUseCase(email)
            _userExists.postValue(handleResponse(result))
        }
    }
    fun signIn(task: SignInCredential) {
        launchOnBack {
            _user.postValue(Resource.Loading())
            val result = signInUseCase(task)
            val processedResult = handleResponse(result)
            val user = processedResult?.data!!
            _user.postValue(Resource.Success(user))
        }
    }
}