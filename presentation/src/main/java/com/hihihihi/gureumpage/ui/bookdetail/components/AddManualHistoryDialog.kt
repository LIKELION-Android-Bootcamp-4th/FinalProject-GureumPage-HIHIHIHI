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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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

    var startPage by remember { mutableStateOf("") }
    var endPage by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // 읽은 시간 계산 (초)
    val readTime = remember(startTime, endTime) {
        if (startTime != null && endTime != null) {
            val seconds = Duration.between(startTime, endTime).seconds
            if (seconds >= 0) seconds.toInt() else null
        } else null
    }

    // 읽은 페이지 계산
    val readPageCount = remember(startPage, endPage) {
        (endPage.toIntOrNull() ?: 0) - (startPage.toIntOrNull() ?: 0)
    }



    Dialog(onDismissRequest = onDismiss) {
        GureumCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
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

                Spacer(modifier = Modifier.height(16.dp))
                Semi12Text(
                    "날짜",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
                DatePickTextField(
                    value = date?.format(dateFormatter) ?: "",
                    placeholder = "책 읽은 날",
                    onClick = { showDatePicker = true }
                )


                Spacer(modifier = Modifier.height(16.dp))
                Semi12Text(
                    "페이지",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
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
                Semi12Text(
                    "읽은 시간",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GureumTextField(
                        value = startTime?.format(timeFormatter) ?: "",
                        onValueChange = {},
                        hint = "시작 시간",
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStartTimePicker = true }
                    )
                    Text("~")
                    GureumTextField(
                        value = endTime?.format(timeFormatter) ?: "",
                        onValueChange = {},
                        hint = "끝난 시간",
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndTimePicker = true }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))
                Semi12Text(
                    "총 읽은 시간",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
                GureumTextField(
                    value = readTime?.let { formatSecondsToReadableTimeWithoutSecond(it)  } ?: "",
                    onValueChange = {},
                    hint = "총 읽은 시간",
                    enabled = false,
                )



                Spacer(modifier = Modifier.height(20.dp))

                // 저장 버튼
                GureumButton(
                    text = "저장하기",
                    enabled = date != null && startTime != null && endTime != null,
                    onClick = {
                        onSave(
                            date!!,
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

        // DatePicker
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

        // TimePickers
        val startTimePickerState = rememberTimePickerState()
        if (showStartTimePicker) {
            if (showStartTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showStartTimePicker = false },
                    onConfirm = {
                        date?.let { d ->
                            startTime = LocalDateTime.of(
                                d.toLocalDate(),
                                LocalTime.of(startTimePickerState.hour, startTimePickerState.minute)
                            )
                        }
                        showStartTimePicker = false
                    }
                ) {
                    TimePicker(state = startTimePickerState)
                }
            }
        }

        val endTimePickerState = rememberTimePickerState()
        if (showEndTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showEndTimePicker = false },
                onConfirm = {
                    date?.let { d ->
                        endTime = LocalDateTime.of(
                            d.toLocalDate(),
                            LocalTime.of(endTimePickerState.hour, endTimePickerState.minute)
                        )
                    }
                    showEndTimePicker = false
                }
            ) {
                TimePicker(state = endTimePickerState)
            }
        }
    }

}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("취소")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("확인")
            }
        },
        text = { content() }
    )
}
