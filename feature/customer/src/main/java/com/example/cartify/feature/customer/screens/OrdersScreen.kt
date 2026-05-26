package com.example.cartify.feature.customer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Order
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.theme.*
import java.util.Locale

/**
 * Orders Content - Decoupled UI layer for customer orders.
 * Focuses on visual presentation and minimalist Japandi design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersContent(
    orders: List<Order>,
    isLoading: Boolean = false,
    selectedTab: String = "All Orders",
    onTabSelect: (String) -> Unit,
    onBackClick: () -> Unit,
    onOrderClick: (Order) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Orders",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Tabs
            OrderFilterTabs(
                selectedTab = selectedTab,
                onTabSelect = onTabSelect
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = JapandiSage
                    )
                } else if (orders.isEmpty()) {
                    EmptyOrdersView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(orders) { order ->
                            OrderItemCard(
                                order = order,
                                onClick = { onOrderClick(order) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderFilterTabs(
    selectedTab: String,
    onTabSelect: (String) -> Unit
) {
    val tabs = listOf("All Orders", "Pending", "Processing", "Delivered", "Cancelled")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tabs) { tab ->
            val isSelected = selectedTab == tab
            Surface(
                modifier = Modifier.clickable { onTabSelect(tab) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) JapandiSage else Color.White,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, JapandiDivider) else null
            ) {
                Text(
                    text = tab,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = if (isSelected) Color.White else JapandiCharcoal,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun OrderItemCard(
    order: Order,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(JapandiCanvas),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (order.storeName.contains("Green")) "🥦" else "📦",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (order.items.isNotEmpty()) order.items.first().product.name else order.storeName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = JapandiCharcoal
                )
                Text(
                    text = order.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray
                )
                Text(
                    text = "PKR ${String.format(Locale.getDefault(), "%.0f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = JapandiSage
                )
            }

            OrderStatusBadge(status = order.status)
        }
    }
}

@Composable
private fun OrderStatusBadge(status: String) {
    val (backgroundColor, contentColor) = when (status.lowercase()) {
        "delivered" -> JapandiSage.copy(alpha = 0.1f) to JapandiSage
        "pending" -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
        "processing" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        "cancelled" -> JapandiError.copy(alpha = 0.1f) to JapandiError
        else -> JapandiDivider to JapandiCharcoal
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = contentColor
        )
    }
}

@Composable
private fun EmptyOrdersView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No orders found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = JapandiCharcoal
        )
        Text(
            text = "Your order history will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = JapandiEarthyGray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true, name = "Orders List")
@Composable
fun PreviewOrdersContent() {
    val mockOrders = listOf(
        Order("1", "Green Grocers", "Delivered", "24 Oct 2023", 1250.0, listOf(
            CartItem(Product("1", "Organic Spinach", 150.0, "pack", "", "", "", "", ""), 2)
        )),
        Order("2", "Fresh Bakery", "Processing", "Today", 850.0, listOf(
            CartItem(Product("2", "Whole Grain Bread", 400.0, "loaf", "", "", "", "", ""), 1)
        )),
        Order("3", "Dairy Delight", "Pending", "Yesterday", 2100.0, listOf(
            CartItem(Product("3", "Greek Yogurt", 700.0, "tub", "", "", "", "", ""), 3)
        ))
    )
    OrdersContent(
        orders = mockOrders,
        onTabSelect = {},
        onBackClick = {},
        onOrderClick = {}
    )
}
