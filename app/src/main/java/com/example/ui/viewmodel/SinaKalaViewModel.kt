package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SampleData
import com.example.data.model.CartItemWithProduct
import com.example.data.model.CategoryEntity
import com.example.data.model.DiscountCodeEntity
import com.example.data.model.JsonHelper
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PromoBannerEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserAddress
import com.example.data.model.UserProfileEntity
import com.example.data.repository.SinaKalaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    CATALOG,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    ORDER_SUCCESS,
    PROFILE,
    ORDERS_LIST,
    WISHLIST,
    COMPARE,
    ADMIN
}

enum class SortOrder(val title: String) {
    POPULAR("محبوب‌ترین"),
    NEWEST("جدیدترین"),
    BEST_SELLER("پرفروش‌ترین"),
    CHEAPEST("ارزان‌ترین"),
    EXPENSIVE("گران‌ترین"),
    DISCOUNT("بیشترین تخفیف")
}

data class FilterState(
    val categoryId: String? = null,
    val selectedBrand: String? = null,
    val minPrice: Long = 0L,
    val maxPrice: Long = 300000000L,
    val inStockOnly: Boolean = false,
    val amazingDealsOnly: Boolean = false,
    val sortOrder: SortOrder = SortOrder.POPULAR,
    val searchQuery: String = ""
)

class SinaKalaViewModel(private val repository: SinaKalaRepository) : ViewModel() {

    // Current Screen & Navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedProductId = MutableStateFlow<Long?>(null)
    val selectedProductId: StateFlow<Long?> = _selectedProductId.asStateFlow()

    private val _lastCreatedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastCreatedOrder: StateFlow<OrderEntity?> = _lastCreatedOrder.asStateFlow()

    // Comparison list (Product IDs)
    private val _comparisonProductIds = MutableStateFlow<List<Long>>(emptyList())
    val comparisonProductIds: StateFlow<List<Long>> = _comparisonProductIds.asStateFlow()

    // Toast/Snackbar events
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    // Dark / Light Theme
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Filter & Search State
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // Applied coupon
    private val _appliedCoupon = MutableStateFlow<DiscountCodeEntity?>(null)
    val appliedCoupon: StateFlow<DiscountCodeEntity?> = _appliedCoupon.asStateFlow()

    // Selected Checkout details
    private val _selectedAddress = MutableStateFlow<UserAddress?>(null)
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress.asStateFlow()

    private val _selectedShippingMethod = MutableStateFlow("ارسال اکسپرس سینا کالا (تحویل امروز)")
    val selectedShippingMethod: StateFlow<String> = _selectedShippingMethod.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("درگاه بانکی امن شتاب (سامان / ملت)")
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    // Realtime Countdown for Amazing Offers
    private val _countdownSeconds = MutableStateFlow(54230L)
    val countdownSeconds: StateFlow<Long> = _countdownSeconds.asStateFlow()

