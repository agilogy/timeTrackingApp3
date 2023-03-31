package com.agilogy.timetracking.registration.integration

import arrow.fx.coroutines.resourceScope
import com.agilogy.db.hikari.HikariCp.dataSource
import com.agilogy.timetracking.TimeTrackingDatabaseSetUp
import com.agilogy.timetracking.TimeTrackingDatabaseSetUp.createTimeEntriesTable
import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.registration.domain.TimeEntry
import com.agilogy.timetracking.registration.infrastructure.PostgresTimeEntriesRepository
import com.agilogy.timetracking.registration.infrastructure.PostgresTimeEntriesTestRepository
import com.agilogy.timetracking.user.domain.UserName
import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import java.time.temporal.ChronoUnit

class PostgresTimeEntriesRepositoryIntegrationTest : FunSpec() {
    init {
        test("it should save time entries") {
            resourceScope {
                val now = Instant.now()
                val entries = listOf(
                    TimeEntry(
                        UserName("John Doe"),
                        ProjectName("Agilogy School"),
                        now,
                        now.plus(1, ChronoUnit.HOURS)
                    )
                )
                with(dataSource("jdbc:postgresql://localhost/", "postgres", "postgres").bind()) {
                    TimeTrackingDatabaseSetUp.setUp()
                }

                val dataSource = dataSource("jdbc:postgresql://localhost/test", "postgres", "postgres").bind()
                with(dataSource) {
                    createTimeEntriesTable()
                }
                val testee = PostgresTimeEntriesRepository(dataSource)
                testee.save(entries)
                val testRepo = PostgresTimeEntriesTestRepository(dataSource)
                val result = testRepo.getAll()
                assertEquals(entries, result)
            }
        }
    }
}