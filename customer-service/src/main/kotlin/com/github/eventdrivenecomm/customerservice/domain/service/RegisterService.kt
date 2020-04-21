package com.github.eventdrivenecomm.customerservice.domain.service

import com.github.eventdrivenecomm.customerservice.auth.Base64Encryptor
import com.github.eventdrivenecomm.customerservice.domain.exceptions.EmailAlreadyRegisteredException
import com.github.eventdrivenecomm.customerservice.domain.model.User
import com.github.eventdrivenecomm.customerservice.domain.repository.UserRepository

class RegisterService(
    private val userRepository: UserRepository,
    private val encryptor: Base64Encryptor
) {
    fun registerUser(user: User) {
        userRepository.findByEmail(user.email)?.let { throw EmailAlreadyRegisteredException() }
        userRepository.create(user.copy(password = encryptor.encrypt(user.password)))
    }
}