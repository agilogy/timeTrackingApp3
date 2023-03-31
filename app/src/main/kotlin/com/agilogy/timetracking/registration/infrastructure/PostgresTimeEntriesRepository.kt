package com.agilogy.timetracking.registration.infrastructure

import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.Sql.update
import com.agilogy.db.sql.param
import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.registration.domain.TimeEntriesRepository
import com.agilogy.timetracking.registration.domain.TimeEntry
import com.agilogy.timetracking.user.domain.UserName
import javax.sql.DataSource

class PostgresTimeEntriesRepository(private val dataSource: DataSource): TimeEntriesRepository{
    private val UserName.param get() = value.param
    private val ProjectName.param get() = value.param

    override suspend fun save(timeEntries: Iterable<TimeEntry>) {
        dataSource.sql {
            timeEntries.forEach {
                update("""INSERT INTO time_entries(user_name, project_name, start, "end") VALUES (?, ?, ?, ?)""",
                    it.userName.param,
                    it.projectName.param,
                    it.start.param,
                    it.end.param
                )
            }
        }
    }
}