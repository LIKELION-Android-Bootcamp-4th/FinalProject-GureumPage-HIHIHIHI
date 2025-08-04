package com.hihihihi.gureumpage.navigation

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme


sealed class BottomNavItem(
    val route: String,
    val label: String,
    val unSelectedIconResId: Int,
    val onSelectedIconResId: Int
) {
    object Home : BottomNavItem("home", "홈", R.drawable.ic_home_outline, R.drawable.ic_home_filled)
    object Library : BottomNavItem("library", "내 서재", R.drawable.ic_book_outline, R.drawable.ic_book_filled)
    object Quotes : BottomNavItem("quotes", "필사", R.drawable.ic_lightbulb_outline, R.drawable.ic_lightbulb_filled)
    object Statistics :
        BottomNavItem("statistics", "통계", R.drawable.ic_chart_pie_outline, R.drawable.ic_chart_pie_filled)

    object MyPage : BottomNavItem("mypage", "마이페이지", R.drawable.ic_user_outline, R.drawable.ic_user_filled)

    companion object {
        val items = listOf(Library, Quotes, Home, Statistics, MyPage)
    }
}

@Composable
fun GureumBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
            BottomNavItem.items.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconId = if (isSelected) item.onSelectedIconResId else item.unSelectedIconResId

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = item.label,
                        )
                    },
                    selected = isSelected,
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = GureumTheme.colors.primary,
                        unselectedIconColor = GureumTheme.colors.gray300,
                        selectedTextColor = GureumTheme.colors.primary,
                        unselectedTextColor = GureumTheme.colors.gray300,
                    ),
                )
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumBottomNavPreview() {
    val navController = rememberNavController()
    GureumPageTheme {
        GureumBottomNavBar(navController)
    }
}