    init {
        // Seed initial data if DB is empty
        viewModelScope.launch {
            repository.seedIfEmpty()
        }

        // Live Countdown ticker
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _countdownSeconds.value = if (_countdownSeconds.value > 0) _countdownSeconds.value - 1 else 86400L
            }
        }
    }

    // Repository Flows
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val amazingOffers: StateFlow<List<ProductEntity>> = repository.amazingOffers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val specialOffers: StateFlow<List<ProductEntity>> = repository.specialOffers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSellers: StateFlow<List<ProductEntity>> = repository.bestSellers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newArrivals: StateFlow<List<ProductEntity>> = repository.newArrivals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sinaKalaPicks: StateFlow<List<ProductEntity>> = repository.sinaKalaPicks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBanners: StateFlow<List<PromoBannerEntity>> = repository.allBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartWithProducts: StateFlow<List<CartItemWithProduct>> = repository.cartWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistProducts: StateFlow<List<ProductEntity>> = repository.wishlistWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyViewed: StateFlow<List<ProductEntity>> = repository.recentlyViewedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDiscountCodes: StateFlow<List<DiscountCodeEntity>> = repository.allDiscountCodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected product flow
    val currentSelectedProduct: StateFlow<ProductEntity?> = combine(
        allProducts,
        _selectedProductId
    ) { products, id ->
        if (id == null) null else products.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered Products Catalog
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        _filterState
    ) { products, filter ->
        var list = products

        if (!filter.searchQuery.isBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.titleEn.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.categoryId.lowercase().contains(q)
            }
        }

        if (filter.categoryId != null) {
            list = list.filter { it.categoryId == filter.categoryId }
        }

        if (filter.selectedBrand != null) {
            list = list.filter { it.brand.contains(filter.selectedBrand) }
        }

        if (filter.inStockOnly) {
            list = list.filter { it.stockCount > 0 }
        }

        if (filter.amazingDealsOnly) {
            list = list.filter { it.isAmazingOffer }
        }

        list = list.filter { it.price in filter.minPrice..filter.maxPrice }

        when (filter.sortOrder) {
            SortOrder.POPULAR -> list.sortedByDescending { it.rating * 100 + it.ratingCount }
            SortOrder.NEWEST -> list.sortedByDescending { it.id }
            SortOrder.BEST_SELLER -> list.sortedByDescending { it.salesCount }
            SortOrder.CHEAPEST -> list.sortedBy { it.price }
            SortOrder.EXPENSIVE -> list.sortedByDescending { it.price }
            SortOrder.DISCOUNT -> list.sortedByDescending { it.discountPercent }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart calculations
    val cartTotalRaw: StateFlow<Long> = cartWithProducts.combine(_currentScreen) { items, _ ->
        items.sumOf { it.product.price * it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val cartTotalOriginal: StateFlow<Long> = cartWithProducts.combine(_currentScreen) { items, _ ->
        items.sumOf { it.product.originalPrice * it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val cartTotalDiscountProduct: StateFlow<Long> = combine(cartTotalOriginal, cartTotalRaw) { orig, raw ->
        (orig - raw).coerceAtLeast(0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val calculatedDiscountAmount: StateFlow<Long> = combine(
        cartTotalRaw,
        _appliedCoupon
    ) { rawTotal, coupon ->
        if (coupon == null) 0L
        else {
            val percentDiscount = (rawTotal * coupon.percent) / 100
            percentDiscount.coerceAtMost(coupon.maxDiscount)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val shippingFee: StateFlow<Long> = cartTotalRaw.combine(_currentScreen) { total, _ ->
        if (total >= 10000000L || total == 0L) 0L else 49000L // Free shipping over 10M
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val cartFinalPayable: StateFlow<Long> = combine(
        cartTotalRaw,
        calculatedDiscountAmount,
        shippingFee
    ) { raw, couponDiscount, ship ->
        if (raw == 0L) 0L else (raw - couponDiscount + ship).coerceAtLeast(0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Navigation Actions
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateToProduct(productId: Long) {
        _selectedProductId.value = productId
        _currentScreen.value = Screen.PRODUCT_DETAIL
        viewModelScope.launch {
            repository.recordRecentlyViewed(productId)
        }
    }

    fun navigateToCategory(categoryId: String) {
        _filterState.value = _filterState.value.copy(
            categoryId = categoryId,
            searchQuery = "",
            selectedBrand = null
        )
        _currentScreen.value = Screen.CATALOG
    }

    fun searchAndNavigate(query: String) {
        _filterState.value = _filterState.value.copy(
            searchQuery = query,
            categoryId = null
        )
        _currentScreen.value = Screen.CATALOG
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Filter controls
    fun updateSortOrder(sort: SortOrder) {
        _filterState.value = _filterState.value.copy(sortOrder = sort)
    }

    fun updateCategoryFilter(catId: String?) {
        _filterState.value = _filterState.value.copy(categoryId = catId)
    }

    fun updateBrandFilter(brand: String?) {
        _filterState.value = _filterState.value.copy(selectedBrand = brand)
    }

    fun updateInStockFilter(inStock: Boolean) {
        _filterState.value = _filterState.value.copy(inStockOnly = inStock)
    }

    fun updateAmazingDealsFilter(amazingOnly: Boolean) {
        _filterState.value = _filterState.value.copy(amazingDealsOnly = amazingOnly)
    }

    fun updatePriceRange(min: Long, max: Long) {
        _filterState.value = _filterState.value.copy(minPrice = min, maxPrice = max)
    }

    fun clearFilters() {
        _filterState.value = FilterState()
    }

    // Cart operations
    fun addToCart(
        productId: Long,
        quantity: Int = 1,
        colorName: String = "",
        colorHex: String = "#000000",
        model: String = ""
    ) {
        viewModelScope.launch {
            val success = repository.addToCart(productId, quantity, colorName, colorHex, model)
            if (success) {
                _toastMessage.emit("محصول با موفقیت به سبد خرید اضافه شد.")
            }
        }
    }

    fun instantBuy(productId: Long, colorName: String = "", colorHex: String = "#000000", model: String = "") {
        viewModelScope.launch {
            repository.addToCart(productId, 1, colorName, colorHex, model)
            _currentScreen.value = Screen.CART
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
            _toastMessage.emit("محصول از سبد خرید حذف شد.")
        }
    }

    fun applyCouponCode(code: String) {
        viewModelScope.launch {
            val rawTotal = cartTotalRaw.value
            val (valid, coupon) = repository.validateDiscountCode(code, rawTotal)
            if (valid && coupon != null) {
                _appliedCoupon.value = coupon
                _toastMessage.emit("کد تخفیف «${coupon.code}» با موفقیت اعمال گردید.")
            } else {
                _toastMessage.emit("کد تخفیف نامعتبر است، منقضی شده یا حداقل خرید رعایت نشده است.")
            }
        }
    }

    fun removeAppliedCoupon() {
        _appliedCoupon.value = null
        viewModelScope.launch {
            _toastMessage.emit("کد تخفیف حذف شد.")
        }
    }

    // Wishlist operations
    fun toggleWishlist(productId: Long) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
            _toastMessage.emit("لیست علاقه‌مندی‌ها بروزرسانی شد.")
        }
    }

    fun isProductInWishlist(productId: Long): StateFlow<Boolean> {
        return repository.isProductInWishlist(productId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }

    // Comparison operations
    fun toggleComparison(productId: Long) {
        val current = _comparisonProductIds.value.toMutableList()
        if (current.contains(productId)) {
            current.remove(productId)
            _comparisonProductIds.value = current
            viewModelScope.launch { _toastMessage.emit("محصول از لیست مقایسه حذف شد.") }
        } else {
            if (current.size >= 3) {
                viewModelScope.launch { _toastMessage.emit("حداکثر ۳ محصول را می‌توانید مقایسه کنید.") }
            } else {
                current.add(productId)
                _comparisonProductIds.value = current
                viewModelScope.launch { _toastMessage.emit("محصول به لیست مقایسه اضافه شد.") }
            }
        }
    }

    fun clearComparison() {
        _comparisonProductIds.value = emptyList()
    }

    // Checkout execution
    fun setSelectedAddress(address: UserAddress) {
        _selectedAddress.value = address
    }

    fun setSelectedShippingMethod(method: String) {
        _selectedShippingMethod.value = method
    }

    fun setSelectedPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun completeOrder() {
        viewModelScope.launch {
            val items = cartWithProducts.value
            if (items.isEmpty()) {
                _toastMessage.emit("سبد خرید شما خالی است!")
                return@launch
            }

            val profile = userProfile.value ?: SampleData.getInitialUserProfile()
            val addresses = JsonHelper.parseAddresses(profile.addressesJson)
            val address = _selectedAddress.value ?: addresses.firstOrNull() ?: UserAddress(
                id = "def",
                title = "آدرس پیش‌فرض",
                fullAddress = "تهران، خیابان ولیعصر، برج سینا کالا",
                city = "تهران",
                postalCode = "۱۹۶۸۷۱۴۵۲۳",
                recipientName = profile.fullName,
                recipientPhone = profile.phone,
                isDefault = true
            )

            val order = repository.createOrder(
                cartItems = items,
                address = address,
                shippingMethod = _selectedShippingMethod.value,
                shippingFee = shippingFee.value,
                paymentMethod = _selectedPaymentMethod.value,
                discountAmount = calculatedDiscountAmount.value,
                discountCodeApplied = _appliedCoupon.value?.code
            )

            _lastCreatedOrder.value = order
            _appliedCoupon.value = null
            _currentScreen.value = Screen.ORDER_SUCCESS
            _toastMessage.emit("سفارش شما با موفقیت ثبت شد و در صف ارسال قرار گرفت.")
        }
    }

    // Profile & Addresses
    fun saveAddress(address: UserAddress) {
        viewModelScope.launch {
            repository.saveUserAddress(address)
            _toastMessage.emit("آدرس با موفقیت ذخیره شد.")
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            repository.deleteUserAddress(addressId)
            _toastMessage.emit("آدرس حذف شد.")
        }
    }

    fun topUpWallet(amount: Long) {
        viewModelScope.launch {
            val current = userProfile.value?.walletBalance ?: 0L
            repository.updateWallet(current + amount)
            _toastMessage.emit("کیف پول شما با موفقیت شارژ شد.")
        }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email, phone)
            _toastMessage.emit("اطلاعات کاربری با موفقیت ویرایش گردید.")
        }
    }

    // Reviews
    fun addReview(productId: Long, userName: String, rating: Double, comment: String, pros: List<String>, cons: List<String>) {
        viewModelScope.launch {
            repository.addReview(productId, userName, rating, comment, pros, cons)
            _toastMessage.emit("نظر شما با موفقیت ثبت شد و پس از تایید نمایش داده خواهد شد.")
        }
    }

    fun getReviewsForProduct(productId: Long): StateFlow<List<ReviewEntity>> {
        return repository.getReviewsForProduct(productId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Admin Panel Actions
    fun adminAddProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.addProduct(product)
            _toastMessage.emit("محصول جدید با موفقیت به انبار سینا کالا اضافه شد.")
        }
    }

    fun adminUpdateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
            _toastMessage.emit("مشخصات محصول با موفقیت بروزرسانی شد.")
        }
    }

    fun adminDeleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            _toastMessage.emit("محصول با موفقیت حذف گردید.")
        }
    }

    fun adminUpdateOrderStatus(orderId: Long, status: String, statusTitle: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, statusTitle)
            _toastMessage.emit("وضعیت سفارش تغییر یافت.")
        }
    }

    fun adminAddDiscountCode(code: DiscountCodeEntity) {
        viewModelScope.launch {
            repository.addDiscountCode(code)
            _toastMessage.emit("کد تخفیف جدید ایجاد شد.")
        }
    }

    fun adminDeleteDiscountCode(id: Long) {
        viewModelScope.launch {
            repository.deleteDiscountCode(id)
            _toastMessage.emit("کد تخفیف حذف شد.")
        }
    }

    fun adminAddCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.addCategory(category)
            _toastMessage.emit("دسته‌بندی جدید ثبت شد.")
        }
    }

    fun adminDeleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            _toastMessage.emit("دسته‌بندی حذف شد.")
        }
    }

    fun adminAddBanner(banner: PromoBannerEntity) {
        viewModelScope.launch {
            repository.addBanner(banner)
            _toastMessage.emit("بنر تبلیغاتی جدید افزوده شد.")
        }
    }

    fun adminDeleteBanner(id: Long) {
        viewModelScope.launch {
            repository.deleteBanner(id)
            _toastMessage.emit("بنر تبلیغاتی حذف شد.")
        }
    }
}

class SinaKalaViewModelFactory(private val repository: SinaKalaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SinaKalaViewModel::class.java)) {
            return SinaKalaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
