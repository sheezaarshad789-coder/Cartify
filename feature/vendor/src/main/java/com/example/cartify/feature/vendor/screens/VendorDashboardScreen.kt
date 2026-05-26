package com.example.cartify.feature.vendor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.cartify.core.common.model.Order
import com.example.cartify.core.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardContent(
    totalSales: String,
    activeOrdersCount: Int,
    totalProducts: Int,
    orders: List<Order>,
    isLoading: Boolean = false,
    selectedTab: Int = 0,
    onTabSelect: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onOrderUpdateStatus: (Order, String) -> Unit,
    onOrderClick: (Order) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vendor Console",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = JapandiSage,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        containerColor = JapandiCanvas
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = JapandiSage
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        BusinessOverviewSection(totalSales, activeOrdersCount, totalProducts, onInventoryClick)
                    }

                    item {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = JapandiSage,
                            divider = { HorizontalDivider(color = JapandiDivider.copy(alpha = 0.5f)) },
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = JapandiSage,
                                    height = 3.dp
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { onTabSelect(0) }
                            ) {
                                Text(
                                    text = "Active",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { onTabSelect(1) }
                            ) {
                                Text(
                                    text = "History",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    if (orders.isEmpty()) {
                        item { EmptyOrdersPlaceholder() }
                    } else {
                        items(orders) { order ->
                            OrderListItem(
                                order = order,
                                onUpdateStatus = { onOrderUpdateStatus(order, it) },
                                onClick = { onOrderClick(order) }
                            )
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun BusinessOverviewSection(sales: String, orders: Int, products: Int, onInventory: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Business Overview", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Sales", "PKR $sales", Icons.Default.Payments, modifier = Modifier.weight(1f))
            StatCard("Orders", "$orders", Icons.Default.ShoppingCart, modifier = Modifier.weight(1f))
        }
        StatCard("Inventory", "$products Products", Icons.Default.Inventory, modifier = Modifier.fillMaxWidth().clickable { onInventory() })
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.shadow(1.dp, RoundedCornerShape(18.dp)), shape = RoundedCornerShape(18.dp), color = Color.White) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(JapandiCanvas), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = JapandiSage, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = JapandiEarthyGray)
                Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
            }
        }
    }
}

@Composable
private fun OrderListItem(order: Order, onUpdateStatus: (String) -> Unit, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).shadow(1.dp, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), color = Color.White) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("ORDER #${order.id.takeLast(6).uppercase()}", style = MaterialTheme.typography.labelSmall, color = JapandiEarthyGray)
                Text(order.customerName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = JapandiCharcoal)
                Text("PKR ${order.totalAmount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = JapandiSage)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = JapandiSage.copy(alpha = 0.1f)) {
                Text(order.status.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = JapandiSage)
            }
        }
    }
}

@Composable private fun EmptyOrdersPlaceholder() { Box(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) { Text("No orders found", color = JapandiEarthyGray) } }
