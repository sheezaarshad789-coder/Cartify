package com.example.cartify.ui.screens.listing

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
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.ProductListingState
import com.example.cartify.ui.ProductListingViewModel
import com.example.cartify.ui.components.ProductCard

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
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

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
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open Filters */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = cartifyGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = backgroundColor
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
                placeholder = { Text("Search in $categoryName...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = cartifyGreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
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
                            selectedContainerColor = cartifyGreen,
                            selectedLabelColor = Color.White,
                            labelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedSort == option) cartifyGreen else Color.LightGray,
                            borderWidth = 1.dp,
                            enabled = true,
                            selected = selectedSort == option
                        )
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (listingState) {
                    is ProductListingState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = cartifyGreen)
                    }
                    is ProductListingState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = (listingState as ProductListingState.Error).message, color = Color.Red)
                            Button(onClick = { viewModel.loadProductsByCategory(categoryId) }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Retry")
                            }
                        }
                    }
                    is ProductListingState.Success -> {
                        val products = (listingState as ProductListingState.Success).products
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
                            Text("No products found", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
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
                                        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) }
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
