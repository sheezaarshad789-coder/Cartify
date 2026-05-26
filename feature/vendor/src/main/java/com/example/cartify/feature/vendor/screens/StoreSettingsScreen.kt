package com.example.cartify.feature.vendor.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cartify.core.common.theme.*
import com.example.cartify.core.common.ui.components.ShimmerItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- 1. State Models (Decoupled & Immutable) ---

data class StoreSettingsUiState(
    val storeName: String = "Japandi Fresh Organics",
    val deliveryTime: String = "20-30 mins",
    val logoUri: Uri? = null,
    val bannerUri: Uri? = null,
    val currentLogoUrl: String? = "https://images.unsplash.com/photo-1542838132-92c53300491e?q=80&w=200&auto=format&fit=crop",
    val currentBannerUrl: String? = "https://images.unsplash.com/photo-1534723452862-4c874018d66d?q=80&w=800&auto=format&fit=crop",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val showSuccess: Boolean = false
)

data class StoreSettingsActions(
    val onNameChange: (String) -> Unit = {},
    val onDeliveryTimeChange: (String) -> Unit = {},
    val onLogoPickerClick: () -> Unit = {},
    val onBannerPickerClick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val onSaveClick: () -> Unit = {}
)

/**
 * Masterpiece Store Settings Screen.
 * Refactored for pure frontend excellence, decoupled logic, and premium Japandi UI.
 */
@Composable
fun StoreSettingsScreen(
    onBackClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(StoreSettingsUiState()) }
    val scope = rememberCoroutineScope()

    // Simulate entry loading sequence for premium UX
    LaunchedEffect(Unit) {
        delay(1200)
        uiState = uiState.copy(isLoading = false)
    }

    // Success feedback timeout logic
    LaunchedEffect(uiState.showSuccess) {
        if (uiState.showSuccess) {
            delay(3000)
            uiState = uiState.copy(showSuccess = false)
        }
    }

    // Actions isolation for performance and cleanliness
    val actions = remember {
        StoreSettingsActions(
            onNameChange = { name ->
                uiState = uiState.copy(
                    storeName = name,
                    nameError = if (name.isBlank()) "Store name is required" else null
                )
            },
            onDeliveryTimeChange = { uiState = uiState.copy(deliveryTime = it) },
            onBackClick = onBackClick,
            onSaveClick = {
                if (uiState.storeName.isBlank()) {
                    uiState = uiState.copy(nameError = "Store name is required")
                } else {
                    scope.launch {
                        uiState = uiState.copy(isSaving = true)
                        delay(1500) // Simulation of saving process
                        uiState = uiState.copy(isSaving = false, showSuccess = true)
                    }
                }
            },
            onLogoPickerClick = { /* Simulation: Handle local image picking */ },
            onBannerPickerClick = { /* Simulation: Handle local image picking */ }
        )
    }

    CartifyTheme {
        StoreSettingsContent(state = uiState, actions = actions)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreSettingsContent(
    state: StoreSettingsUiState,
    actions: StoreSettingsActions
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Store Settings",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = actions.onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        containerColor = JapandiCanvas
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                if (state.isLoading) {
                    StoreSettingsShimmer()
                } else {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Branding Section
                    SettingsSection(title = "Visual Identity") {
                        ImageUploadCard(
                            label = "Store Cover",
                            uri = state.bannerUri,
                            url = state.currentBannerUrl,
                            height = 200.dp,
                            onClick = actions.onBannerPickerClick
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        AvatarUploadCard(
                            uri = state.logoUri,
                            url = state.currentLogoUrl,
                            onClick = actions.onLogoPickerClick
                        )
                    }

                    // General Info Section
                    SettingsSection(title = "Business Details") {
                        SettingsTextField(
                            value = state.storeName,
                            onValueChange = actions.onNameChange,
                            label = "Store Name",
                            error = state.nameError,
                            icon = Icons.Default.Store,
                            imeAction = ImeAction.Next,
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        SettingsTextField(
                            value = state.deliveryTime,
                            onValueChange = actions.onDeliveryTimeChange,
                            label = "Delivery Estimation",
                            placeholder = "e.g. 20-30 mins",
                            icon = Icons.Default.Timer,
                            imeAction = ImeAction.Done,
                            keyboardActions = KeyboardActions(onDone = { 
                                focusManager.clearFocus()
                                actions.onSaveClick()
                            })
                        )
                    }

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            // High-Polish Feedback Toast
            FeedbackToast(visible = state.showSuccess)

            // Sticky Bottom Bar
            StickyBottomBar(
                visible = !state.isLoading,
                isSaving = state.isSaving,
                onSaveClick = {
                    focusManager.clearFocus()
                    actions.onSaveClick()
                }
            )
        }
    }
}

// --- Sub-Composables (Decoupled & Highly Scannable) ---

@Composable
private fun StoreSettingsShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        ShimmerItem(height = 20.dp, modifier = Modifier.width(100.dp))
        ShimmerItem(height = 180.dp, shape = RoundedCornerShape(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerItem(height = 64.dp, modifier = Modifier.size(64.dp).clip(CircleShape))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerItem(height = 14.dp, modifier = Modifier.width(120.dp))
                ShimmerItem(height = 10.dp, modifier = Modifier.width(180.dp))
            }
        }
        repeat(2) {
            ShimmerItem(height = 56.dp, shape = RoundedCornerShape(14.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = JapandiCharcoal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}

@Composable
private fun ImageUploadCard(
    label: String,
    uri: Uri?,
    url: String?,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = JapandiEarthyGray,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onClick
                )
                .shadow(2.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = uri ?: url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "CHANGE COVER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarUploadCard(
    uri: Uri?,
    url: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(JapandiCanvas)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 40.dp),
                        onClick = onClick
                    )
                    .border(2.dp, JapandiDivider, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri ?: url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)))
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = "Store Avatar",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = JapandiCharcoal
                )
                Text(
                    text = "High resolution square recommended",
                    style = MaterialTheme.typography.bodySmall,
                    color = JapandiEarthyGray
                )
            }
        }
    }
}

@Composable
private fun FeedbackToast(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Surface(
            color = JapandiSage,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Store profile updated successfully", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun BoxScope.StickyBottomBar(
    visible: Boolean,
    isSaving: Boolean,
    onSaveClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 24.dp,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JapandiSage,
                        contentColor = Color.White
                    ),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "UPDATE SETTINGS",
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

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    placeholder: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder, color = JapandiEarthyGray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = if(error != null) JapandiError else JapandiSage, modifier = Modifier.size(22.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JapandiSage,
                unfocusedBorderColor = JapandiDivider,
                errorBorderColor = JapandiError,
                cursorColor = JapandiSage,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = keyboardActions,
            singleLine = true
        )
        
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = JapandiError,
                modifier = Modifier.padding(start = 16.dp, top = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Store Settings - Masterpiece")
@Composable
fun PreviewStoreSettingsMasterpiece() {
    CartifyTheme {
        StoreSettingsContent(
            state = StoreSettingsUiState(isLoading = false),
            actions = StoreSettingsActions()
        )
    }
}
