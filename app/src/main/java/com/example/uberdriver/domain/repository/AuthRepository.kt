package com.example.uberdriver.domain.repository
import com.example.uberdriver.data.remote.api.backend.authentication.api.AuthenticationService
import com.example.uberdriver.data.remote.api.backend.authentication.mapper.toDomain
import com.example.uberdriver.data.remote.api.backend.authentication.model.request.CreateDriverReq
import com.example.uberdriver.data.remote.api.backend.authentication.model.request.CreateDriverRequest
import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import com.example.uberdriver.domain.backend.authentication.model.response.CreateDriver
import com.example.uberdriver.domain.backend.authentication.model.response.DriverExists
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val api:AuthenticationService): AuthRepository {
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override suspend fun checkIfDriverExists(email: String):Response<DriverExists> {
        return try {
            Response.success(api.checkUserExists(email).body()?.toDomain())
        }
        catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }

    override suspend fun signIn(task: SignInCredential):Response<FirebaseUser?> {
        var user: FirebaseUser? = null
        return try {
            val credential = GoogleAuthProvider.getCredential(task.googleIdToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user = auth.currentUser
                    }
                }.await()
            Response.success(user)
        } catch (exp: Exception) {
            Response.error(500, ResponseBody.create(null, "Network error: ${exp.localizedMessage}"))
        }
    }

    override suspend fun createDriver(driver: CreateDriverRequest): Response<CreateDriver> {
        return try {
            Response.success(api.createDriver(CreateDriverReq(driver)).body()?.toDomain())
        }
        catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }
}