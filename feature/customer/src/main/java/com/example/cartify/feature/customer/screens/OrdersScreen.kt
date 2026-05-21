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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.model.Order
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.OrdersState
import com.example.cartify.feature.customer.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavController,
    rootNavController: NavController,
    viewModel: OrdersViewModel = viewModel()
) {
    val ordersState by viewModel.ordersState
    var selectedTab by remember { mutableStateOf("All Order") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Orders",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        when (val state = ordersState) {
            is OrdersState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = JapandiSage)
                }
            }
            is OrdersState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = JapandiError)
                        Button(onClick = { viewModel.loadOrders() }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                            Text("Retry")
                        }
                    }
                }
            }
            is OrdersState.Success -> {
                val allOrders = state.orders
                val filteredOrders = if (selectedTab == "All Order") {
                    allOrders
                } else {
                    allOrders.filter { it.status.equals(selectedTab, ignoreCase = true) }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Filter Tabs
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, top = 4.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val tabs = listOf("All Order", "Pending", "Processing", "Delivered")
                        items(tabs) { tab ->
                            val isSelected = selectedTab == tab
                            Surface(
                                modifier = Modifier.clickable { selectedTab = tab },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) JapandiSage else JapandiDivider,
                            ) {
                                Text(
                                    text = tab,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                    color = if (isSelected) Color.White else JapandiCharcoal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (filteredOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No orders found in this category", color = JapandiEarthyGray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredOrders) { order ->
                                OrderItemCard(
                                    order = order,
                                    onClick = {
                                        rootNavController.navigate(Screen.OrderDetail.createRoute(order.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun OrderItemCard(
    order: Order,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(JapandiCanvas),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (order.storeName.contains("Mart", ignoreCase = true)) "🥑" else "📦",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (order.items.isNotEmpty()) order.items.first().product.name else order.storeName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = JapandiCharcoal
                )
                Text(
                    text = order.date,
                    color = JapandiEarthyGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "PKR ${order.totalAmount.toInt()}",
                    color = JapandiSage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = JapandiDivider.copy(alpha = 0.5f)
            ) {
                val statusColor = when(order.status.lowercase()) {
                    "pending" -> JapandiEarthyGray
                    "processing" -> JapandiSageLight
                    "delivered" -> JapandiSage
                    else -> JapandiCharcoal
                }
                Text(
                    text = order.status,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
