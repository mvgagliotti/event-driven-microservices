package com.github.eventdrivenecomm.customerservice

import com.github.eventdrivenecomm.customerservice.infrastructure.persistence.UserTable
import com.github.eventdrivenecomm.customerservice.modules.authModule
import com.github.eventdrivenecomm.customerservice.modules.configModule
import com.github.eventdrivenecomm.customerservice.modules.datasourceModule
import com.github.eventdrivenecomm.customerservice.modules.domainModule
import com.github.eventdrivenecomm.customerservice.modules.webModule
import com.github.eventdrivenecomm.customerservice.web.Router
import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import javax.sql.DataSource

class App : KoinComponent {

    private val router: Router by inject()
    private val dataSource: DataSource by inject()

    fun setup() {

        startKoin {
            modules(configModule,
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
            }.apply {
                router.routes(this)
            }.start(7000)
    }
}


/**
 * App entry point
 *
 */
fun main() {

    App().setup()

}
