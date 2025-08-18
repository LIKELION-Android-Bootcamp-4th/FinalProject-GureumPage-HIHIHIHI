package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatMillisToLocalDateTime
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumClickEventTextField
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.search.component.CategoryRow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetReadingStatusBottomSheet(
    userBook: UserBook,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (ReadingStatus, Int?, LocalDateTime?, LocalDateTime?) -> Unit,
) {
    var selectedStatus by remember { mutableStateOf(userBook.status) }
    var pageInput by remember { mutableStateOf("0") }

    var startDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDate by remember { mutableStateOf<LocalDateTime?>(null) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val today = LocalDate.now()


    val canSave = when (selectedStatus) {
        ReadingStatus.FINISHED -> startDate != null && endDate != null
        ReadingStatus.READING -> startDate != null && pageInput.isNotBlank()
        else -> true
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GureumTheme.colors.card,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Medi16Text("상태 변경")
            Spacer(modifier = Modifier.height(14.dp))

            ReadingStatusSelector(
                selectedStatus = selectedStatus,
                onStatusChange = { selectedStatus = it }
            )
            Spacer(modifier = Modifier.height(24.dp))

            ReadingStatusInputs(
                selectedStatus = selectedStatus,
                pageInput = pageInput,
                onPageChange = { pageInput = it },
                startDate = startDate,
                onStartDateClick = { showStartDatePicker = true },
                endDate = endDate,
                onEndDateClick = { showEndDatePicker = true },
            )
            Spacer(modifier = Modifier.height(24.dp))

            GureumButton(
                text = "변경하기",
                enabled = canSave,
                onClick = {
                    focusManager.clearFocus()
                    onConfirm(
                        selectedStatus,
                        if (selectedStatus != ReadingStatus.FINISHED) pageInput.toIntOrNull() else userBook.totalPage,
                        if (selectedStatus == ReadingStatus.PLANNED) null else startDate,
                        if (selectedStatus == ReadingStatus.FINISHED) endDate else null
                    )
                }
            )
            Spacer(modifier = Modifier.height(26.dp))
        }
    }

    // 시작 날짜 피커
    if (showStartDatePicker) {
        val startDatePickerState = rememberDatePickerState(
            // 오늘 이후는 선택 불가
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    return !selectedDate.isAfter(today)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showStartDatePicker = false
                    startDatePickerState.selectedDateMillis?.let { millis ->
                        val selectedDateTime = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .atStartOfDay()
                        startDate = selectedDateTime

                        // 종료일이 시작일보다 이전이면 종료일 초기화
                        if (endDate != null && selectedDateTime.isAfter(endDate)) {
                            endDate = null
                        }
                    }
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // 종료 날짜 피커
    if (showEndDatePicker) {
        val endDatePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    // 오늘 이후는 선택 불가
                    if (selectedDate.isAfter(today)) return false

                    // 시작일이 있으면 시작일 이후만 선택 가능
                    if (startDate != null) {
                        return !selectedDate.isBefore(startDate!!.toLocalDate())
                    }

                    return true
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showEndDatePicker = false
                    endDatePickerState.selectedDateMillis?.let { millis ->
                        val selectedDateTime = formatMillisToLocalDateTime(millis)
                        endDate = selectedDateTime
                    }
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}

@Composable
fun ReadingStatusSelector(
    selectedStatus: ReadingStatus,
    onStatusChange: (ReadingStatus) -> Unit
) {
    val statuses = listOf(
        ReadingStatus.PLANNED to ("읽을 예정인 책"),
        ReadingStatus.READING to ("현재 읽고 있는 책"),
        ReadingStatus.FINISHED to ("완독한 책"),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        statuses.forEach { (status, subtitle) ->
            val isSelected = selectedStatus == status
            CategoryRow(
                title = status.displayName,
                subtitle = subtitle,
                isSelected = isSelected,
                onClick = { onStatusChange(status) }
            )
        }
    }
}


@Composable
fun ReadingStatusInputs(
    selectedStatus: ReadingStatus,
    pageInput: String,
    onPageChange: (String) -> Unit,
    startDate: LocalDateTime?,
    onStartDateClick: () -> Unit,
    endDate: LocalDateTime?,
    onEndDateClick: () -> Unit,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    when (selectedStatus) {
        ReadingStatus.READING -> {
            Semi16Text("시작한 날")
            Spacer(Modifier.height(14.dp))
            GureumClickEventTextField(
                value = startDate?.format(formatter) ?: "",
                hint = "시작한 날",
                onClick = onStartDateClick
            )
            Spacer(Modifier.height(14.dp))
            Semi16Text("현재 페이지 (선택사항)")
            Spacer(Modifier.height(14.dp))
            GureumTextField(
                value = pageInput,
                onValueChange = { onPageChange(it.filter(Char::isDigit)) },
                hint = "예 : 157",
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_book_outline),
                        contentDescription = "페이지"
                    )
                },
                keyboardType = KeyboardType.Number
            )
            BodySubText("시작할 페이지를 입력하세요 (기본값: 0페이지)")
        }

        ReadingStatus.FINISHED -> {
            Semi16Text("독서 기간")
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GureumClickEventTextField(
                    value = startDate?.format(formatter) ?: "",
                    hint = "시작한 날",
                    onClick = onStartDateClick
                )
                GureumClickEventTextField(
                    value = endDate?.format(formatter) ?: "",
                    hint = "다 읽은 날",
                    onClick = onEndDateClick
                )
            }
        }

        else -> Unit // PLANNED일 땐 아무 입력 없음
    }
}