package com.example.cartify.feature.vendor.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cartify.core.common.model.Category
import com.example.cartify.core.common.theme.*

/**
 * Add Product Content - Decoupled UI layer for vendors to add new items.
 * Clean Japandi aesthetic with a focus on ease of use and minimalist form design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductContent(
    name: String,
    price: String,
    unit: String,
    description: String,
    selectedCategory: Category?,
    categories: List<Category>,
    imageUri: Uri?,
    isLoading: Boolean = false,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onImagePickerClick: () -> Unit,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Product",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Image Selection Area
                ProductImagePicker(
                    imageUri = imageUri,
                    onClick = onImagePickerClick
                )

                // Basic Info Section
                AddProductFormField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Product Name",
                    placeholder = "e.g. Organic Hass Avocado"
                )

                // Category Dropdown
                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    categories = categories,
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    onCategorySelect = onCategorySelect
                )

                // Price and Unit Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AddProductFormField(
                        value = price,
                        onValueChange = onPriceChange,
                        label = "Price (PKR)",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    AddProductFormField(
                        value = unit,
                        onValueChange = onUnitChange,
                        label = "Unit",
                        placeholder = "e.g. kg, pc, pack",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Description
                AddProductFormField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = "Description",
                    placeholder = "Tell customers about your product...",
                    singleLine = false,
                    minLines = 4
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Action Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    Button(
                        onClick = onUploadClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
                        enabled = !isLoading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "UPLOAD PRODUCT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductImagePicker(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(160.dp)
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, JapandiDivider)
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Product Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = JapandiSage,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add Photo",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapandiSage
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedCategory: Category?,
    categories: List<Category>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelect: (Category) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            placeholder = { Text("Select a category", color = JapandiEarthyGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JapandiSage,
                focusedLabelColor = JapandiSage,
                unfocusedBorderColor = JapandiDivider,
                cursorColor = JapandiSage,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(Color.White)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onCategorySelect(category)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun AddProductFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder, color = JapandiEarthyGray) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JapandiSage,
            focusedLabelColor = JapandiSage,
            unfocusedBorderColor = JapandiDivider,
            cursorColor = JapandiSage,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines
    )
}

@Preview(showBackground = true, name = "Add Product Form")
@Composable
fun PreviewAddProduct() {
    val mockCategories = listOf(
        Category("1", "Fruits"),
        Category("2", "Vegetables"),
        Category("3", "Dairy")
    )
    AddProductContent(
        name = "",
        price = "",
        unit = "kg",
        description = "",
        selectedCategory = null,
        categories = mockCategories,
        imageUri = null,
        onNameChange = {},
        onPriceChange = {},
        onUnitChange = {},
        onDescriptionChange = {},
        onCategorySelect = {},
        onImagePickerClick = {},
        onBackClick = {},
        onUploadClick = {}
    )
}
