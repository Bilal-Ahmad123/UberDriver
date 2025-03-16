package com.example.uberdriver.presentation.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uberdriver.R
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.domain.remote.authentication.model.response.CreateDriver
import com.example.uberdriver.presentation.auth.register.ui.VehicleRegisterFragment
import com.example.uberdriver.presentation.auth.register.viewmodels.RegisterViewModel
import com.example.uberdriver.presentation.auth.register.viewmodels.VehicleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels<RegisterViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        showVehicleRegisterFragment()
    }

    private fun showVehicleRegisterFragment(){
        val bundle = intent.extras

        val fragmentName = bundle?.getString("FragmentName")
        val driverId = bundle?.getSerializable("driverId") as UUID
        when(fragmentName){
             "VehicleRegisterFragment" -> {
                 registerViewModel._user.value = Resource.Success(CreateDriver(driverId))
                 val fragment = VehicleRegisterFragment()
                 supportFragmentManager.beginTransaction()
                     .replace(R.id.main,fragment)
                     .commit()
            }
        }
    }
}