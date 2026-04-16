package com.hihihihi.gureumpage.ui.nonetwork

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi24Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun NoNetworkScreen(
    onRefresh: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler {
        onExit()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = GureumTheme.colors.gray400
        )

        Spacer(modifier = Modifier.height(24.dp))

        Semi24Text(
            "인터넷 연결이 끊어졌어요",
            color = GureumTheme.colors.gray700,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Medi16Text(
            "Wi-Fi나 모바일 데이터 연결을\n확인해 주세요",
            color = GureumTheme.colors.gray600,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onRefresh,
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GureumTheme.colors.primary
            )
        ) {
            Semi16Text(
                "새로 고침",
                color = GureumTheme.colors.white
            )
        }

    }
}