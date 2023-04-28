package com.agilogy.timetracking.registration.infrastructure

import arrow.core.Either
import arrow.core.getOrElse
import arrow.fx.coroutines.use
import com.agilogy.db.hikari.HikariCp
import com.agilogy.timetracking.registration.domain.TimeEntriesRegister
import com.agilogy.timetracking.user.domain.UserName
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.Duration
import java.time.LocalDate

class TimeEntriesController(private val timeEntriesRegister: TimeEntriesRegister) {

    context(Application)
    fun routes() {
        routing { timeEntriesRoutes() }
    }

    context(Routing)
    fun timeEntriesRoutes() {
        // GET /time-entries/daily-user-hours?userName=userName&startDate=startDate&endDate=endDate
        get("/time-entries/daily-user-hours") {

            Either.zipOrAccumulate(
                call.request.queryParameters.parseRequiredParam("userName") { UserName(it) },
                call.request.queryParameters.parseRequiredParam("startDate") { LocalDate.parse(it) },
                call.request.queryParameters.parseRequiredParam("endDate") { LocalDate.parse(it) }
            ) { userName, startDate, endDate ->
                val result: List<Pair<LocalDate, Duration>> =
                    timeEntriesRegister.getDailyUserHours(userName, startDate, endDate)
                val jsonResult = JsonArray(result.map { (date, duration) ->
                    JsonObject(
                        mapOf(
                            "date" to JsonPrimitive(date.toString()),
                            "duration" to JsonPrimitive(duration.toString())
                        )
                    )
                })
                call.respondText(jsonResult.toString(), ContentType.Application.Json)
            }.getOrElse { errors ->
                call.respondText(
                    JsonObject(
                        mapOf(
                            "validationErrors" to JsonObject(
                                errors.map { it.field to JsonPrimitive(it.description) }.toMap()
                            )
                        )
                    ).toString(),
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }

        }
    }

    private fun <A> Parameters.parseRequiredParam(param: String, parser: (String) -> A): Either<ValidationError, A> =
        this[param]?.let {
            Either.catch { parser(it) }.mapLeft { ValidationError(param, "invalid.format") }
        } ?: Either.Left(ValidationError(param, "required"))
}

data class ValidationError(val field: String, val description: String)

suspend fun main() {

    HikariCp.dataSource("jdbc:postgresql://localhost/test", "postgres", "postgres").use { dataSource ->
        val repo = PostgresTimeEntriesRepository(dataSource)
        val timeEntriesRegister = TimeEntriesRegister(repo)

        val timeEntriesController = TimeEntriesController(timeEntriesRegister)

        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            timeEntriesController.routes()
        }.start(wait = true)
    }
}
