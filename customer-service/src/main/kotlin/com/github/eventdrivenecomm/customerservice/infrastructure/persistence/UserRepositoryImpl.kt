package com.github.eventdrivenecomm.customerservice.infrastructure.persistence

import com.github.eventdrivenecomm.customerservice.domain.model.User
import com.github.eventdrivenecomm.customerservice.domain.repository.UserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    override fun create(user: User): User = transaction {
        UserTable.insert {
            it[email] = user.email
            it[password] = user.password
        }.let { user }
    }

    override fun findByEmail(email: String): User? = transaction {
        UserTable.select {
            UserTable.email eq email
        }.firstOrNull()?.let {
            User(email = it[UserTable.email],
                 password = it[UserTable.password])
        }
    }
}