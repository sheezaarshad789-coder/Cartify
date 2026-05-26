package com.example.cartify.core.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cartify.core.common.model.Store
import com.example.cartify.core.common.theme.*

/**
 * Store Card Component - Decoupled UI component.
 * Premium, minimalist design following Japandi principles.
 */
@Composable
fun StoreCard(
    store: Store, 
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column {
            // Banner / Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(JapandiDivider.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (store.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = store.imageUrl,
                        contentDescription = store.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "🏪",
                        fontSize = 56.sp
                    )
                }
                
                // Rating Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = store.rating.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = JapandiCharcoal
                        )
                    }
                }
            }

            // Store Info Area
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${store.distance} • ${store.deliveryTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = JapandiEarthyGray
                    )
                    
                    Surface(
                        color = JapandiSage.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "OPEN",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = JapandiSage
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Store Card - Sample")
@Composable
fun PreviewStoreCard() {
    val mockStore = Store(
        id = "1",
        name = "Premium Organic Market",
        rating = 4.8,
        distance = "1.2 km",
        imageUrl = "",
        deliveryTime = "15-25 mins"
    )
    Box(modifier = Modifier.padding(20.dp)) {
        StoreCard(
            store = mockStore,
            onClick = {}
        )
    }
}
