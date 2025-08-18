package com.hihihihi.gureumpage.designsystem.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GureumPastToTodayDatePicker(
    onDismiss: () -> Unit,
    onConfirm: (millis: Long) -> Unit,
) {
    val systemTime = ZoneId.systemDefault()
    val today = LocalDate.now(systemTime)

    val selectable = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = toLocalDate(utcTimeMillis, systemTime)
                return !date.isAfter(today) // 오늘 포함 미래 불가
            }

            override fun isSelectableYear(year: Int) = true
        }
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        yearRange = 1900..today.year,
        selectableDates = selectable
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let(onConfirm)
                    onDismiss
                }
            ) { Text("확인") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("취소")
            }
        }
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GureumBetweenDatePicker(
    isSelectingStart: Boolean,  // true: 시작 날짜 선택, false: 종료 날짜 선택
    startDate: LocalDateTime?,  // null 이면 최소 날짜 없음
    endDate: LocalDateTime?,    // null 이면 최대 날짜 오늘
    onDismiss: () -> Unit,
    onConfirm: (millis: Long) -> Unit,
) {
    val systemTime = ZoneId.systemDefault()
    val today = LocalDate.now(systemTime)
    val min: LocalDate? = if (isSelectingStart) null else startDate?.toLocalDate()
    val max: LocalDate? = if (isSelectingStart) (endDate?.toLocalDate() ?: today) else today

    val selectable = remember(isSelectingStart, startDate, endDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = toLocalDate(utcTimeMillis, systemTime)
                val validMin = min?.let { !date.isBefore(it) } ?: true
                val validMax = max?.let { !date.isAfter(it) } ?: true
                return validMin && validMax
            }
            override fun isSelectableYear(year: Int): Boolean {
                val yearMin = min?.year ?: 1900
                val yearMax = max?.year ?: today.year
                return year in yearMin..yearMax
            }
        }
    }

    val suggested = when {
        isSelectingStart -> startDate?.toLocalDate() ?: (max ?: today)
        else -> endDate?.toLocalDate() ?: today
    }
    val initDate = clamp(suggested, min, max)
    val initMillis = initDate.atStartOfDay(systemTime).toInstant().toEpochMilli()

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initMillis,
        yearRange = (min?.year ?: 1900)..(max?.year ?: today.year),
        selectableDates = selectable
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let(onConfirm)
                    onDismiss()
                }
            ) { Text("확인") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("취소")
            }
        }
    ) {
        DatePicker(state = state)
    }
}


private fun toLocalDate(millis: Long, zone: ZoneId) =
    Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

private fun clamp(localDate: LocalDate, min: LocalDate?, max: LocalDate?): LocalDate =
    when {
        min != null && localDate.isBefore(min) -> min
        max != null && localDate.isAfter(max) -> max
        else -> localDate
    }