package com.cyphermoon.tictaczone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cyphermoon.tictaczone.ui.theme.LightSecondary
import com.cyphermoon.tictaczone.ui.theme.Primary

@Composable
fun BottomTabBarNavigation (selectedIdx: Int, onTabSelected: (Int) -> Unit, navController: NavController) {
    val items by rememberSaveable{mutableStateOf(listOf(
        BottomTabNavigationItem(
            route = ScreenRoutes.MainScreen.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            title = "Home"
        ),
        BottomTabNavigationItem(
            route = ScreenRoutes.AICharacters.route,
            selectedIcon = Icons.Filled.Face,
            unselectedIcon = Icons.Outlined.Face,
            title = "AI Mode"
        ),
        BottomTabNavigationItem(
            route = ScreenRoutes.OnlineGameScreen.route,
            selectedIcon = Icons.Filled.Place,
            unselectedIcon = Icons.Outlined.Place,
            title = "Online Mode"
        ),
    ))}

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.tertiary,
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = Primary,
                    selectedIconColor = Primary,
                    unselectedTextColor = LightSecondary,
                    unselectedIconColor = LightSecondary
                ),
                icon = {
                    Icon(
                        imageVector = if(index == selectedIdx) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                    )

                },
                label = {

                    Text(
                        text = item.title,
                        fontSize = 12.sp
                    )
                },
                selected = index == selectedIdx,
                onClick = {
                    onTabSelected(index)
                    navController.navigate(item.route)
                }
            )
        }

    }

}


// Bottom Tab Navigation Item Class

data class BottomTabNavigationItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val title: String
)