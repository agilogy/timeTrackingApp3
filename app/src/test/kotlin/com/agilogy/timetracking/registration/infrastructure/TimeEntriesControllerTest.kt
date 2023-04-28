package com.agilogy.timetracking.registration.infrastructure

import com.agilogy.timetracking.registration.domain.InMemoryTimeEntriesRepository
import com.agilogy.timetracking.registration.domain.TimeEntriesRegister
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals

class TimeEntriesControllerTest : FunSpec() {

    @JvmName("testWithHttpClient")
    fun withApp(f: suspend (HttpClient) -> Unit) = testApplication {
        val timeEntriesController = TimeEntriesController(TimeEntriesRegister(InMemoryTimeEntriesRepository()))
        application { timeEntriesController.routes() }
        val client: HttpClient = createClient {}
        f(client)
    }

    init {
        test("empty list of entries") {
            withApp { client ->
                val response = client.get("/time-entries/daily-user-hours?userName=john&startDate=2023-04-01&endDate=2023-04-30")
                assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())
                assertEquals("[]", response.bodyAsText())
            }
        }
    }
}
