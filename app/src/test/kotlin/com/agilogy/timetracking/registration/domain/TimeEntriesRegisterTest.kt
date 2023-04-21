package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.user.domain.UserName
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.*
import java.time.temporal.ChronoUnit

class TimeEntriesRegisterTest : FunSpec() {
    private val timeEntriesRepository = InMemoryTimeEntriesRepository()
    private val timeEntriesRegister = TimeEntriesRegister(timeEntriesRepository)

    init {
        fun at(day: Int, hour: Int): Instant =
            LocalDateTime.of(2023, Month.APRIL, day, hour, 0).atZone(ZoneId.systemDefault()).toInstant()

        val agilogySchool = ProjectName("Agilogy School")
        val user1 = UserName("John Doe")

        test("it should return the daily user hours from a user within a time range") {
            val entries = listOf(
                TimeEntry(user1, agilogySchool, at(1, 9), at(1, 10)),
                TimeEntry(user1, agilogySchool, at(1, 10), at(1, 11)),
                TimeEntry(user1, agilogySchool, at(1, 10), at(1, 11))
            )

            timeEntriesRepository.save(entries)

            val actual = timeEntriesRegister.getDailyUserHours(
                user1,
                LocalDate.of(2023, Month.APRIL, 1),
                LocalDate.of(2023, Month.APRIL, 1)
            )

            val expected = listOf(
                LocalDate.of(2023, Month.APRIL, 1) to Duration.of(3, ChronoUnit.HOURS),
            )

            assertEquals(actual, expected)

        }
    }
}
