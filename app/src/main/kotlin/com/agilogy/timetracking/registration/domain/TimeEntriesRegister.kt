package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.user.domain.UserName
import java.time.LocalDate

class TimeEntriesRegister(private val timeEntriesRepository: TimeEntriesRepository) {
    suspend fun registerEntries(timeEntries: Iterable<TimeEntry>): Unit = timeEntriesRepository.save(timeEntries)

    suspend fun getDailyUserHours(userName: UserName, startDate: LocalDate, endDate: LocalDate): List<Pair<LocalDate, Int>> =
        TODO("Not yet implemented")

}
