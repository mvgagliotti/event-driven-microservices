package com.github.eventdrivenecomm.customerservice.config

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import java.lang.IllegalStateException

internal object database : PropertyGroup() {
    val host by stringType
    val port by intType
    val url by stringType
    val username by stringType
    val password by stringType
}

internal object persistence: PropertyGroup() {
    val mode by stringType
}

class EnvironmentConfig {

    private val config = systemProperties() overriding
        EnvironmentVariables() overriding
        ConfigurationProperties.fromResource("defaults.properties")

    val persistenceMode = config[persistence.mode]
    val databaseHost = config[database.host]
    val databasePort = config[database.port]
    val databaseUsername = config[database.username]
    val databasePassword = config[database.password]

    val databaseDriver = when (persistenceMode) {
        "in-memory" -> "org.h2.Driver"
        "postgres" -> "org.postgresql.Driver"
        else -> throw IllegalStateException("Unsupported persistence mode: $persistenceMode")
    }

    val databaseUrl = when (persistenceMode) {
        "in-memory" -> config[database.url]
        "postgres" -> "jdbc:postgresql://${databaseHost}:${databasePort}/customer_service?user=${databaseUsername}"
        else -> throw IllegalStateException("Unsupported persistence mode: $persistenceMode")
    }
}