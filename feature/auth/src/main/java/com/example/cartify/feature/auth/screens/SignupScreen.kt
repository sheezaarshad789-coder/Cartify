package com.example.cartify.feature.auth.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.R
import com.example.cartify.core.common.theme.*
import kotlinx.coroutines.delay

/**
 * Interactive Signup Screen.
 * Implements real-time validation, Japandi design, and fluid transitions.
 */
@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("user") } // "user" or "vendor"
    var isLoading by remember { mutableStateOf(false) }
    
    // Validation States
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    SignupContent(
        nameState = name,
        emailState = email,
        passwordState = password,
        selectedRole = selectedRole,
        isLoading = isLoading,
        nameError = nameError,
        emailError = emailError,
        passwordError = passwordError,
        onNameChange = { 
            name = it
            if (nameError != null) nameError = if (it.isNotBlank()) null else "Name is required"
        },
        onEmailChange = { 
            email = it
            if (emailError != null) emailError = if (it.contains("@")) null else "Enter a valid email"
        },
        onPasswordChange = { 
            password = it
            if (passwordError != null) passwordError = if (it.length >= 6) null else "Min 6 characters"
        },
        onRoleSelect = { selectedRole = it },
        onSignupClick = {
            // Local Validation
            var hasError = false
            if (name.isBlank()) { nameError = "Full name is required"; hasError = true }
            if (!email.contains("@")) { emailError = "Invalid email address"; hasError = true }
            if (password.length < 6) { passwordError = "Password too short"; hasError = true }

            if (!hasError) {
                isLoading = true
                // Simulation
            }
        },
        onLoginClick = onLoginClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupContent(
    nameState: String,
    emailState: String,
    passwordState: String,
    selectedRole: String,
    isLoading: Boolean = false,
    nameError: String? = null,
    emailError: String? = null,
    passwordError: String? = null,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleSelect: (String) -> Unit,
    onSignupClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.online_groceries_cuate),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = JapandiCharcoal
            )

            Text(
                text = "Join the Cartify family",
                style = MaterialTheme.typography.bodyLarge,
                color = JapandiEarthyGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Role Selection with Animation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleOption(
                    title = "Customer",
                    isSelected = selectedRole == "user",
                    onClick = { onRoleSelect("user") },
                    modifier = Modifier.weight(1f)
                )
                RoleOption(
                    title = "Vendor",
                    isSelected = selectedRole == "vendor",
                    onClick = { onRoleSelect("vendor") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AuthTextField(
                    value = nameState,
                    onValueChange = onNameChange,
                    placeholder = "Full Name",
                    leadingIcon = Icons.Outlined.Person,
                    error = nameError,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                AuthTextField(
                    value = emailState,
                    onValueChange = onEmailChange,
                    placeholder = "Email Address",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email,
                    error = emailError,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )

                AuthTextField(
                    value = passwordState,
                    onValueChange = onPasswordChange,
                    placeholder = "Create Password",
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    error = passwordError,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { 
                        focusManager.clearFocus()
                        onSignupClick()
                    })
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Action Button
            Button(
                onClick = onSignupClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(58.dp)
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JapandiSage),
                enabled = !isLoading,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "GET STARTED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = JapandiEarthyGray
                )
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = JapandiSage
                    ),
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}

@Composable
private fun RoleOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(if (isSelected) JapandiSage else Color.White, label = "bg")
    val contentColor by animateColorAsState(if (isSelected) Color.White else JapandiCharcoal, label = "content")
    val borderColor by animateColorAsState(if (isSelected) JapandiSage else JapandiDivider, label = "border")

    Surface(
        modifier = modifier
            .height(54.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val borderColor by animateColorAsState(
        targetValue = if (error != null) JapandiError else JapandiDivider,
        label = "border"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = JapandiEarthyGray
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (error != null) JapandiError else JapandiSage,
                    modifier = Modifier.size(22.dp)
                ) 
            },
            shape = RoundedCornerShape(16.dp),
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = JapandiSage,
                unfocusedBorderColor = borderColor,
                errorBorderColor = JapandiError,
                cursorColor = JapandiSage
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = keyboardActions,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge
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

@Preview(showBackground = true, name = "Signup - Customer")
@Composable
fun PreviewSignupCustomer() {
    CartifyTheme {
        SignupContent(
            nameState = "",
            emailState = "",
            passwordState = "",
            selectedRole = "user",
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRoleSelect = {},
            onSignupClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Signup - Vendor")
@Composable
fun PreviewSignupVendor() {
    CartifyTheme {
        SignupContent(
            nameState = "Fresh Market",
            emailState = "vendor@market.com",
            passwordState = "securepass",
            selectedRole = "vendor",
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRoleSelect = {},
            onSignupClick = {},
            onLoginClick = {}
        )
    }
}
