package com.example.cartify.ui.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.screens.main.tabs.*

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    val forestGreen = Color(0xFF2E7D32)
    val lightBarGreen = Color(0xFFEBF3E8)
    
    val items = listOf(
        NavigationItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem("Cart", Screen.Cart.route, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        NavigationItem("Orders", Screen.Orders.route,
            Icons.AutoMirrored.Filled.ListAlt, Icons.AutoMirrored.Outlined.ListAlt
        ),
        NavigationItem("Messages", Screen.Messages.route,
            Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat
        ),
        NavigationItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding() 
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 8.dp, top = 0.dp) 
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = lightBarGreen,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        
                        CustomNavigationItem(
                            item = item,
                            selected = selected,
                            forestGreen = forestGreen,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() - 35.dp // Reduces the white space above the bar
            )
        ) {
            composable(Screen.Home.route) { HomeScreen(navController, rootNavController) }
            composable(Screen.Cart.route) { CartScreen(navController, rootNavController) }
            composable(Screen.Orders.route) { OrdersScreen(navController, rootNavController) }
            composable(Screen.Messages.route) { MessagesScreen(navController, rootNavController) }
            composable(Screen.Profile.route) { ProfileScreen(navController, rootNavController) }
        }
    }
}

@Composable
fun CustomNavigationItem(
    item: NavigationItem,
    selected: Boolean,
    forestGreen: Color,
    onClick: () -> Unit
) {
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (selected) 0.25f else 0f,
        label = "backgroundAlpha"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (selected) forestGreen else Color.Gray.copy(alpha = 0.8f),
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (selected) forestGreen.copy(alpha = backgroundAlpha) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = item.title,
            color = if (selected) forestGreen else Color.Gray,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
