package com.github.eventdrivenecomm.orderservice

import com.github.eventdrivenecomm.orderservice.application.auth.TokenHandler
import com.github.eventdrivenecomm.orderservice.application.dimodules.authModule
import com.github.eventdrivenecomm.orderservice.application.dimodules.startMainModule
import com.github.eventdrivenecomm.orderservice.application.restapi.routes.OrderRouter
import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.http.ForbiddenResponse
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject

/**
 * TODO: review the way ports are obtained: it's better to put them on env variables rather than command line params
 *
 * To run in memory:
 * -Dconfig.resource=app-in-mem.conf
 *
 */
class OrderServiceApp : KoinComponent {

    private val orderRouter: OrderRouter by inject()
    private val tokenHandler: TokenHandler by inject()

    enum class Roles : Role {
        ANYONE, AUTHENTICATED
    }

    fun start(httpPort: Int): Javalin {
        val app = Javalin
            .create { config ->
                config.enableDevLogging()
                config.accessManager { handler, ctx, permittedRoles ->

                    if (permittedRoles.isEmpty() ||
                        permittedRoles.size == 1 && permittedRoles.first() == Roles.ANYONE) {
                        handler.handle(ctx)
                        return@accessManager
                    }

                    val authBase64Token = ctx.header("Authorization")?.substringAfter("Token ") ?: ""
                    val jwtToken = tokenHandler.extractJwtToken(authBase64Token)

                    tokenHandler
                        .verify(permittedRoles = *permittedRoles.map { it.toString() }.toTypedArray(),
                            token = jwtToken)
                        .also { if (!it) throw ForbiddenResponse() }

                    ctx.attribute("USER", jwtToken.subject)
                    handler.handle(ctx)
                }
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
        modules(authModule,
            startMainModule(mapOf("akka-node-port" to "$clusterNodePort")))
    }
        .koin
        .get<OrderServiceApp>()
        .start(7001)
}
