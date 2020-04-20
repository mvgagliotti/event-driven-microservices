package com.github.eventdrivenecomm.customerservice.web.controllers

import com.github.eventdrivenecomm.customerservice.domain.model.User
import com.github.eventdrivenecomm.customerservice.application.service.RegisterService
import io.javalin.http.Context
import org.slf4j.LoggerFactory

class RegisterController(
    private val registerService: RegisterService
) {

    private val logger = LoggerFactory.getLogger(RegisterController::class.java)

    fun register(ctx: Context) {
        val dto = ctx.bodyAsClass(RegisterDTO::class.java)
        logger.info("Received a register request: ${dto.email}")
        registerService.registerUser(User(email = dto.email, password = dto.password))
    }
}

data class RegisterDTO(
    val email: String,
    val password: String
)
