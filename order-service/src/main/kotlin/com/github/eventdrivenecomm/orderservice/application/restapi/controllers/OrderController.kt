package com.github.eventdrivenecomm.orderservice.application.restapi.controllers

import com.github.eventdrivenecomm.orderservice.domain.*
import com.github.eventdrivenecomm.orderservice.infrastructure.eventsourcing.CommandFirer
import com.github.eventdrivenecomm.orderservice.application.restapi.dtos.ItemDTO
import com.github.eventdrivenecomm.orderservice.application.restapi.dtos.OrderCreatedResponseDTO
import com.github.eventdrivenecomm.orderservice.application.restapi.dtos.OrderDTO
import com.github.eventdrivenecomm.orderservice.application.restapi.extensions.currentUser
import io.javalin.http.Context
import org.slf4j.LoggerFactory
import java.util.*


class OrderController(
    private val commandFirer: CommandFirer<OrderCommand, OrderEvent>
) {

    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    fun get(ctx: Context) {
        val id = ctx.pathParam("order-id")
        val command = Get(id)

        val future = commandFirer
            .fire(command, id)
            .toCompletableFuture()
            .thenApply { evt ->
                val order = (evt as GetEvent).order
                OrderDTO(order.items.map {
                    ItemDTO(it.id, it.description, it.amount, it.value)
                })
            }

        ctx.json(future)
    }

    fun post(ctx: Context) {
        logger.info("$commandFirer is ready!")

        //step1: translate to dto
        val dto = ctx.bodyAsClass(OrderDTO::class.java)
        logger.info(dto.toString())

        //step2: convert from dto to domain
        val id = UUID.randomUUID().toString()
        val items = dto.items.map {
            Item(
                id = it.id,
                description = it.description,
                amount = it.amount,
                value = it.price
            )
        }

        //step3: create the command and fire it
        val user = ctx.currentUser()
        val command: OrderCommand = CreateOrderCommand(
            id, user, items
        )

        val future =
            commandFirer
                .fire(command, id)
                .toCompletableFuture()
                .thenApply { orderCreatedEvent -> OrderCreatedResponseDTO(orderCreatedEvent.orderId) }

        //step4: set the async future to the result
        ctx.json(future)
    }
}
