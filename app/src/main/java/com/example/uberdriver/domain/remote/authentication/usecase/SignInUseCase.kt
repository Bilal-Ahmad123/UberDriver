package com.example.uberdriver.domain.remote.authentication.usecase

import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import retrofit2.Response
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(task: SignInCredential):Response<FirebaseUser?>{
        return authRepository.signIn(task)
    }
}