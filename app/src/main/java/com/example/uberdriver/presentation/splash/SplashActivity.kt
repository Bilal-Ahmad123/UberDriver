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
import com.example.uberdriver.presentation.driver.MainActivity
import com.example.uberdriver.R
import com.example.uberdriver.databinding.ActivitySplashBinding
import com.example.uberdriver.presentation.animator.AnimationManager
import com.example.uberdriver.presentation.auth.AuthActivity
import com.example.uberdriver.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uberdriver.presentation.auth.register.viewmodels.RegisterViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()
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
        checkIfUserExists()
    }

    private fun translateToRight() {
        AnimationManager.translateToRight(binding?.ivArrow!!,2000){
            if(it){
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun checkIfUserExists(){
        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance()?.currentUser
        if(user != null){
            lifecycleScope.launch {
                loginViewModel.apply {
                    userExists.collectLatest {
                        if(it != null){
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }
}