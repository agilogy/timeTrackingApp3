package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.user.domain.UserName
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class InMemoryTimeEntriesRepository : TimeEntriesRepository {
    private val entries = mutableListOf<TimeEntry>()

    override suspend fun save(timeEntries: Iterable<TimeEntry>) {
        entries.addAll(timeEntries)
    }

    override suspend fun getUserTimeEntries(userName: UserName, start: LocalDate, end: LocalDate): List<TimeEntry> =
        entries.filter {
            it.userName == userName && it.start >= start.atStartOfDay(ZoneId.systemDefault())
                .toInstant() && it.start <= end.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
        }

}
