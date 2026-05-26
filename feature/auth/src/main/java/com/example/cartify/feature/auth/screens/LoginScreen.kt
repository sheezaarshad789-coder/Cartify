package com.example.cartify.feature.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
 * Interactive Login Screen.
 * Provides real-time validation feedback and smooth transitions.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Validation States
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LoginContent(
        emailState = email,
        passwordState = password,
        isLoading = isLoading,
        emailError = emailError,
        passwordError = passwordError,
        onEmailChange = { 
            email = it
            if (emailError != null) emailError = if (it.contains("@")) null else "Enter a valid email"
        },
        onPasswordChange = { 
            password = it
            if (passwordError != null) passwordError = if (it.length >= 6) null else "Min 6 characters"
        },
        onLoginClick = {
            // Local Validation Logic
            var hasError = false
            if (!email.contains("@")) {
                emailError = "Please enter a valid email address"
                hasError = true
            }
            if (password.length < 6) {
                passwordError = "Password must be at least 6 characters"
                hasError = true
            }

            if (!hasError) {
                isLoading = true
                // Simulate network latency
                // In a real app, this would call the ViewModel
            }
        },
        onSignUpClick = onSignUpClick
    )
}

@Composable
fun LoginContent(
    emailState: String,
    passwordState: String,
    isLoading: Boolean = false,
    emailError: String? = null,
    passwordError: String? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = JapandiCanvas
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 40.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BrandingHeader()
                Spacer(modifier = Modifier.height(48.dp))
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
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
                        placeholder = "Password",
                        leadingIcon = Icons.Outlined.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        error = passwordError,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = { 
                            focusManager.clearFocus()
                            onLoginClick()
                        })
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .animateContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JapandiSage,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "SIGN IN",
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "New to Cartify? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = JapandiEarthyGray
                        )
                        Text(
                            text = "Create Account",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = JapandiSage
                            ),
                            modifier = Modifier.clickable { onSignUpClick() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandingHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.online_groceries_cuate),
                contentDescription = "Cartify Welcome Illustration",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            ),
            color = JapandiCharcoal
        )
        
        Text(
            text = "Sign in to your curated space",
            style = MaterialTheme.typography.bodyLarge,
            color = JapandiEarthyGray
        )
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
        label = "borderColor"
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

@Preview(showBackground = true, name = "Login - Premium Design")
@Composable
fun PreviewLoginContent() {
    CartifyTheme {
        LoginContent(
            emailState = "hello@cartify.com",
            passwordState = "password123",
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Login - Error State")
@Composable
fun PreviewLoginError() {
    CartifyTheme {
        LoginContent(
            emailState = "invalid-email",
            passwordState = "123",
            emailError = "Invalid email format",
            passwordError = "Password too short",
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}
