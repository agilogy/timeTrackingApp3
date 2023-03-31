package com.agilogy.timetracking.registration.domain

interface TimeEntriesRepository {
    suspend fun save(timeEntries: Iterable<TimeEntry>)
}
