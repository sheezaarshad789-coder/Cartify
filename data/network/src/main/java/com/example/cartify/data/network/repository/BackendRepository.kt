package com.example.cartify.data.network.repository

import com.example.cartify.core.common.model.Address
import com.example.cartify.core.common.model.Order
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.model.StoreSettings
import com.example.cartify.core.common.model.UserProfile
import com.example.cartify.core.common.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

/**
 * Masterpiece Backend Repository for Cartify.
 * Strictly aligned with project-wide models to fix type mismatches.
 */
class BackendRepository {

    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        delay(800)
        return Result.success(
            UserProfile(
                id = userId,
                name = "Shiza Arshad",
                email = "shiza@devsquad.com",
                role = UserRole.VENDOR
            )
        )
    }

    fun getProductsStream(): Flow<List<Product>> = flow {
        emit(emptyList())
        delay(1500)
        emit(listOf(
            Product(
                id = "1",
                name = "Organic Hass Avocado",
                price = 450.0,
                unit = "kg",
                storeId = "101",
                storeName = "Green Grocers",
                categoryId = "fruits",
                imageUrl = "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578",
                description = "Fresh organic avocados."
            )
        ))
    }

    suspend fun getVendorOrders(): Result<List<Order>> {
        delay(1200)
        return Result.success(listOf(
            Order(
                id = "ORD-102",
                storeName = "Green Grocers",
                status = "Processing",
                date = "Today",
                totalAmount = 1850.0,
                items = emptyList(),
                customerName = "Kanwal Maryam"
            )
        ))
    }
}
