package com.github.eventdrivenecomm.orderservice.infrastructure.actor

import akka.persistence.typed.PersistenceId
import com.github.eventdrivenecomm.orderservice.infrastructure.akka.adapter.EventSourcingActorAdapter
import com.github.eventdrivenecomm.orderservice.domain.OrderAggregate
import com.github.eventdrivenecomm.orderservice.domain.OrderCommand
import com.github.eventdrivenecomm.orderservice.domain.OrderEvent
import com.github.eventdrivenecomm.orderservice.domain.OrderState
import com.github.eventdrivenecomm.orderservice.infrastructure.eventsourcing.ESAggregate

class OrderActor(
    persistenceId: PersistenceId? = null
) : EventSourcingActorAdapter<OrderCommand, OrderEvent, OrderState>(persistenceId) {

    override val adaptee: ESAggregate<OrderCommand, OrderEvent, OrderState>
        get() = OrderAggregate
}