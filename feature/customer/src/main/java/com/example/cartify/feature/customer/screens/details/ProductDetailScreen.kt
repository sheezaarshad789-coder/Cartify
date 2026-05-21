package com.example.cartify.feature.customer.screens.details

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.CartViewModel
import com.example.cartify.feature.customer.ProductDetailState
import com.example.cartify.feature.customer.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    viewModel: ProductDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val detailState by viewModel.productDetailState

    LaunchedEffect(productId) {
        productId?.let { viewModel.loadProductDetail(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(JapandiCanvas)) {
        when (val state = detailState) {
            is ProductDetailState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
            }
            is ProductDetailState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = JapandiError)
                    Button(onClick = { productId?.let { viewModel.loadProductDetail(it) } }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
            is ProductDetailState.Success -> {
                val product = state.product
                var quantity by remember { mutableIntStateOf(1) }
                var isFavorite by remember { mutableStateOf(product.isFavorite) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .background(JapandiDivider.copy(alpha = 0.3f))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("🌿", fontSize = 120.sp)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                modifier = Modifier.size(44.dp).clickable { navController.popBackStack() },
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                                }
                            }

                            Surface(
                                modifier = Modifier.size(44.dp).clickable { isFavorite = !isFavorite },
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) JapandiError else JapandiCharcoal
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = JapandiCharcoal
                                )
                                Text(
                                    text = "From ${product.storeName}",
                                    color = JapandiSage,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Surface(
                                color = Color(0xFFFFB300).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                    Text(" 4.8", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JapandiCharcoal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "PKR ${product.price.toInt()}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = JapandiSage
                            )
                            Text(
                                text = " / ${product.unit}",
                                fontSize = 16.sp,
                                color = JapandiEarthyGray,
                                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Product Description", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = product.description.ifBlank { "High-quality fresh organic products sourced directly from local farms." },
                            color = JapandiEarthyGray,
                            lineHeight = 24.sp,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.height(56.dp).padding(end = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = JapandiDivider.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                                        Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = JapandiSage)
                                    }
                                    Text(
                                        text = quantity.toString(),
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = JapandiCharcoal
                                    )
                                    IconButton(onClick = { quantity++ }) {
                                        Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = JapandiSage)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    repeat(quantity) { cartViewModel.addToCart(product) }
                                    navController.popBackStack()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Add to Cart", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
            else -> {}
        }
    }
}
