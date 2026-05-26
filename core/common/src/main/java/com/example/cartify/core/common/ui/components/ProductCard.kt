package com.example.cartify.core.common.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*

/**
 * Product Card Component - Highly Polished & Interactive.
 * Follows Japandi Design System with micro-interactions.
 */
@Composable
fun ProductCard(
    product: Product, 
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val favoriteColor by animateColorAsState(
        targetValue = if (isFavorite) JapandiError else JapandiEarthyGray,
        animationSpec = spring(),
        label = "favoriteColor"
    )

    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "favoriteScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = JapandiCharcoal.copy(alpha = 0.1f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column {
            // Image & Favorite Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(JapandiDivider.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Product Image
                Text(
                    text = getCategoryEmoji(product.categoryId),
                    fontSize = 52.sp,
                    modifier = Modifier.scale(if (isFavorite) 1.05f else 1.0f)
                )
                
                // Favorite Button
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .scale(favoriteScale)
                        .clickable(
                            interactionSource = null,
                            indication = ripple(bounded = false, radius = 24.dp),
                            onClick = onFavoriteClick
                        ),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = favoriteColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Product Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = product.storeName,
                    style = MaterialTheme.typography.labelSmall,
                    color = JapandiEarthyGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PKR ${product.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = JapandiSage
                        )
                        Text(
                            text = "/ ${product.unit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = JapandiEarthyGray
                        )
                    }
                    
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JapandiSage,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "+",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-1).dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getCategoryEmoji(categoryId: String): String {
    return when {
        categoryId.contains("fruit", true) -> "🍎"
        categoryId.contains("veg", true) -> "🥦"
        categoryId.contains("dairy", true) -> "🥛"
        categoryId.contains("bakery", true) -> "🥖"
        categoryId.contains("meat", true) -> "🥩"
        else -> "📦"
    }
}

@Preview(showBackground = true, name = "Product Card - Sample")
@Composable
fun PreviewProductCard() {
    var isFavorite by remember { mutableStateOf(false) }
    CartifyTheme {
        Box(modifier = Modifier.padding(20.dp).width(200.dp)) {
            ProductCard(
                product = Product(
                    id = "1",
                    name = "Organic Hass Avocado",
                    price = 450.0,
                    unit = "kg",
                    imageUrl = "",
                    description = "Fresh avocado",
                    storeId = "101",
                    storeName = "Green Mart",
                    categoryId = "fruits"
                ),
                isFavorite = isFavorite,
                onClick = {},
                onFavoriteClick = { isFavorite = !isFavorite },
                onAddToCart = {}
            )
        }
    }
}
