package com.github.eventdrivenecomm.customerservice.config

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

internal object database : PropertyGroup() {
    val url by stringType
    val driver by stringType
    val username by stringType
    val password by stringType
}

class EnvironmentConfig {

    private val config =
        EnvironmentVariables() overriding
        ConfigurationProperties.fromResource("defaults.properties")

    val databaseUrl = config[database.url]
    val databaseDriver = config[database.driver]
    val databaseUsername = config[database.username]
    val databasePassword = config[database.password]

}