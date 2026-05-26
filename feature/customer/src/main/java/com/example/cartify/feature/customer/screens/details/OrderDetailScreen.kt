package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.model.Order
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*
import java.util.Locale

/**
 * Order Detail Content - Purely UI focused and decoupled.
 * Displays order status tracking, items, and cost summary in Japandi style.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    order: Order?,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Details",
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
            } else if (order == null) {
                Text("Order not found", modifier = Modifier.align(Alignment.Center), color = JapandiEarthyGray)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // 1. Order ID & Date
                    Text(
                        text = "ORDER #${order.id.takeLast(8).uppercase()}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = JapandiEarthyGray
                    )
                    Text(
                        text = "Placed on ${order.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = JapandiEarthyGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 2. Order Status Stepper
                    Text(
                        text = "Tracking Status",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = JapandiCharcoal
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OrderStepper(currentStatus = order.status)

                    Spacer(modifier = Modifier.height(40.dp))

                    // 3. Delivery Address
                    Text(
                        text = "Delivery Address",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = JapandiCharcoal
                    )
                    AddressSummaryCard(address = order.customerAddress)

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4. Items List
                    Text(
                        text = "Items Summary",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = JapandiCharcoal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    order.items.forEach { item ->
                        OrderItemRow(item = item)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 5. Cost Summary
                    Text(
                        text = "Payment Summary",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = JapandiCharcoal
                    )
                    CostSummaryCard(totalAmount = order.totalAmount)

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onHelpClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JapandiSage.copy(alpha = 0.1f)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "NEED HELP WITH THIS ORDER?",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = JapandiSage,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderStepper(currentStatus: String) {
    val steps = listOf("Pending", "Processing", "Shipped", "Delivered")
    val currentIndex = when (currentStatus.lowercase()) {
        "pending" -> 0
        "processing" -> 1
        "shipped" -> 2
        "delivered" -> 3
        else -> 0
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (index <= currentIndex) JapandiSage else JapandiDivider),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index <= currentIndex) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(if (index < currentIndex) JapandiSage else JapandiDivider)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (index <= currentIndex) JapandiCharcoal else JapandiEarthyGray
                    )
                    if (index <= currentIndex) {
                        Text(
                            text = if (index == currentIndex) "In progress" else "Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (index == currentIndex) JapandiSage else JapandiEarthyGray.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressSummaryCard(address: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(JapandiSage.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = JapandiSage, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Delivery Location",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = JapandiDivider.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📦", fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = "Qty: ${item.quantity}",
                    style = MaterialTheme.typography.labelSmall,
                    color = JapandiEarthyGray
                )
            }
        }
        Text(
            text = "PKR ${String.format(Locale.getDefault(), "%.0f", item.product.price * item.quantity)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = JapandiCharcoal
        )
    }
}

@Composable
private fun CostSummaryCard(totalAmount: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            val deliveryFee = 50.0
            val subtotal = totalAmount - deliveryFee

            SummaryRow(label = "Subtotal", value = subtotal)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(label = "Delivery Fee", value = deliveryFee)
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = JapandiDivider.copy(alpha = 0.5f)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = "PKR ${String.format(Locale.getDefault(), "%.0f", totalAmount)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = JapandiSage
                    )
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = JapandiEarthyGray)
        Text(
            text = "PKR ${String.format(Locale.getDefault(), "%.0f", value)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = JapandiCharcoal
        )
    }
}

@Preview(showBackground = true, name = "Order Detail - Processing")
@Composable
fun PreviewOrderDetail() {
    val mockItems = listOf(
        CartItem(Product("1", "Organic Avocado", 450.0, "kg", "", "", "", "", ""), 2),
        CartItem(Product("2", "Artisan Bread", 300.0, "loaf", "", "", "", "", ""), 1)
    )
    val mockOrder = Order(
        id = "ORD-99283-XJ",
        storeName = "Green Grocers",
        status = "Processing",
        date = "Oct 25, 2023",
        totalAmount = 1250.0,
        items = mockItems,
        customerAddress = "123 Serenity Heights, Karachi"
    )
    OrderDetailContent(
        order = mockOrder,
        onBackClick = {},
        onHelpClick = {}
    )
}
