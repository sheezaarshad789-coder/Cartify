package com.example.cartify.ui.screens.main.tabs

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
import com.example.cartify.data.model.Order
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.OrdersState
import com.example.cartify.ui.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavController, 
    rootNavController: NavController,
    viewModel: OrdersViewModel = viewModel()
) {
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val softGray = Color(0xFFF0F0F0)
    
    val ordersState by viewModel.ordersState
    var selectedTab by remember { mutableStateOf("All Order") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Orders",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (ordersState) {
            is OrdersState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = cartifyGreen)
                }
            }
            is OrdersState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = (ordersState as OrdersState.Error).message, color = Color.Red)
                        Button(onClick = { viewModel.loadOrders() }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Retry")
                        }
                    }
                }
            }
            is OrdersState.Success -> {
                val allOrders = (ordersState as OrdersState.Success).orders
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
                                color = if (isSelected) cartifyGreen else softGray,
                            ) {
                                Text(
                                    text = tab,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (filteredOrders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No orders found in this category", color = Color.Gray)
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
                                    cartifyGreen = cartifyGreen, 
                                    softGray = softGray,
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
    cartifyGreen: Color, 
    softGray: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = softGray,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (order.storeName.contains("Mart")) Color(0xFFE8F5E9) else Color(0xFFF5E6D3)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (order.storeName.contains("Mart")) "🥑" else "📦",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (order.items.isNotEmpty()) order.items.first().product.name else order.storeName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = order.date,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "PKR ${order.totalAmount.toInt()}",
                    color = cartifyGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            ) {
                Text(
                    text = order.status,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
