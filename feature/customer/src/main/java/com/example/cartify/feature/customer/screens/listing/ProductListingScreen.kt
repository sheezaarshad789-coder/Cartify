package com.example.cartify.feature.customer.screens.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard
import com.example.cartify.feature.customer.ProductListingState
import com.example.cartify.feature.customer.ProductListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListingScreen(
    navController: NavController,
    categoryId: String?,
    categoryName: String?,
    viewModel: ProductListingViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("Popular") }
    val listingState by viewModel.listingState

    val sortOptions = listOf("Popular", "Price: Low to High", "Price: High to Low", "Newest")

    LaunchedEffect(categoryId) {
        viewModel.loadProductsByCategory(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName ?: "Products",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open Filters */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = JapandiSage)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = JapandiCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search in $categoryName...", color = JapandiEarthyGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JapandiSage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Sort Options
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortOptions) { option ->
                    FilterChip(
                        selected = selectedSort == option,
                        onClick = { selectedSort = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JapandiSage,
                            selectedLabelColor = Color.White,
                            labelColor = JapandiEarthyGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = null
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = listingState) {
                    is ProductListingState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
                    }
                    is ProductListingState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = JapandiError)
                            Button(onClick = { viewModel.loadProductsByCategory(categoryId) }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                                Text("Retry")
                            }
                        }
                    }
                    is ProductListingState.Success -> {
                        val products = state.products
                        val filteredProducts = products.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }.let { list ->
                            when (selectedSort) {
                                "Price: Low to High" -> list.sortedBy { it.price }
                                "Price: High to Low" -> list.sortedByDescending { it.price }
                                else -> list
                            }
                        }

                        if (filteredProducts.isEmpty()) {
                            Text("No products found", modifier = Modifier.align(Alignment.Center), color = JapandiEarthyGray)
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredProducts) { product ->
                                    ProductCard(
                                        product = product,
                                        isFavorite = product.isFavorite,
                                        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                        onFavoriteClick = { /* TODO */ },
                                        onAddToCart = { /* TODO */ }
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
