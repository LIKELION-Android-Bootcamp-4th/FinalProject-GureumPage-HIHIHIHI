package com.hihihihi.gureumpage.ui.onboarding.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.GureumLinearProgressBar
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun OnboardingTopContents(
    navController: NavHostController,
    progress: Float,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "onboarding_up_button",
                tint = GureumTheme.colors.gray900,
                modifier = Modifier.size(36.dp)
            )
        }

        GureumLinearProgressBar(
            height = 6,
            progress = progress,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
