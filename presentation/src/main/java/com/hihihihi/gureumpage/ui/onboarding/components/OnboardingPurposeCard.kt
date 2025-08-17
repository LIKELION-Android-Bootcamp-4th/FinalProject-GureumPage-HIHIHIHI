package com.hihihihi.gureumpage.ui.onboarding.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingPurposeContents

@Composable
fun OnboardingPurposeCard(
    cardItem: OnboardingPurposeContents,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = GureumTheme.colors
    val bgColor by animateColorAsState(
        targetValue = if (checked) colors.primary50 else colors.background10
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.25.dp))
            .clickable { onCheckedChange(!checked) }
            .semantics { role = Role.Checkbox }
            .border(
                width = 1.dp,
                color = if (checked) colors.primary50 else colors.background50,
                shape = CardDefaults.shape
            ),
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
            contentColor = if (checked) colors.gray800 else colors.gray400,
        ),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(cardItem.gureumRes), contentDescription = "")
            Spacer(Modifier.width(8.dp))
            Column {
                Semi14Text(
                    text = cardItem.title,
                    color = if (checked) colors.gray800 else colors.gray500
                )
                Spacer(Modifier.height(8.dp))
                Medi12Text(
                    text = cardItem.contents,
                    color = if (checked) colors.gray800 else colors.gray500
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(
                    if (checked) R.drawable.ic_checked else R.drawable.ic_none_checked
                ),
                contentDescription = if (checked) "선택됨" else "선택 안됨",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(26.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OnboardingPurposeCardPreview() {
    GureumPageTheme {
        OnboardingPurposeCard(
            cardItem = OnboardingPurposeContents(
                R.drawable.ic_cloud_reading,
                "독서 기록",
                "읽은 책을 기록하고 관리해요"
            ),
            checked = false,
            onCheckedChange = { }
        )
    }
}