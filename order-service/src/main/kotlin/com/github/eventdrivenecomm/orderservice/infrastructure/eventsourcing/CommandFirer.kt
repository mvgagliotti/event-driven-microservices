package com.github.eventdrivenecomm.orderservice.infrastructure.eventsourcing

import java.util.concurrent.CompletionStage

interface CommandFirer<Command, Event> {

    fun fire(command: Command, entityId: String): CompletionStage<Event>

}
