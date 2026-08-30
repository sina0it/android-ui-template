package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PersianUtils
import com.example.ui.components.ProductCard
import com.example.ui.components.SinaKalaHeader
import com.example.ui.theme.SinaDark
import com.example.ui.theme.SinaGold
import com.example.ui.theme.SinaNavy
import com.example.ui.theme.SinaRed
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SinaKalaViewModel
import com.example.ui.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: SinaKalaViewModel,
    modifier: Modifier = Modifier
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val cartItems by viewModel.cartWithProducts.collectAsState()
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()
    val comparisonIds by viewModel.comparisonProductIds.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val wishListIds = wishlistProducts.map { it.id }.toSet()
    val cartCount = cartItems.sumOf { it.cartItem.quantity }

    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Header
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

        // Filter Bar & Results Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filter Button
                Button(
                    onClick = { showFilterSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SinaNavy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "فیلترها",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                if (filterState.categoryId != null || filterState.selectedBrand != null || filterState.inStockOnly || filterState.amazingDealsOnly || filterState.searchQuery.isNotBlank()) {
                    OutlinedButton(
                        onClick = { viewModel.clearFilters() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SinaRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "حذف فیلترها",
                            fontSize = 11.sp,
                            color = SinaRed
                        )
                    }
                }
            }

            // Results count
            Text(
                text = "${PersianUtils.toPersianDigits(filteredProducts.size)} کالا",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Active Search/Category tag indicator if present
        if (filterState.searchQuery.isNotBlank() || filterState.categoryId != null) {
            val catName = allCategories.find { it.id == filterState.categoryId }?.name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نمایش نتایج برای: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (filterState.searchQuery.isNotBlank()) "«${filterState.searchQuery}»" else (catName ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = SinaRed
                )
            }
        }

        // Sort Tabs
        ScrollableTabRow(
            selectedTabIndex = SortOrder.values().indexOf(filterState.sortOrder),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent
        ) {
            SortOrder.values().forEach { sort ->
                val isSelected = filterState.sortOrder == sort
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.updateSortOrder(sort) },
                    text = {
                        Text(
                            text = sort.title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SinaRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Product Grid (2 Columns)
        if (filteredProducts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SinaRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = SinaRed,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "کالایی با مشخصات انتخابی یافت نشد!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "لطفاً فیلترها را تغییر داده یا واژه جستجوی دیگری را امتحان کنید.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.clearFilters() },
                    colors = ButtonDefaults.buttonColors(containerColor = SinaRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("مشاهده همه محصولات")
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        isInWishlist = wishListIds.contains(product.id),
                        onProductClick = { viewModel.navigateToProduct(product.id) },
                        onAddToCart = { viewModel.addToCart(product.id) },
                        onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                        onToggleCompare = { viewModel.toggleComparison(product.id) },
                        isCompared = comparisonIds.contains(product.id),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فیلتر پیشرفته محصولات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "پاکسازی همه",
                        color = SinaRed,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.clearFilters() }
                            .padding(4.dp)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // In stock switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فقط کالاهای موجود در انبار",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = filterState.inStockOnly,
                        onCheckedChange = { viewModel.updateInStockFilter(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = SinaRed, checkedTrackColor = SinaRed.copy(alpha = 0.4f))
                    )
                }

                // Amazing Deals switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فقط تخفیف‌های شگفت‌انگیز",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = filterState.amazingDealsOnly,
                        onCheckedChange = { viewModel.updateAmazingDealsFilter(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = SinaRed, checkedTrackColor = SinaRed.copy(alpha = 0.4f))
                    )
                }

                // Category Chips
                Column {
                    Text(
                        text = "دسته‌بندی:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = filterState.categoryId == null,
                                onClick = { viewModel.updateCategoryFilter(null) },
                                label = { Text("همه دسته‌ها") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SinaRed, selectedLabelColor = Color.White)
                            )
                        }
                        items(allCategories) { cat ->
                            FilterChip(
                                selected = filterState.categoryId == cat.id,
                                onClick = { viewModel.updateCategoryFilter(cat.id) },
                                label = { Text(cat.name) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SinaRed, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }

                // Brand Chips
                val brands = listOf("اپل", "سامسونگ", "سونی", "ایسوس", "نایکی", "دایسون", "بوش", "مارشال", "ری‌بن", "دلونگی")
                Column {
                    Text(
                        text = "برند کالا:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = filterState.selectedBrand == null,
                                onClick = { viewModel.updateBrandFilter(null) },
                                label = { Text("همه برندها") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SinaRed, selectedLabelColor = Color.White)
                            )
                        }
                        items(brands) { b ->
                            FilterChip(
                                selected = filterState.selectedBrand == b,
                                onClick = { viewModel.updateBrandFilter(b) },
                                label = { Text(b) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SinaRed, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SinaRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "مشاهده ${PersianUtils.toPersianDigits(filteredProducts.size)} کالا",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
