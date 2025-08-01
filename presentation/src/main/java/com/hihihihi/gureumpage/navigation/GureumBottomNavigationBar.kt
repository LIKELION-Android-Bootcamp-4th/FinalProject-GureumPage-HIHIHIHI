package com.hihihihi.gureumpage.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hihihihi.gureumpage.R


sealed class BottomNavItem(
    val route: String,
    val label: String,
    val unSelectedIconResId: Int,
    val onSelectedIconResId: Int
) {
    object Home : BottomNavItem("home", "홈", R.drawable.ic_home_outline ,R.drawable.ic_home_filled)
    object Library : BottomNavItem("library", "내 서재", R.drawable.ic_book_outline ,R.drawable.ic_book_filled)
    object Quotes : BottomNavItem("quotes", "필사",R.drawable.ic_lightbulb_outline ,R.drawable.ic_lightbulb_filled)
    object Statistics : BottomNavItem("statistics", "통계", R.drawable.ic_chart_pie_outline ,R.drawable.ic_chart_pie_filled)
    object MyPage : BottomNavItem("mypage", "마이페이지", R.drawable.ic_user_outline ,R.drawable.ic_user_filled)

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
            val iconId = if(currentRoute == item.route) item.onSelectedIconResId else item.unSelectedIconResId

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = item.label
                    )
                },
                selected = currentRoute == item.route,
                label = { Text(item.label)},
                onClick = { navController.navigate(item.route){
                    popUpTo(navController.graph.startDestinationId){
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                } },
            )
        }
    }
}