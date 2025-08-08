package com.hihihihi.gureumpage.ui.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun OnBoardingMainContents(
    titleText: String,
    subTitleText: String,
    gureumRes: Int = R.drawable.ic_cloud_reading,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(gureumRes),
            contentDescription = "OnboardingGureumImage",
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = titleText,
            style = GureumTypography.displaySmall,
            color = GureumTheme.colors.gray900,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = subTitleText,
            color = GureumTheme.colors.gray500,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        content()
    }
}