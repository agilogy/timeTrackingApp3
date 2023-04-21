package com.agilogy.timetracking.registration.infrastructure

import com.agilogy.db.sql.Sql.select
import com.agilogy.db.sql.Sql.sql
import com.agilogy.db.sql.Sql.update
import com.agilogy.db.sql.param
import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.registration.domain.TimeEntriesRepository
import com.agilogy.timetracking.registration.domain.TimeEntry
import com.agilogy.timetracking.user.domain.UserName
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.sql.DataSource

class PostgresTimeEntriesRepository(private val dataSource: DataSource) : TimeEntriesRepository {
    private val UserName.param get() = value.param
    private val ProjectName.param get() = value.param

    override suspend fun save(timeEntries: Iterable<TimeEntry>) {
        dataSource.sql {
            timeEntries.forEach {
                update(
                    """INSERT INTO time_entries(user_name, project_name, start, "end") VALUES (?, ?, ?, ?)""",
                    it.userName.param,
                    it.projectName.param,
                    it.start.param,
                    it.end.param
                )
            }
        }
    }

    override suspend fun getUserTimeEntries(userName: UserName, start: LocalDate, end: LocalDate): List<TimeEntry> =
        dataSource.sql {
            select(
                """SELECT user_name, project_name, start, "end" FROM time_entries WHERE user_name = ? AND start >= ? AND start <= ?""",
                userName.value.param,
                start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().param,
                end.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().param
            ) {
                TimeEntry(
                    UserName(it.string(1)!!),
                    ProjectName(it.string(2)!!),
                    it.timestamp(3)!!,
                    it.timestamp(4)!!
                )
            }
        }


}
