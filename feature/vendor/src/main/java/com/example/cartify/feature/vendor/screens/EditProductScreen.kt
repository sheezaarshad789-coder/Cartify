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
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.theme.*

/**
 * Edit Product Content - Decoupled UI layer for vendors to modify existing items.
 * Strictly follows Japandi design principles for a premium, minimalist experience.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductContent(
    product: Product?,
    name: String,
    price: String,
    unit: String,
    description: String,
    selectedCategory: Category?,
    categories: List<Category>,
    newImageUri: Uri?,
    isLoading: Boolean = false,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onImagePickerClick: () -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Details",
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
        if (product == null && !isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Product not found", color = JapandiEarthyGray)
            }
        } else {
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

                    // Image Section
                    EditImageSection(
                        currentImageUrl = product?.imageUrl ?: "",
                        newImageUri = newImageUri,
                        onClick = onImagePickerClick
                    )

                    // Form Fields
                    EditProductFormField(
                        value = name,
                        onValueChange = onNameChange,
                        label = "Product Name"
                    )

                    CategoryDropdown(
                        selectedCategory = selectedCategory,
                        categories = categories,
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        onCategorySelect = onCategorySelect
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EditProductFormField(
                            value = price,
                            onValueChange = onPriceChange,
                            label = "Price (PKR)",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        EditProductFormField(
                            value = unit,
                            onValueChange = onUnitChange,
                            label = "Unit",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    EditProductFormField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = "Description",
                        singleLine = false,
                        minLines = 4
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Bottom Action Button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        Button(
                            onClick = onSaveClick,
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
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "SAVE CHANGES",
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
}

@Composable
private fun EditImageSection(
    currentImageUrl: String,
    newImageUri: Uri?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(160.dp)
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, JapandiDivider), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = newImageUri ?: currentImageUrl,
            contentDescription = "Product Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.25f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CHANGE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                }
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
private fun EditProductFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
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

@Preview(showBackground = true, name = "Edit Product - Mock Data")
@Composable
fun PreviewEditProduct() {
    val mockProduct = Product("1", "Organic Avocado", 450.0, "kg", "", "Creamy hass avocados.", "101", "Green Mart", "cat_1")
    val mockCategories = listOf(Category("cat_1", "Fruits"), Category("cat_2", "Vegetables"))
    
    EditProductContent(
        product = mockProduct,
        name = "Organic Avocado",
        price = "450",
        unit = "kg",
        description = "Creamy hass avocados sourced from local farms.",
        selectedCategory = mockCategories[0],
        categories = mockCategories,
        newImageUri = null,
        onNameChange = {},
        onPriceChange = {},
        onUnitChange = {},
        onDescriptionChange = {},
        onCategorySelect = {},
        onImagePickerClick = {},
        onBackClick = {},
        onSaveClick = {}
    )
}
