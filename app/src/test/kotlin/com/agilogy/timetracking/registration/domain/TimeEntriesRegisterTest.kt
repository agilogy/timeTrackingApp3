package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.user.domain.UserName
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.*
import java.time.temporal.ChronoUnit

class TimeEntriesRegisterTest : FunSpec() {

    init {
        fun at(day: Int, hour: Int): Instant =
            LocalDateTime.of(2023, Month.APRIL, day, hour, 0).atZone(ZoneId.systemDefault()).toInstant()

        val agilogySchool = ProjectName("Agilogy School")
        val user1 = UserName("John Doe")

        val entries = listOf(
            TimeEntry(user1, agilogySchool, at(1, 9), at(1, 10)),
            TimeEntry(user1, agilogySchool, at(1, 10), at(1, 11)),
            TimeEntry(user1, agilogySchool, at(1, 10), at(1, 11))
        )

        test("it should return the daily user hours from a user within a time range") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val timeEntriesRegister = TimeEntriesRegister(timeEntriesRepository)

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

        test("it should register the given time entries") {
            val timeEntriesRepository = InMemoryTimeEntriesRepository()
            val timeEntriesRegister = TimeEntriesRegister(timeEntriesRepository)

            timeEntriesRegister.registerEntries(entries)

            val actual = timeEntriesRepository.getAllTimeEntries()

            assertEquals(actual, entries)
        }
    }
}
