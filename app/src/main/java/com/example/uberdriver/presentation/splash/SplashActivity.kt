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
import com.example.uberdriver.databinding.ActivitySplashBinding
import com.example.uberdriver.presentation.animator.AnimationManager
import com.example.uberdriver.presentation.auth.AuthActivity
import com.example.uberdriver.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uberdriver.presentation.driver.MainActivity
import com.example.uberdriver.presentation.splash.viewmodel.DriverRoomViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()
    private val driverRoomViewModel: DriverRoomViewModel by viewModels<DriverRoomViewModel>()
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
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
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
}