package com.github.eventdrivenecomm.orderservice.application.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class TokenCreator(
    private val algorithm: Algorithm
) {

    fun createToken(email: String, role: String): String {
        return JWT.create()
            .withIssuedAt(Date())
            .withSubject(email)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000))
            .sign(algorithm)
    }
}
