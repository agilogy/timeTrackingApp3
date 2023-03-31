package com.agilogy.timetracking.registration.infrastructure

import com.agilogy.db.sql.Sql.select
import com.agilogy.db.sql.Sql.sql
import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.registration.domain.TimeEntry
import com.agilogy.timetracking.user.domain.UserName
import javax.sql.DataSource

class PostgresTimeEntriesTestRepository(private val dataSource: DataSource) {
    suspend fun getAll(): List<TimeEntry> = dataSource.sql {
        select("SELECT user_name, project_name, start, end FROM time_entries") {
            TimeEntry(
                UserName(it.string(1)!!),
                ProjectName(it.string(2)!!),
                it.timestamp(3)!!,
                it.timestamp(4)!!
            )
        }
    }
}