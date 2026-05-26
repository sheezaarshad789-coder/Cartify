package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.theme.*

/**
 * Order Tracking Content - Decoupled UI layer for real-time delivery tracking.
 * Features a clean map placeholder and vertical status stepper.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingContent(
    orderId: String,
    estimatedTime: String,
    riderName: String,
    riderDistance: String,
    currentStepIndex: Int,
    onBackClick: () -> Unit,
    onCallRiderClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Track Order",
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
                actions = {
                    IconButton(onClick = onMoreClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Map Placeholder Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(JapandiDivider.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                MapPlaceholder(riderDistance = riderDistance)

                // Rider Info Overlay Card
                RiderInfoCard(
                    riderName = riderName,
                    onCallClick = onCallRiderClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                )
            }

            // Order Status Bottom Sheet Style Content
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.3f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize()
                ) {
                    TrackingHeader(orderId = orderId, estimatedTime = estimatedTime)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Vertical Stepper
                    TrackingStepper(currentStepIndex = currentStepIndex)
                }
            }
        }
    }
}

@Composable
private fun MapPlaceholder(riderDistance: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = JapandiSage.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = JapandiSage, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Live Map View",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = JapandiCharcoal
        )
        Text(
            text = "Rider is $riderDistance away",
            style = MaterialTheme.typography.bodySmall,
            color = JapandiEarthyGray
        )
    }
}

@Composable
private fun RiderInfoCard(
    riderName: String,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(JapandiSage.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = riderName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = "Delivery Hero",
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray
                )
            }
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onCallClick() },
                shape = CircleShape,
                color = JapandiSage
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun TrackingHeader(orderId: String, estimatedTime: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Order ID: #$orderId",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = JapandiEarthyGray
            )
            Text(
                text = "Estimated Delivery",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = JapandiCharcoal
            )
        }
        Text(
            text = estimatedTime,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = JapandiSage
            )
        )
    }
}

@Composable
private fun TrackingStepper(currentStepIndex: Int) {
    val steps = listOf(
        "Order Placed",
        "Preparing your order",
        "Rider at Store",
        "Out for Delivery",
        "Order Delivered"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < currentStepIndex
            val isCurrent = index == currentStepIndex
            
            TrackingStepItem(
                title = step,
                time = if (isCompleted || isCurrent) "Done" else "Pending",
                isCompleted = isCompleted,
                isCurrent = isCurrent,
                isLast = index == steps.size - 1
            )
        }
    }
}

@Composable
private fun TrackingStepItem(
    title: String,
    time: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted || isCurrent) JapandiSage else JapandiDivider),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else if (isCurrent) {
                    Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (isCompleted) JapandiSage else JapandiDivider)
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.padding(bottom = if (!isLast) 24.dp else 0.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isCurrent || isCompleted) JapandiCharcoal else JapandiEarthyGray
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = JapandiEarthyGray.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Order Tracking - Ongoing")
@Composable
fun PreviewOrderTracking() {
    OrderTrackingContent(
        orderId = "CRTF-98721",
        estimatedTime = "10:45 AM",
        riderName = "Ahmed Khan",
        riderDistance = "1.2 km",
        currentStepIndex = 2,
        onBackClick = {},
        onCallRiderClick = {},
        onMoreClick = {}
    )
}
