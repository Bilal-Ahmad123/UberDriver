package com.example.uberdriver.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.uberdriver.R
import com.example.uberdriver.core.common.Constants
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.databinding.ActivitySplashBinding
import com.example.uberdriver.presentation.animator.AnimationManager
import com.example.uberdriver.presentation.auth.AuthActivity
import com.example.uberdriver.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uberdriver.presentation.auth.register.viewmodels.VehicleViewModel
import com.example.uberdriver.presentation.driver.MainActivity
import com.example.uberdriver.presentation.splash.viewmodel.DriverRoomViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()
    private val driverRoomViewModel: DriverRoomViewModel by viewModels<DriverRoomViewModel>()
    private val vehicleViewModel: VehicleViewModel by viewModels<VehicleViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        translateToRight()
        lifecycleScope.launch {
            delay(2000)
            checkIfUserLoggedIn()
        }
    }

    private fun translateToRight() {
        AnimationManager.translateToRight(binding?.ivArrow!!, 2000) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getUser() {
        driverRoomViewModel.getDriver()
    }


    private fun checkIfUserLoggedIn() {
        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance()?.currentUser
        if (user != null) {
            getUser()
            lifecycleScope.launch {
                driverRoomViewModel.apply {
                    driver.collectLatest {
                        if (it?.driverId != null) {
                            checkIfVehicleRegistered(it.driverId)
                        } else if (it?.driverId == null) {
                            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                            finish()
                        }

                    }
                }
            }
        } else {
            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            finish()
        }
    }

    private fun checkIfVehicleRegistered(driverId: UUID) {
        observeVehicleExists(driverId)
        vehicleViewModel.checkVehicleExists(driverId)
    }

    private fun observeVehicleExists(driverId: UUID) {
        lifecycleScope.launch {
            vehicleViewModel.apply {
                vehicleExists.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data!!.exists) {

                                val intent = Intent(
                                    this@SplashActivity,
                                    MainActivity::class.java
                                )
                                intent.putExtra(Constants.DRIVER_ID,driverId)
                                startActivity(intent)

                            } else {
                                val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                                intent.putExtra("FragmentName", "VehicleRegisterFragment")
                                intent.putExtra("driverId", driverId)
                                startActivity(intent)
                            }
                            finish()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}