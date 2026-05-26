package com.example.cartify.feature.customer.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.R
import com.example.cartify.core.common.model.Category
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.model.Store
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard
import com.example.cartify.core.common.ui.components.StoreCard
import com.example.cartify.core.common.ui.components.ShimmerItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

/**
 * Interactive Home Screen Component.
 * Manages local states to showcase a fully functional frontend without a backend.
 */
@Composable
fun HomeScreen(
    onNotificationsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onStoreClick: (Store) -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onFilterClick: () -> Unit
) {
    // Local Interactive States
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    
    // Simulate Loading
    LaunchedEffect(Unit) {
        delay(1500)
        isLoading = false
    }

    // Mock Data Source
    val categories = remember { MockData.categories }
    val stores = remember { MockData.stores }
    var popularProducts by remember { mutableStateOf(MockData.products) }

    HomeContent(
        isLoading = isLoading,
        categories = categories,
        stores = stores,
        products = popularProducts,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter,
        onSearchQueryChange = { searchQuery = it },
        onFilterSelect = { selectedFilter = it },
        onFavoriteToggle = { product ->
            popularProducts = popularProducts.map {
                if (it.id == product.id) it.copy(isFavorite = !it.isFavorite) else it
            }
        },
        onCategoryClick = onCategoryClick,
        onStoreClick = onStoreClick,
        onProductClick = onProductClick,
        onNotificationsClick = onNotificationsClick,
        onFavoritesClick = onFavoritesClick,
        onFilterClick = onFilterClick,
        onAddToCart = { /* Local Cart Feedback */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    isLoading: Boolean,
    categories: List<Category>,
    stores: List<Store>,
    products: List<Product>,
    searchQuery: String,
    selectedFilter: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelect: (String) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onStoreClick: (Store) -> Unit,
    onProductClick: (Product) -> Unit,
    onNotificationsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Animated Header
            HomeHeader(
                onNotificationsClick = onNotificationsClick,
                onFavoritesClick = onFavoritesClick
            )

            // Dynamic Search & Filter
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onFilterClick = onFilterClick
            )

            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelect = onFilterSelect
            )

            if (isLoading) {
                HomeShimmerEffect()
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedVisibility(
                    visible = true, // Force visible once loading is done
                    enter = fadeIn() + expandVertically()
                ) {
                    Column {
                        AutoSlidingBanner()

                        SectionHeader(title = "Categories", onSeeAll = {})
                        CategoryRow(categories = categories, onCategoryClick = onCategoryClick)

                        SectionHeader(title = "Featured Stores", onSeeAll = {})
                        StoreRow(stores = stores, onStoreClick = onStoreClick)

                        SectionHeader(title = "Popular Items", onSeeAll = {})
                        ProductGrid(
                            products = products.filter { it.name.contains(searchQuery, ignoreCase = true) },
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteToggle,
                            onAddToCart = onAddToCart
                        )
                        
                        if (products.none { it.name.contains(searchQuery, ignoreCase = true) } && searchQuery.isNotEmpty()) {
                            EmptyHomeState(query = searchQuery)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    onNotificationsClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome to Cartify",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = JapandiCharcoal,
                    letterSpacing = (-0.8).sp
                )
            )
            Text(
                text = "Discover fresh essentials today",
                style = MaterialTheme.typography.bodyMedium,
                color = JapandiEarthyGray
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HeaderActionIcon(icon = Icons.Default.FavoriteBorder, onClick = onFavoritesClick)
            HeaderActionIcon(icon = Icons.Default.Notifications, onClick = onNotificationsClick)
        }
    }
}

@Composable
private fun HeaderActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, CircleShape, spotColor = JapandiSage.copy(alpha = 0.2f)),
        shape = CircleShape,
        color = Color.White,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = JapandiSage, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search products or stores...", color = JapandiEarthyGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JapandiSage) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = JapandiSage
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
        
        Surface(
            modifier = Modifier
                .size(56.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = JapandiSage,
            onClick = onFilterClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
) {
    val filters = listOf("All", "Fastest", "Rating 4.5+", "Offers", "Organic")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            val backgroundColor by animateColorAsState(if (isSelected) JapandiSage else Color.White, label = "bg")
            val contentColor by animateColorAsState(if (isSelected) Color.White else JapandiCharcoal, label = "content")
            
            Surface(
                modifier = Modifier
                    .clickable { onFilterSelect(filter) }
                    .animateContentSize(),
                shape = RoundedCornerShape(14.dp),
                color = backgroundColor,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, JapandiDivider) else null
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = JapandiCharcoal
            )
        )
        Text(
            text = "See All",
            style = MaterialTheme.typography.labelLarge.copy(
                color = JapandiSage,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
private fun CategoryRow(categories: List<Category>, onCategoryClick: (Category) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "scale")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(76.dp)
                    .scale(scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onCategoryClick(category) }
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(2.dp, RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFFBFBFB))),
                            shape = RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon ?: Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = JapandiSage,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StoreRow(stores: List<Store>, onStoreClick: (Store) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(stores) { store ->
            StoreCard(store = store) { onStoreClick(store) }
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        products.chunked(2).forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                rowProducts.forEach { product ->
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = product,
                            isFavorite = product.isFavorite,
                            onClick = { onProductClick(product) },
                            onFavoriteClick = { onFavoriteClick(product) },
                            onAddToCart = { onAddToCart(product) }
                        )
                    }
                }
                if (rowProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeShimmerEffect() {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        ShimmerItem(height = 180.dp, shape = RoundedCornerShape(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { ShimmerItem(height = 70.dp, modifier = Modifier.size(70.dp), shape = RoundedCornerShape(20.dp)) }
        }
        ShimmerItem(height = 140.dp, shape = RoundedCornerShape(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            ShimmerItem(height = 200.dp, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp))
            ShimmerItem(height = 200.dp, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp))
        }
    }
}

