package com.github.eventdrivenecomm.orderservice.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.LoggerFactory

class TokenVerifier(
    private val algorithm: Algorithm
) {

    private val logger = LoggerFactory.getLogger(TokenVerifier::class.java)

    fun verify(vararg permittedRoles: String, token: String): Boolean = kotlin.runCatching {
        return JWT
            .require(algorithm)
            .build()
            .verify(token)
            .let { jwtToken ->
                val userRole = jwtToken?.getClaim("role")?.asString() ?: return false
                permittedRoles.contains(userRole)
            }
    }.getOrElse {
        logger.error(it.message)
        false
    }
}
