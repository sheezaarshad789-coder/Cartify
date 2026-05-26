package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.model.Store
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard

/**
 * Store Detail Content - Decoupled UI layer for store information and product listing.
 * Follows Japandi design principles with minimalist aesthetics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailContent(
    store: Store?,
    products: List<Product>,
    isLoading: Boolean = false,
    selectedTab: Int = 0,
    onTabSelect: (Int) -> Unit,
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(JapandiCanvas)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
        } else if (store == null) {
            Text("Store not found", modifier = Modifier.align(Alignment.Center), color = JapandiEarthyGray)
        } else {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onChatClick,
                        containerColor = JapandiSage,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat with Store")
                    }
                },
                containerColor = JapandiCanvas
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Store Banner & Header
                    StoreHeader(
                        storeName = store.name,
                        rating = store.rating,
                        deliveryTime = store.deliveryTime,
                        distance = store.distance,
                        onBackClick = onBackClick
                    )

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Custom Tabs
                        StoreDetailTabs(
                            selectedTabIndex = selectedTab,
                            onTabSelect = onTabSelect
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedTab == 0) {
                            ProductGrid(
                                products = products,
                                onProductClick = onProductClick,
                                onFavoriteClick = onFavoriteClick,
                                onAddToCart = onAddToCart
                            )
                        } else {
                            StoreReviewsSection()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreHeader(
    storeName: String,
    rating: Double,
    deliveryTime: String,
    distance: String,
    onBackClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(JapandiDivider.copy(alpha = 0.2f))
        ) {
            // Placeholder for Banner Image
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("🏪", fontSize = 80.sp)
            }

            Surface(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(44.dp)
                    .shadow(2.dp, CircleShape),
                shape = CircleShape,
                color = Color.White,
                onClick = onBackClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = JapandiCharcoal
                    )
                )
                
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Text(
                            text = rating.toString(),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = JapandiCharcoal
                        )
                    }
                }
            }
            
            Text(
                text = "Delivery in $deliveryTime • $distance away",
                style = MaterialTheme.typography.bodyMedium,
                color = JapandiEarthyGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun StoreDetailTabs(
    selectedTabIndex: Int,
    onTabSelect: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = JapandiSage,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = JapandiSage,
                height = 3.dp
            )
        },
        divider = {
            HorizontalDivider(color = JapandiDivider.copy(alpha = 0.5f))
        }
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelect(0) },
            selectedContentColor = JapandiSage,
            unselectedContentColor = JapandiEarthyGray
        ) {
            Text(
                text = "Products",
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelect(1) },
            selectedContentColor = JapandiSage,
            unselectedContentColor = JapandiEarthyGray
        ) {
            Text(
                text = "Reviews",
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                isFavorite = product.isFavorite,
                onClick = { onProductClick(product) },
                onFavoriteClick = { onFavoriteClick(product) },
                onAddToCart = { onAddToCart(product) }
            )
        }
    }
}

@Composable
fun StoreReviewsSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "4.5",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = JapandiSage
                    )
                )
                Row {
                    repeat(4) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp)) }
                    Icon(Icons.AutoMirrored.Filled.StarHalf, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "128 Reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(40.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RatingBar(5, 0.82f)
                RatingBar(4, 0.12f)
                RatingBar(3, 0.04f)
                RatingBar(2, 0.01f)
                RatingBar(1, 0.01f)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Latest Reviews",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = JapandiCharcoal
        )

        Spacer(modifier = Modifier.height(8.dp))
        ReviewItem("Alice M.", "Absolutely fresh produce and extremely quick delivery. Highly recommended!", "5.0")
        ReviewItem("Daniel R.", "Great quality, but the packaging could be better. Overall good experience.", "4.0")
        ReviewItem("Sophie W.", "Best organic store in the neighborhood.", "5.0")
    }
}

@Composable
fun RatingBar(stars: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stars.toString(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.width(12.dp),
            color = JapandiCharcoal
        )
        Spacer(modifier = Modifier.width(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .height(6.dp)
                .weight(1f)
                .clip(CircleShape),
            color = JapandiSage,
            trackColor = JapandiDivider.copy(alpha = 0.5f),
        )
    }
}

@Composable
fun ReviewItem(name: String, comment: String, rating: String) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(JapandiSage.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiSage
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text("2 days ago", style = MaterialTheme.typography.bodySmall, color = JapandiEarthyGray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                Text(
                    text = rating,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
        Text(
            text = comment,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = JapandiCharcoal,
            modifier = Modifier.padding(top = 10.dp, start = 52.dp)
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            color = JapandiDivider.copy(alpha = 0.3f)
        )
    }
}

@Preview(showBackground = true, name = "Store Detail - Products")
@Composable
fun PreviewStoreDetailProducts() {
    val mockStore = Store("1", "Premium Organic Market", 4.9, "1.5 km", "", "", "", "", "15-25 mins")
    val mockProducts = listOf(
        Product("1", "Organic Kale", 120.0, "bunch", "", "", "1", "Premium Organic Market", "1"),
        Product("2", "Strawberries", 350.0, "box", "", "", "1", "Premium Organic Market", "1"),
        Product("3", "Almond Milk", 450.0, "L", "", "", "1", "Premium Organic Market", "2")
    )
    StoreDetailContent(
        store = mockStore,
        products = mockProducts,
        selectedTab = 0,
        onTabSelect = {},
        onBackClick = {},
        onChatClick = {},
        onProductClick = {},
        onFavoriteClick = {},
        onAddToCart = {}
    )
}