@Composable
private fun EmptyHomeState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = JapandiSage.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(40.dp), tint = JapandiSage)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No results for \"$query\"",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = JapandiCharcoal
        )
        Text(
            text = "Try searching for something else or browse categories.",
            style = MaterialTheme.typography.bodyMedium,
            color = JapandiEarthyGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AutoSlidingBanner() {
    val banners = listOf(R.drawable.take_away_pana, R.drawable.online_groceries_cuate)
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(5000)
            if (banners.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = JapandiSage.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(24.dp))
    ) { page ->
        Box(modifier = Modifier.background(Color.White)) {
            Image(
                painter = painterResource(id = banners[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 8.5f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Isolated Mock Data for clean decoupled development.
 */
private object MockData {
    val categories = listOf(
        Category("1", "Fruits", null),
        Category("2", "Veggie", null),
        Category("3", "Dairy", null),
        Category("4", "Bakery", null),
        Category("5", "Meat", null)
    )
    val stores = listOf(
        Store("1", "Green Grocers", 4.9, "1.2 km", "", deliveryTime = "15-20 mins"),
        Store("2", "Organic Mart", 4.7, "2.5 km", "", deliveryTime = "25-30 mins"),
        Store("3", "Dairy Fresh", 4.5, "0.8 km", "", deliveryTime = "10-15 mins")
    )
    val products = listOf(
        Product("1", "Organic Avocado", 450.0, "kg", "", "", "1", "Green Grocers", "fruits"),
        Product("2", "Fresh Milk", 180.0, "L", "", "", "3", "Dairy Fresh", "dairy"),
        Product("3", "Sourdough", 320.0, "loaf", "", "", "2", "Organic Mart", "bakery"),
        Product("4", "Red Apples", 240.0, "kg", "", "", "1", "Green Grocers", "fruits")
    )
}

@Preview(showBackground = true, name = "Customer Home - Premium Interactive")
@Composable
fun PreviewHomeScreen() {
    CartifyTheme {
        HomeScreen(
            onNotificationsClick = {},
            onFavoritesClick = {},
            onStoreClick = {},
            onProductClick = {},
            onCategoryClick = {},
            onFilterClick = {}
        )
    }
}
