package com.agilogy.timetracking.registration.infrastructure

import arrow.core.Either
import arrow.core.getOrElse
import arrow.fx.coroutines.use
import com.agilogy.db.hikari.HikariCp
import com.agilogy.timetracking.registration.domain.TimeEntriesRegister
import com.agilogy.timetracking.registration.domain.TimeEntriesRepository
import com.agilogy.timetracking.user.domain.UserName
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

class TimeEntriesController(private val timeEntriesRegister: TimeEntriesRegister) {

    context(Routing)
    fun timeEntriesRoutes() {
        // GET /time-entries/daily-user-hours?userName=userName&startDate=startDate&endDate=endDate
        get("/time-entries/daily-user-hours") {
            val userName = call.request.queryParameters.parseRequiredParam("userName") { UserName(it) }
            val startDate = call.request.queryParameters.parseRequiredParam("startDate") { LocalDate.parse(it) }
            val endDate = call.request.queryParameters.parseRequiredParam("endDate") { LocalDate.parse(it) }

            Either.zipOrAccumulate(userName, startDate, endDate) { u, s, d ->
                val result = timeEntriesRegister.getDailyUserHours(u, s, d)
                call.respondText(result.toString(), ContentType.Text.Plain)
            }.getOrElse { errors ->
                call.respondText(
                    """{"message":${errors.joinToString(",", "[", "]")}}""",
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }

        }
    }

    private fun <A> Parameters.parseRequiredParam(param: String, parser: (String) -> A): Either<String, A> =
        this[param]?.let {
            Either.catch { parser(it) }.mapLeft { "$param.invalid.format" }
        } ?: Either.Left("$param.required")


}


suspend fun main() {

    HikariCp.dataSource("jdbc:postgresql://localhost/", "postgres", "postgres").use { dataSource ->
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
