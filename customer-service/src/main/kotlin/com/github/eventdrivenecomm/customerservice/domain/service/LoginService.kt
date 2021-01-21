package com.github.eventdrivenecomm.customerservice.domain.service

import com.github.eventdrivenecomm.customerservice.application.auth.Base64Encryptor
import com.github.eventdrivenecomm.customerservice.domain.dto.LoginDTO
import com.github.eventdrivenecomm.customerservice.domain.repository.UserRepository

class LoginService(
    private val userRepository: UserRepository,
    private val encryptor: Base64Encryptor
) {

    fun validateLogin(login: LoginDTO): Boolean {

        val user = userRepository.findByEmail(login.email) ?: return false
        val valid = encryptor.encrypt(login.password) == user.password

        return valid
    }
}