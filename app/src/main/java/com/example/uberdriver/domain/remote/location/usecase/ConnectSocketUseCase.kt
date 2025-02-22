package com.example.uberdriver.domain.remote.location.usecase

import com.example.uberdriver.domain.remote.location.repository.SocketRepository
import javax.inject.Inject

class ConnectSocketUseCase @Inject constructor(private val repository: SocketRepository) {
     operator fun invoke(url: String) = repository.connect(url)
}