package com.example.cartify.feature.customer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard
import com.example.cartify.core.common.ui.components.StoreCard
import com.example.cartify.feature.customer.CartViewModel
import com.example.cartify.feature.customer.HomeState
import com.example.cartify.feature.customer.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    rootNavController: NavController,
    viewModel: HomeViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val homeState by viewModel.homeState

    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Sort & Filter", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))

                ListItem(
                    headlineContent = { Text("Price: Low to High") },
                    modifier = Modifier.clickable { showFilterSheet = false }
                )
                ListItem(
                    headlineContent = { Text("Distance") },
                    modifier = Modifier.clickable { showFilterSheet = false }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(JapandiCanvas)) {
        when (val state = homeState) {
            is HomeState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = JapandiSage)
                }
            }
            is HomeState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${state.message}", color = JapandiError)
                        Button(onClick = { viewModel.loadHomeData() }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                            Text("Retry")
                        }
                    }
                }
            }
            is HomeState.Success -> {
                val filteredStores = remember(selectedFilter, state.stores) {
                    when (selectedFilter) {
                        "Rating 4.0+" -> state.stores.filter { it.rating >= 4.0 }
                        "Fastest" -> state.stores.sortedBy { it.distance }
                        "Offers" -> state.stores.take(2)
                        else -> state.stores
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Header Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome to Cartify",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = JapandiCharcoal,
                                    letterSpacing = (-0.5).sp,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Fresh groceries at your door",
                                style = MaterialTheme.typography.bodyMedium,
                                color = JapandiEarthyGray
                            )
                        }
                        Row {
                            IconButton(
                                onClick = { rootNavController.navigate(Screen.Favorites.route) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White, CircleShape)
                                    .shadow(2.dp, CircleShape)
                            ) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorites", tint = JapandiSage, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { rootNavController.navigate(Screen.Notifications.route) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White, CircleShape)
                                    .shadow(2.dp, CircleShape)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = JapandiSage, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Search Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search products or stores...", style = MaterialTheme.typography.bodySmall, color = JapandiEarthyGray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JapandiEarthyGray, modifier = Modifier.size(20.dp)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        rootNavController.navigate(Screen.SearchResults.createRoute(searchQuery))
                                    }
                                }
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clickable { showFilterSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            color = JapandiSage
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    // Filters
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf("All", "Fastest", "Rating 4.0+", "Offers", "Organic")
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(text = filter, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = JapandiSage,
                                    selectedLabelColor = Color.White,
                                    labelColor = JapandiCharcoal
                                ),
                                border = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    AutoSlidingBanner()

                    // Categories from API
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal),
                        modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.categories) { category ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(68.dp)
                                    .clickable {
                                        rootNavController.navigate(Screen.ProductListing.createRoute(category.id, category.name))
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .shadow(2.dp, RoundedCornerShape(16.dp))
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color.White, Color(0xFFF1F8E9))
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    category.icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = null,
                                            tint = JapandiSage,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.2.sp,
                                        color = JapandiCharcoal
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Nearby Stores from API
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nearby Stores",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal)
                        )
                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = JapandiSage,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.clickable {
                                rootNavController.navigate(Screen.NearbyStores.route)
                            }
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(filteredStores) { store ->
                            StoreCard(store) {
                                rootNavController.navigate(Screen.StoreDetail.createRoute(store.id))
                            }
                        }
                    }

                    // Popular Items from API
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular Items",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal)
                        )
                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = JapandiSage,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.clickable {
                                rootNavController.navigate(Screen.SearchResults.createRoute("Items"))
                            }
                        )
                    }

                    val popularProducts = state.products
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in popularProducts.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    val product = popularProducts[i]
                                    ProductCard(
                                        product = product,
                                        isFavorite = product.isFavorite,
                                        onClick = { rootNavController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                        onFavoriteClick = { },
                                        onAddToCart = { cartViewModel.addToCart(product) }
                                    )
                                }
                                if (i + 1 < popularProducts.size) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        val product = popularProducts[i + 1]
                                        ProductCard(
                                            product = product,
                                            isFavorite = product.isFavorite,
                                            onClick = { rootNavController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                            onFavoriteClick = { },
                                            onAddToCart = { cartViewModel.addToCart(product) }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            else -> {}
        }
    }
}

@Composable
fun AutoSlidingBanner() {
    // Fallback if drawables don't exist
    val banners = listOf(com.example.cartify.core.common.R.drawable.online_groceries_cuate)
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(3500)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) { page ->
        Image(
            painter = painterResource(id = banners[page]),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 8f),
            contentScale = ContentScale.Crop
        )
    }
}
