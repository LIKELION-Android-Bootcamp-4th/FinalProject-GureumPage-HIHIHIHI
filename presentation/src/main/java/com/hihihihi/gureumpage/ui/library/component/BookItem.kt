package com.hihihihi.gureumpage.ui.library.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.library.model.Book


//한 권의 책정보
@Composable
fun BookItem(book: Book, modifier: Modifier = Modifier) {
    Log.d("BookItem", "BookItem 실행됨: ${book.title}")
    val context = LocalContext.current
    val imageRequest = ImageRequest.Builder(context)
        .data(book.imageUrl)
        .crossfade(true)
        .listener(
            onStart = {
                Log.d("bookItem", "이미지 로딩 시작 : ${book.imageUrl}")
            },
            onSuccess = { _, _ ->
                Log.d("bookItem", "이미지 로딩 성공 : ${book.imageUrl}")
            },
            onError = { _, result ->
                Log.e("bookItem", "이미지 로딩 실패 : ${book.imageUrl}", result.throwable)
            }
        ).build()
    // 책 카드 ui
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        //이미지
        AsyncImage(
            model = imageRequest,
            contentDescription = book.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(8.dp))

        //제목
        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyMedium,
            color = GureumTheme.colors.gray800,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //작가
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = GureumTheme.colors.gray500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }

}