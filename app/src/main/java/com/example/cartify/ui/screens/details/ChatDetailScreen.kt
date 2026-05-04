package com.example.cartify.ui.screens.details

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cartify.data.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(navController: NavController, vendorName: String?) {
    var messageText by remember { mutableStateOf("") }
    val cartifyGreen = Color(0xFF2E7D32)
    val softGray = Color(0xFFF5F5F5)
    
    // Decode the vendor name since it might be URL encoded from navigation
    val decodedVendorName = remember(vendorName) {
        Uri.decode(vendorName ?: "Store")
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    modifier = Modifier.height(84.dp), // Increased height for professional look
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            // Enlarge Store Image Placeholder
                            Box(
                                modifier = Modifier
                                    .size(52.dp) // Bigger store image
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏪", fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(
                                    decodedVendorName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(cartifyGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Rider at Store",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = cartifyGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Black)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier
                .background(Color.White)
                .navigationBarsPadding()
            ) {
                // Smart Quick Replies
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickReplies = listOf("Leave at door", "Check expiry date", "Is it fresh?", "Call me")
                    items(quickReplies) { reply ->
                        Surface(
                            modifier = Modifier.clickable { messageText = reply },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = reply,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Multimedia Input Bar
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* Attachment */ }) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        }
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Message...", color = Color.Gray) },
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = softGray,
                                unfocusedContainerColor = softGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = { /* Location */ }) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                                    }
                                    IconButton(onClick = { /* Voice */ }) {
                                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Gray)
                                    }
                                }
                            }
                        )
                        if (messageText.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { messageText = "" },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = cartifyGreen)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Today", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            // Interactive Replacement Card
            item {
                ReplacementCard()
            }

            item {
                ChatBubbleModern(
                    text = "The rider is heading to your location.",
                    time = "12:30 PM",
                    isMe = false,
                    isRead = true
                )
            }
            
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                    Text("Rider is typing", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    // Simple dots animation placeholder
                    Text("...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ReplacementCard() {
    val forestGreen = Color(0xFF2E7D32)
    Card(
        modifier = Modifier.fillMaxWidth(0.85f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Product Out of Stock", fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Suggested replacement for 'Red Apples':", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                        Text("Original", fontSize = 10.sp, color = Color.Gray)
                    }
                    Text("Red Apple", style = MaterialTheme.typography.labelSmall)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(top = 32.dp).size(16.dp), tint = Color.Gray)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                        Text("Suggested", fontSize = 10.sp, color = Color.Gray)
                    }
                    Text("Green Apple", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = forestGreen)
                ) {
                    Text("Approve")
                }
            }
        }
    }
}

@Composable
fun ChatBubbleModern(text: String, time: String, isMe: Boolean, isRead: Boolean) {
    val forestGreen = Color(0xFF2E7D32)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) forestGreen else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            shadowElevation = 1.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isMe) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(text = time, fontSize = 11.sp, color = Color.Gray)
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (isRead) Color(0xFF2196F3) else Color.Gray
                )
            }
        }
    }
}
