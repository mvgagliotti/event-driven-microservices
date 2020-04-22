package com.github.eventdrivenecomm.customerservice.web.controllers

import com.codahale.metrics.health.HealthCheckRegistry
import io.javalin.http.Context

class HealthCheckController(
    private val healthCheckRegistry: HealthCheckRegistry
) {

    fun performHealthCheck(ctx: Context) {
        healthCheckRegistry
            .runHealthChecks()
            .map { it.key to it.value }
            .partition { (key, result) -> result.isHealthy }
            .also { (successHCs, failedHCs) ->
                if (failedHCs.isEmpty()) {
                    ctx.json(
                        HealthCheckDTO(status = "HEALTHY",
                                       details = successHCs.map {
                                           HCDetail(item = it.first,
                                                    message = it.second.message ?: "OK")
                                       }))
                } else {
                    ctx.status(400) //TODO: check proper status
                    ctx.json(
                        HealthCheckDTO(status = "UNHEALTHY",
                                       details = failedHCs.map {
                                           HCDetail(item = it.first,
                                                    message = it.second.message)
                                       }))

                }
            }
    }
}

data class HealthCheckDTO(
    val status: String,
    val details: List<HCDetail>? = null
)

data class HCDetail(
    val item: String,
    val message: String
)