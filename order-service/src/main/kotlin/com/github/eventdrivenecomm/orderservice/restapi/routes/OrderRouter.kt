package com.github.eventdrivenecomm.orderservice.restapi.routes

import com.github.eventdrivenecomm.orderservice.OrderServiceApp
import com.github.eventdrivenecomm.orderservice.restapi.controllers.LoginController
import com.github.eventdrivenecomm.orderservice.restapi.controllers.OrderController
import io.javalin.apibuilder.ApiBuilder

class OrderRouter(
    private val orderController: OrderController,
    private val loginController: LoginController
) {
    fun addRoutes() {

        ApiBuilder.path("order") {
            ApiBuilder.post(orderController::post, setOf(OrderServiceApp.Roles.AUTHENTICATED))
            ApiBuilder.get(":order-id", orderController::get, setOf(OrderServiceApp.Roles.AUTHENTICATED))
        }

        ApiBuilder.path("/login") {
            ApiBuilder.post(loginController::login, setOf(OrderServiceApp.Roles.ANYONE))
        }
    }
}