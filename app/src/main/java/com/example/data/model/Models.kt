package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val titleEn: String,
    val brand: String,
    val categoryId: String,
    val price: Long,
    val originalPrice: Long,
    val discountPercent: Int,
    val rating: Double,
    val ratingCount: Int,
    val stockCount: Int,
    val isAmazingOffer: Boolean = false,
    val isSpecialOffer: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false,
    val isSinaKalaPick: Boolean = false,
    val colorsJson: String = "[]",
    val modelsJson: String = "[]",
    val specsJson: String = "[]",
    val description: String = "",
    val imagesJson: String = "[]",
    val warranty: String = "گارانتی ۱۸ ماهه شرکتی سینا سرویس",
    val seller: String = "سینا کالا (ارسال سریع)",
    val salesCount: Int = 0,
    val viewsCount: Int = 0,
    val remainingOfferSeconds: Long = 86400L
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameEn: String,
    val iconName: String,
    val productCount: Int,
    val gradientStartHex: String,
    val gradientEndHex: String
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val quantity: Int = 1,
    val selectedColorName: String = "",
    val selectedColorHex: String = "#000000",
    val selectedModel: String = "",
    val unitPrice: Long = 0L,
    val originalUnitPrice: Long = 0L
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val itemsJson: String,
    val totalAmount: Long,
    val discountAmount: Long,
    val shippingFee: Long,
    val finalAmount: Long,
    val status: String, // PROCESSING, SHIPPED, DELIVERED, CANCELLED
    val statusTitle: String,
    val shippingAddress: String,
    val recipientName: String,
    val recipientPhone: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val trackingCode: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1,
    val fullName: String = "کاربر محترم سینا کالا",
    val phone: String = "۰۹۱۲۳۴۵۶۷۸۹",
    val email: String = "user@sinakala.com",
    val walletBalance: Long = 2500000L,
    val vipLevel: String = "کاربر الماس سینا کالا",
    val addressesJson: String = "[]"
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val userName: String,
    val rating: Double,
    val comment: String,
    val prosJson: String = "[]",
    val consJson: String = "[]",
    val date: String,
    val isBuyer: Boolean = true,
    val likesCount: Int = 0
)

@Entity(tableName = "discount_codes")
data class DiscountCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val percent: Int,
    val maxDiscount: Long,
    val minOrder: Long,
    val expireDate: String,
    val isUsed: Boolean = false
)

@Entity(tableName = "promo_banners")
data class PromoBannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val imageUrl: String,
    val gradientStartHex: String,
    val gradientEndHex: String,
    val targetCategory: String
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val viewedAt: Long = System.currentTimeMillis()
)

// Non-entity UI & Helper models
data class ColorOption(
    val name: String,
    val hex: String,
    val inStock: Boolean = true
)

data class SpecItem(
    val title: String,
    val value: String
)

data class SpecGroup(
    val groupName: String,
    val items: List<SpecItem>
)

data class UserAddress(
    val id: String,
    val title: String,
    val fullAddress: String,
    val city: String,
    val postalCode: String,
    val recipientName: String,
    val recipientPhone: String,
    val isDefault: Boolean = false
)

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

data class OrderItemDetail(
    val productId: Long,
    val productTitle: String,
    val quantity: Int,
    val unitPrice: Long,
    val colorName: String,
    val modelName: String,
    val imageUrl: String
)
