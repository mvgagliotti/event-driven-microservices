package com.github.eventdrivenecomm.customerservice.application.auth

import com.auth0.jwt.algorithms.Algorithm

class Base64Encryptor(
    private val algorithm: Algorithm
) {
    fun encrypt(value: String): String {
        return String(algorithm.sign(value.toByteArray()))
    }
}
