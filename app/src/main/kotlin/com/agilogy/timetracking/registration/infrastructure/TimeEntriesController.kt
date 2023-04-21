package com.agilogy.timetracking.registration.infrastructure

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class TimeEntriesController {

    context(Routing)
    fun timeEntriesRoutes() {
        get("/") {
            call.respondText("[]", ContentType.Application.Json)
        }
    }

}


    fun main() {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            routing {
                TimeEntriesController().timeEntriesRoutes()
                post("/haha") {
                    call.respondText("[]", ContentType.Application.Json)
                }
            }
        }.start(wait = true)
    }
