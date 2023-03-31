package com.agilogy.timetracking.registration.domain

import com.agilogy.timetracking.project.domain.ProjectName
import com.agilogy.timetracking.user.domain.UserName
import java.time.Instant

data class TimeEntry(val userName: UserName, val projectName: ProjectName, val start: Instant, val end: Instant)