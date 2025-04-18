package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.remote.socket.socket.usecase.ConnectSocketUseCase
import com.example.uberdriver.domain.remote.socket.socket.usecase.ObserveConnectedToSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val connectSocketUseCase: ConnectSocketUseCase,
    private val observeConnectedToSocketUseCase: ObserveConnectedToSocket,
    ) : BaseViewModel(dispatcher) {

    private val connectedToSocket = MutableStateFlow<Boolean>(false)
    val socketConnected = connectedToSocket.asStateFlow()
    fun connectSocket(url: String) {
        connectSocketUseCase(url)
    }

    fun observeConnectedToSocket(){
        launchOnBack {
            observeConnectedToSocketUseCase().collect{
                connectedToSocket.emit(it)
            }
        }
    }
}