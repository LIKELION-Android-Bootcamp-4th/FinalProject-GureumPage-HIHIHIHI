package com.hihihihi.gureumpage.ui.bookdetail.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodyText
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyUserBook

@Composable
fun BookSimpleInfoSection(
    userBook: UserBook,
    onReadingStatusClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .heightIn(max = 140.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userBook.imageUrl,
            contentDescription = "",
            modifier = Modifier
                .size(100.dp, 140.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Semi16Text(userBook.title, maxLine = 2)
            Spacer(modifier = Modifier.height(8.dp))
            BodyText(userBook.author, color = GureumTheme.colors.gray500)
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cloud_reading),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    userBook.status.displayName,
                    style = GureumTypography.bodyMedium,
                    color = GureumTheme.colors.systemGreen,
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GureumTheme.colors.gray800),
                    modifier = Modifier.clickable { onReadingStatusClick() }
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookSimpleInfoSectionPreview() {
    GureumPageTheme {
        BookSimpleInfoSection(dummyUserBook, {})
    }
}