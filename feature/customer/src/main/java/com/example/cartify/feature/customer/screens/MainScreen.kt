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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.theme.JapandiCanvas

/**
 * Main Content Shell - Decoupled UI for the bottom navigation structure.
 * Handles visual state for navigation items and provide interaction callbacks.
 */
@Composable
fun MainContent(
    currentRoute: String?,
    navigationItems: List<NavigationItem>,
    onItemClick: (NavigationItem) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val lightBarGreen = Color(0xFFE8F1E7) // Clean Japandi-ish light green
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding() 
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp) 
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navigationItems.forEach { item ->
                        val selected = currentRoute == item.route
                        
                        CustomNavigationItem(
                            item = item,
                            selected = selected,
                            activeColor = primaryColor,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        },
        containerColor = JapandiCanvas,
        content = content
    )
}

@Composable
private fun CustomNavigationItem(
    item: NavigationItem,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (selected) 0.12f else 0f,
        label = "backgroundAlpha"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (selected) activeColor else Color.Gray.copy(alpha = 0.6f),
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (selected) activeColor.copy(alpha = backgroundAlpha) else Color.Transparent),
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
            color = if (selected) activeColor else Color.Gray,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Preview(showBackground = true, name = "Main Shell - Customer Mode")
@Composable
fun PreviewMainContentCustomer() {
    val items = listOf(
        NavigationItem("Home", "home", Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem("Cart", "cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        NavigationItem("Orders", "orders", Icons.AutoMirrored.Filled.ListAlt, Icons.AutoMirrored.Outlined.ListAlt),
        NavigationItem("Messages", "messages", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        NavigationItem("Profile", "profile", Icons.Filled.Person, Icons.Outlined.Person)
    )
    MainContent(
        currentRoute = "home",
        navigationItems = items,
        onItemClick = {},
        content = { Box(modifier = Modifier.fillMaxSize()) }
    )
}

@Preview(showBackground = true, name = "Main Shell - Vendor Mode")
@Composable
fun PreviewMainContentVendor() {
    val items = listOf(
        NavigationItem("Dashboard", "dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        NavigationItem("Inventory", "inventory", Icons.Filled.Inventory, Icons.Outlined.Inventory),
        NavigationItem("Add", "add", Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
        NavigationItem("Messages", "messages", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        NavigationItem("Profile", "profile", Icons.Filled.Person, Icons.Outlined.Person)
    )
    MainContent(
        currentRoute = "dashboard",
        navigationItems = items,
        onItemClick = {},
        content = { Box(modifier = Modifier.fillMaxSize()) }
    )
}
