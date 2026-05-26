package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Address
import com.example.cartify.core.common.theme.*

/**
 * Address Management Content - Decoupled UI layer for managing user addresses.
 * Focuses on a clean, minimalist Japandi aesthetic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementContent(
    addresses: List<Address>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onDeleteAddressClick: (Address) -> Unit,
    onSetDefaultAddress: (Address) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Addresses",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAddressClick,
                containerColor = JapandiSage,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Address")
            }
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = JapandiSage
                )
            } else if (addresses.isEmpty()) {
                EmptyAddressesView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(addresses) { address ->
                        AddressItemCard(
                            address = address,
                            onDeleteClick = { onDeleteAddressClick(address) },
                            onCardClick = { onSetDefaultAddress(address) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressItemCard(
    address: Address,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = if (address.isDefault) androidx.compose.foundation.BorderStroke(1.5.dp, JapandiSage) else null,
        onClick = onCardClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (address.isDefault) JapandiSage else JapandiSage.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (address.isDefault) Color.White else JapandiSage,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = address.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = JapandiCharcoal
                    )
                    if (address.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = JapandiSage.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "DEFAULT",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = JapandiSage
                                )
                            )
                        }
                    }
                }
                Text(
                    text = address.fullAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = JapandiEarthyGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyAddressesView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(JapandiDivider.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = JapandiEarthyGray.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No addresses saved",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = JapandiCharcoal
        )
        Text(
            text = "Add your delivery addresses for a faster checkout.",
            style = MaterialTheme.typography.bodySmall,
            color = JapandiEarthyGray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, name = "Address List")
@Composable
fun PreviewAddressManagement() {
    val mockAddresses = listOf(
        Address("1", "Home", "123 Serenity Lane, Heights Apartment, Karachi", true),
        Address("2", "Office", "Business Center, Floor 4, I.I Chundrigar Road, Karachi", false),
        Address("3", "Parent's House", "Street 5, Gulshan-e-Iqbal, Karachi", false)
    )
    AddressManagementContent(
        addresses = mockAddresses,
        onBackClick = {},
        onAddAddressClick = {},
        onDeleteAddressClick = {},
        onSetDefaultAddress = {}
    )
}
