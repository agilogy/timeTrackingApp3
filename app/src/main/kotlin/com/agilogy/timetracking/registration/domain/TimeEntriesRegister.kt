package com.agilogy.timetracking.registration.domain

class TimeEntriesRegister(private val timeEntriesRepository: TimeEntriesRepository) {
    suspend fun registerEntries(timeEntries: Iterable<TimeEntry>): Unit = timeEntriesRepository.save(timeEntries)
}