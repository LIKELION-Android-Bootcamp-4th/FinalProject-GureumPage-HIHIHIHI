package com.hihihihi.gureumpage.ui.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatDateToSimpleString
import com.hihihihi.gureumpage.common.utils.formatMillisToLocalDateTime
import com.hihihihi.gureumpage.designsystem.components.BodyMediumText
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.components.BookCoverImage
import com.hihihihi.gureumpage.designsystem.components.GureumBetweenDatePicker
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.GureumClickEventTextField
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi18Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.search.model.Book
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookBottomSheet(
    book: SearchBook,
    sheetState: SheetState,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (Book) -> Unit,
    onGetBookPageCount: (isbn: String, onResult: (Int?) -> Unit) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ReadingStatus.PLANNED.displayName) }
    var pageInput by remember { mutableStateOf("0") }
    var bookPageCount by remember { mutableStateOf<Int?>(null) }
    val focusManager = LocalFocusManager.current

    var startDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isSelectingStartDate by remember { mutableStateOf(true) }

    // 화면진입 시 페이지 수를 가져오는 로직
    LaunchedEffect(book.isbn) {
        if (book.isbn.isNotBlank()) {
            onGetBookPageCount(book.isbn) { pageCount ->
                bookPageCount = pageCount
            }
        }
    }

    val isButtonEnabled by remember {
        derivedStateOf {
            if (isLoading) {
                false
            } else {
                when (selectedCategory.toReadingStatus()) {
                    ReadingStatus.PLANNED -> true
                    ReadingStatus.READING -> {
                        startDate != null && pageInput.isNotBlank()
                    }
                    ReadingStatus.FINISHED -> {
                        startDate != null && endDate != null
                    }
                    else -> false
                }
            }
        }
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
                .padding(horizontal = 20.dp),
        ) {
            Semi18Text(
                text = "서재에 추가",
                color = GureumTheme.colors.gray800
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                //책 표지
                BookCoverImage(
                    modifier = Modifier
                        .size(width = 80.dp, height = 112.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    imageUrl = book.coverImageUrl,
                )
                Spacer(modifier = Modifier.width(12.dp))
                //책 설명
                Column(modifier = Modifier.height(112.dp)) {
                    Semi14Text(book.title)                      //책 이름
                    Spacer(modifier = Modifier.height(4.dp))
                    BodyMediumText(book.author)                 //저자명
                    BodyMediumText(book.publisher)              //출판사명
                    BodyMediumText(book.categoryName.split(">")[1]) //카테고리
                    //페이지
                    bookPageCount?.let { pageCount ->
                        BodyMediumText("${pageCount}페이지")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            //카테고리 (읽을 책, 읽는 중, 읽은 책)
            Semi16Text(
                text = "카테고리 선택",
                color = GureumTheme.colors.gray800
            )
            Spacer(modifier = Modifier.height(14.dp))
            //카테고리 선택 토글
            val categories = listOf(
                "읽을 책" to "읽을 예정인 책",
                "읽는 중" to "현재 읽고 있는 책",
                "읽은 책" to "완독한 책",
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { (title, subtitle) ->
                    val isSelected = selectedCategory == title

                    CategoryRow(
                        title = title,
                        subtitle = subtitle,
                        isSelected = isSelected,
                        onClick = {
                            selectedCategory = title
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            //토글 여부에 따라 달라지는 하단 뷰
            when (selectedCategory.toReadingStatus()) {
                ReadingStatus.READING -> {
                    Semi16Text("시작한 날")
                    Spacer(Modifier.height(14.dp))
                    GureumClickEventTextField(
                        value = startDate?.let { formatDateToSimpleString(it) } ?: "",
                        hint = "시작한 날",
                        onClick = {
                            isSelectingStartDate = true
                            showDatePicker = true
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar_outline),
                                contentDescription = "날짜 선택"
                            )
                        }
                    )
                    Spacer(Modifier.height(8.dp))

                    Semi16Text("현재 페이지 (선택사항)")
                    Spacer(modifier = Modifier.height(14.dp))
                    //사용자 페이지 입력 필드
                    GureumTextField(
                        value = pageInput,
                        onValueChange = {
                            val digits = it.filter(Char::isDigit)
                            val max = bookPageCount ?: Int.MAX_VALUE
                            pageInput = digits.toIntOrNull()
                                ?.coerceAtMost(max)
                                ?.toString()
                                ?: ""
                        },
                        hint = "예 : 157",
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_book_outline),
                                contentDescription = "페이지"
                            )
                        },
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                    BodySubText(
                        "시작할 페이지를 입력하세요 (기본값: 0페이지)",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                ReadingStatus.FINISHED -> {
                    Semi16Text("독서 기간")
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // 시작일 입력 필드
                        GureumClickEventTextField(
                            value = startDate?.let { formatDateToSimpleString(it) } ?: "",
                            hint = "시작한 날",
                            onClick = {
                                isSelectingStartDate = true
                                showDatePicker = true
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar_outline),
                                    contentDescription = "날짜 선택"
                                )
                            }
                        )
                        // 종료일 입력 필드
                        GureumClickEventTextField(
                            value = formatDateToSimpleString(endDate),
                            hint = "다 읽은 날",
                            onClick = {
                                isSelectingStartDate = false
                                showDatePicker = true
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar_outline),
                                    contentDescription = "날짜 선택"
                                )
                            }
                        )
                    }
                }

                else -> {}
            }
            if (showDatePicker) {
                GureumBetweenDatePicker(
                    isSelectingStart = isSelectingStartDate,
                    startDate = startDate,
                    endDate = endDate,
                    onDismiss = { showDatePicker = false },
                    onConfirm = { millis ->
                        val selectedDate = formatMillisToLocalDateTime(millis)

                        if (isSelectingStartDate) {
                            startDate = selectedDate
                            if (selectedCategory.toReadingStatus() == ReadingStatus.READING && pageInput.isEmpty()) {
                                pageInput = "0"
                            }
                        } else {
                            endDate = selectedDate
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            GureumButton(
                "서재에 추가",
                enabled = isButtonEnabled,
                onClick = {
                    //포커스 제거
                    focusManager.clearFocus()
                    //페이지 가져오기 기본값 0
                    val page = pageInput.toIntOrNull() ?: 0
                    //viewModel에 전달 할 데이터
                    val addBook = Book(
                        searchBook = book,
                        startDate = startDate ?: LocalDateTime.now(),
                        endDate = endDate ?: LocalDateTime.now(),
                        currentPage = page,
                        totalPage = bookPageCount ?: 0,
                        status = selectedCategory.toReadingStatus()!!
                    )
                    onConfirm(addBook)
                }
            )
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

private fun String.toReadingStatus(): ReadingStatus? {
    return when (this.trim()) {
        ReadingStatus.PLANNED.displayName -> ReadingStatus.PLANNED
        ReadingStatus.READING.displayName -> ReadingStatus.READING
        ReadingStatus.FINISHED.displayName -> ReadingStatus.FINISHED
        else -> null
    }
}