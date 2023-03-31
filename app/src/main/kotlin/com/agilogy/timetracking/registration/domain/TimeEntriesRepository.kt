package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.user.domain.UserName
import java.time.LocalDate

interface TimeEntriesRepository {
    suspend fun save(timeEntries: Iterable<TimeEntry>)

    suspend fun getUserTimeEntries(userName: UserName, start: LocalDate, end: LocalDate): List<TimeEntry>
}
