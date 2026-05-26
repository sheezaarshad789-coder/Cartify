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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ProductCard

/**
 * Product Listing Content - Decoupled UI layer for browsing products by category or search.
 * Focuses on a clean grid layout and polished filtering interface.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListingContent(
    categoryName: String,
    products: List<Product>,
    searchQuery: String,
    selectedSort: String,
    isLoading: Boolean = false,
    onSearchQueryChange: (String) -> Unit,
    onSortSelect: (String) -> Unit,
    onFilterClick: () -> Unit,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val sortOptions = listOf("Popular", "Price: Low to High", "Price: High to Low", "Newest")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
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
                actions = {
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = JapandiSage)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        containerColor = JapandiCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar Section
            SearchBarSection(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = "Search in $categoryName..."
            )

            // Sort Options
            SortOptionsRow(
                options = sortOptions,
                selectedOption = selectedSort,
                onOptionSelect = onSortSelect
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = JapandiSage
                    )
                } else if (products.isEmpty()) {
                    EmptyListingView()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
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
private fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholder, color = JapandiEarthyGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JapandiSage) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = JapandiSage
            ),
            singleLine = true
        )
    }
}

@Composable
private fun SortOptionsRow(
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(options) { option ->
            val isSelected = selectedOption == option
            Surface(
                modifier = Modifier.clickable { onOptionSelect(option) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) JapandiSage else Color.White,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, JapandiDivider) else null
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) Color.White else JapandiCharcoal
                )
            }
        }
    }
}

@Composable
private fun EmptyListingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No products found", style = MaterialTheme.typography.titleMedium, color = JapandiEarthyGray)
    }
}

@Preview(showBackground = true, name = "Product Listing")
@Composable
fun PreviewProductListing() {
    val mockProducts = listOf(
        Product("1", "Organic Avocado", 450.0, "kg", "", "", "1", "Green Mart", "1"),
        Product("2", "Red Apples", 300.0, "kg", "", "", "1", "Green Mart", "1"),
        Product("3", "Whole Milk", 180.0, "L", "", "", "2", "Dairy Farm", "3"),
        Product("4", "Sourdough Bread", 250.0, "loaf", "", "", "3", "Local Bakery", "4")
    )
    ProductListingContent(
        categoryName = "Fresh Produce",
        products = mockProducts,
        searchQuery = "",
        selectedSort = "Popular",
        onSearchQueryChange = {},
        onSortSelect = {},
        onFilterClick = {},
        onBackClick = {},
        onProductClick = {},
        onFavoriteToggle = {},
        onAddToCart = {}
    )
}
