package com.example.cartify.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Main : Screen("main")
    
    // Main App Tabs
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Orders : Screen("orders")
    object Messages : Screen("messages")
    object Profile : Screen("profile")
    
    // Vendor Dashboard
    object VendorDashboard : Screen("vendor_dashboard")
    object AddProduct : Screen("add_product")
    object VendorInventory : Screen("vendor_inventory")
    object StoreSettings : Screen("store_settings")
    object EditProduct : Screen("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }
    
    // Details
    object StoreDetail : Screen("store_detail/{storeId}") {
        fun createRoute(storeId: String) = "store_detail/$storeId"
    }
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Checkout : Screen("checkout")
    object ChatDetail : Screen("chat_detail/{vendorId}") {
        fun createRoute(vendorId: String) = "chat_detail/$vendorId"
    }
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object SearchResults : Screen("search_results/{query}") {
        fun createRoute(query: String) = "search_results/$query"
    }
    object ProductListing : Screen("product_listing/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) = "product_listing/$categoryId/$categoryName"
    }
    object NearbyStores : Screen("nearby_stores")
    object AddressManagement : Screen("address_management")
    object Favorites : Screen("favorites")
    object Notifications : Screen("notifications")
    object HelpCenter : Screen("help_center")
    object AddAddress : Screen("add_address")
}
