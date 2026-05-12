package com.example.cartify.ui.screens.main.tabs

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.data.model.Message
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.MessagesState
import com.example.cartify.ui.MessagesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    navController: NavController, 
    rootNavController: NavController,
    viewModel: MessagesViewModel = viewModel()
) {
    val messagesState by viewModel.messagesState
    val cartifyGreen = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (messagesState) {
                is MessagesState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = cartifyGreen)
                }
                is MessagesState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = (messagesState as MessagesState.Error).message, color = Color.Red)
                        Button(onClick = { viewModel.loadMessages() }) { Text("Retry") }
                    }
                }
                is MessagesState.Success -> {
                    val messages = (messagesState as MessagesState.Success).messages
                    if (messages.isEmpty()) {
                        Text("No messages yet", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(messages) { message ->
                                MessageItemCard(message, cartifyGreen) {
                                    // In a real app, we'd navigate to ChatDetail with the other user's ID
                                    // For now, we use senderName as a placeholder for the chat ID
                                    rootNavController.navigate(Screen.ChatDetail.createRoute(message.senderName))
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MessageItemCard(message: Message, cartifyGreen: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9F9F9)
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
                Text(
                    text = message.senderName.take(1).uppercase(),
                    color = cartifyGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = message.senderName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = message.time, color = Color.Gray, fontSize = 11.sp)
                }
                Text(
                    text = message.lastMessage,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
