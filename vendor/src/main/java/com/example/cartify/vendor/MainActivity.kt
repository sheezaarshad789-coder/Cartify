package com.example.cartify.vendor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.feature.auth.screens.LoginScreen
import com.example.cartify.feature.auth.screens.OnboardingScreen
import com.example.cartify.feature.auth.screens.SignupScreen
import com.example.cartify.feature.auth.screens.SplashScreen
import com.example.cartify.feature.vendor.screens.*
import com.example.cartify.vendor.ui.theme.CartifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // System Splash Fix: Robot icon hatane ke liye
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CartifyTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(navController = navController)
                    }
                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(navController = navController)
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }
                    composable(Screen.Signup.route) {
                        SignupScreen(navController = navController)
                    }
                    
                    // Vendor Dashboard & Related Routes (Preventing Crash)
                    composable(Screen.VendorDashboard.route) {
                        VendorDashboardScreen(
                            navController = navController,
                            rootNavController = navController
                        )
                    }
                    composable(Screen.AddProduct.route) {
                        AddProductScreen(navController = navController)
                    }
                    composable(Screen.VendorInventory.route) {
                        VendorInventoryScreen(navController = navController)
                    }
                    composable(Screen.StoreSettings.route) {
                        StoreSettingsScreen(navController = navController)
                    }
                    composable(
                        route = Screen.EditProduct.route,
                        arguments = listOf(navArgument("productId") { type = NavType.StringType })
                    ) { 
                        EditProductScreen(navController, it.arguments?.getString("productId"))
                    }
                }
            }
        }
    }
}
