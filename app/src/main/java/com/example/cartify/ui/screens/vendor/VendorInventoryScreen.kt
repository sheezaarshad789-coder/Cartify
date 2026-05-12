package com.example.cartify.ui.screens.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorInventoryScreen(navController: NavController, viewModel: VendorViewModel = viewModel()) {
    val state by viewModel.state
    var searchQuery by remember { mutableStateOf("") }
    val cartifyGreen = MaterialTheme.colorScheme.primary

    val filteredProducts = state.products.filter { 
        it.name.contains(searchQuery, ignoreCase = true) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7))
        ) {
            // Professional Search Feature
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search your products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = cartifyGreen)
                }
            } else if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchQuery.isEmpty()) "No products added" else "No results found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredProducts) { product ->
                        InventoryItemCard(
                            name = product.name,
                            price = "$${product.price}/${product.unit}",
                            imageUrl = product.imageUrl,
                            isAvailable = product.isAvailable,
                            onDelete = { viewModel.deleteProduct(product.id) },
                            onEdit = { navController.navigate(Screen.EditProduct.createRoute(product.id)) },
                            onToggleStock = { viewModel.toggleProductAvailability(product.id, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    name: String,
    price: String,
    imageUrl: String,
    isAvailable: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleStock: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAvailable) "In Stock" else "Out of Stock",
                    color = if (isAvailable) Color(0xFF4CAF50) else Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = isAvailable,
                    onCheckedChange = onToggleStock,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White, 
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
    }
}
