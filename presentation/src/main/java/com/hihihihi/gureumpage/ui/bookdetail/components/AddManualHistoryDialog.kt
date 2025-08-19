package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTimeWithoutSecond
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumClickEventTextField
import com.hihihihi.gureumpage.designsystem.components.GureumPastToTodayDatePicker
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi12Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.home.components.GureumNumberPicker
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
    lastPage: Int,
    onDismiss: () -> Unit,
    onSave: (
        date: LocalDateTime,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        readTime: Int,
        readPageCount: Int,
            currentPage: Int
    ) -> Unit,
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

    val startPageNum = startPage.toIntOrNull()
    val endPageNum = endPage.toIntOrNull()
    val hasPageError = endPageNum != null && startPageNum != null && endPageNum < startPageNum


    val canSave = startTime != null && endTime != null &&
            (endTime!!.isAfter(startTime) || endTime == startTime) &&
            startPageNum != null && endPageNum != null &&
            startPageNum > 0 && endPageNum > 0 &&
            endPageNum >= startPageNum

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

                Semi12Text(
                    "날짜",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
                GureumClickEventTextField(
                    value = date.format(dateFormatter),
                    hint = "책 읽은 날",
                    onClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            GureumTextField(
                                value = startPage,
                                onValueChange = {
                                    startPage = it.filter(Char::isDigit)
                                        .toIntOrNull()
                                        ?.coerceAtMost(lastPage)
                                        ?.toString()
                                        ?: ""
                                },
                                hint = "시작 페이지",
                                isError = hasPageError,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardType = KeyboardType.Number,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Top)
                        ) {
                            Medi16Text(
                                "~",
                                color = GureumTheme.colors.gray600
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            GureumTextField(
                                value = endPage,
                                onValueChange = {
                                    endPage = it.filter(Char::isDigit)
                                        .toIntOrNull()
                                        ?.coerceAtMost(lastPage)
                                        ?.toString()
                                        ?: ""
                                },
                                hint = "끝 페이지",
                                isError = hasPageError,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = hasPageError,
                        enter = fadeIn(animationSpec = tween(200)) + slideInVertically(
                            initialOffsetY = { -it / 3 },
                            animationSpec = tween(200)
                        ),
                        exit = fadeOut(animationSpec = tween(150)) + slideOutVertically(
                            targetOffsetY = { -it / 3 },
                            animationSpec = tween(150)
                        )
                    ) {
                        Medi12Text(
                            text = "종료 페이지는 시작 페이지보다 커야 합니다",
                            color = GureumTheme.colors.systemRed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Semi12Text(
                    "읽은 시간",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GureumClickEventTextField(
                        value = startTime?.format(timeFormatter) ?: "",
                        onValueChange = {},
                        hint = "시작 시간",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            editingStartTime = true
                            tempHour = startTime?.hour ?: 0
                            tempMinute = startTime?.minute ?: 0
                            showTimePicker = true
                        }
                    )
                    Medi16Text(
                        "~",
                        color = GureumTheme.colors.gray600
                    )
                    GureumClickEventTextField(
                        value = endTime?.format(timeFormatter) ?: "",
                        onValueChange = {},
                        hint = "끝난 시간",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            editingStartTime = false
                            tempHour = endTime?.hour ?: 0
                            tempMinute = endTime?.minute ?: 0
                            showTimePicker = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Semi12Text(
                    "총 읽은 시간",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    color = GureumTheme.colors.gray800
                )
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
                            readPageCount,
                            endPage.toInt()
                        )
                        onDismiss()
                    }
                )
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            GureumPastToTodayDatePicker(
                onDismiss = { showDatePicker = false },
                onConfirm = {
                    datePickerState.selectedDateMillis?.let {
                        val localDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        date = localDate.atStartOfDay()
                    }
                }
            )
        }

        if (showTimePicker) {
            Dialog(
                onDismissRequest = { showTimePicker = false },
            ) {
                GureumCard {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // 종료 시간 선택 시 제약 조건 계산
                        val (hourValues, minuteValues) = if (editingStartTime) {
                            Pair((0..23).toList(), (0..59).toList())
                        } else {
                            val startHour = startTime?.hour ?: 0
                            val startMinute = startTime?.minute ?: 0

                            val availableHours = (startHour..23).toList()
                            val availableMinutes = if (tempHour == startHour) {
                                (startMinute + 0..59).toList()
                            } else {
                                (0..59).toList()
                            }

                            Pair(availableHours, availableMinutes)
                        }

                        val validTempHour = if (hourValues.contains(tempHour)) tempHour else hourValues.first()
                        val validTempMinute =
                            if (minuteValues.contains(tempMinute)) tempMinute else minuteValues.first()


                        GureumNumberPicker(
                            initialHour = validTempHour,
                            initialMinute = validTempMinute,
                            hourValues = hourValues,
                            minuteValues = minuteValues,
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
                                    val newStartTime = LocalDateTime.of(d.toLocalDate(), selectedTime)
                                    startTime = newStartTime

                                    // 종료 시간이 시작 시간보다 이전이면 조정
                                    if (endTime != null && endTime!!.isBefore(newStartTime)) {
                                        endTime = newStartTime
                                    }
                                } else {
                                    // 종료 시간 설정
                                    val newEndTime = LocalDateTime.of(d.toLocalDate(), selectedTime)
                                    // 시작 시간 이후인지 다시 한번 확인
                                    if (startTime == null || !newEndTime.isBefore(startTime!!)) {
                                        endTime = newEndTime
                                    }
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
