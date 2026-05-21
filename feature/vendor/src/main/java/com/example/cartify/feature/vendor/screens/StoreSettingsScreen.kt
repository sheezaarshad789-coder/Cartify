package com.example.cartify.feature.vendor.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cartify.data.network.model.StoreDto
import com.example.cartify.feature.vendor.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreSettingsScreen(navController: NavController, viewModel: VendorViewModel = viewModel()) {
    val state by viewModel.state
    val context = LocalContext.current
    val cartifyGreen = MaterialTheme.colorScheme.primary

    var name by remember { mutableStateOf("") }
    var deliveryTime by remember { mutableStateOf("") }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }

    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { logoUri = it }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { bannerUri = it }

    LaunchedEffect(state.store) {
        state.store?.let {
            name = it.name
            deliveryTime = it.deliveryTime
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Store Settings", 
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(start = 8.dp)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { paddingValues ->
        if (state.store == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = cartifyGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Section: Store Branding
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Store Branding", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Gray)
                    
                    // Banner Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Banner Image", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0F0F0))
                                    .clickable { bannerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = bannerUri ?: state.store?.bannerUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }

                    // Logo Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Store Logo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Upload a square logo", fontSize = 11.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F0F0))
                                    .clickable { logoLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = logoUri ?: state.store?.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }

                // Section: Store Information
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Basic Information", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Gray)
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Store Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = cartifyGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = deliveryTime,
                        onValueChange = { deliveryTime = it },
                        label = { Text("Delivery Time") },
                        placeholder = { Text("e.g., 20-30 mins") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, tint = cartifyGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Store name cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val logoBytes = logoUri?.let { context.contentResolver.openInputStream(it)?.readBytes() }
                        val bannerBytes = bannerUri?.let { context.contentResolver.openInputStream(it)?.readBytes() }

                        val updatedStoreDto = StoreDto(
                            id = state.store!!.id,
                            name = name,
                            deliveryTime = deliveryTime,
                            rating = state.store!!.rating,
                            distance = state.store!!.distance,
                            imageUrl = state.store!!.imageUrl,
                            bannerUrl = state.store!!.bannerUrl,
                            isFavorite = state.store!!.isFavorite
                        )

                        viewModel.updateStore(updatedStoreDto, logoBytes, bannerBytes) {
                            Toast.makeText(context, "Settings updated successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cartifyGreen),
                    enabled = !state.isOperationLoading
                ) {
                    if (state.isOperationLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SAVE CHANGES", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                if (state.error != null) {
                    Text(text = state.error!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
