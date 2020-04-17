package com.github.eventdrivenecomm.customerservice.infrastructure.datasource

import com.github.eventdrivenecomm.customerservice.config.EnvironmentConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

class DataSourceBuilder(
    private val config: EnvironmentConfig
) {

    fun build(): DataSource =
        HikariConfig().also {
            it.jdbcUrl = config.databaseUrl
            it.driverClassName = config.databaseDriver
            it.username = config.databaseUsername
            it.password = config.databasePassword
        }.let {
            HikariDataSource(it)
        }
}