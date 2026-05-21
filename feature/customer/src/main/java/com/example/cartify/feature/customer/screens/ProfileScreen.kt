package com.example.cartify.feature.customer.screens

import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.*
import com.example.cartify.feature.customer.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    rootNavController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val userName by viewModel.userName
    val userEmail by viewModel.userEmail
    val isVendorMode by viewModel.isVendorMode
    val profileImageUri by viewModel.profileImageUri

    var showLanguageSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedLanguage by remember { mutableStateOf("English (US)") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfileImage(it.toString()) }
    }

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                )
                Spacer(modifier = Modifier.height(12.dp))

                LanguageOption("English", selectedLanguage == "English") {
                    selectedLanguage = "English"
                    showLanguageSheet = false
                }
                LanguageOption("Urdu", selectedLanguage == "Urdu") {
                    selectedLanguage = "Urdu"
                    showLanguageSheet = false
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = JapandiCharcoal),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Professional Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JapandiSage)
                    .padding(top = 16.dp, bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text("👤", fontSize = 48.sp)
                            }
                        }
                        
                        // Edit Icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { imagePickerLauncher.launch("image/*") }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change DP",
                                tint = JapandiSage,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(text = userEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                // Switch Mode Logic
                VendorSwitchCard(
                    isVendorMode = isVendorMode,
                    onSwitch = {
                        viewModel.toggleVendorMode()
                        val targetRoute = if (!isVendorMode) Screen.VendorDashboard.route else Screen.Home.route
                        navController.navigate(targetRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isVendorMode) "Vendor Settings" else "Account Settings",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = JapandiCharcoal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (isVendorMode) {
                    ProfileOptionCard(icon = Icons.Default.Store, title = "Store Settings", subtitle = "Name, Logo & Banner") {
                        rootNavController.navigate(Screen.StoreSettings.route)
                    }
                    ProfileOptionCard(icon = Icons.Default.Inventory, title = "Inventory", subtitle = "Manage products & stock") {
                        navController.navigate(Screen.VendorInventory.route)
                    }
                } else {
                    ProfileOptionCard(icon = Icons.Default.LocationOn, title = "Saved Addresses", subtitle = "Home, Office & more") {
                        rootNavController.navigate(Screen.AddressManagement.route)
                    }
                    ProfileOptionCard(icon = Icons.Default.Favorite, title = "My Wishlist", subtitle = "Your favorite grocery items") {
                        rootNavController.navigate(Screen.Favorites.route)
                    }
                    ProfileOptionCard(icon = Icons.Default.History, title = "Order History", subtitle = "View all your past orders") {
                        navController.navigate(Screen.Orders.route)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("App Preferences", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = JapandiCharcoal, modifier = Modifier.padding(bottom = 8.dp))

                ProfileOptionCard(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "Alerts, offers & status") {
                    rootNavController.navigate(Screen.Notifications.route)
                }
                ProfileOptionCard(icon = Icons.Default.Language, title = "Language", subtitle = selectedLanguage) {
                    showLanguageSheet = true
                }
                ProfileOptionCard(icon = Icons.AutoMirrored.Filled.Help, title = "Help Center", subtitle = "FAQ & customer support") {
                    rootNavController.navigate(Screen.HelpCenter.route)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.logout()
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JapandiError.copy(alpha = 0.1f), contentColor = JapandiError)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Logout", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VendorSwitchCard(isVendorMode: Boolean, onSwitch: () -> Unit) {
    val gradientColors = if (isVendorMode) {
        listOf(Color(0xFF1976D2), Color(0xFF2196F3))
    } else {
        listOf(JapandiSage, JapandiSageLight)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSwitch),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors = gradientColors))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVendorMode) Icons.Default.Person else Icons.Default.Storefront,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isVendorMode) "Switch to Customer Mode" else "Switch to Vendor Mode",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (isVendorMode) "Start shopping for groceries" else "Manage your store and products",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun LanguageOption(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyLarge, color = JapandiCharcoal)
        RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = JapandiSage))
    }
}

@Composable
fun ProfileOptionCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(JapandiSage.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = JapandiSage, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = JapandiCharcoal)
                Text(text = subtitle, fontSize = 11.sp, color = JapandiEarthyGray)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = JapandiEarthyGray, modifier = Modifier.size(18.dp))
        }
    }
}
