package com.github.eventdrivenecomm.orderservice.restapi.extensions

import io.javalin.http.Context

fun Context.currentUser() = this.attribute<String>("USER")!!