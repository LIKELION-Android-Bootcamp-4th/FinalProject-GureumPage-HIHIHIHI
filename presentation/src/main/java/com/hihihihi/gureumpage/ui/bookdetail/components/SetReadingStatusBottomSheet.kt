package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.search.component.CategoryRow
import com.hihihihi.gureumpage.ui.search.component.DatePickTextField
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetReadingStatusBottomSheet(
    userBook: UserBook,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (ReadingStatus, Int?, LocalDateTime?, LocalDateTime?) -> Unit,
) {
    var selectedStatus by remember { mutableStateOf(userBook.status) }
    var pageInput by remember { mutableStateOf(userBook.currentPage.toString()) }
    var startDate by remember { mutableStateOf<LocalDateTime?>(userBook.startDate) }
    var endDate by remember { mutableStateOf<LocalDateTime?>(userBook.endDate) }

    var showDatePicker by remember { mutableStateOf(false) }
    var dateFieldToUpdate by remember { mutableStateOf<((LocalDateTime) -> Unit)?>(null) }
    val datePickerState = rememberDatePickerState()
    val focusManager = LocalFocusManager.current

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
                onStartDateClick = {
                    dateFieldToUpdate = { startDate = it }
                    showDatePicker = true
                },
                endDate = endDate,
                onEndDateClick = {
                    dateFieldToUpdate = { endDate = it }
                    showDatePicker = true
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            GureumButton(
                text = "변경하기",
                onClick = {
                    focusManager.clearFocus()
                    onConfirm(
                        selectedStatus,
                        pageInput.toIntOrNull(),
                        startDate ,
                        endDate
                    )
                }
            )
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDateTime = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .atStartOfDay() // LocalDateTime 변환
                        dateFieldToUpdate?.invoke(selectedDateTime)
                    }
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
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
    onEndDateClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    when (selectedStatus) {
        ReadingStatus.READING -> {
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
                }
            )
            BodySubText("시작할 페이지를 입력하세요 (기본값: 1페이지)")
        }

        ReadingStatus.FINISHED -> {
            Semi16Text("독서 기간")
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickTextField(value = startDate?.format(formatter) ?: "", placeholder = "시작한 날", onClick = onStartDateClick)
                DatePickTextField(value = endDate?.format(formatter) ?: "", placeholder = "다 읽은 날", onClick = onEndDateClick)
            }
        }

        else -> Unit // PLANNED일 땐 아무 입력 없음
    }
}

