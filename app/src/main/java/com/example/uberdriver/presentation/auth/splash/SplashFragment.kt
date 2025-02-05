package com.example.uberdriver.presentation.auth.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uberdriver.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp

class SplashFragment : Fragment() {

    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private val RC_SIGN_IN = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun startIdentityIntent(){
        FirebaseApp.initializeApp(requireContext())
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun signInWithOneTap() {
        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        RC_SIGN_IN,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: Exception) {
                    Log.e("OneTap", "Error starting One Tap Sign-In", e)
                }
            }
            ?.addOnFailureListener { e ->
                Log.e("OneTap", "One Tap Sign-In failed: ${e.localizedMessage}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val credential = oneTapClient?.getSignInCredentialFromIntent(data)
                if (credential != null) {
                    _loginViewModel.signIn(credential)
                } else {
                    Log.e("OneTap", "No ID token found!")
                }
            } catch (e: ApiException) {
                Log.e("OneTap", "One Tap Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

}