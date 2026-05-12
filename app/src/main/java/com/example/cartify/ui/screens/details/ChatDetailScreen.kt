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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.ui.ChatState
import com.example.cartify.ui.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    navController: NavController, 
    otherUserId: String?, 
    viewModel: ChatViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val softGray = Color(0xFFF5F5F5)
    val state by viewModel.chatState
    
    val decodedOtherId = remember(otherUserId) {
        Uri.decode(otherUserId ?: "")
    }

    LaunchedEffect(decodedOtherId) {
        if (decodedOtherId.isNotEmpty()) {
            viewModel.loadMessages(decodedOtherId)
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    modifier = Modifier.height(84.dp),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(cartifyGreen.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(decodedOtherId.take(1).uppercase(), color = cartifyGreen, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(
                                    decodedOtherId,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Online", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4CAF50)))
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White).navigationBarsPadding()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickReplies = listOf("Is it available?", "When will it be delivered?", "Thanks!", "OK")
                    items(quickReplies) { reply ->
                        Surface(
                            modifier = Modifier.clickable { messageText = reply },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Text(text = reply, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 13.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...", color = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = softGray,
                            unfocusedContainerColor = softGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    if (messageText.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { 
                                viewModel.sendMessage(decodedOtherId, decodedOtherId, messageText)
                                messageText = ""
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = cartifyGreen)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF9F9F9))) {
            when (state) {
                is ChatState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = cartifyGreen)
                is ChatState.Error -> Text((state as ChatState.Error).message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                is ChatState.Success -> {
                    val messages = (state as ChatState.Success).messages
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        reverseLayout = false
                    ) {
                        items(messages) { message ->
                            ChatBubbleModern(
                                text = message.lastMessage,
                                time = message.time,
                                isMe = message.isMe
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ChatBubbleModern(text: String, time: String, isMe: Boolean) {
    val cartifyGreen = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) cartifyGreen else Color.White,
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
                fontSize = 15.sp
            )
        }
        Text(text = time, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}
