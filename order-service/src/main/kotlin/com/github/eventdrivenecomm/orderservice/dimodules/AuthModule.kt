package com.github.eventdrivenecomm.orderservice.dimodules

import com.auth0.jwt.algorithms.Algorithm
import com.github.eventdrivenecomm.orderservice.auth.Base64Encryptor
import com.github.eventdrivenecomm.orderservice.auth.TokenCreator
import com.github.eventdrivenecomm.orderservice.auth.TokenVerifier
import org.koin.dsl.module

val authModule = module {
    single { Algorithm.HMAC256("test-secret") }
    single { Base64Encryptor(get()) }
    single { TokenCreator(get()) }
    single { TokenVerifier(get()) }
}