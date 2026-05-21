package com.example.cartify.feature.customer.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.feature.customer.ProfileViewModel
import com.example.cartify.feature.vendor.screens.*
import com.example.cartify.feature.customer.screens.*

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    val forestGreen = MaterialTheme.colorScheme.primary
    // Changed to a more visible light green
    val lightBarGreen = Color(0xFFD5E8D4) 
    
    val profileViewModel: ProfileViewModel = viewModel()
    val userRole by profileViewModel.userRole
    val isVendor = userRole == "vendor"

    // Customer Navigation Items
    val customerItems = listOf(
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

    // Vendor Navigation Items
    val vendorItems = listOf(
        NavigationItem("Dashboard", Screen.VendorDashboard.route, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        NavigationItem("Inventory", Screen.VendorInventory.route, Icons.Filled.Inventory, Icons.Outlined.Inventory),
        NavigationItem("Add", Screen.AddProduct.route, Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
        NavigationItem("Messages", Screen.Messages.route,
            Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat
        ),
        NavigationItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
    )

    val currentItems = if (isVendor) vendorItems else customerItems

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding() 
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp) 
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = lightBarGreen,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    currentItems.forEach { item ->
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
            startDestination = if (isVendor) Screen.VendorDashboard.route else Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Shared Screens
            composable(Screen.Profile.route) { ProfileScreen(navController, rootNavController, profileViewModel) }
            composable(Screen.Messages.route) { MessagesScreen(navController, rootNavController) }

            // Customer Specific Screens
            composable(Screen.Home.route) { HomeScreen(navController, rootNavController) }
            composable(Screen.Cart.route) { CartScreen(navController, rootNavController) }
            composable(Screen.Orders.route) { OrdersScreen(navController, rootNavController) }

            // Vendor Specific Screens
            composable(Screen.VendorDashboard.route) { 
                VendorDashboardScreen(navController = navController, rootNavController = rootNavController)
            }
            composable(Screen.VendorInventory.route) { 
                VendorInventoryScreen(navController = navController) 
            }
            composable(Screen.AddProduct.route) { 
                AddProductScreen(navController = navController) 
            }
            composable(Screen.StoreSettings.route) {
                StoreSettingsScreen(navController = navController)
            }
            composable(
                route = Screen.EditProduct.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                EditProductScreen(navController = navController, productId = productId)
            }
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
        targetValue = if (selected) 0.3f else 0f,
        label = "backgroundAlpha"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (selected) forestGreen else Color.DarkGray.copy(alpha = 0.6f),
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (selected) forestGreen.copy(alpha = backgroundAlpha) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }
        
        Text(
            text = item.title,
            color = if (selected) forestGreen else Color.DarkGray,
            fontSize = 11.sp,
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
