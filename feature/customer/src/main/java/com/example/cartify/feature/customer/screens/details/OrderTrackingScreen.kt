package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(navController: NavController, orderId: String?) {
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val softGray = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = cartifyGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help or More */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(softGray)
        ) {
            // Map Placeholder Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = cartifyGreen, modifier = Modifier.size(48.dp))
                    Text("Live Map View", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("Rider is 1.2 km away", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                // Floating Rider Info Card
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(cartifyGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("John Doe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Your Delivery Rider", color = Color.Gray, fontSize = 12.sp)
                        }
                        IconButton(
                            onClick = { /* Call Rider */ },
                            modifier = Modifier.background(cartifyGreen, CircleShape)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // Order Status Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Order ID: #$orderId", color = Color.Gray, fontSize = 12.sp)
                            Text("Estimated Delivery", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Text("10:45 AM", color = cartifyGreen, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Vertical Step Tracker
                    TrackingStep(title = "Order Placed", time = "10:00 AM", isCompleted = true, isCurrent = false, cartifyGreen = cartifyGreen)
                    TrackingConnector(isCompleted = true, cartifyGreen = cartifyGreen)
                    TrackingStep(title = "Preparing your order", time = "10:15 AM", isCompleted = true, isCurrent = false, cartifyGreen = cartifyGreen)
                    TrackingConnector(isCompleted = true, cartifyGreen = cartifyGreen)
                    TrackingStep(title = "Rider at Store", time = "10:30 AM", isCompleted = true, isCurrent = true, cartifyGreen = cartifyGreen)
                    TrackingConnector(isCompleted = false, cartifyGreen = cartifyGreen)
                    TrackingStep(title = "Out for Delivery", time = "Pending", isCompleted = false, isCurrent = false, cartifyGreen = cartifyGreen)
                    TrackingConnector(isCompleted = false, cartifyGreen = cartifyGreen)
                    TrackingStep(title = "Order Delivered", time = "Pending", isCompleted = false, isCurrent = false, cartifyGreen = cartifyGreen)
                }
            }
        }
    }
}

@Composable
fun TrackingStep(title: String, time: String, isCompleted: Boolean, isCurrent: Boolean, cartifyGreen: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isCompleted) cartifyGreen else Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) Color.Black else if (isCompleted) Color.Black.copy(alpha = 0.7f) else Color.Gray,
                fontSize = 15.sp
            )
            Text(text = time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun TrackingConnector(isCompleted: Boolean, cartifyGreen: Color) {
    Box(
        modifier = Modifier
            .padding(start = 11.dp)
            .width(2.dp)
            .height(30.dp)
            .background(if (isCompleted) cartifyGreen else Color.LightGray.copy(alpha = 0.5f))
    )
}
