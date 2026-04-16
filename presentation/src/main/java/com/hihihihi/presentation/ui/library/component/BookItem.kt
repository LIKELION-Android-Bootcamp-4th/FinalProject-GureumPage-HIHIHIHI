package com.hihihihi.presentation.ui.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.components.BookCoverImage
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.model.UserBookUiModel
import com.hihihihi.presentation.ui.model.isRead

@Composable
fun BookItem(book: UserBookUiModel, onClicked: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClicked(book.userBookId) },
    ) {
        val iconSize = 16.dp
        val offsetX = iconSize / 2
        val offsetY = -iconSize / 2

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            BookCoverImage(
                imageUrl = book.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyMedium,
                color = GureumTheme.colors.gray800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = GureumTheme.colors.gray500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (book.isRead()) {
            Image(
                painter = painterResource(id = R.drawable.ic_finish_stamp),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = offsetX, y = offsetY)
                    .size(36.dp),
                colorFilter = ColorFilter.tint(GureumTheme.colors.primary, blendMode = BlendMode.SrcIn),
            )
        }
    }
}
