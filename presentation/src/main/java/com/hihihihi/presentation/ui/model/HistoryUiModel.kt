package com.hihihihi.presentation.ui.model

import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.RecordType
import java.time.LocalDateTime

data class HistoryUiModel(
    val readTime: Int,
    val readPageCount: Int,
    val date: LocalDateTime?,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val recordType: RecordType,
)

fun History.toUiModel() = HistoryUiModel(
    readTime = readTime,
    readPageCount = readPageCount,
    date = date,
    startTime = startTime,
    endTime = endTime,
    recordType = recordType,
)
