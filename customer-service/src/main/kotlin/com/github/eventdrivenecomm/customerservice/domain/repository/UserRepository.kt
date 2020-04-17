package com.github.eventdrivenecomm.customerservice.domain.repository

import com.github.eventdrivenecomm.customerservice.domain.model.User

interface UserRepository {

    fun create(user: User): User
    fun findByEmail(email: String): User?

}