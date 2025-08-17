package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTimeWithoutSecond
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Semi12Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.home.components.GureumNumberPicker
import com.hihihihi.gureumpage.ui.search.component.DatePickTextField
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddManualHistoryDialog(
    currentPage: Int,
    onDismiss: () -> Unit,
    onSave: (
        date: LocalDateTime,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        readTime: Int,
        readPageCount: Int
    ) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    var date by remember { mutableStateOf(LocalDateTime.now()) }
    var startTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endTime by remember { mutableStateOf<LocalDateTime?>(null) }

    var startPage by remember { mutableStateOf(currentPage.toString()) }
    var endPage by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var editingStartTime by remember { mutableStateOf(true) }

    var tempHour by remember { mutableStateOf(0) }
    var tempMinute by remember { mutableStateOf(0) }

    val readTime = remember(startTime, endTime) {
        if (startTime != null && endTime != null) {
            val seconds = Duration.between(startTime, endTime).seconds
            if (seconds >= 0) seconds.toInt() else null
        } else null
    }

    val readPageCount = remember(startPage, endPage) {
        (endPage.toIntOrNull() ?: 0) - (startPage.toIntOrNull() ?: 0)
    }

    val canSave = startTime != null && endTime != null &&
            (endTime!!.isAfter(startTime) || endTime == startTime) &&
            (endPage.toIntOrNull() ?: 0) >= (startPage.toIntOrNull() ?: 0)

    Dialog(onDismissRequest = onDismiss) {
        GureumCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = GureumTheme.colors.primary50,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit_alt_filled),
                                contentDescription = null,
                                tint = GureumTheme.colors.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Semi16Text(text = "직접 기록 추가", color = GureumTheme.colors.gray900)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "닫기"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Semi12Text("날짜", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp), color = GureumTheme.colors.gray800)
                DatePickTextField(
                    value = date.format(dateFormatter),
                    placeholder = "책 읽은 날",
                    onClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Semi12Text("페이지", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp), color = GureumTheme.colors.gray800)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GureumTextField(
                        value = startPage,
                        onValueChange = { startPage = it.filter(Char::isDigit) },
                        hint = "시작 페이지",
                        modifier = Modifier.weight(1f)
                    )
                    Text("~")
                    GureumTextField(
                        value = endPage,
                        onValueChange = { endPage = it.filter(Char::isDigit) },
                        hint = "끝 페이지",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Semi12Text("읽은 시간", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp), color = GureumTheme.colors.gray800)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GureumTextField(
                        value = startTime?.format(timeFormatter) ?: "",
                        onValueChange = {},
                        hint = "시작 시간",
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                editingStartTime = true
                                tempHour = startTime?.hour ?: 0
                                tempMinute = startTime?.minute ?: 0
                                showTimePicker = true
                            }
                    )
                    Text("~")
                    GureumTextField(
                        value = endTime?.format(timeFormatter) ?: "",
                        onValueChange = {},
                        hint = "끝난 시간",
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                editingStartTime = false
                                tempHour = endTime?.hour ?: 0
                                tempMinute = endTime?.minute ?: 0
                                showTimePicker = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Semi12Text("총 읽은 시간", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp), color = GureumTheme.colors.gray800)
                GureumTextField(
                    value = readTime?.let { formatSecondsToReadableTimeWithoutSecond(it) } ?: "",
                    onValueChange = {},
                    hint = "시작 시간과 끝 시간을 설정해주세요",
                    enabled = false
                )

                Spacer(modifier = Modifier.height(20.dp))

                GureumButton(
                    text = "저장하기",
                    enabled = canSave,
                    onClick = {
                        onSave(
                            date,
                            startTime!!,
                            endTime!!,
                            readTime ?: 0,
                            readPageCount
                        )
                        onDismiss()
                    }
                )
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val localDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            date = localDate.atStartOfDay()
                        }
                        showDatePicker = false
                    }) { Text("확인") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            Dialog(
                onDismissRequest = { showTimePicker = false },
            ) {
                GureumCard {
                    Column (modifier = Modifier.padding(20.dp)){

                        val hourValuesForEnd = remember(startTime) {
                            val startHour = startTime?.hour ?: 0
                            (startHour..23).toList()
                        }
                        val minuteValuesForEnd = remember(startTime, tempHour) {
                            val startMinute = if (tempHour == (startTime?.hour ?: 0)) (startTime?.minute ?: 0) + 1 else 0
                            (startMinute..59).toList()
                        }


                        GureumNumberPicker(
                            initialHour = tempHour,
                            initialMinute = tempMinute,
                            hourValues = if (editingStartTime) (0..23).toList() else hourValuesForEnd,
                            minuteValues = if (editingStartTime) (0..59).toList() else minuteValuesForEnd,
                            visibleItemsCount = 5,
                            unitLabel = "시",

                        ) { h, m ->
                            tempHour = h
                            tempMinute = m
                        }
                        Spacer(Modifier.height(24.dp))
                        GureumButton(text = "확인", onClick = {
                            date?.let { d ->
                                val selectedTime = LocalTime.of(tempHour, tempMinute)
                                if (editingStartTime) {
                                    startTime = LocalDateTime.of(d.toLocalDate(), selectedTime)
                                    // 확정 시점에만 체크
                                    if (endTime != null && startTime!!.toLocalTime() > endTime!!.toLocalTime()) {
                                        endTime = null
                                    }
                                } else {
                                    endTime = LocalDateTime.of(d.toLocalDate(), selectedTime)
                                }
                            }
                            showTimePicker = false
                        })
                    }
                }
            }
        }
    }
}
