package com.example.cartify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cartify.data.repository.FakeData
import com.example.cartify.navigation.Screen
import com.example.cartify.ui.screens.auth.LoginScreen
import com.example.cartify.ui.screens.auth.OnboardingScreen
import com.example.cartify.ui.screens.auth.SignupScreen
import com.example.cartify.ui.screens.auth.SplashScreen
import com.example.cartify.ui.screens.checkout.CheckoutScreen
import com.example.cartify.ui.screens.details.*
import com.example.cartify.ui.screens.listing.NearbyStoresScreen
import com.example.cartify.ui.screens.listing.ProductListingScreen
import com.example.cartify.ui.screens.main.MainScreen
import com.example.cartify.ui.screens.vendor.StoreSettingsScreen
import com.example.cartify.ui.theme.CartifyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        lifecycleScope.launch {
            FakeData.syncFromBackend()
        }

        setContent {
            CartifyTheme {
                AppNavigation()
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
        
        // Common & Details Screens
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
        composable(Screen.StoreSettings.route) { StoreSettingsScreen(navController) }
    }
}
