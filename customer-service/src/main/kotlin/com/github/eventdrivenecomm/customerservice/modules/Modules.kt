package com.github.eventdrivenecomm.customerservice.modules

import com.auth0.jwt.algorithms.Algorithm
import com.github.eventdrivenecomm.customerservice.auth.Base64Encryptor
import com.github.eventdrivenecomm.customerservice.auth.TokenCreator
import com.github.eventdrivenecomm.customerservice.auth.TokenVerifier
import com.github.eventdrivenecomm.customerservice.config.EnvironmentConfig
import com.github.eventdrivenecomm.customerservice.domain.repository.UserRepository
import com.github.eventdrivenecomm.customerservice.domain.service.LoginService
import com.github.eventdrivenecomm.customerservice.domain.service.RegisterService
import com.github.eventdrivenecomm.customerservice.infrastructure.datasource.DataSourceBuilder
import com.github.eventdrivenecomm.customerservice.infrastructure.persistence.UserRepositoryImpl
import com.github.eventdrivenecomm.customerservice.web.Router
import com.github.eventdrivenecomm.customerservice.web.controllers.LoginController
import com.github.eventdrivenecomm.customerservice.web.controllers.RegisterController
import org.koin.dsl.module
import javax.sql.DataSource

val configModule = module {
    single { EnvironmentConfig() }
}

val datasourceModule = module {
    single { DataSourceBuilder(get()).build() }
}

val domainModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single { RegisterService(get(), get()) }
    single { LoginService(get(), get()) }
}

val authModule = module {
    single { Algorithm.HMAC256("test-secret") }
    single { Base64Encryptor(get()) }
    single { TokenCreator(get()) }
    single { TokenVerifier(get()) }
}

val webModule = module {
    single { RegisterController(get()) }
    single { LoginController(get(), get()) }
    single { Router(get(), get()) }
}