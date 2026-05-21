package com.example.cartify.feature.customer.screens.details

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard
import com.example.cartify.feature.customer.CartViewModel
import com.example.cartify.feature.customer.StoreDetailState
import com.example.cartify.feature.customer.StoreDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    navController: NavController,
    storeId: String?,
    viewModel: StoreDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val detailState by viewModel.storeDetailState
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(storeId) {
        storeId?.let { viewModel.loadStoreDetail(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(JapandiCanvas)) {
        when (val state = detailState) {
            is StoreDetailState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
            }
            is StoreDetailState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = JapandiError)
                    Button(onClick = { storeId?.let { viewModel.loadStoreDetail(it) } }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
            is StoreDetailState.Success -> {
                val store = state.store
                val storeProducts = state.products

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(Screen.ChatDetail.createRoute(store.id))
                            },
                            containerColor = JapandiSage,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat")
                        }
                    },
                    containerColor = JapandiCanvas
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Banner & Back Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(JapandiDivider.copy(alpha = 0.5f))
                        ) {
                            // Store Banner Placeholder
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("🏪", fontSize = 64.sp)
                            }

                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = JapandiCharcoal)
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = store.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                                Surface(
                                    color = Color(0xFFFFB300).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                        Text(text = store.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JapandiCharcoal)
                                    }
                                }
                            }
                            Text(text = "Delivery in ${store.deliveryTime} • ${store.distance}", color = JapandiEarthyGray, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(24.dp))

                            TabRow(
                                selectedTabIndex = selectedTab,
                                containerColor = Color.Transparent,
                                contentColor = JapandiSage,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                        color = JapandiSage
                                    )
                                },
                                divider = {}
                            ) {
                                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                                    Text("Products", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                                }
                                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                                    Text("Reviews", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                                }
                            }

                            if (selectedTab == 0) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(storeProducts) { product ->
                                        ProductCard(
                                            product = product,
                                            isFavorite = product.isFavorite,
                                            onClick = {
                                                navController.navigate(Screen.ProductDetail.createRoute(product.id))
                                            },
                                            onFavoriteClick = { /* TODO */ },
                                            onAddToCart = { cartViewModel.addToCart(product) }
                                        )
                                    }
                                }
                            } else {
                                StoreReviewsSection()
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun StoreReviewsSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("4.5", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = JapandiSage)
                Row {
                    repeat(4) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp)) }
                    Icon(Icons.AutoMirrored.Filled.StarHalf, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                }
                Text("128 Reviews", fontSize = 12.sp, color = JapandiEarthyGray)
            }
            Spacer(modifier = Modifier.width(32.dp))
            Column(modifier = Modifier.weight(1f)) {
                RatingBar(5, 0.8f)
                RatingBar(4, 0.15f)
                RatingBar(3, 0.03f)
                RatingBar(2, 0.01f)
                RatingBar(1, 0.01f)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("User Reviews", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JapandiCharcoal)

        ReviewItem("Alice Smith", "Great service! Fresh vegetables always.", "5.0")
        ReviewItem("Bob Jones", "Delivery was a bit late, but quality is top notch.", "4.0")
    }
}

@Composable
fun RatingBar(stars: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stars.toString(), fontSize = 12.sp, modifier = Modifier.width(12.dp), color = JapandiCharcoal)
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .height(6.dp)
                .weight(1f)
                .clip(CircleShape),
            color = JapandiSage,
            trackColor = JapandiDivider,
        )
    }
}

@Composable
fun ReviewItem(name: String, comment: String, rating: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(JapandiDivider), contentAlignment = Alignment.Center) {
                Text(name.take(1), fontWeight = FontWeight.Bold, color = JapandiCharcoal)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JapandiCharcoal)
                Text("2 days ago", color = JapandiEarthyGray, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                Text(rating, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JapandiCharcoal)
            }
        }
        Text(comment, fontSize = 13.sp, color = JapandiCharcoal, modifier = Modifier.padding(top = 8.dp, start = 44.dp))
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = JapandiDivider)
    }
}
