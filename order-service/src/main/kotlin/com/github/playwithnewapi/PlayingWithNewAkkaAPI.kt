package com.github.playwithnewapi

import akka.actor.typed.ActorSystem
import com.github.eventdrivenecomm.orderservice.infrastructure.actor.OrderActor
import com.github.eventdrivenecomm.orderservice.infrastructure.actor.guardianActor
import com.github.eventdrivenecomm.orderservice.infrastructure.akka.factory.CommandFirerFactory
import com.github.eventdrivenecomm.orderservice.domain.Get
import com.typesafe.config.ConfigFactory


fun main(args: Array<String>) {
    runAppV2(args)
}

fun runAppV2(args: Array<String>) {

    val config =
        ConfigFactory
            .parseString(
                """
                    akka.remote.artery.canonical.port = ${args[0]}    
                    """.trimIndent())
            .withFallback(ConfigFactory.defaultApplication())

    val mySystem = ActorSystem.create<Void>(guardianActor,
                                           "my-system",
                                           config)

    val commandFirer = CommandFirerFactory().create("OrderV2", mySystem) { id -> OrderActor(id) }

    Thread.sleep(10000)

//    val command: OrderCommand = CreateOrderCommand(
//        "order-12",
//        "user-2",
//        listOf())
//
//    commandFirer
//        .fire(command, "order-12")
//        .whenComplete { response, failure ->
//            if (failure == null) {
//                println("Create Order worked well: $response")
//            } else {
//                println("Did not work: $failure")
//            }
//        }


    Thread.sleep(2000)

    commandFirer
        .fire(Get("order-10"), "order-10")
        .whenComplete { response, failure ->
            if (failure == null) {
                println("Worked well: $response")
            } else {
                println("Did not work: $failure")
            }
        }

}