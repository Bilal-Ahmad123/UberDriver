package com.example.uberdriver.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val db: CoroutineDispatcher

    val computation: CoroutineDispatcher
}