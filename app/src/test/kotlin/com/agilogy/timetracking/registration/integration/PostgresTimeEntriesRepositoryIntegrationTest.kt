package com.agilogy.timetracking.registration.integration

import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import com.agilogy.db.hikari.HikariCp.dataSource
import com.agilogy.timetracking.TimeTrackingDatabaseSetUp.createTimeEntriesTable
import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.registration.domain.TimeEntry
import com.agilogy.timetracking.registration.infrastructure.PostgresTimeEntriesRepository
import com.agilogy.timetracking.registration.infrastructure.PostgresTimeEntriesTestRepository
import com.agilogy.timetracking.user.domain.UserName
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.*
import java.time.temporal.ChronoUnit
import javax.sql.DataSource

class PostgresTimeEntriesRepositoryIntegrationTest : FunSpec() {
    init {
        suspend fun ResourceScope.setupDataSource(): DataSource {
            with(dataSource("jdbc:postgresql://localhost/", "postgres", "postgres").bind()) {
                com.agilogy.timetracking.TimeTrackingDatabaseSetUp.setUp()
            }

            val dataSource = dataSource("jdbc:postgresql://localhost/test", "postgres", "postgres").bind()
            with(dataSource) {
                createTimeEntriesTable()
            }
            return dataSource
        }

        fun at(day: Int, hour: Int): Instant =
            LocalDateTime.of(2023, Month.APRIL, day, hour, 0).atZone(ZoneId.systemDefault()).toInstant()

        val agilogySchool = ProjectName("Agilogy School")
        val user1 = UserName("John Doe")
        test("it should save time entries") {
            resourceScope {
                val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
                val entries = listOf(
                    TimeEntry(user1, agilogySchool, now, now.plus(1, ChronoUnit.HOURS))
                )
                val dataSource = setupDataSource()

                val testee = PostgresTimeEntriesRepository(dataSource)
                testee.save(entries)
                val testRepo = PostgresTimeEntriesTestRepository(dataSource)
                val result = testRepo.getAll()
                assertEquals(entries, result)
            }
        }

        test("it should return all user time entries between a time range") {
            resourceScope {
                val entries = listOf(
                    TimeEntry(user1, agilogySchool, at(1, 9), at(1, 10)),
                    TimeEntry(user1, agilogySchool, at(2, 10), at(2, 11)),
                    TimeEntry(user1, agilogySchool, at(20, 10), at(20, 11))
                )

                val dataSource = setupDataSource()

                val testee = PostgresTimeEntriesRepository(dataSource)
                testee.save(entries)
                val result = testee.getUserTimeEntries(
                    user1,
                    LocalDate.of(2023, Month.APRIL, 1),
                    LocalDate.of(2023, Month.APRIL, 10)
                )
                val expected = entries.take(2)
                assertEquals(expected, result)
            }
        }
    }
}
