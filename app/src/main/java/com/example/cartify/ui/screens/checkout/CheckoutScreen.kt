package com.example.cartify.ui.screens.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.data.remote.SupabaseManager
import com.example.cartify.ui.CartViewModel
import com.example.cartify.ui.CheckoutState
import com.example.cartify.ui.CheckoutViewModel
import io.github.jan.supabase.auth.auth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController, 
    checkoutViewModel: CheckoutViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    var selectedPayment by remember { mutableStateOf("Cash") }
    val cartItems = cartViewModel.cartItems
    val total by cartViewModel.totalPrice
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val checkoutState by checkoutViewModel.checkoutState
    
    val userId = remember { SupabaseManager.client.auth.currentUserOrNull()?.id ?: "" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Checkout", 
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = cartifyGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color.White)
                    .padding(16.dp),
                color = Color.White
            ) {
                Button(
                    onClick = { 
                        if (userId.isNotEmpty()) {
                            checkoutViewModel.placeOrder(cartItems, total + 50.0, userId) {
                                cartViewModel.clearCart()
                                Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                                navController.navigate("main") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please login to place order", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cartifyGreen),
                    enabled = checkoutState !is CheckoutState.Loading && cartItems.isNotEmpty()
                ) {
                    if (checkoutState is CheckoutState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Delivery Address
            Text(text = "Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = cartifyGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Home", fontWeight = FontWeight.Bold)
                        Text(text = "123 Street Name, City, Country", color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { }) {
                        Text("Change", color = cartifyGreen)
                    }
                }
            }

            // Payment Methods
            Text(text = "Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
            val paymentOptions = listOf("Cash", "EasyPaisa", "JazzCash")
            paymentOptions.forEach { option ->
                PaymentOptionRow(
                    title = option,
                    isSelected = selectedPayment == option,
                    onSelect = { selectedPayment = option },
                    cartifyGreen = cartifyGreen
                )
            }

            // Order Summary
            Text(text = "Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    cartItems.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "${item.quantity}x ${item.product.name}", color = Color.Gray)
                            Text(text = "PKR ${String.format(Locale.getDefault(), "%.2f", item.product.price * item.quantity)}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Delivery Fee", color = Color.Gray)
                        Text(text = "PKR 50.00")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "PKR ${String.format(Locale.getDefault(), "%.2f", total + 50.0)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = cartifyGreen
                        )
                    }
                }
            }

            if (checkoutState is CheckoutState.Error) {
                Text(
                    text = (checkoutState as CheckoutState.Error).message, 
                    color = Color.Red, 
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PaymentOptionRow(title: String, isSelected: Boolean, onSelect: () -> Unit, cartifyGreen: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) cartifyGreen else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = isSelected, 
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = cartifyGreen)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontWeight = FontWeight.Medium)
    }
}
