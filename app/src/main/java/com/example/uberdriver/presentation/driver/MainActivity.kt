package com.example.uberdriver.presentation.driver

import android.app.PictureInPictureParams
import android.graphics.Point
import android.os.Bundle
import android.util.Rational
import android.view.Display
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uberdriver.R
import com.example.uberdriver.core.common.Constants
import com.example.uberdriver.presentation.driver.map.viewmodel.DriverViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val driverViewModel : DriverViewModel by viewModels<DriverViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navBarHeight = systemBarsInsets.bottom
            view.setPadding(0, 0, 0, navBarHeight)
            insets
        }
        getDriverId()
    }


    private fun getDriverId(){
        val bundle = intent.extras
        val riderId = bundle?.getSerializable(Constants.DRIVER_ID) as UUID
        driverViewModel.setDriverId(riderId)
    }

}