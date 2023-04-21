package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.user.domain.UserName
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

class TimeEntriesRegister(private val timeEntriesRepository: TimeEntriesRepository) {
    suspend fun registerEntries(timeEntries: Iterable<TimeEntry>): Unit = timeEntriesRepository.save(timeEntries)

    suspend fun getDailyUserHours(
        userName: UserName,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Pair<LocalDate, Duration>> {
        val entries = timeEntriesRepository.getUserTimeEntries(userName, startDate, endDate)
        return entries
            .groupBy { it.start.atZone(ZoneId.systemDefault()).toLocalDate() }
            .mapValues {
                it.value.fold(Duration.ZERO) { acc, timeEntry ->
                    Duration.between(
                        timeEntry.start,
                        timeEntry.end
                    ) + acc
                }
            }.toList()
    }
}
