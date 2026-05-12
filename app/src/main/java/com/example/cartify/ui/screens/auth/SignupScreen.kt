package com.example.cartify.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartify.R
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.AuthState
import com.example.cartify.ui.AuthViewModel

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("user") } // "user" or "vendor"
    
    val cartifyGreen = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val authState by viewModel.authState

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Success ke baad role check karke navigate karenge (UserSession se)
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Signup.route) { inclusive = true }
                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.online_groceries_cuate),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .aspectRatio(1f)
            )

            Text(
                text = "Cartify",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = Color.Black
            )

            Text(
                text = "Join us as a User or Vendor",
                style = MaterialTheme.typography.bodyMedium,
                color = cartifyGreen,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Selection UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleOption(
                    title = "Customer",
                    isSelected = selectedRole == "user",
                    onClick = { selectedRole = "user" },
                    modifier = Modifier.weight(1f),
                    color = cartifyGreen
                )
                RoleOption(
                    title = "Vendor",
                    isSelected = selectedRole == "vendor",
                    onClick = { selectedRole = "vendor" },
                    modifier = Modifier.weight(1f),
                    color = cartifyGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name Field
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Full Name", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = cartifyGreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email Field
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = cartifyGreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = cartifyGreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        viewModel.signup(name.trim(), email.trim(), password, selectedRole)
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cartifyGreen),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "CREATE ACCOUNT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an account? ", color = Color.Gray)
                Text(
                    text = "Log In",
                    color = cartifyGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun RoleOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
