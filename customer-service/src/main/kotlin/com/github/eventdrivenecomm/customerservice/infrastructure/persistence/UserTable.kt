package com.github.eventdrivenecomm.customerservice.infrastructure.persistence

import org.jetbrains.exposed.sql.Table

object UserTable: Table() {
    val email = varchar("email", 50)
    val password = varchar("password", 100)

    override val primaryKey: PrimaryKey? = PrimaryKey(email)
}