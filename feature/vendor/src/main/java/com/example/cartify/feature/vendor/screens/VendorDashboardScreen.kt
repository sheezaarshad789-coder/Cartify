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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.feature.vendor.VendorViewModel
import com.example.cartify.data.network.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    navController: NavController, 
    rootNavController: NavController,
    viewModel: VendorViewModel = viewModel()
) {
    val state by viewModel.state
    val cartifyGreen = MaterialTheme.colorScheme.primary
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active, 1: History

    val activeOrders = state.recentOrders.filter { 
        it.status.lowercase() != "delivered" && it.status.lowercase() != "cancelled" 
    }
    val historyOrders = state.recentOrders.filter { 
        it.status.lowercase() == "delivered" || it.status.lowercase() == "cancelled" 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Vendor Dashboard", 
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    ) 
                },
                actions = {
                    IconButton(onClick = { 
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddProduct.route) },
                containerColor = cartifyGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = cartifyGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF7F7F7))
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                item {
                    Text(
                        text = "Business Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Sales",
                            value = "PKR ${state.stats.totalSales}",
                            icon = Icons.Default.Payments,
                            color = Color(0xFFE8F5E9),
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Active Orders",
                            value = "${state.stats.activeOrders}",
                            icon = Icons.Default.ShoppingCart,
                            color = Color(0xFFE3F2FD),
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    StatCard(
                        title = "Inventory Management",
                        value = "${state.stats.totalProducts} Products",
                        icon = Icons.Default.Inventory,
                        color = Color(0xFFFFF3E0),
                        tint = Color(0xFFFF9800),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.VendorInventory.route) }
                    )
                }

                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = cartifyGreen,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Active Orders (${activeOrders.size})", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("History", fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                val displayedOrders = if (selectedTab == 0) activeOrders else historyOrders

                if (displayedOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No orders to show", color = Color.Gray)
                        }
                    }
                } else {
                    items(displayedOrders) { order ->
                        OrderListItem(
                            orderId = "#${order.id.takeLast(6).uppercase()}",
                            customer = order.customerName,
                            address = order.customerAddress,
                            amount = "PKR ${order.totalAmount}",
                            status = order.status,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus)
                            },
                            onClick = {
                                rootNavController.navigate(Screen.OrderDetail.createRoute(order.id))
                            }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Text(text = title, color = Color.Gray, fontSize = 13.sp)
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

@Composable
fun OrderListItem(
    orderId: String, 
    customer: String, 
    address: String,
    amount: String, 
    status: String,
    onUpdateStatus: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = orderId, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                    Text(text = customer, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = address, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = amount, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32), fontSize = 16.sp)
                    Surface(
                        color = when(status.lowercase()) {
                            "pending" -> Color(0xFFFFF3E0)
                            "processing" -> Color(0xFFE3F2FD)
                            "delivered" -> Color(0xFFE8F5E9)
                            else -> Color(0xFFF5F5F5)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = status.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = when(status.lowercase()) {
                                "pending" -> Color(0xFFFF9800)
                                "processing" -> Color(0xFF2196F3)
                                "delivered" -> Color(0xFF4CAF50)
                                else -> Color.Gray
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            
            if (status.lowercase() == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onUpdateStatus("Processing") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Accept Order", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { onUpdateStatus("Cancelled") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Reject", fontSize = 12.sp)
                    }
                }
            } else if (status.lowercase() == "processing") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onUpdateStatus("Delivered") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mark as Delivered", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
