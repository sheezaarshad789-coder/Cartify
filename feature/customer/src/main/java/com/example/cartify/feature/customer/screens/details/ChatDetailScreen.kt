package com.example.cartify.feature.customer.screens.details

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Message
import com.example.cartify.core.common.theme.*

/**
 * Chat Detail Content - Decoupled UI layer for real-time messaging.
 * Uses State Hoisting for message input and list display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailContent(
    otherUserName: String,
    messages: List<Message>,
    inputText: String,
    isLoading: Boolean = false,
    onInputTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSendMessage: () -> Unit,
    onQuickReplyClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    modifier = Modifier.height(84.dp),
                    title = {
                        ChatHeaderTitle(userName = otherUserName)
                    },
                    navigationIcon = {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            ChatBottomBar(
                inputText = inputText,
                onInputTextChange = onInputTextChange,
                onSendMessage = onSendMessage,
                onQuickReplyClick = onQuickReplyClick
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(
                            text = message.lastMessage,
                            time = message.time,
                            isMe = message.isMe
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatHeaderTitle(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(JapandiSage.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = JapandiSage,
                    fontWeight = FontWeight.Black
                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = JapandiCharcoal
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Online",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun ChatBottomBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickReplyClick: (String) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.background(Color.White).navigationBarsPadding()) {
            QuickRepliesRow(onQuickReplyClick = onQuickReplyClick)

            Row(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp, top = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...", color = JapandiEarthyGray) },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = JapandiCanvas,
                        unfocusedContainerColor = JapandiCanvas,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = JapandiSage
                    ),
                    maxLines = 4
                )
                
                if (inputText.isNotBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onSendMessage,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = JapandiSage),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickRepliesRow(onQuickReplyClick: (String) -> Unit) {
    val quickReplies = listOf("Is it available?", "When can I expect delivery?", "Thanks!", "OK")
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(quickReplies) { reply ->
            Surface(
                modifier = Modifier.clickable { onQuickReplyClick(reply) },
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, JapandiDivider)
            ) {
                Text(
                    text = reply,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = JapandiCharcoal
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(text: String, time: String, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) JapandiSage else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 16.dp
            ),
            shadowElevation = 1.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isMe) Color.White else JapandiCharcoal
                )
            )
        }
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = JapandiEarthyGray.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Preview(showBackground = true, name = "Chat View - Mock Data")
@Composable
fun PreviewChatDetail() {
    val mockMessages = listOf(
        Message("1", "Store", "Hello! How can we help you today?", "10:00 AM", false),
        Message("2", "Me", "Hi, is the organic avocado still in stock?", "10:05 AM", true),
        Message("3", "Store", "Yes, we just received a fresh batch!", "10:06 AM", false)
    )
    ChatDetailContent(
        otherUserName = "Green Grocers",
        messages = mockMessages,
        inputText = "Great, I'll order two kg",
        onInputTextChange = {},
        onBackClick = {},
        onSendMessage = {},
        onQuickReplyClick = {}
    )
}
