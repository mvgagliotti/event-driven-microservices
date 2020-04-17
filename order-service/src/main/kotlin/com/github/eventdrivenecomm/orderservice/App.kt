package com.github.eventdrivenecomm.orderservice

import com.github.eventdrivenecomm.orderservice.auth.TokenVerifier
import com.github.eventdrivenecomm.orderservice.dimodules.authModule
import com.github.eventdrivenecomm.orderservice.dimodules.startMainModule
import com.github.eventdrivenecomm.orderservice.restapi.routes.OrderRouter
import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.http.ForbiddenResponse
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject

class App : KoinComponent {

    private val orderRouter: OrderRouter by inject()
    private val tokenVerifier: TokenVerifier by inject()

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

                    val token = ctx.header("Authorization")?.substringAfter("Token ") ?: ""
                    tokenVerifier
                        .verify(permittedRoles = *permittedRoles.map { it.toString() }.toTypedArray(),
                                token = token)
                        .also { if (!it) throw ForbiddenResponse() }

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

/**
 * TODO: review the way ports are obtained: it's better to put them on env variables rather than command line params
 *
 */
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
        .get<App>()
        .start(7001)
}
