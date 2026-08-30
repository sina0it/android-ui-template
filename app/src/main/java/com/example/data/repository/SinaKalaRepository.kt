package com.example.data.repository

import com.example.data.local.SampleData
import com.example.data.local.SinaKalaDao
import com.example.data.model.CartItemEntity
import com.example.data.model.CartItemWithProduct
import com.example.data.model.CategoryEntity
import com.example.data.model.DiscountCodeEntity
import com.example.data.model.JsonHelper
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemDetail
import com.example.data.model.ProductEntity
import com.example.data.model.PromoBannerEntity
import com.example.data.model.RecentlyViewedEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserAddress
import com.example.data.model.UserProfileEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class SinaKalaRepository(private val dao: SinaKalaDao) {

    // Products
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val amazingOffers: Flow<List<ProductEntity>> = dao.getAmazingOffers()
    val specialOffers: Flow<List<ProductEntity>> = dao.getSpecialOffers()
    val bestSellers: Flow<List<ProductEntity>> = dao.getBestSellers()
    val newArrivals: Flow<List<ProductEntity>> = dao.getNewArrivals()
    val sinaKalaPicks: Flow<List<ProductEntity>> = dao.getSinaKalaPicks()
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val allBanners: Flow<List<PromoBannerEntity>> = dao.getAllBanners()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allDiscountCodes: Flow<List<DiscountCodeEntity>> = dao.getAllDiscountCodes()

    fun getProductById(id: Long): Flow<ProductEntity?> = dao.getProductById(id)

    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>> = dao.getProductsByCategory(categoryId)

    fun searchProducts(query: String): Flow<List<ProductEntity>> = dao.searchProducts(query)

    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> = dao.getReviewsForProduct(productId)

    fun isProductInWishlist(productId: Long): Flow<Boolean> = dao.isProductInWishlist(productId)

    // Wishlist Flow
    val wishlistWithProducts: Flow<List<ProductEntity>> = combine(
        dao.getWishlistItems(),
        dao.getAllProducts()
    ) { wishlistItems, products ->
        val productMap = products.associateBy { it.id }
        wishlistItems.mapNotNull { productMap[it.productId] }
    }

    // Cart Items with associated Product Details
    val cartWithProducts: Flow<List<CartItemWithProduct>> = combine(
        dao.getCartItems(),
        dao.getAllProducts()
    ) { cartItems, products ->
        val productMap = products.associateBy { it.id }
        cartItems.mapNotNull { cartItem ->
            productMap[cartItem.productId]?.let { product ->
                CartItemWithProduct(cartItem, product)
            }
        }
    }

    // Cart Actions
    suspend fun addToCart(
        productId: Long,
        quantity: Int = 1,
        colorName: String = "",
        colorHex: String = "#000000",
        model: String = ""
    ): Boolean {
        val product = dao.getProductDirect(productId) ?: return false
        val existing = dao.getCartItemByProductAndVariants(productId, colorName, model)
        if (existing != null) {
            val newQty = existing.quantity + quantity
            dao.updateCartItemQuantity(existing.id, newQty)
        } else {
            val cartItem = CartItemEntity(
                productId = productId,
                quantity = quantity,
                selectedColorName = colorName,
                selectedColorHex = colorHex,
                selectedModel = model,
                unitPrice = product.price,
                originalUnitPrice = product.originalPrice
            )
            dao.insertCartItem(cartItem)
        }
        return true
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        if (quantity <= 0) {
            dao.deleteCartItem(cartItemId)
        } else {
            dao.updateCartItemQuantity(cartItemId, quantity)
        }
    }

    suspend fun removeFromCart(cartItemId: Long) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    // Wishlist Actions
    suspend fun toggleWishlist(productId: Long) {
        val isInWishlist = dao.isProductInWishlist(productId).firstOrNull() ?: false
        if (isInWishlist) {
            dao.deleteWishlistByProductId(productId)
        } else {
            dao.insertWishlist(WishlistItemEntity(productId = productId))
        }
    }

    // Recently Viewed
    suspend fun recordRecentlyViewed(productId: Long) {
        dao.insertRecentlyViewed(RecentlyViewedEntity(productId = productId))
    }

    val recentlyViewedProducts: Flow<List<ProductEntity>> = combine(
        dao.getRecentlyViewed(),
        dao.getAllProducts()
    ) { recentItems, products ->
        val productMap = products.associateBy { it.id }
        val distinctProductIds = recentItems.map { it.productId }.distinct()
        distinctProductIds.mapNotNull { productMap[it] }
    }

    // Reviews
    suspend fun addReview(productId: Long, userName: String, rating: Double, comment: String, pros: List<String>, cons: List<String>): Long {
        return dao.insertReview(
            ReviewEntity(
                productId = productId,
                userName = userName.ifBlank { "کاربر سینا کالا" },
                rating = rating,
                comment = comment,
                prosJson = JsonHelper.toJsonStringList(pros),
                consJson = JsonHelper.toJsonStringList(cons),
                date = "لحظاتی پیش",
                isBuyer = true
            )
        )
    }

    // Checkout & Orders
    suspend fun createOrder(
        cartItems: List<CartItemWithProduct>,
        address: UserAddress,
        shippingMethod: String,
        shippingFee: Long,
        paymentMethod: String,
        discountAmount: Long,
        discountCodeApplied: String?
    ): OrderEntity {
        val totalRawPrice = cartItems.sumOf { it.product.price * it.cartItem.quantity }
        val finalAmount = (totalRawPrice + shippingFee - discountAmount).coerceAtLeast(0L)

        val orderItems = cartItems.map {
            OrderItemDetail(
                productId = it.product.id,
                productTitle = it.product.title,
                quantity = it.cartItem.quantity,
                unitPrice = it.product.price,
                colorName = it.cartItem.selectedColorName,
                modelName = it.cartItem.selectedModel,
                imageUrl = JsonHelper.parseStringList(it.product.imagesJson).firstOrNull() ?: ""
            )
        }

        val randomCode = 100000 + Random.nextInt(900000)
        val orderNum = "SNA-$randomCode"
        val trackingNum = "TRK-${System.currentTimeMillis().toString().takeLast(8)}"

        val orderEntity = OrderEntity(
            orderNumber = orderNum,
            itemsJson = JsonHelper.toJsonOrderItems(orderItems),
            totalAmount = totalRawPrice,
            discountAmount = discountAmount,
            shippingFee = shippingFee,
            finalAmount = finalAmount,
            status = "PROCESSING",
            statusTitle = "درحال پردازش و آماده‌سازی",
            shippingAddress = "${address.city}، ${address.fullAddress} (کد پستی: ${address.postalCode})",
            recipientName = address.recipientName,
            recipientPhone = address.recipientPhone,
            shippingMethod = shippingMethod,
            paymentMethod = paymentMethod,
            trackingCode = trackingNum,
            createdAt = System.currentTimeMillis()
        )

        val orderId = dao.insertOrder(orderEntity)
        if (!discountCodeApplied.isNullOrBlank()) {
            dao.markDiscountCodeUsed(discountCodeApplied)
        }

        // Deduct stock
        cartItems.forEach {
            val remainingStock = (it.product.stockCount - it.cartItem.quantity).coerceAtLeast(0)
            dao.updateProductStock(it.product.id, remainingStock)
        }

        // Clear user's cart
        dao.clearCart()

        return orderEntity.copy(id = orderId)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String, statusTitle: String) {
        dao.updateOrderStatus(orderId, status, statusTitle)
    }

    // User Profile & Addresses
    suspend fun saveUserAddress(newAddress: UserAddress) {
        val profile = dao.getUserProfile().firstOrNull() ?: SampleData.getInitialUserProfile()
        val currentAddresses = JsonHelper.parseAddresses(profile.addressesJson).toMutableList()
        val existingIndex = currentAddresses.indexOfFirst { it.id == newAddress.id }
        if (existingIndex >= 0) {
            currentAddresses[existingIndex] = newAddress
        } else {
            currentAddresses.add(newAddress)
        }
        val updatedJson = JsonHelper.toJsonAddresses(currentAddresses)
        dao.updateUserAddresses(updatedJson)
    }

    suspend fun deleteUserAddress(addressId: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        val currentAddresses = JsonHelper.parseAddresses(profile.addressesJson).filter { it.id != addressId }
        val updatedJson = JsonHelper.toJsonAddresses(currentAddresses)
        dao.updateUserAddresses(updatedJson)
    }

    suspend fun updateWallet(newBalance: Long) {
        dao.updateWalletBalance(newBalance)
    }

    suspend fun updateProfile(name: String, email: String, phone: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: SampleData.getInitialUserProfile()
        val updated = profile.copy(fullName = name, email = email, phone = phone)
        dao.insertOrUpdateProfile(updated)
    }

    // Admin Operations
    suspend fun addProduct(product: ProductEntity): Long = dao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = dao.updateProduct(product)
    suspend fun deleteProduct(productId: Long) = dao.deleteProductById(productId)
    suspend fun updateProductPrice(productId: Long, price: Long, origPrice: Long, discount: Int) = dao.updateProductPrice(productId, price, origPrice, discount)
    suspend fun updateProductStock(productId: Long, stock: Int) = dao.updateProductStock(productId, stock)

    suspend fun addCategory(category: CategoryEntity) = dao.insertCategory(category)
    suspend fun deleteCategory(categoryId: String) = dao.deleteCategoryById(categoryId)

    suspend fun addDiscountCode(code: DiscountCodeEntity) = dao.insertDiscountCode(code)
    suspend fun deleteDiscountCode(id: Long) = dao.deleteDiscountCode(id)

    suspend fun addBanner(banner: PromoBannerEntity) = dao.insertBanner(banner)
    suspend fun deleteBanner(id: Long) = dao.deleteBannerById(id)

    suspend fun validateDiscountCode(codeStr: String, currentOrderTotal: Long): Pair<Boolean, DiscountCodeEntity?> {
        val code = dao.getDiscountCode(codeStr.trim().uppercase()) ?: return Pair(false, null)
        if (code.isUsed) return Pair(false, null)
        if (currentOrderTotal < code.minOrder) return Pair(false, null)
        return Pair(true, code)
    }

    suspend fun seedIfEmpty() {
        val count = dao.getProductsCount()
        if (count == 0) {
            dao.insertCategories(SampleData.getInitialCategories())
            dao.insertBanners(SampleData.getInitialBanners())
            dao.insertProducts(SampleData.getInitialProducts())
            dao.insertOrUpdateProfile(SampleData.getInitialUserProfile())
            SampleData.getInitialDiscountCodes().forEach { dao.insertDiscountCode(it) }
            SampleData.getInitialReviews().forEach { dao.insertReview(it) }
        }
    }
}
