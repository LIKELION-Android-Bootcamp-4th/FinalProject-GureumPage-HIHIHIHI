package com.hihihihi.data.common.util

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

private val KST = ZoneId.of("Asia/Seoul")

fun Timestamp.toLocalDateTime(): LocalDateTime =
    this.toDate().toInstant().atZone(KST).toLocalDateTime()

fun LocalDateTime.toTimestamp(): Timestamp =
    Timestamp(this.atZone(KST).toInstant())