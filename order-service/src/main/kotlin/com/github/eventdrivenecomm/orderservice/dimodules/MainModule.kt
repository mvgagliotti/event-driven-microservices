package com.github.eventdrivenecomm.orderservice.dimodules

import akka.actor.typed.ActorSystem
import com.github.eventdrivenecomm.orderservice.OrderServiceApp
import com.github.eventdrivenecomm.orderservice.actor.OrderActor
import com.github.eventdrivenecomm.orderservice.actor.guardianActor
import com.github.eventdrivenecomm.orderservice.akka.factory.CommandFirerFactory
import com.github.eventdrivenecomm.orderservice.domain.OrderCommand
import com.github.eventdrivenecomm.orderservice.domain.OrderEvent
import com.github.eventdrivenecomm.orderservice.eventsourcing.CommandFirer
import com.github.eventdrivenecomm.orderservice.restapi.controllers.LoginController
import com.github.eventdrivenecomm.orderservice.restapi.controllers.OrderController
import com.github.eventdrivenecomm.orderservice.restapi.routes.OrderRouter
import com.typesafe.config.ConfigFactory
import org.koin.dsl.module

fun startMainModule(values: Map<String, String>) = module {

    single<ActorSystem<Void>> {
        //TODO: move boilerplate code to a factory
        val config =
            ConfigFactory
                .parseString(
                    """
                    akka.remote.artery.canonical.port = ${values["akka-node-port"]}    
                    """.trimIndent()
                )
                .withFallback(ConfigFactory.defaultApplication())

        val mySystem = ActorSystem.create<Void>(
            guardianActor,
            "my-system", //TODO: refactor this, get it from the outside
            config
        )
        return@single mySystem
    }

    single<CommandFirer<OrderCommand, OrderEvent>> {
        CommandFirerFactory().create("OrderEntity", get()) { id -> OrderActor(id) }
    }

    single { OrderController(get()) }
    single { LoginController() }
    single { OrderRouter(get(), get()) }
    single { OrderServiceApp() }
}
