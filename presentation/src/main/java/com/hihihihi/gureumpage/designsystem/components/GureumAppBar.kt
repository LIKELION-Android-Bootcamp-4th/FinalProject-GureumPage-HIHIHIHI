package com.hihihihi.gureumpage.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GureumAppBar(
    navController: NavHostController? = null,
    title: String = "",
    showUpButton: Boolean = false,
    actions: @Composable () -> Unit = {},
    onUpClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = { Semi16Text(text = title) },
        navigationIcon = {
            if (showUpButton) {
                IconButton(
                    onClick = {
                        onUpClick?.invoke()
                            ?: navController?.popBackStack()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "upButton",
                        tint = GureumTheme.colors.gray900,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        actions = { actions() },
        modifier = Modifier.shadow(
            elevation = 6.dp
        )
    )
}
