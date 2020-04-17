package com.github.eventdrivenecomm.orderservice

import com.github.eventdrivenecomm.orderservice.dimodules.startMainModule
import com.github.eventdrivenecomm.orderservice.restapi.routes.OrderRouter
import io.javalin.Javalin
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin

class App(
    private val orderRouter: OrderRouter
) : KoinComponent {

    fun start(httpPort: Int): Javalin {
        val app = Javalin
            .create { config ->
                config.enableDevLogging()
            }
            .routes {
                orderRouter.addRoutes()
            }
            .start(httpPort)

        return app
    }
}

fun main(args: Array<String>) {

    //Disabling Jetty logs
    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog")
    System.setProperty("org.eclipse.jetty.LEVEL", "OFF")

    val clusterNodePort = if (args.isNotEmpty()) args[0].toInt() else 2551
    check(clusterNodePort > 0)

    startKoin {
        modules(startMainModule(mapOf("akka-node-port" to "$clusterNodePort")))
    }
        .koin
        .get<App>()
        .start(7001)
}
