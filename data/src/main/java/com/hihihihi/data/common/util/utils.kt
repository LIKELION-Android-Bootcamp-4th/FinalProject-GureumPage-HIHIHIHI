package com.hihihihi.data.common.util

import com.google.firebase.Timestamp
import com.hihihihi.domain.model.ReadingStatus
import java.time.LocalDateTime
import java.time.ZoneId

fun String.toReadingStatus(): ReadingStatus = when (this.lowercase()) {
    "planned" -> ReadingStatus.PLANNED
    "reading" -> ReadingStatus.READING
    "finished" -> ReadingStatus.FINISHED
    else -> ReadingStatus.PLANNED
}

fun ReadingStatus.toReadingStatusString(): String = when (this) {
    ReadingStatus.PLANNED -> "planned"
    ReadingStatus.READING -> "reading"
    ReadingStatus.FINISHED -> "finished"
}

fun Timestamp.toLocalDateTime(): LocalDateTime = this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()