package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.AmazingOfferSection
import com.example.ui.components.BrandLogosSection
import com.example.ui.components.CategoryGrid
import com.example.ui.components.HeroBannerSlider
import com.example.ui.components.PersianUtils
import com.example.ui.components.ProductCard
import com.example.ui.components.ServicePerksSection
import com.example.ui.components.SinaKalaFooter
import com.example.ui.components.SinaKalaHeader
import com.example.ui.theme.SinaDark
import com.example.ui.theme.SinaGold
import com.example.ui.theme.SinaNavy
import com.example.ui.theme.SinaRed
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SinaKalaViewModel

@Composable
fun HomeScreen(
    viewModel: SinaKalaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val amazingOffers by viewModel.amazingOffers.collectAsState()
    val bestSellers by viewModel.bestSellers.collectAsState()
    val newArrivals by viewModel.newArrivals.collectAsState()
    val sinaKalaPicks by viewModel.sinaKalaPicks.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val allBanners by viewModel.allBanners.collectAsState()
    val cartItems by viewModel.cartWithProducts.collectAsState()
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val comparisonIds by viewModel.comparisonProductIds.collectAsState()

    val wishListIds = wishlistProducts.map { it.id }.toSet()
    val cartCount = cartItems.sumOf { it.cartItem.quantity }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Top Header
        SinaKalaHeader(
            cartItemCount = cartCount,
            wishlistItemCount = wishlistProducts.size,
            isDarkTheme = isDarkTheme,
            onSearch = { query -> viewModel.searchAndNavigate(query) },
            onNavigateCart = { viewModel.navigateTo(Screen.CART) },
            onNavigateWishlist = { viewModel.navigateTo(Screen.WISHLIST) },
            onNavigateAdmin = { viewModel.navigateTo(Screen.ADMIN) },
            onNavigateHome = { viewModel.navigateTo(Screen.HOME) },
            onToggleTheme = { viewModel.toggleTheme() },
            onSelectProduct = { productId -> viewModel.navigateToProduct(productId) },
            allProducts = allProducts
        )

        // Scrollable Home Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Hero Promo Banners Carousel
            item {
                Spacer(modifier = Modifier.height(10.dp))
                HeroBannerSlider(
                    banners = allBanners,
                    onBannerClick = { catId -> viewModel.navigateToCategory(catId) }
                )
            }

            // 2. Category Cards Row
            item {
                CategoryGrid(
                    categories = allCategories,
                    selectedCategoryId = null,
                    onCategoryClick = { catId -> viewModel.navigateToCategory(catId) }
                )
            }

            // 3. Wonder Deals (Amazing Offers with Countdown Timer)
            if (amazingOffers.isNotEmpty()) {
                item {
                    AmazingOfferSection(
                        products = amazingOffers,
                        countdownSeconds = countdownSeconds,
                        wishlistProductIds = wishListIds,
                        onProductClick = { productId -> viewModel.navigateToProduct(productId) },
                        onAddToCart = { productId -> viewModel.addToCart(productId) },
                        onToggleWishlist = { productId -> viewModel.toggleWishlist(productId) },
                        onViewAllClick = {
                            viewModel.updateAmazingDealsFilter(true)
                            viewModel.navigateTo(Screen.CATALOG)
                        }
                    )
                }
            }

            // 4. Best Sellers (پرفروش‌ترین محصولات)
            if (bestSellers.isNotEmpty()) {
                item {
                    ProductSectionHeader(
                        title = "پرفروش‌ترین محصولات بازار",
                        subtitle = "محبوب‌ترین انتخاب‌های کاربران سینا کالا",
                        icon = Icons.Default.Whatshot,
                        iconTint = SinaRed,
                        onSeeAllClick = { viewModel.navigateTo(Screen.CATALOG) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bestSellers) { product ->
                            ProductCard(
                                product = product,
                                isInWishlist = wishListIds.contains(product.id),
                                onProductClick = { viewModel.navigateToProduct(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) },
                                onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                                onToggleCompare = { viewModel.toggleComparison(product.id) },
                                isCompared = comparisonIds.contains(product.id),
                                modifier = Modifier.width(210.dp)
                            )
                        }
                    }
                }
            }

            // 5. SinaKala Curated Picks (پیشنهاد سینا کالا)
            if (sinaKalaPicks.isNotEmpty()) {
                item {
                    ProductSectionHeader(
                        title = "منتخب و پیشنهاد سینا کالا",
                        subtitle = "کالاهای برگزیده با تضمین بالاترین کیفیت و رضایت",
                        icon = Icons.Default.Diamond,
                        iconTint = SinaGold,
                        onSeeAllClick = { viewModel.navigateTo(Screen.CATALOG) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sinaKalaPicks) { product ->
                            ProductCard(
                                product = product,
                                isInWishlist = wishListIds.contains(product.id),
                                onProductClick = { viewModel.navigateToProduct(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) },
                                onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                                onToggleCompare = { viewModel.toggleComparison(product.id) },
                                isCompared = comparisonIds.contains(product.id),
                                modifier = Modifier.width(210.dp)
                            )
                        }
                    }
                }
            }

            // 6. New Arrivals (جدیدترین محصولات)
            if (newArrivals.isNotEmpty()) {
                item {
                    ProductSectionHeader(
                        title = "جدیدترین محصولات وارداتی",
                        subtitle = "تازه‌ترین رونمایی‌های دنیای تکنولوژی و مد",
                        icon = Icons.Default.TrendingUp,
                        iconTint = Color(0xFF06B6D4),
                        onSeeAllClick = { viewModel.navigateTo(Screen.CATALOG) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(newArrivals) { product ->
                            ProductCard(
                                product = product,
                                isInWishlist = wishListIds.contains(product.id),
                                onProductClick = { viewModel.navigateToProduct(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) },
                                onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                                onToggleCompare = { viewModel.toggleComparison(product.id) },
                                isCompared = comparisonIds.contains(product.id),
                                modifier = Modifier.width(210.dp)
                            )
                        }
                    }
                }
            }

            // 7. Popular Brands
            item {
                BrandLogosSection(
                    onBrandClick = { brandName ->
                        viewModel.updateBrandFilter(brandName)
                        viewModel.navigateTo(Screen.CATALOG)
                    }
                )
            }

            // 8. Service Perks & Trust Guarantees
            item {
                ServicePerksSection()
            }

            // 9. Full Rich Footer
            item {
                SinaKalaFooter()
            }
        }
    }
}

@Composable
fun ProductSectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSeeAllClick() }
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مشاهده همه",
                style = MaterialTheme.typography.labelSmall,
                color = SinaRed,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = SinaRed,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
