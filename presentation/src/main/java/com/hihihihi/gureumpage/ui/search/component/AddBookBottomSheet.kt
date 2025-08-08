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
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hihihihi.gureumpage.designsystem.components.TitleSubText
import com.hihihihi.gureumpage.designsystem.components.TitleText
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookBottomSheet(
    book: SearchBook,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (category: String, page: Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("읽을 책") }
    var pageInput by remember { mutableStateOf("") }
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
                    TitleSubText(book.title)
                    Spacer(modifier = Modifier.height(4.dp))
                    //저자명
                    BodyMediumText(book.author)
                    //출판사명
                    BodyMediumText(book.publisher)
                    Spacer(modifier = Modifier.weight(1f))
                    //카테고리
                    BodyMediumText(book.categoryName.split(">")[1])
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            //카테고리 (읽을 책, 읽는 중, 읽은 책)
            TitleText(
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
