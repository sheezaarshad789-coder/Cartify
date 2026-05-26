package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
 * Favorites Content - Decoupled UI layer for the wishlist.
 * Displays user's favorited items in a clean, aesthetic grid.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    favoriteProducts: List<Product>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onGoShoppingClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Wishlist",
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
            } else if (favoriteProducts.isEmpty()) {
                EmptyFavoritesView(onGoShoppingClick = onGoShoppingClick)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteProducts) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = true,
                            onClick = { onProductClick(product) },
                            onFavoriteClick = { onFavoriteToggle(product) },
                            onAddToCart = { onAddToCart(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesView(onGoShoppingClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(32.dp),
            color = JapandiSage.copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = JapandiSage
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Your wishlist is empty",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = JapandiCharcoal
        )
        Text(
            text = "Save items you like for later access.",
            style = MaterialTheme.typography.bodyMedium,
            color = JapandiEarthyGray,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Button(
            onClick = onGoShoppingClick,
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "EXPLORE PRODUCTS",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Wishlist - Items")
@Composable
fun PreviewFavoritesContent() {
    val mockProducts = listOf(
        Product("1", "Organic Avocado", 2.99, "pc", "", "Fresh avocado", "1", "Green Mart", "1"),
        Product("2", "Red Apples", 4.50, "kg", "", "Crispy apples", "1", "Green Mart", "1"),
        Product("3", "Whole Milk", 3.20, "L", "", "Pure milk", "2", "Dairy Farm", "3")
    )
    FavoritesContent(
        favoriteProducts = mockProducts,
        onBackClick = {},
        onProductClick = {},
        onFavoriteToggle = {},
        onAddToCart = {},
        onGoShoppingClick = {}
    )
}

@Preview(showBackground = true, name = "Wishlist - Empty")
@Composable
fun PreviewEmptyFavorites() {
    FavoritesContent(
        favoriteProducts = emptyList(),
        onBackClick = {},
        onProductClick = {},
        onFavoriteToggle = {},
        onAddToCart = {},
        onGoShoppingClick = {}
    )
}
