package com.github.eventdrivenecomm.customerservice.application.modules

import com.auth0.jwt.algorithms.Algorithm
import com.codahale.metrics.health.HealthCheckRegistry
import com.github.eventdrivenecomm.customerservice.application.auth.Base64Encryptor
import com.github.eventdrivenecomm.customerservice.application.auth.TokenCreator
import com.github.eventdrivenecomm.customerservice.application.auth.TokenVerifier
import com.github.eventdrivenecomm.customerservice.application.config.EnvironmentConfig
import com.github.eventdrivenecomm.customerservice.domain.repository.UserRepository
import com.github.eventdrivenecomm.customerservice.domain.service.LoginService
import com.github.eventdrivenecomm.customerservice.domain.service.RegisterService
import com.github.eventdrivenecomm.customerservice.infrastructure.datasource.DataSourceBuilder
import com.github.eventdrivenecomm.customerservice.infrastructure.persistence.UserRepositoryImpl
import com.github.eventdrivenecomm.customerservice.application.web.Router
import com.github.eventdrivenecomm.customerservice.application.web.controllers.HealthCheckController
import com.github.eventdrivenecomm.customerservice.application.web.controllers.LoginController
import com.github.eventdrivenecomm.customerservice.application.web.controllers.RegisterController
import org.koin.dsl.module

val configModule = module {
    single { EnvironmentConfig() }
}

val healthCheckModule = module {
    single { HealthCheckRegistry() }
}

val datasourceModule = module {
    single { DataSourceBuilder(get(), get()).build() }
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
    single { Router(get(), get(), get()) }
    single { HealthCheckController(get()) }
}