package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.theme.*

/**
 * Add Address Content - Decoupled UI layer for adding or editing addresses.
 * Focuses on a clean form layout and minimalist Japandi design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressContent(
    addressType: String,
    houseNo: String,
    street: String,
    city: String,
    isDefault: Boolean,
    onAddressTypeChange: (String) -> Unit,
    onHouseNoChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onDefaultToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Address",
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
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Address Type",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = JapandiCharcoal
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AddressTypeChip(
                        label = "Home",
                        isSelected = addressType == "Home",
                        icon = Icons.Default.Home,
                        onClick = { onAddressTypeChange("Home") }
                    )
                    AddressTypeChip(
                        label = "Office",
                        isSelected = addressType == "Office",
                        icon = Icons.Default.Business,
                        onClick = { onAddressTypeChange("Office") }
                    )
                    AddressTypeChip(
                        label = "Other",
                        isSelected = addressType == "Other",
                        icon = Icons.Default.LocationOn,
                        onClick = { onAddressTypeChange("Other") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AddressFormField(
                    value = houseNo,
                    onValueChange = onHouseNoChange,
                    label = "House / Flat / Block No."
                )

                Spacer(modifier = Modifier.height(20.dp))

                AddressFormField(
                    value = street,
                    onValueChange = onStreetChange,
                    label = "Street Name / Area"
                )

                Spacer(modifier = Modifier.height(20.dp))

                AddressFormField(
                    value = city,
                    onValueChange = onCityChange,
                    label = "City"
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Set as Default",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = JapandiCharcoal
                        )
                        Text(
                            text = "Use this address for all future orders",
                            style = MaterialTheme.typography.bodySmall,
                            color = JapandiEarthyGray
                        )
                    }
                    Switch(
                        checked = isDefault,
                        onCheckedChange = onDefaultToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = JapandiSage,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = JapandiDivider
                        )
                    )
                }
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "SAVE ADDRESS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun AddressFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JapandiSage,
            focusedLabelColor = JapandiSage,
            unfocusedBorderColor = JapandiDivider,
            cursorColor = JapandiSage,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
private fun AddressTypeChip(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) JapandiSage else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, JapandiDivider)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else JapandiEarthyGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else JapandiCharcoal
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddAddressContent() {
    AddAddressContent(
        addressType = "Home",
        houseNo = "Flat 402",
        street = "Serenity Heights",
        city = "Karachi",
        isDefault = true,
        onAddressTypeChange = {},
        onHouseNoChange = {},
        onStreetChange = {},
        onCityChange = {},
        onDefaultToggle = {},
        onBackClick = {},
        onSaveClick = {}
    )
}
