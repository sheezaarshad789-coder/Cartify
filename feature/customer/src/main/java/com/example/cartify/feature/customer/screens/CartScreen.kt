package com.example.cartify.feature.customer.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ShimmerItem
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Interactive Cart Screen Wrapper.
 * Manages local states to provide a premium, alive UI experience.
 */
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    // Local Interactive States
    var isLoading by remember { mutableStateOf(true) }
    var cartItemsState by remember { mutableStateOf(MockCartData.items) }
    
    val totalPrice = remember(cartItemsState) {
        cartItemsState.sumOf { it.product.price * it.quantity }
    }

    // Simulate loading
    LaunchedEffect(Unit) {
        delay(1200)
        isLoading = false
    }

    CartifyTheme {
        CartContent(
            cartItems = cartItemsState,
            totalPrice = totalPrice,
            isLoading = isLoading,
            onBackClick = onBackClick,
            onIncreaseQuantity = { item ->
                cartItemsState = cartItemsState.map {
                    if (it.product.id == item.product.id) it.copy(quantity = it.quantity + 1) else it
                }
            },
            onDecreaseQuantity = { item ->
                if (item.quantity > 1) {
                    cartItemsState = cartItemsState.map {
                        if (it.product.id == item.product.id) it.copy(quantity = it.quantity - 1) else it
                    }
                } else {
                    cartItemsState = cartItemsState.filter { it.product.id != item.product.id }
                }
            },
            onCheckoutClick = onCheckoutClick
        )
    }
}

/**
 * Cart Content - Refactored for Elite Japandi UI/UX.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    cartItems: List<CartItem>,
    totalPrice: Double,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onCheckoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Basket",
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
        bottomBar = {
            AnimatedVisibility(
                visible = cartItems.isNotEmpty() && !isLoading,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                CartBottomBar(totalPrice, onCheckoutClick)
            }
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                CartShimmer()
            } else if (cartItems.isEmpty()) {
                EmptyCartView(onBackClick)
            } else {
                val groupedItems = cartItems.groupBy { it.product.storeName }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    groupedItems.forEach { (storeName, items) ->
                        item(key = storeName) {
                            Text(
                                text = storeName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = JapandiCharcoal,
                                modifier = Modifier.padding(bottom = 12.dp).animateItem()
                            )
                        }
                        items(items, key = { it.product.id }) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onIncrease = { onIncreaseQuantity(cartItem) },
                                onDecrease = { onDecreaseQuantity(cartItem) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp), spotColor = JapandiCharcoal.copy(alpha = 0.1f)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(JapandiDivider.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(getCategoryEmoji(cartItem.product.categoryId), fontSize = 36.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal,
                    maxLines = 1
                )
                Text(
                    text = "PKR ${String.format(Locale.getDefault(), "%.0f", cartItem.product.price)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiSage
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QuantityAction(icon = Icons.Default.Remove, onClick = onDecrease)
                
                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = JapandiCharcoal
                )
                
                QuantityAction(icon = Icons.Default.Add, onClick = onIncrease)
            }
        }
    }
}

@Composable
private fun QuantityAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(34.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = JapandiSage.copy(alpha = 0.08f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = JapandiSage, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun CartBottomBar(totalPrice: Double, onCheckout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 24.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Price",
                        style = MaterialTheme.typography.bodySmall,
                        color = JapandiEarthyGray
                    )
                    Text(
                        text = "PKR ${String.format(Locale.getDefault(), "%.0f", totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = JapandiCharcoal
                    )
                }
                
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .height(56.dp)
                        .padding(start = 16.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "CHECKOUT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CartShimmer() {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        repeat(3) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ShimmerItem(height = 20.dp, modifier = Modifier.width(100.dp))
                ShimmerItem(height = 100.dp, shape = RoundedCornerShape(20.dp))
            }
        }
    }
}

@Composable
private fun EmptyCartView(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = JapandiSage.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart, 
                    contentDescription = null, 
                    modifier = Modifier.size(60.dp), 
                    tint = JapandiSage
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Your basket is empty",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = JapandiCharcoal
        )
        Text(
            text = "Discover fresh products from nearby stores and start building your list.",
            style = MaterialTheme.typography.bodyMedium,
            color = JapandiEarthyGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onExploreClick,
            modifier = Modifier.height(56.dp).fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("EXPLORE STORES", fontWeight = FontWeight.Bold)
        }
    }
}

private fun getCategoryEmoji(categoryId: String): String {
    return when {
        categoryId.contains("fruit", true) -> "🍎"
        categoryId.contains("veg", true) -> "🥦"
        categoryId.contains("dairy", true) -> "🥛"
        categoryId.contains("bakery", true) -> "🥖"
        else -> "📦"
    }
}

private object MockCartData {
    val items = listOf(
        CartItem(
            Product("1", "Organic Avocado", 450.0, "kg", "", "", "1", "Green Mart", "fruits"),
            2
        ),
        CartItem(
            Product("2", "Whole Wheat Bread", 320.0, "loaf", "", "", "1", "Green Mart", "bakery"),
            1
        ),
        CartItem(
            Product("3", "Fresh Milk", 180.0, "L", "", "", "2", "Dairy Fresh", "dairy"),
            3
        )
    )
}

@Preview(showBackground = true, name = "Cart - Interactive Premium")
@Composable
fun PreviewCartScreen() {
    CartifyTheme {
        CartContent(
            cartItems = MockCartData.items,
            totalPrice = 1760.0,
            onBackClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onCheckoutClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Cart - Loading")
@Composable
fun PreviewCartLoading() {
    CartifyTheme {
        CartContent(
            cartItems = emptyList(),
            totalPrice = 0.0,
            isLoading = true,
            onBackClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onCheckoutClick = {}
        )
    }
}
