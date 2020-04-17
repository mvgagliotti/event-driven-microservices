package com.github.eventdrivenecomm.customerservice.web.controllers

import com.github.eventdrivenecomm.customerservice.auth.TokenCreator
import com.github.eventdrivenecomm.customerservice.domain.model.User
import io.javalin.http.Context

class LoginController(
    private val tokenCreator: TokenCreator
) {

    fun login(ctx: Context) {
        val login = ctx.bodyAsClass(LoginDTO::class.java)

        //TODO: validate credentials
        val user: User = User(email = login.email,
                                                                                                                                                                                      password = login.password)

        val token = TokenDTO(token = tokenCreator.createToken(user, "AUTHENTICATED"))

        ctx.json(token)
    }
}

data class LoginDTO(
    val email: String,
    val password: String
)

data class TokenDTO(
    val token: String
)
