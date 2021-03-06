package com.github.eventdrivenecomm.orderservice.application.dimodules

import com.auth0.jwt.algorithms.Algorithm
import com.github.eventdrivenecomm.orderservice.application.auth.Base64Encryptor
import com.github.eventdrivenecomm.orderservice.application.auth.TokenCreator
import com.github.eventdrivenecomm.orderservice.application.auth.TokenHandler
import org.koin.dsl.module

val authModule = module {
    single { Algorithm.HMAC256("test-secret") }
    single { Base64Encryptor(get()) }
    single { TokenCreator(get()) }
    single { TokenHandler(get()) }
}