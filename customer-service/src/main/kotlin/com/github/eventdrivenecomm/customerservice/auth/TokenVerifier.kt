package com.github.eventdrivenecomm.customerservice.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class TokenVerifier(
    private val algorithm: Algorithm
) {

    fun verify(vararg permittedRoles: String, token: String): Boolean {
        return JWT
            .require(algorithm)
            .build()
            .verify(token)
            .let { jwtToken ->
                val userRole = jwtToken?.getClaim("role")?.asString() ?: return false
                permittedRoles.contains(userRole)
            }
    }
}
