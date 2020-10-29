package com.github.eventdrivenecomm.customerservice

import com.github.eventdrivenecomm.customerservice.infrastructure.persistence.UserTable
import com.github.eventdrivenecomm.customerservice.modules.authModule
import com.github.eventdrivenecomm.customerservice.modules.configModule
import com.github.eventdrivenecomm.customerservice.modules.datasourceModule
import com.github.eventdrivenecomm.customerservice.modules.domainModule
import com.github.eventdrivenecomm.customerservice.modules.healthCheckModule
import com.github.eventdrivenecomm.customerservice.modules.webModule
import com.github.eventdrivenecomm.customerservice.web.Router
import io.javalin.Javalin
import io.javalin.plugin.metrics.MicrometerPlugin
import io.micrometer.core.instrument.Metrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import javax.sql.DataSource

class CustomerApp : KoinComponent {

    private val router: Router by inject()
    private val dataSource: DataSource by inject()

    fun setup() {

        startKoin {
            modules(configModule,
                    healthCheckModule,
                    domainModule,
                    authModule,
                    datasourceModule,
                    webModule)
        }

        Database.connect(dataSource)

        //TODO: think of a better strategy to bootstrap the database
        transaction {
            SchemaUtils.create(UserTable)
        }

        Javalin
            .create { config ->
                config.accessManager { handler, ctx, permittedRoles ->
                    handler.handle(ctx)
                }
                config.registerPlugin(MicrometerPlugin())
            }.apply {
                router.routes(this)

                //configuring metrics endpoint
                val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
                Metrics.globalRegistry.add(prometheusRegistry)
                this.get("/metrics") { ctx ->
                    ctx.result(prometheusRegistry.scrape())
                }

                this.exception(RuntimeException::class.java) { e, ctx ->
                    ctx.status(HttpStatus.BAD_REQUEST_400)
                    ctx.result(e.message ?: "Bad request")
                }
            }.start(7002)
    }
}


/**
 * App entry point
 * Running inside Intellij with postgres; set environment variables:
 *  DATABASE_USERNAME=root;DATABASE_PASSWORD=test123;DATABASE_PORT=5432;PERSISTENCE_MODE=postgres
 *
 * Running with docker:
 *
 * docker run --name postgres -e POSTGRES_DB=customer_service -e POSTGRES_USER=root -e POSTGRES_PASSWORD=test123 -d -p 5432:5432 postgres
 * docker run mvgagliotti/customer-service:1.2 -e DATABASE_USERNAME=root -e DATABASE_PASSWORD=test123 -e DATABASE_PORT=5432 -e PERSISTENCE_MODE=postgres -d -p 5432:5432 customer-service
 *
 */
fun main() {

    CustomerApp().setup()

}
