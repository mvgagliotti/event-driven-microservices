package com.github.eventdrivenecomm.customerservice.web.controllers

import com.github.eventdrivenecomm.customerservice.auth.TokenCreator
import com.github.eventdrivenecomm.customerservice.application.dto.LoginDTO
import com.github.eventdrivenecomm.customerservice.domain.model.User
import com.github.eventdrivenecomm.customerservice.application.service.LoginService
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse

class LoginController(
    private val tokenCreator: TokenCreator,
    private val loginService: LoginService
) {

    fun login(ctx: Context) {
        val login = ctx.bodyAsClass(LoginDTO::class.java)

        loginService.validateLogin(login).also { if (!it) throw ForbiddenResponse() }

        val user = User(email = login.email, password = login.password)
        val token = TokenDTO(token = tokenCreator.createToken(user, "AUTHENTICATED"))

        ctx.json(token)
    }

    fun validateLogin(ctx: Context) {
        val login = ctx.bodyAsClass(LoginDTO::class.java)
        loginService.validateLogin(login).also { if (!it) throw ForbiddenResponse() }
    }
}

data class TokenDTO(
    val token: String
)
