package com.github.eventdrivenecomm.orderservice.application.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.slf4j.LoggerFactory
import java.lang.RuntimeException

class TokenHandler(
    private val algorithm: Algorithm
) {

    private val logger = LoggerFactory.getLogger(TokenHandler::class.java)

    fun extractJwtToken(token: String) = kotlin.runCatching {
        JWT
            .require(algorithm)
            .build()
            .verify(token)
    }.getOrElse { throw RuntimeException("Error parsing token: ${it.message}") }

    fun verify(vararg permittedRoles: String, token: DecodedJWT): Boolean = kotlin.runCatching {
        val userRole = token?.getClaim("role")?.asString() ?: return false
        permittedRoles.contains(userRole)
    }.getOrElse {
        logger.error(it.message)
        false
    }
}
