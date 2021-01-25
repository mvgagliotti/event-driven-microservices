package com.github.eventdrivenecomm.orderservice.application.restapi.dtos

import java.math.BigDecimal

data class OrderDTO(
    val items: List<ItemDTO>
)

data class ItemDTO(
    val id: String,
    val description: String,
    val amount: Int,
    val price: BigDecimal
)

data class OrderCreatedResponseDTO(
    val id: String
)
