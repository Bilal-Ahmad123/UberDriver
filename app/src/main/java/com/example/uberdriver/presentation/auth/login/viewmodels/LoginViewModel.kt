package com.example.uberdriver.presentation.auth.login.viewmodels

import android.util.Log
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val checkDriverExistsUseCase: CheckDriverExists,
    private val signInUseCase: SignInUseCase,
) : BaseViewModel(dispatcher) {
    private val _user = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val user get() = _user
    private val _userExists = MutableStateFlow<Resource<DriverExists>?>(null)
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
            _userExists.emit(Resource.Loading())
            val result = checkDriverExistsUseCase(email)
            _userExists.emit(handleResponse(result))
        }
    }
    fun signIn(task: SignInCredential) {
        launchOnBack {
            _user.emit(Resource.Loading())
            val result = signInUseCase(task)
            val processedResult = handleResponse(result)
            val user = processedResult?.data!!
            _user.emit(Resource.Success(user))
        }
    }

    fun clearUser(){
        _user.value = null
    }

    fun clearUserId(){
        _userExists.value = null
    }
}