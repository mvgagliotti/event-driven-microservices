package com.github.eventdrivenecomm.customerservice

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object database : PropertyGroup() {
    val url by stringType
    val driver by stringType
    val username by stringType
    val password by stringType
}

/**
 * To run with postgres:
 *
 * 1. docker run --name postgres -e POSTGRES_USER=root -e POSTGRES_PASSWORD=test123 -d -p 5432:5432 postgres
 * 2. enter psql and create the database: docker exec -it postgres /bin/bash | psql | create database customer_service
 * 3. Place the environment variables:
 *
 *  DATABASE_URL=jdbc:postgresql://localhost:5432/customer_service?user=root;
 *  DATABASE_DRIVER=org.postgresql.Driver;
 *  DATABASE_USERNAME=root;
 *  DATABASE_PASSWORD=test123
 */

fun main() {

    val config =
        EnvironmentVariables() overriding
        ConfigurationProperties.fromResource("defaults.properties")

    val dataSource =
        HikariConfig().also {
            it.jdbcUrl = config[database.url]
            it.driverClassName = config[database.driver]
            it.username = config[database.username]
            it.password = config[database.password]
        }.let {
            HikariDataSource(it)
        }

    //an example connection to H2 DB
    //Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    Database.connect(dataSource)

    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Cities)

        // insert new city. SQL: INSERT INTO Cities (name) VALUES ('St. Petersburg')
        val stPeteId = Cities.insert {
            it[name] = "St. Petersburg"
        } get Cities.id

        // 'select *' SQL: SELECT Cities.id, Cities.name FROM Cities
        println("Cities:")
        Cities.selectAll().forEach {
            println(it[Cities.name])
        }
    }
}

object Cities : IntIdTable() {
    val name = varchar("name", 50)
}