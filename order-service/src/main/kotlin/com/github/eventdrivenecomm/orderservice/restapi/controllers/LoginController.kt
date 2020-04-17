package com.github.eventdrivenecomm.orderservice.restapi.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import org.slf4j.LoggerFactory
import java.nio.charset.Charset

class LoginController {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)


    /**
     * A lot of refactoring to be done here:
     *
     * 1. use konfig to get url to customer-service
     * 2. maybe use async request to avoid blocking
     * 3. extract DTOs to somewhere else
     * 4. extract fuel logic to some http client utils
     *
     */

    fun login(ctx: Context) {
        val credentials = ctx.bodyAsClass(LoginDTO::class.java)
        logger.info("Validation credentials for ${credentials.email}")

        val mapper = ObjectMapper().registerKotlinModule()

        Fuel
            .post("http://localhost:7002/login")
            .objectBody(bodyObject = credentials, mapper = mapper)
            .responseString()
            .let { (request, response, result) ->

                result.fold(
                    success = { result ->
                        ctx.json(mapper.readValue(result, TokenDTO::class.java))
                    },
                    failure = { failure ->
                        logger.error("Failed to get credentials: ${failure.response}")
                        throw UnauthorizedResponse()
                    }
                )
            }
    }
}

data class LoginDTO(
    val email: String,
    val password: String
)

data class TokenDTO(
    val token: String
)

/**
 * Set the body to an Object to be serialized
 */
fun Request.objectBody(
    bodyObject: Any,
    charset: Charset = Charsets.UTF_8,
    mapper: ObjectMapper
): Request {
    val bodyString = mapper.writeValueAsString(bodyObject)
    this[Headers.CONTENT_TYPE] = "application/json"
    return body(bodyString, charset)
}