package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.SearchState
import com.example.cartify.feature.customer.SearchViewModel
import com.example.cartify.core.common.ui.components.ProductCard
import com.example.cartify.feature.customer.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    navController: NavController,
    query: String?,
    viewModel: SearchViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val searchState by viewModel.searchState
    val isItemsQuery = query == "Items" || query == "Popular"

    LaunchedEffect(query) {
        query?.let { viewModel.search(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isItemsQuery) "Popular Items" else "Results for \"$query\"",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (val state = searchState) {
                is SearchState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
                }
                is SearchState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = JapandiError)
                        Button(onClick = { query?.let { viewModel.search(it) } }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                            Text("Retry")
                        }
                    }
                }
                is SearchState.Success -> {
                    val filteredProducts = state.products
                    if (filteredProducts.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(80.dp), tint = JapandiEarthyGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No items found", fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                            Text("Try a different keyword", color = JapandiEarthyGray, fontSize = 14.sp)
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "${filteredProducts.size} items found",
                                modifier = Modifier.padding(16.dp),
                                color = JapandiEarthyGray,
                                fontSize = 14.sp
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredProducts) { product ->
                                    ProductCard(
                                        product = product,
                                        isFavorite = product.isFavorite,
                                        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                                        onFavoriteClick = { viewModel.toggleFavorite(product) },
                                        onAddToCart = { cartViewModel.addToCart(product) }
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
