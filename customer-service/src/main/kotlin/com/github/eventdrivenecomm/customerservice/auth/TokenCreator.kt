package com.github.eventdrivenecomm.customerservice.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.eventdrivenecomm.customerservice.domain.model.User
import java.util.*

class TokenCreator(
    private val algorithm: Algorithm
) {

    fun createToken(user: User, role: String): String {
        return JWT.create()
            .withIssuedAt(Date())
            .withSubject(user.email)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000))
            .sign(algorithm)
    }
}
