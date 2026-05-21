package com.example.cartify.feature.customer.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.CartState
import com.example.cartify.feature.customer.CartViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController, 
    rootNavController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    val cartState by viewModel.cartState
    val totalPrice by viewModel.totalPrice

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        bottomBar = {
            if (cartState is CartState.Success && (cartState as CartState.Success).items.isNotEmpty()) {
                CartBottomBar(totalPrice) {
                    rootNavController.navigate(Screen.Checkout.route)
                }
            }
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = cartState) {
                is CartState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
                }
                is CartState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = JapandiError)
                }
                is CartState.Success -> {
                    if (state.items.isEmpty()) {
                        EmptyCartView()
                    } else {
                        val groupedItems = state.items.groupBy { it.product.storeName }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            groupedItems.forEach { (storeName, items) ->
                                item {
                                    Text(
                                        text = "Store: $storeName",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = JapandiCharcoal
                                    )
                                }
                                items(items) { cartItem ->
                                    CartItemCard(
                                        cartItem = cartItem,
                                        onIncrease = { viewModel.addToCart(cartItem.product) },
                                        onDecrease = { viewModel.removeFromCart(cartItem) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(JapandiCanvas),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItem.product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                Text(
                    text = "PKR ${String.format(Locale.getDefault(), "%.2f", cartItem.product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = JapandiSage
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuantityButton(Icons.Default.Remove) { onDecrease() }
                Text(text = cartItem.quantity.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                QuantityButton(Icons.Default.Add) { onIncrease() }
            }
        }
    }
}

@Composable
fun QuantityButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(28.dp).clickable { onClick() },
        shape = CircleShape,
        color = JapandiSage
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun CartBottomBar(totalPrice: Double, onCheckout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = JapandiSurface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Amount", style = MaterialTheme.typography.bodyMedium, color = JapandiEarthyGray)
                Text(
                    text = "PKR ${String.format(Locale.getDefault(), "%.2f", totalPrice)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = JapandiSage,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)
            ) {
                Text("Check Out", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun EmptyCartView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart, 
            contentDescription = null, 
            modifier = Modifier.size(80.dp), 
            tint = JapandiEarthyGray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your cart is empty", style = MaterialTheme.typography.bodyMedium, color = JapandiEarthyGray)
    }
}
