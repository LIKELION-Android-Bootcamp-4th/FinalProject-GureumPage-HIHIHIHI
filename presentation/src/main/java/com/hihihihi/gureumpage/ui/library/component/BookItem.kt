package com.hihihihi.gureumpage.ui.library.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.library.model.Book

//한 권의 책정보
@Composable
fun BookItem(book: Book) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(100.dp)
    ) {
        //이미지
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GureumTheme.colors.bookBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("이미지", color = GureumTheme.colors.gray600)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyMedium,
                color = GureumTheme.colors.gray800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = GureumTheme.colors.gray500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
//        book.publisher?.let {
//            Text(
//                text = it,
//                style = MaterialTheme.typography.labelSmall,
//                color = Color.Gray
//            )
//        }
        }


    }
}