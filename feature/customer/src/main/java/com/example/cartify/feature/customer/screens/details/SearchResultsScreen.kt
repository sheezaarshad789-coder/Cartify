package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard

/**
 * Search Results Content - Decoupled UI layer for displaying search results.
 * Clean, minimalist layout following Japandi design principles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsContent(
    query: String,
    products: List<Product>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val isItemsQuery = query == "Items" || query == "Popular"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isItemsQuery) "Popular Items" else "Results for \"$query\"",
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = JapandiSage
                )
            } else if (products.isEmpty()) {
                EmptySearchResultsView()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "${products.size} items matching your search",
                        style = MaterialTheme.typography.bodySmall,
                        color = JapandiEarthyGray,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = product.isFavorite,
                                onClick = { onProductClick(product) },
                                onFavoriteClick = { onFavoriteToggle(product) },
                                onAddToCart = { onAddToCart(product) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResultsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(JapandiDivider.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = JapandiEarthyGray.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No items found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = JapandiCharcoal
        )
        Text(
            text = "Try adjusting your search or filters.",
            style = MaterialTheme.typography.bodySmall,
            color = JapandiEarthyGray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, name = "Search Results - Products")
@Composable
fun PreviewSearchResults() {
    val mockProducts = listOf(
        Product("1", "Organic Avocado", 450.0, "kg", "", "", "1", "Green Mart", "1"),
        Product("2", "Red Apples", 300.0, "kg", "", "", "1", "Green Mart", "1"),
        Product("3", "Whole Milk", 180.0, "L", "", "", "2", "Dairy Farm", "3")
    )
    SearchResultsContent(
        query = "Fresh",
        products = mockProducts,
        onBackClick = {},
        onProductClick = {},
        onFavoriteToggle = {},
        onAddToCart = {}
    )
}
