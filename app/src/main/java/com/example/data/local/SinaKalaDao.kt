package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.DiscountCodeEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PromoBannerEntity
import com.example.data.model.RecentlyViewedEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SinaKalaDao {

    // --- PRODUCTS ---
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductDirect(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE isAmazingOffer = 1 ORDER BY discountPercent DESC")
    fun getAmazingOffers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isSpecialOffer = 1 ORDER BY id DESC")
    fun getSpecialOffers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isBestSeller = 1 ORDER BY salesCount DESC")
    fun getBestSellers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isNewArrival = 1 ORDER BY id DESC")
    fun getNewArrivals(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isSinaKalaPick = 1 ORDER BY rating DESC")
    fun getSinaKalaPicks(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY id DESC")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' OR titleEn LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("UPDATE products SET stockCount = :newStock WHERE id = :id")
    suspend fun updateProductStock(id: Long, newStock: Int)

    @Query("UPDATE products SET price = :price, originalPrice = :originalPrice, discountPercent = :discountPercent WHERE id = :id")
    suspend fun updateProductPrice(id: Long, price: Long, originalPrice: Long, discountPercent: Int)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int

    // --- CATEGORIES ---
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    // --- CART ---
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND selectedColorName = :colorName AND selectedModel = :model LIMIT 1")
    suspend fun getCartItemByProductAndVariants(productId: Long, colorName: String, model: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateCartItemQuantity(id: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- WISHLIST ---
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getWishlistItems(): Flow<List<WishlistItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isProductInWishlist(productId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun deleteWishlistByProductId(productId: Long)

    // --- ORDERS ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: Long): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status, statusTitle = :statusTitle WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, status: String, statusTitle: String)

    // --- USER PROFILE ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET walletBalance = :newBalance WHERE id = 1")
    suspend fun updateWalletBalance(newBalance: Long)

    @Query("UPDATE user_profile SET addressesJson = :addressesJson WHERE id = 1")
    suspend fun updateUserAddresses(addressesJson: String)

    // --- REVIEWS ---
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY id DESC")
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReviewById(id: Long)

    // --- DISCOUNT CODES ---
    @Query("SELECT * FROM discount_codes ORDER BY id DESC")
    fun getAllDiscountCodes(): Flow<List<DiscountCodeEntity>>

    @Query("SELECT * FROM discount_codes WHERE code = :code LIMIT 1")
    suspend fun getDiscountCode(code: String): DiscountCodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscountCode(code: DiscountCodeEntity)

    @Query("UPDATE discount_codes SET isUsed = 1 WHERE code = :code")
    suspend fun markDiscountCodeUsed(code: String)

    @Query("DELETE FROM discount_codes WHERE id = :id")
    suspend fun deleteDiscountCode(id: Long)

    // --- BANNERS ---
    @Query("SELECT * FROM promo_banners ORDER BY id ASC")
    fun getAllBanners(): Flow<List<PromoBannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: PromoBannerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<PromoBannerEntity>)

    @Query("DELETE FROM promo_banners WHERE id = :id")
    suspend fun deleteBannerById(id: Long)

    // --- RECENTLY VIEWED ---
    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT 10")
    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(item: RecentlyViewedEntity)
}
