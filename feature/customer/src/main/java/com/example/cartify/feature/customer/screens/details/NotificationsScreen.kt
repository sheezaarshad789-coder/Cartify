package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Notification
import com.example.cartify.core.common.theme.*

/**
 * Notifications Content - Decoupled UI layer for user notifications.
 * Purely visual focus with Japandi aesthetics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsContent(
    notifications: List<Notification>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = JapandiSage
                )
            } else if (notifications.isEmpty()) {
                EmptyNotificationsView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationItemCard(notification)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(notification: Notification) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(JapandiSage.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = JapandiSage,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = JapandiEarthyGray
                )
                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = JapandiEarthyGray.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(JapandiDivider.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = JapandiEarthyGray.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No notifications yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = JapandiCharcoal
        )
        Text(
            text = "We'll notify you when something important happens.",
            style = MaterialTheme.typography.bodySmall,
            color = JapandiEarthyGray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, name = "Notifications List")
@Composable
fun PreviewNotificationsContent() {
    val mockNotifications = listOf(
        Notification("1", "Order Delivered", "Your order from Green Grocers has been delivered successfully.", "5m ago", "order"),
        Notification("2", "Flash Sale!", "Get 20% off on all organic fruits this weekend.", "2h ago", "promo"),
        Notification("3", "New Message", "The store manager of Organic Mart sent you a message.", "Yesterday", "message")
    )
    NotificationsContent(
        notifications = mockNotifications,
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Empty Notifications")
@Composable
fun PreviewEmptyNotifications() {
    NotificationsContent(
        notifications = emptyList(),
        onBackClick = {}
    )
}
