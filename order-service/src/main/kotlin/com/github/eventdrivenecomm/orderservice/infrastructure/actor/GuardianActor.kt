package com.github.eventdrivenecomm.orderservice.infrastructure.actor

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors
import akka.japi.function.Function
import akka.persistence.cassandra.query.javadsl.CassandraReadJournal
import akka.persistence.query.Offset
import akka.projection.ProjectionBehavior
import akka.projection.ProjectionId
import akka.projection.eventsourced.EventEnvelope
import akka.projection.eventsourced.javadsl.EventSourcedProvider
import akka.projection.javadsl.ExactlyOnceProjection
import akka.projection.javadsl.SourceProvider
import akka.projection.jdbc.JdbcSession
import akka.projection.jdbc.javadsl.JdbcHandler
import akka.projection.jdbc.javadsl.JdbcProjection
import com.github.eventdrivenecomm.orderservice.domain.OrderEvent
import com.typesafe.config.ConfigFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


val guardianActor: Behavior<Void> = Behaviors.setup { ctx ->

    //TODO: continue from:
    // https://doc.akka.io/docs/akka-projection/current/getting-started/source-provider.html

    //TODO: inmem journal does not support queries: use Cassandra's instead;
    //TODO: disable projection for in-memory mode
    //TODO: make Config a koin component, make a guardian actor factory another component, inject the first in the second

    val config = ConfigFactory.defaultApplication()

    //projection does not get started for in-mem mode
    if (!config.getBoolean("persistence.in-memory")) {
        val sourceProvider: SourceProvider<Offset, EventEnvelope<OrderEvent>> =
            EventSourcedProvider.eventsByTag<OrderEvent>(ctx.system, CassandraReadJournal.Identifier(), "SIMPLE" );

        //TODO: continue from:
        //https://doc.akka.io/docs/akka-projection/current/getting-started/projection-handler.html

        val projection: ExactlyOnceProjection<Offset, EventEnvelope<OrderEvent>> =
            JdbcProjection.exactlyOnce(
                ProjectionId.of("order-projection", "SINGLE"),
                sourceProvider,
                { PlainJdbcSession() },
                { OrderProjectionHandler() },
                ctx.system
            )

        ctx.spawn(ProjectionBehavior.create(projection), projection.projectionId().id());
    }

    Behaviors.empty<Void>()
}

/**
 * JDBC Session for Akka Projections
 *
 * TODO:
 *  1. use hikari pool and datasource (to abstract the target database)
 *
 */
class PlainJdbcSession : JdbcSession {
    private var connection: Connection? = null

    @Throws(Exception::class)
    override fun <Result> withConnection(func: Function<Connection?, Result>): Result {
        return func.apply(connection)
    }

    @Throws(SQLException::class)
    override fun commit() {
        connection!!.commit()
    }

    @Throws(SQLException::class)
    override fun rollback() {
        connection!!.rollback()
    }

    @Throws(SQLException::class)
    override fun close() {
        connection!!.close()
    }

    init {
        try {
            Class.forName("org.h2.Driver")
            connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
            connection!!.setAutoCommit(false)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }
}

/**
 * Projection Handler for Order events
 */
class OrderProjectionHandler : JdbcHandler<EventEnvelope<OrderEvent>, PlainJdbcSession>() {

    override fun process(session: PlainJdbcSession?, envelope: EventEnvelope<OrderEvent>) {
        val event = envelope.event()
        println("Projecting event: $event")
    }

}