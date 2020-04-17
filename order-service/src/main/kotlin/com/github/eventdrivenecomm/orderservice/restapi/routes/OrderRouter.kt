package com.github.eventdrivenecomm.orderservice.restapi.routes

import com.github.eventdrivenecomm.orderservice.App
import com.github.eventdrivenecomm.orderservice.restapi.controllers.LoginController
import com.github.eventdrivenecomm.orderservice.restapi.controllers.OrderController
import io.javalin.apibuilder.ApiBuilder

class OrderRouter(
    private val orderController: OrderController,
    private val loginController: LoginController
) {
    fun addRoutes() {

        ApiBuilder.path("order") {
            ApiBuilder.post(orderController::post, setOf(App.Roles.AUTHENTICATED))
            ApiBuilder.get(":order-id", orderController::get, setOf(App.Roles.AUTHENTICATED))
        }

        ApiBuilder.path("/login") {
            ApiBuilder.post(loginController::login, setOf(App.Roles.ANYONE))
        }
    }
}