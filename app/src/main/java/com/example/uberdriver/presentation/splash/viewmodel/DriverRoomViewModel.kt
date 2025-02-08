package com.example.uberdriver.presentation.splash.viewmodel

import android.util.Log
import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.local.Driver.model.Driver
import com.example.uberdriver.domain.local.Driver.usecase.GetDriver
import com.example.uberdriver.domain.local.Driver.usecase.InsertDriver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DriverRoomViewModel @Inject constructor(
    private val getDriverUseCase: GetDriver,
    private val insertDriverUseCase: InsertDriver,
    dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {
    private val _driver = MutableStateFlow<Driver?>(null)
    val driver get() = _driver
    fun getDriver() {
        launchOnDb {
            _driver.emit(getDriverUseCase())
        }
    }

    fun insertDriver(driver: Driver) {
        launchOnDb {
            insertDriverUseCase(driver)
        }
    }
}