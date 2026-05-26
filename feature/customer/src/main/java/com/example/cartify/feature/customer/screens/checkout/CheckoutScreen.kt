package com.example.cartify.feature.customer.screens.checkout

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*
import java.util.Locale

/**
 * Checkout Content - Decoupled UI layer for the checkout process.
 * Focuses on clarity, minimalist layout, and Japandi aesthetic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    cartItems: List<CartItem>,
    totalAmount: Double,
    deliveryFee: Double = 50.0,
    selectedPaymentMethod: String = "Cash",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit,
    onChangeAddressClick: () -> Unit,
    onPaymentMethodSelect: (String) -> Unit,
    onPlaceOrderClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
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
            CheckoutBottomBar(
                isEnabled = cartItems.isNotEmpty() && !isLoading,
                isLoading = isLoading,
                onPlaceOrderClick = onPlaceOrderClick
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Delivery Address Section
            SectionHeader(title = "Delivery Address")
            AddressCard(
                addressTitle = "Home",
                addressDetail = "123 Japandi Lane, Serenity Heights, Karachi",
                onEditClick = onChangeAddressClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Payment Methods Section
            SectionHeader(title = "Payment Method")
            PaymentMethodsList(
                selectedMethod = selectedPaymentMethod,
                onMethodSelect = onPaymentMethodSelect
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Order Summary Section
            SectionHeader(title = "Order Summary")
            OrderSummaryCard(
                items = cartItems,
                deliveryFee = deliveryFee,
                total = totalAmount
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = JapandiError,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = JapandiCharcoal
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun AddressCard(
    addressTitle: String,
    addressDetail: String,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(JapandiSage.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = JapandiSage)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = addressTitle, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                Text(text = addressDetail, style = MaterialTheme.typography.bodySmall, color = JapandiEarthyGray)
            }
            TextButton(onClick = onEditClick) {
                Text("Change", color = JapandiSage, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PaymentMethodsList(
    selectedMethod: String,
    onMethodSelect: (String) -> Unit
) {
    val options = listOf("Cash", "EasyPaisa", "JazzCash")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            val isSelected = selectedMethod == option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMethodSelect(option) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) JapandiSage.copy(alpha = 0.05f) else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) JapandiSage else JapandiDivider
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onMethodSelect(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = JapandiSage)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) JapandiCharcoal else JapandiEarthyGray
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    items: List<CartItem>,
    deliveryFee: Double,
    total: Double
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.product.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = JapandiEarthyGray
                    )
                    Text(
                        text = "PKR ${String.format(Locale.getDefault(), "%.0f", item.product.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = JapandiDivider.copy(alpha = 0.5f)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Delivery Fee", style = MaterialTheme.typography.bodyMedium, color = JapandiEarthyGray)
                Text(text = "PKR ${String.format(Locale.getDefault(), "%.0f", deliveryFee)}", style = MaterialTheme.typography.bodyMedium)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Amount", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "PKR ${String.format(Locale.getDefault(), "%.0f", total + deliveryFee)}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = JapandiSage
                    )
                )
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    isEnabled: Boolean,
    isLoading: Boolean,
    onPlaceOrderClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
            Button(
                onClick = onPlaceOrderClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
                enabled = isEnabled,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "PLACE ORDER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Checkout - Standard")
@Composable
fun PreviewCheckoutContent() {
    val mockItems = listOf(
        CartItem(Product("1", "Fresh Avocado", 450.0, "kg", "", "", "", "", ""), 2),
        CartItem(Product("2", "Artisan Sourdough", 600.0, "loaf", "", "", "", "", ""), 1)
    )
    CheckoutContent(
        cartItems = mockItems,
        totalAmount = 1500.0,
        onBackClick = {},
        onChangeAddressClick = {},
        onPaymentMethodSelect = {},
        onPlaceOrderClick = {}
    )
}
