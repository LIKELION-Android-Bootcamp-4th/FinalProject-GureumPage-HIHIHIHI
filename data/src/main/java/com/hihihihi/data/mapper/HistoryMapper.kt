package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.HistoryDto
import com.hihihihi.domain.model.History
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.data.common.util.toTimestamp
import com.hihihihi.domain.model.RecordType

fun HistoryDto.toDomain(): History = History(
    id = historyId,
    userId = userId,
    userBookId = userBookId,
    date = date?.toLocalDateTime(),
    startTime = startTime?.toLocalDateTime(),
    endTime = endTime?.toLocalDateTime(),
    readTime = readTime,
    readPageCount = readPageCount,
    recordType = RecordType.valueOf(recordType.uppercase())
)

fun History.toDto(): HistoryDto = HistoryDto(
    historyId = id,
    userId = userId,
    userBookId = userBookId,
    date = date?.toTimestamp(),
    startTime = startTime?.toTimestamp(),
    endTime = endTime?.toTimestamp(),
    readTime = readTime,
    readPageCount = readPageCount,
    recordType = recordType.name.lowercase()
)
