package com.github.eventdrivenecomm.customerservice.web

import com.github.eventdrivenecomm.customerservice.web.controllers.HealthCheckController
import com.github.eventdrivenecomm.customerservice.web.controllers.LoginController
import com.github.eventdrivenecomm.customerservice.web.controllers.RegisterController
import io.javalin.Javalin
import io.javalin.core.security.Role

enum class Roles : Role {
    ANYONE, AUTHENTICATED
}

class Router(
    private val registerController: RegisterController,
    private val loginController: LoginController,
    private val healthCheckController: HealthCheckController
) {

    fun routes(javalin: Javalin) = javalin.apply {

        post("/register", registerController::register, setOf(Roles.ANYONE))

        post("/login", loginController::login, setOf(Roles.ANYONE))

        post("/validate-login", loginController::validateLogin, setOf(Roles.ANYONE))

        get("/health", healthCheckController::performHealthCheck)

    }
}
