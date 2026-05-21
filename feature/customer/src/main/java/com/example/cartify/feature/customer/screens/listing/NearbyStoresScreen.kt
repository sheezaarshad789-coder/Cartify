package com.example.cartify.feature.customer.screens.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.core.common.model.Store
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.NearbyStoresState
import com.example.cartify.feature.customer.NearbyStoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyStoresScreen(navController: NavController, viewModel: NearbyStoresViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val storesState by viewModel.storesState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Stores Near You",
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
                placeholder = { Text("Search for a store", color = JapandiEarthyGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JapandiSage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
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

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = storesState) {
                    is NearbyStoresState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = JapandiSage)
                    }
                    is NearbyStoresState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = JapandiError)
                            Button(onClick = { viewModel.loadStores() }, modifier = Modifier.padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = JapandiSage)) {
                                Text("Retry")
                            }
                        }
                    }
                    is NearbyStoresState.Success -> {
                        val stores = state.stores
                        val filteredStores = stores.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredStores.isEmpty()) {
                            Text("No stores found", modifier = Modifier.align(Alignment.Center), color = JapandiEarthyGray)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredStores) { store ->
                                    StoreListItem(store = store) {
                                        navController.navigate(Screen.StoreDetail.createRoute(store.id))
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
}

@Composable
fun StoreListItem(
    store: Store,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(JapandiCanvas),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = store.name.take(1),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = JapandiSage
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = JapandiCharcoal
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = JapandiSage,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = store.distance,
                        style = MaterialTheme.typography.bodySmall,
                        color = JapandiEarthyGray,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = store.rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = JapandiCharcoal,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Open Now",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = JapandiSage
                    )
                )
            }
        }
    }
}
