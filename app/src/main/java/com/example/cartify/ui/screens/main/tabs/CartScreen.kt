package com.example.cartify.ui.screens.main.tabs

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.data.model.CartItem
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.CartViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController, 
    rootNavController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    val cartItems = viewModel.cartItems
    val groupedItems = cartItems.groupBy { it.product.storeName }
    val totalPrice by viewModel.totalPrice
    val cartifyGreen = Color(0xFF2E7D32)
    val softGray = Color(0xFFF0F0F0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 4.dp)
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
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = { rootNavController.navigate(Screen.Checkout.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cartifyGreen)
                ) {
                    Text("Check Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedItems.forEach { (storeName, cartItemList) ->
                    item {
                        Text(
                            text = "Store: $storeName",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = cartifyGreen
                            )
                        )
                    }
                    
                    items(cartItemList) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem, 
                            cartifyGreen = cartifyGreen, 
                            softGray = softGray,
                            onIncrease = { viewModel.addToCart(cartItem.product) },
                            onDecrease = { viewModel.removeFromCart(cartItem) }
                        )
                    }
                    
                    item {
                        val subtotal = cartItemList.sumOf { it.product.price * it.quantity }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Subtotal: PKR ${String.format(Locale.getDefault(), "%.2f", subtotal)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = cartifyGreen
                            )
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = softGray,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Total: PKR ${String.format(Locale.getDefault(), "%.2f", totalPrice)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = cartifyGreen
                                )
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem, 
    cartifyGreen: Color, 
    softGray: Color,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = softGray,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📦", fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItem.product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "PKR ${cartItem.product.price.toInt()}", color = cartifyGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(modifier = Modifier.size(28.dp), shape = CircleShape, color = cartifyGreen) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { onDecrease() }) {
                        Text("-", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = cartItem.quantity.toString(), fontWeight = FontWeight.Bold)
                Surface(modifier = Modifier.size(28.dp), shape = CircleShape, color = cartifyGreen) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { onIncrease() }) {
                        Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
