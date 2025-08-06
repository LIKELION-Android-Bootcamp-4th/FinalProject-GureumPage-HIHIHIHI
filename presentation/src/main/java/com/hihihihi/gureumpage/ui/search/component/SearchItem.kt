package com.hihihihi.gureumpage.ui.search.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.search.Search

@Composable
fun SearchItem(result: Search, onItemClick: (Search) -> Unit, onAddClick: (Search) -> Unit) {
    Column(
        modifier = Modifier
            .background(GureumTheme.colors.background)
            .clickable { onItemClick(result) },
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            //책 이미지
            AsyncImage(
                modifier = Modifier
                    .size(width = 57.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                model = result.imgUrl,
                contentDescription = "책",
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                //책 제목
                Text(
                    text = result.title,
                    style = GureumTypography.bodyMedium,
                    color = GureumTheme.colors.gray600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                //저자
                BodySubText(result.author, modifier = Modifier.padding(top = 4.dp))
                //출판사
                BodySubText(result.publisher, modifier = Modifier.padding(top = 4.dp))
                //페이지
                BodySubText(result.page.toString() + 'p', modifier = Modifier.padding(top = 4.dp))
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
                    .background(GureumTheme.colors.primary)
                    .clickable { onAddClick(result) },
                contentAlignment = Alignment.Center,

                ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "추가하기",
                    tint = GureumTheme.colors.white
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(GureumTheme.colors.dividerDeep)
        )
    }
}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SearchPreView() {
    GureumPageTheme {
        SearchItem(
            Search(
                "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158511982.jpg",
                title = "test title",
                author = "test author",
                publisher = "test publisher",
                page = 1,
            ),
            onItemClick = {},
            onAddClick = {}
        )
    }
}
