package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.OrderDetailState
import com.example.cartify.feature.customer.OrderDetailViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String?,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val state by viewModel.orderState

    LaunchedEffect(orderId) {
        orderId?.let { viewModel.loadOrderDetail(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold, color = JapandiCharcoal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (val orderState = state) {
                is OrderDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
                }
                is OrderDetailState.Error -> {
                    Text(
                        text = orderState.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = JapandiError
                    )
                }
                is OrderDetailState.Success -> {
                    val order = orderState.order
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // 1. Order Status
                        Text(text = "Order Status", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JapandiCharcoal)
                        Spacer(modifier = Modifier.height(16.dp))

                        OrderStepper(currentStatus = order.status)

                        Spacer(modifier = Modifier.height(32.dp))

                        // 2. Delivery Address Section
                        Text(text = "Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JapandiCharcoal)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = JapandiSage)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Delivery Location", fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                                    Text(text = order.customerAddress, color = JapandiEarthyGray, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Items Summary
                        Text(text = "Items Ordered", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JapandiCharcoal)
                        order.items.forEach { cartItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${cartItem.quantity}x ${cartItem.product.name}", modifier = Modifier.weight(1f), color = JapandiCharcoal)
                                Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", cartItem.product.price * cartItem.quantity)}", color = JapandiCharcoal)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. Price Summary Section
                        Text(text = "Price Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JapandiCharcoal)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val subtotal = order.totalAmount - 50.0 // Assuming 50 was added as fee
                                val deliveryFee = 50.0
                                val total = order.totalAmount

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Subtotal", color = JapandiEarthyGray)
                                    Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", subtotal)}", color = JapandiCharcoal)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Delivery Fee", color = JapandiEarthyGray)
                                    Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", deliveryFee)}", color = JapandiCharcoal)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = JapandiDivider)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JapandiCharcoal)
                                    Text(
                                        text = "PKR ${String.format(Locale.getDefault(), "%.2f", total)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = JapandiSage
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { /* Help logic */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)
                        ) {
                            Text(text = "Need Help?", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                is OrderDetailState.Idle -> {}
            }
        }
    }
}

@Composable
fun OrderStepper(currentStatus: String) {
    val steps = listOf("Pending", "Processing", "Shipped", "Delivered")
    val currentIndex = when (currentStatus.lowercase()) {
        "pending" -> 0
        "processing" -> 1
        "shipped" -> 2
        "delivered" -> 3
        else -> 0
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (index <= currentIndex) JapandiSage else JapandiDivider),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index <= currentIndex) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(if (index < currentIndex) JapandiSage else JapandiDivider)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = step,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index <= currentIndex) JapandiCharcoal else JapandiEarthyGray,
                    modifier = Modifier.padding(bottom = if (index < steps.size - 1) 32.dp else 0.dp)
                )
            }
        }
    }
}
