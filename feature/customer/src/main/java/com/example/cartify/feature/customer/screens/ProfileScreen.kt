package com.example.cartify.feature.customer.screens

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cartify.core.common.theme.*

/**
 * Profile Content - Decoupled UI layer for user profile and settings.
 * Uses State Hoisting for all interactions and mock data for previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    userName: String,
    userEmail: String,
    isVendorMode: Boolean,
    profileImageUri: String? = null,
    selectedLanguage: String = "English (US)",
    onBackClick: () -> Unit,
    onImagePickerClick: () -> Unit,
    onToggleVendorMode: () -> Unit,
    onSavedAddressesClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onStoreSettingsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onHelpCenterClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            ProfileHeader(
                name = userName,
                email = userEmail,
                imageUri = profileImageUri,
                onEditImageClick = onImagePickerClick
            )

            Column(modifier = Modifier.padding(24.dp)) {
                // Mode Switcher
                VendorSwitchCard(
                    isVendorMode = isVendorMode,
                    onSwitch = onToggleVendorMode
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = if (isVendorMode) "Vendor Management" else "Account Settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = JapandiCharcoal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isVendorMode) {
                    ProfileOptionCard(
                        icon = Icons.Default.Store,
                        title = "Store Settings",
                        subtitle = "Customize name, logo & banner",
                        onClick = onStoreSettingsClick
                    )
                    ProfileOptionCard(
                        icon = Icons.Default.Inventory,
                        title = "Inventory",
                        subtitle = "Manage products & stock levels",
                        onClick = onInventoryClick
                    )
                } else {
                    ProfileOptionCard(
                        icon = Icons.Default.LocationOn,
                        title = "Saved Addresses",
                        subtitle = "Home, Office & other locations",
                        onClick = onSavedAddressesClick
                    )
                    ProfileOptionCard(
                        icon = Icons.Default.Favorite,
                        title = "My Wishlist",
                        subtitle = "All your favorite grocery items",
                        onClick = onWishlistClick
                    )
                    ProfileOptionCard(
                        icon = Icons.Default.History,
                        title = "Order History",
                        subtitle = "Track and reorder past purchases",
                        onClick = onOrderHistoryClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "App Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = JapandiCharcoal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ProfileOptionCard(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage alerts & promo updates",
                    onClick = onNotificationsClick
                )
                ProfileOptionCard(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = selectedLanguage,
                    onClick = onLanguageClick
                )
                ProfileOptionCard(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Help Center",
                    subtitle = "FAQs & customer support",
                    onClick = onHelpCenterClick
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JapandiError.copy(alpha = 0.08f),
                        contentColor = JapandiError
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "LOGOUT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    imageUri: String?,
    onEditImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(JapandiSage)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(8.dp, CircleShape)
                        .clickable { onEditImageClick() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👤", fontSize = 56.sp)
                        }
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .shadow(4.dp, CircleShape)
                        .clickable { onEditImageClick() },
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Edit",
                            tint = JapandiSage,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
            
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun VendorSwitchCard(isVendorMode: Boolean, onSwitch: () -> Unit) {
    val gradientColors = if (isVendorMode) {
        listOf(Color(0xFF2E7D32), Color(0xFF43A047))
    } else {
        listOf(JapandiSage, Color(0xFF7FA37C))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSwitch)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors = gradientColors))
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
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
                        text = if (isVendorMode) "Customer Mode" else "Vendor Mode",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isVendorMode) "Switch to shop groceries" else "Manage your store & products",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall
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
private fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(JapandiSage.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = JapandiSage,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = JapandiDivider,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile - Customer")
@Composable
fun PreviewProfileCustomer() {
    ProfileContent(
        userName = "Sarah Jenkins",
        userEmail = "sarah.j@example.com",
        isVendorMode = false,
        onBackClick = {},
        onImagePickerClick = {},
        onToggleVendorMode = {},
        onSavedAddressesClick = {},
        onWishlistClick = {},
        onOrderHistoryClick = {},
        onStoreSettingsClick = {},
        onInventoryClick = {},
        onNotificationsClick = {},
        onLanguageClick = {},
        onHelpCenterClick = {},
        onLogoutClick = {}
    )
}

@Preview(showBackground = true, name = "Profile - Vendor")
@Composable
fun PreviewProfileVendor() {
    ProfileContent(
        userName = "Fresh Mart Admin",
        userEmail = "admin@freshmart.com",
        isVendorMode = true,
        onBackClick = {},
        onImagePickerClick = {},
        onToggleVendorMode = {},
        onSavedAddressesClick = {},
        onWishlistClick = {},
        onOrderHistoryClick = {},
        onStoreSettingsClick = {},
        onInventoryClick = {},
        onNotificationsClick = {},
        onLanguageClick = {},
        onHelpCenterClick = {},
        onLogoutClick = {}
    )
}
