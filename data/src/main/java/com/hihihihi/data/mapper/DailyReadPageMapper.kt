package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.DailyReadPageDto
import com.hihihihi.domain.model.DailyReadPage
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val isoDate = DateTimeFormatter.ISO_LOCAL_DATE

fun DailyReadPageDto.toDomain(): DailyReadPage =
    DailyReadPage(
        date = runCatching { LocalDate.parse(date, isoDate) }.getOrElse { LocalDate.MIN },
        totalReadPageCount = totalReadPageCount.toInt(),
        uid = uid,
        updatedAt = updatedAt?.toDate()
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDateTime()
    )