package com.example.cartify.ui.screens.details

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
import com.example.cartify.ui.OrderDetailState
import com.example.cartify.ui.OrderDetailViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController, 
    orderId: String?,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val state by viewModel.orderState
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val softGray = Color(0xFFF5F5F5)

    LaunchedEffect(orderId) {
        orderId?.let { viewModel.loadOrderDetail(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (state) {
                is OrderDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = cartifyGreen)
                }
                is OrderDetailState.Error -> {
                    Text(
                        text = (state as OrderDetailState.Error).message,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                is OrderDetailState.Success -> {
                    val order = (state as OrderDetailState.Success).order
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // 1. Order Stepper
                        Text(text = "Order Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OrderStepper(currentStatus = order.status)

                        Spacer(modifier = Modifier.height(32.dp))

                        // 2. Delivery Address Section
                        Text(text = "Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = softGray)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = cartifyGreen)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Delivery Location", fontWeight = FontWeight.Bold)
                                    Text(text = order.customerAddress, color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Items Summary
                        Text(text = "Items Ordered", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        order.items.forEach { cartItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${cartItem.quantity}x ${cartItem.product.name}", modifier = Modifier.weight(1f))
                                Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", cartItem.product.price * cartItem.quantity)}")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. Price Summary Section
                        Text(text = "Price Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = softGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val subtotal = order.totalAmount - 50.0 // Assuming 50 was added as fee
                                val deliveryFee = 50.0
                                val total = order.totalAmount

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Subtotal", color = Color.Gray)
                                    Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", subtotal)}")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Delivery Fee", color = Color.Gray)
                                    Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", deliveryFee)}")
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text(
                                        text = "PKR ${String.format(Locale.getDefault(), "%.2f", total)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = cartifyGreen
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { /* Help logic */ },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cartifyGreen)
                        ) {
                            Text(text = "Need Help?", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {}
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
                            .background(if (index <= currentIndex) MaterialTheme.colorScheme.primary else Color.LightGray),
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
                                .background(if (index < currentIndex) MaterialTheme.colorScheme.primary else Color.LightGray)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = step,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index <= currentIndex) Color.Black else Color.Gray,
                    modifier = Modifier.padding(bottom = if (index < steps.size - 1) 32.dp else 0.dp)
                )
            }
        }
    }
}
