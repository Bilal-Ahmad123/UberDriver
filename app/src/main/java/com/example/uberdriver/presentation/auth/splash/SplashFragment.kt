package com.example.uberdriver.presentation.auth.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uberdriver.R
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uberdriver.presentation.splash.SplashActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val _loginViewModel: LoginViewModel by activityViewModels()
    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private val RC_SIGN_IN = 2
    private lateinit var navController: NavController
    private var job: Job? = null


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

    override fun onDestroyView() {
        super.onDestroyView()
        oneTapClient = null
        signInRequest = null
        job?.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        startIdentityIntent()
        signInWithOneTap()
        observeUserLogin()
        observerUserExistsStatus()
    }

    private fun startIdentityIntent() {
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

    private fun observeUserLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            _loginViewModel.apply {
                user
                    .collectLatest {
                        if (it != null) {
                            Log.e("User", "Here Again")
                            it.data?.email?.let { email ->
                                _loginViewModel.checkIfUserExists(email)
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Authentication failed",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
            }
        }
    }

    private fun observerUserExistsStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            _loginViewModel.apply {
                userExists.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data
                            data?.let {
                                if (it.driverId != null) {
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            SplashActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()
                                } else {
                                    val bundle = Bundle()
                                    if (navController.currentDestination?.id == R.id.splashFragment) {
                                        bundle.putString(
                                            "displayName",
                                            _loginViewModel.user.value?.data?.displayName
                                        )
                                        navController.navigate(
                                            R.id.action_splashFragment_to_registerDetailsFragment,
                                            bundle
                                        )
                                    }
                                }
                            }
                        }

                        else -> Unit
                    }
                }

            }
        }
    }

}