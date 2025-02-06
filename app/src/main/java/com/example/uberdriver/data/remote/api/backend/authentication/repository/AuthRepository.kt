package com.example.uberdriver.data.remote.api.backend.authentication.repository

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import com.example.uberdriver.domain.backend.authentication.model.response.DriverExists
import com.google.android.gms.auth.api.identity.SignInCredential
import retrofit2.Response
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun checkIfDriverExists(email:String):Response<DriverExists>
    suspend fun signIn(task: SignInCredential):Response<FirebaseUser?>
}