package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(dispatcher : IDispatchers) : BaseViewModel(dispatcher) {
}