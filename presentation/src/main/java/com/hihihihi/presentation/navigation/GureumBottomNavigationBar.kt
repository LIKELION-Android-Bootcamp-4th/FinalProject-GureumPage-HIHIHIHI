package com.hihihihi.presentation.navigation

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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import kotlin.reflect.KClass

sealed class BottomNavItem(
    val destination: Any,
    val routeClass: KClass<*>,
    val label: String,
    val unSelectedIconResId: Int,
    val onSelectedIconResId: Int,
) {
    object HomeItem : BottomNavItem(
        destination = Home,
        routeClass = Home::class,
        label = "홈",
        unSelectedIconResId = R.drawable.ic_home_outline,
        onSelectedIconResId = R.drawable.ic_home_filled,
    )

    object LibraryItem : BottomNavItem(
        destination = Library,
        routeClass = Library::class,
        label = "내 서재",
        unSelectedIconResId = R.drawable.ic_book_outline,
        onSelectedIconResId = R.drawable.ic_book_filled,
    )

    object QuotesItem : BottomNavItem(
        destination = Quotes,
        routeClass = Quotes::class,
        label = "필사",
        unSelectedIconResId = R.drawable.ic_lightbulb_outline,
        onSelectedIconResId = R.drawable.ic_lightbulb_filled,
    )

    object StatisticsItem : BottomNavItem(
        destination = StatisticsWeekly,
        routeClass = StatisticsWeekly::class,
        label = "통계",
        unSelectedIconResId = R.drawable.ic_chart_pie_outline,
        onSelectedIconResId = R.drawable.ic_chart_pie_filled,
    )

    object MyPageItem : BottomNavItem(
        destination = MyPage,
        routeClass = MyPage::class,
        label = "마이페이지",
        unSelectedIconResId = R.drawable.ic_user_outline,
        onSelectedIconResId = R.drawable.ic_user_filled,
    )

    companion object {
        val items = listOf(LibraryItem, QuotesItem, HomeItem, StatisticsItem, MyPageItem)
    }
}

@Composable
fun GureumBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        BottomNavItem.items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.routeClass)
            } == true
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
                    if (isSelected) return@NavigationBarItem

                    if (item is BottomNavItem.HomeItem) {
                        val popped = navController.popBackStack<Home>(inclusive = false)
                        if (!popped) {
                            navController.navigate(Home) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    } else {
                        navController.navigate(item.destination) {
                            popUpTo<Home> {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
