package com.example.cartify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cartify.core.common.navigation.Screen
import com.example.cartify.core.common.theme.CartifyTheme
import com.example.cartify.data.network.repository.FakeData
import com.example.cartify.feature.auth.screens.*
import com.example.cartify.feature.customer.screens.*
import com.example.cartify.feature.customer.screens.checkout.CheckoutScreen
import com.example.cartify.feature.customer.screens.details.*
import com.example.cartify.feature.customer.screens.listing.NearbyStoresScreen
import com.example.cartify.feature.customer.screens.listing.ProductListingScreen
import com.example.cartify.feature.vendor.screens.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        lifecycleScope.launch {
            FakeData.syncFromBackend()
        }

        setContent {
            CartifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        
        composable(Screen.Main.route) { 
            MainScreen(rootNavController = navController) 
        }
        
        composable(Screen.VendorDashboard.route) { 
            VendorDashboardScreen(navController = navController, rootNavController = navController) 
        }
        composable(Screen.AddProduct.route) { AddProductScreen(navController) }
        composable(Screen.VendorInventory.route) { VendorInventoryScreen(navController) }
        composable(Screen.StoreSettings.route) { StoreSettingsScreen(navController) }
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { 
            EditProductScreen(navController, it.arguments?.getString("productId")) 
        }
        
        // Details Screens
        composable(route = Screen.StoreDetail.route, arguments = listOf(navArgument("storeId") { type = NavType.StringType })) {
            StoreDetailScreen(navController, it.arguments?.getString("storeId")) 
        }
        composable(route = Screen.ProductDetail.route, arguments = listOf(navArgument("productId") { type = NavType.StringType })) { 
            ProductDetailScreen(navController, it.arguments?.getString("productId")) 
        }
        composable(Screen.Checkout.route) { CheckoutScreen(navController) }
        composable(route = Screen.ChatDetail.route, arguments = listOf(navArgument("vendorId") { type = NavType.StringType })) { 
            ChatDetailScreen(navController, it.arguments?.getString("vendorId")) 
        }
        composable(route = Screen.OrderTracking.route, arguments = listOf(navArgument("orderId") { type = NavType.StringType })) { 
            OrderTrackingScreen(navController, it.arguments?.getString("orderId")) 
        }
        composable(route = Screen.OrderDetail.route, arguments = listOf(navArgument("orderId") { type = NavType.StringType })) { 
            OrderDetailScreen(navController, it.arguments?.getString("orderId")) 
        }
        composable(route = Screen.SearchResults.route, arguments = listOf(navArgument("query") { type = NavType.StringType })) { 
            SearchResultsScreen(navController, it.arguments?.getString("query")) 
        }
        composable(route = Screen.ProductListing.route, arguments = listOf(navArgument("categoryId") { type = NavType.StringType }, navArgument("categoryName") { type = NavType.StringType })) { 
            ProductListingScreen(navController, it.arguments?.getString("categoryId"), it.arguments?.getString("categoryName")) 
        }
        composable(Screen.AddressManagement.route) { AddressManagementScreen(navController) }
        composable(Screen.NearbyStores.route) { NearbyStoresScreen(navController) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController) }
        composable(Screen.HelpCenter.route) { HelpCenterScreen(navController) }
        composable(Screen.AddAddress.route) { AddAddressScreen(navController) }
    }
}
