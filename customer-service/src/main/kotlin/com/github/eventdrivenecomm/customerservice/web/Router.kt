package com.github.eventdrivenecomm.customerservice.web

import com.github.eventdrivenecomm.customerservice.web.controllers.LoginController
import com.github.eventdrivenecomm.customerservice.web.controllers.RegisterController
import io.javalin.Javalin
import io.javalin.core.security.Role

enum class Roles : Role {
    ANYONE, AUTHENTICATED
}

class Router(
    private val registerController: RegisterController,
    private val loginController: LoginController
) {

    fun routes(javalin: Javalin) = javalin.apply {

        get("/") { ctx -> ctx.result("Hello World") }

        post("/register", registerController::register, setOf(Roles.AUTHENTICATED))

        post("/login", loginController::login, setOf(Roles.ANYONE))

    }
}
