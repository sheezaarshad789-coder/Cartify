package com.example.cartify.feature.customer.screens.details

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ShimmerItem
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Interactive Wrapper for Product Details.
 * Manages local UI states to provide a fluid, alive experience.
 */
@Composable
fun ProductDetailScreen(
    productId: String?,
    onBackClick: () -> Unit
) {
    // Local Interactive States
    var isLoading by remember { mutableStateOf(true) }
    var quantity by remember { mutableIntStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) }

    // Mock Product Data
    val product = remember {
        Product(
            id = productId ?: "1",
            name = "Organic Hass Avocado",
            price = 450.0,
            unit = "kg",
            imageUrl = "",
            description = "Creamy, rich, and full of healthy fats. These avocados are hand-picked at peak ripeness to ensure the best quality for your kitchen. Perfect for toast, salads, or smoothies.",
            storeId = "101",
            storeName = "Green Grocers",
            categoryId = "fruits"
        )
    }

    // Simulate initial fetching
    LaunchedEffect(productId) {
        delay(1200)
        isLoading = false
    }

    ProductDetailContent(
        product = product,
        isLoading = isLoading,
        isFavorite = isFavorite,
        quantity = quantity,
        onBackClick = onBackClick,
        onFavoriteToggle = { isFavorite = !isFavorite },
        onQuantityChange = { quantity = it },
        onAddToCart = { /* Local feedback like a Toast or Snackbar could go here */ }
    )
}

/**
 * Product Detail Content - Refactored for Premium Japandi Aesthetics.
 */
@Composable
fun ProductDetailContent(
    product: Product?,
    isLoading: Boolean = false,
    isFavorite: Boolean = false,
    quantity: Int = 1,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(JapandiCanvas)) {
        if (isLoading) {
            ProductDetailShimmer(onBackClick)
        } else if (product == null) {
            EmptyProductState(onBackClick)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ProductImageHeader(
                    isFavorite = isFavorite,
                    onBackClick = onBackClick,
                    onFavoriteClick = onFavoriteToggle
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .animateContentSize()
                ) {
                    ProductInfoSection(product = product)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "About this item",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = JapandiCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 26.sp,
                            color = JapandiEarthyGray
                        )
                    )

                    Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
                }
            }

            // Sticky Bottom Action Bar with Animations
            ProductBottomBar(
                price = product.price,
                quantity = quantity,
                onQuantityChange = onQuantityChange,
                onAddToCart = onAddToCart,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ProductImageHeader(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(JapandiDivider.copy(alpha = 0.3f))
    ) {
        // Image Placeholder
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("🥑", fontSize = 160.sp, modifier = Modifier.scale(scale))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderActionIcon(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)
            HeaderActionIcon(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                tint = if (isFavorite) JapandiError else JapandiCharcoal,
                onClick = onFavoriteClick
            )
        }
    }
}

@Composable
private fun HeaderActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = JapandiCharcoal,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape, spotColor = JapandiSage.copy(alpha = 0.2f)),
        shape = CircleShape,
        color = Color.White,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ProductInfoSection(product: Product) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = JapandiCharcoal,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "Grown by ${product.storeName}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = JapandiSage,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Surface(
                color = Color(0xFFFFB300).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Text(
                        text = " 4.9",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = JapandiCharcoal
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = JapandiSage.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Fresh Stock",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = JapandiSage
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "In high demand",
                style = MaterialTheme.typography.bodySmall,
                color = JapandiEarthyGray
            )
        }
    }
}

@Composable
private fun ProductBottomBar(
    price: Double,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = price * quantity

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quantity Selector
            Surface(
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = JapandiCanvas,
                border = androidx.compose.foundation.BorderStroke(1.dp, JapandiDivider)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
                        Text("-", fontSize = 24.sp, fontWeight = FontWeight.Light, color = JapandiSage)
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = JapandiCharcoal
                        )
                    )
                    IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Light, color = JapandiSage)
                    }
                }
            }

            // Buy Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ADD TO CART",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Text(
                        text = "PKR ${String.format(Locale.getDefault(), "%.0f", total)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailShimmer(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        ) {
            ShimmerItem(height = 400.dp)
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(20.dp)
                    .size(48.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = JapandiCharcoal)
            }
        }
        
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerItem(height = 32.dp, modifier = Modifier.width(200.dp))
            ShimmerItem(height = 20.dp, modifier = Modifier.width(150.dp))
            Spacer(modifier = Modifier.height(16.dp))
            ShimmerItem(height = 100.dp)
            ShimmerItem(height = 100.dp)
        }
    }
}

@Composable
private fun EmptyProductState(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Product not found", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
            Text("Go Back")
        }
    }
}

@Preview(showBackground = true, name = "Product Details - Interactive")
@Composable
fun PreviewProductDetailScreen() {
    CartifyTheme {
        ProductDetailScreen(productId = "1", onBackClick = {})
    }
}
