package com.agilogy.timetracking.registration.infrastructure

import arrow.core.Either
import arrow.core.getOrElse
import arrow.fx.coroutines.use
import com.agilogy.db.hikari.HikariCp
import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.registration.domain.TimeEntriesRegister
import com.agilogy.timetracking.registration.domain.TimeEntry
import com.agilogy.timetracking.user.domain.UserName
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jdk.jfr.Description
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TimeEntriesController(private val timeEntriesRegister: TimeEntriesRegister) {

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

        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            routing {
                TimeEntriesController(timeEntriesRegister).timeEntriesRoutes()
                post("/haha") {
                    call.respondText("[]", ContentType.Application.Json)
                }
            }
        }.start(wait = true)
    }

}
