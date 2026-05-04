package com.example.cartify.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.data.model.Address
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.AddressState
import com.example.cartify.ui.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(navController: NavController, viewModel: AddressViewModel = viewModel()) {
    val forestGreen = Color(0xFF2E7D32)
    val softGray = Color(0xFFF5F5F5)
    val addressState by viewModel.addressState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "My Addresses", 
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddAddress.route) },
                containerColor = forestGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Address")
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        when (addressState) {
            is AddressState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = forestGreen)
                }
            }
            is AddressState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = (addressState as AddressState.Error).message, color = Color.Red)
                        Button(onClick = { viewModel.loadAddresses() }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Retry")
                        }
                    }
                }
            }
            is AddressState.Success -> {
                val addresses = (addressState as AddressState.Success).addresses
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (addresses.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No addresses found", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(addresses) { address ->
                                AddressItemCard(address, forestGreen, softGray)
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun AddressItemCard(address: Address, forestGreen: Color, softGray: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (address.isDefault) forestGreen.copy(alpha = 0.05f) else softGray,
        shape = RoundedCornerShape(16.dp),
        border = if (address.isDefault) androidx.compose.foundation.BorderStroke(1.dp, forestGreen.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (address.isDefault) forestGreen else Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = address.icon ?: Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (address.isDefault) Color.White else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = address.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (address.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = forestGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "Default", 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = forestGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(text = address.fullAddress, fontSize = 13.sp, color = Color.Gray)
            }
            
            IconButton(onClick = { /* Delete logic */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
