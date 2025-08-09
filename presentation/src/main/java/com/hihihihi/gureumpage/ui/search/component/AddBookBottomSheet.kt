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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodyMediumText
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookBottomSheet(
    book: SearchBook,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (category: String, page: Int) -> Unit,
    onGetBookPageCount: (isbn: String, onResult: (Int?) -> Unit) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("읽을 책") }
    var pageInput by remember { mutableStateOf("") }
    var bookPageCount by remember { mutableStateOf<Int?>(null) }
    val focusManager = LocalFocusManager.current
    // "읽은 책" 상태 변수
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateFieldToUpdate by remember { mutableStateOf<((String) -> Unit)?>(null) }
    val datePickerState = rememberDatePickerState()

    // 화면진입 시 페이지 수를 가져오는 로직
    LaunchedEffect(book.isbn) {
        if (book.isbn.isNotBlank()) {
            onGetBookPageCount(book.isbn) { pageCount ->
                bookPageCount = pageCount
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

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "서재에 추가",
                    //추가 후 수정예정
                    style = GureumTypography.bodyLarge, color = GureumTheme.colors.gray800
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "닫기",
                        tint = GureumTheme.colors.gray500
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                //책 표지
                AsyncImage(
                    modifier = Modifier
                        .size(width = 80.dp, height = 112.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    model = book.coverImageUrl,
                    contentDescription = "책 표지",
                )
                Spacer(modifier = Modifier.width(12.dp))
                //책 설명
                Column(modifier = Modifier.height(112.dp)) {
                    //책 이름
                    Semi14Text(book.title)
                    Spacer(modifier = Modifier.height(4.dp))
                    //저자명
                    BodyMediumText(book.author)
                    //출판사명
                    BodyMediumText(book.publisher)
                    //카테고리
                    BodyMediumText(book.categoryName.split(">")[1])
                    //페이지
                    bookPageCount?.let { pageCount ->
                        BodyMediumText("${pageCount}페이지")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            //카테고리 (읽을 책, 읽는 중, 읽은 책)
            Semi16Text(
                "카테고리 선택", modifier = Modifier.padding(start = 20.dp, 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            //카테고리 선택 토글
            val categories = listOf(
                "읽을 책" to "읽을 예정인 책",
                "읽는 중" to "현재 읽고 있는 책",
                "읽은 책" to "완독한 책",
            )
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { (title, subtitle) ->
                    val isSelected = selectedCategory == title

                    CategoryRow(
                        title = title, subtitle = subtitle, isSelected = isSelected, onClick = {
                            selectedCategory = title
                        })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            //토글 여부에 따라 달라지는 하단 뷰
            when (selectedCategory) {
                "읽는 중" -> {
                    TitleText(
                        "현재 페이지 (선택사항)", modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    //사용자 페이지 입력 필드
                    OutlinedTextField(
                        shape = RoundedCornerShape(12.dp),
                        value = pageInput,
                        //숫자만 입력 되도록 함
                        onValueChange = {
                            pageInput = it.filter { char -> char.isDigit() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp),
                        placeholder = {
                            Text(
                                text = "예 : 157",
                                style = GureumTypography.bodyMedium,
                                color = GureumTheme.colors.gray500
                            )
                        },
                        trailingIcon = {
                            //이미지 부재 대체
                            Icon(
                                painter = painterResource(id = R.drawable.ic_book_outline),
                                contentDescription = "페이지"
                            )
                        })
                    BodySubText(
                        "시작할 페이지를 입력하세요 (기본값: 1페이지)",
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)
                    )
                }

                "읽은 책" -> {
                    TitleText(
                        "독서 기간", modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 시작일 입력 필드
                        DatePickTextField(
                            value = startDate,
                            placeholder = "시작한 날",
                            onClick = {
                                dateFieldToUpdate = { newDate -> startDate = newDate }
                                showDatePicker = true
                            }
                        )
                        // 종료일 입력 필드
                        DatePickTextField(
                            value = endDate,
                            placeholder = "다 읽은 날",
                            onClick = {
                                dateFieldToUpdate = { newDate -> endDate = newDate }
                                showDatePicker = true
                            }
                        )
                    }
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDatePicker = false
                                        // 선택된 날짜를 Long(밀리초) 타입으로 가져옴
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            // "yyyy.MM.dd" 형식의 문자열로 변환
                                            val formattedDate = SimpleDateFormat(
                                                "yyyy.MM.dd",
                                                Locale.getDefault()
                                            ).format(Date(millis))
                                            // 어떤 필드를 업데이트할지 결정하고 상태 업데이트
                                            dateFieldToUpdate?.invoke(formattedDate)
                                        }
                                    }
                                ) { Text("확인") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }

                else -> {}
            }


            Spacer(modifier = Modifier.height(24.dp))
            GureumButton(
                "서재에 추가", modifier = Modifier.padding(start = 20.dp, end = 20.dp), onClick = {
                    //포커스 제거
                    focusManager.clearFocus()
                    //페이지 가져오기 기본값 0
                    val page = pageInput.toIntOrNull() ?: 0
                    //viewModel에 전달 할 데이터
                    onConfirm(selectedCategory, page)
                })
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}
