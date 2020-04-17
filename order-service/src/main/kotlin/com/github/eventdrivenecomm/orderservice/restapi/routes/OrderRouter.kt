package com.github.eventdrivenecomm.orderservice.restapi.routes

import com.github.eventdrivenecomm.orderservice.restapi.controllers.OrderController
import io.javalin.apibuilder.ApiBuilder

class OrderRouter(
    private val orderController: OrderController
) {
    fun addRoutes() {
        ApiBuilder.path("order") {
            ApiBuilder.post(orderController::post)
            ApiBuilder.get(":order-id", orderController::get)
        }
    }
}