package com.agilogy.timetracking.registration.infrastructure

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.*

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*

class TimeEntriesControllerTest : FunSpec() {

    val timeEntriesController = TimeEntriesController(TODO())

    val testApp = testApplication {
        application {
            timeEntriesController.routes()
        }
    }

    val client = createClient {
        install(ContentNegotiation) {
            json()
        }
    }
    init{
        test("foo"){

        }
    }
}
