package com.hihihihi.data.common.util

import com.google.firebase.Timestamp
import com.hihihihi.domain.model.ReadingStatus
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.toLocalDateTime(): LocalDateTime = this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()