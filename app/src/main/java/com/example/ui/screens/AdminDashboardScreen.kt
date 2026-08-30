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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiscountCodeEntity
import com.example.data.model.JsonHelper
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PromoBannerEntity
import com.example.ui.components.PersianUtils
import com.example.ui.theme.SinaDark
import com.example.ui.theme.SinaGold
import com.example.ui.theme.SinaNavy
import com.example.ui.theme.SinaRed
import com.example.ui.theme.SinaSuccess
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SinaKalaViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: SinaKalaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allCoupons by viewModel.allDiscountCodes.collectAsState()
    val allBanners by viewModel.allBanners.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCouponDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }

    val totalSalesRevenue = remember(allOrders) {
        allOrders.sumOf { it.finalAmount }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "داشبورد مدیریت سینا کالا",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SinaNavy)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "ADMIN v2.0", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4 KPI Metric Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricCard(
                        title = "مجموع فروش کل",
                        value = PersianUtils.formatPriceToman(totalSalesRevenue),
                        icon = Icons.Default.Paid,
                        accentColor = SinaGold,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricCard(
                        title = "تعداد سفارشات",
                        value = "${PersianUtils.toPersianDigits(allOrders.size)} سفارش",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = SinaRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricCard(
                        title = "تنوع کالای انبار",
                        value = "${PersianUtils.toPersianDigits(allProducts.size)} محصول",
                        icon = Icons.Default.Inventory2,
                        accentColor = SinaNavy,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricCard(
                        title = "کدهای تخفیف",
                        value = "${PersianUtils.toPersianDigits(allCoupons.size)} کد فعال",
                        icon = Icons.Default.Discount,
                        accentColor = SinaSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sales Performance Visual Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = SinaNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "نمودار فروش هفتگی (میلیون تومان)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "+۲۴٪ رشد",
                            color = SinaSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("شنبه", "۱شنبه", "۲شنبه", "۳شنبه", "۴شنبه", "۵شنبه", "جمعه")
                    val values = listOf(45, 68, 92, 115, 140, 185, 210) // simulated in millions

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            val heightFraction = (values[idx] / 220f).coerceIn(0.1f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = PersianUtils.toPersianDigits(values[idx]),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height((100 * heightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (idx == 6) SinaRed else SinaNavy.copy(alpha = 0.75f))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Admin Tabs Row
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                divider = {},
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("کالاها (${PersianUtils.toPersianDigits(allProducts.size)})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("سفارش‌ها (${PersianUtils.toPersianDigits(allOrders.size)})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("کدهای تخفیف", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("بنرها", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab Contents
        when (selectedTab) {
            0 -> {
                // Products Management
                item {
                    Button(
                        onClick = { showAddProductDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SinaRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن کالای جدید به انبار", fontWeight = FontWeight.Bold)
                    }
                }

                items(allProducts) { product ->
                    AdminProductRow(
                        product = product,
                        onEdit = { productToEdit = product },
                        onDelete = { viewModel.adminDeleteProduct(product.id) }
                    )
                }
            }

            1 -> {
                // Orders Management
                if (allOrders.isEmpty()) {
                    item {
                        Text(
                            text = "سفارشی برای مدیریت وجود ندارد.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(allOrders) { order ->
                        AdminOrderRow(
                            order = order,
                            onStatusChange = { newStatus, newTitle ->
                                viewModel.adminUpdateOrderStatus(order.id, newStatus, newTitle)
                            }
                        )
                    }
                }
            }

            2 -> {
                // Coupons Management
                item {
                    Button(
                        onClick = { showAddCouponDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SinaNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ایجاد کد تخفیف جدید", fontWeight = FontWeight.Bold)
                    }
                }

                items(allCoupons) { coupon ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "کد: ${coupon.code}", fontWeight = FontWeight.Bold, color = SinaNavy)
                                Text(
                                    text = "${PersianUtils.formatDiscount(coupon.percent)} تخفیف | سقف: ${PersianUtils.formatPriceToman(coupon.maxDiscount)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.adminDeleteDiscountCode(coupon.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            3 -> {
                // Promo Banners
                items(allBanners) { banner ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = banner.title, fontWeight = FontWeight.Bold)
                                Text(text = banner.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { viewModel.adminDeleteBanner(banner.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Product Modal Dialog
    if (showAddProductDialog) {
        var title by remember { mutableStateOf("") }
        var titleEn by remember { mutableStateOf("") }
        var brand by remember { mutableStateOf("") }
        var categoryId by remember { mutableStateOf("mobile") }
        var priceStr by remember { mutableStateOf("45000000") }
        var origPriceStr by remember { mutableStateOf("49000000") }
        var discountStr by remember { mutableStateOf("8") }
        var stockStr by remember { mutableStateOf("15") }
        var desc by remember { mutableStateOf("") }
        var isAmazing by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("افزودن کالای جدید به فروشگاه", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("عنوان فارسی کالا") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = titleEn,
                            onValueChange = { titleEn = it },
                            label = { Text("عنوان انگلیسی / مدل") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("برند کالا") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("قیمت فروش (تومان)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = origPriceStr,
                            onValueChange = { origPriceStr = it },
                            label = { Text("قیمت اولیه قبل از تخفیف (تومان)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = discountStr,
                            onValueChange = { discountStr = it },
                            label = { Text("درصد تخفیف") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("موجودی در انبار") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("توضیحات کالا") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val product = ProductEntity(
                                title = title,
                                titleEn = titleEn,
                                categoryId = categoryId,
                                brand = brand,
                                price = priceStr.toLongOrNull() ?: 1000000L,
                                originalPrice = origPriceStr.toLongOrNull() ?: 1000000L,
                                discountPercent = discountStr.toIntOrNull() ?: 0,
                                rating = 5.0,
                                ratingCount = 1,
                                stockCount = stockStr.toIntOrNull() ?: 10,
                                isAmazingOffer = isAmazing,
                                description = desc.ifBlank { "کالای اصیل با گارانتی ۱۸ ماهه سینا سرویس" }
                            )
                            viewModel.adminAddProduct(product)
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SinaRed)
                ) {
                    Text("ثبت کالا")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddProductDialog = false }) { Text("انصراف") }
            }
        )
    }

    // Edit Product Price / Stock Dialog
    if (productToEdit != null) {
        val prod = productToEdit!!
        var priceStr by remember { mutableStateOf(prod.price.toString()) }
        var origPriceStr by remember { mutableStateOf(prod.originalPrice.toString()) }
        var discountStr by remember { mutableStateOf(prod.discountPercent.toString()) }
        var stockStr by remember { mutableStateOf(prod.stockCount.toString()) }

        AlertDialog(
            onDismissRequest = { productToEdit = null },
            title = { Text("ویرایش قیمت و موجودی «${prod.title.take(20)}...»", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("قیمت فروش نهایی (تومان)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = origPriceStr,
                        onValueChange = { origPriceStr = it },
                        label = { Text("قیمت اصلی خط‌خورده (تومان)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = discountStr,
                        onValueChange = { discountStr = it },
                        label = { Text("درصد تخفیف") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("تعداد موجودی در انبار") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = prod.copy(
                            price = priceStr.toLongOrNull() ?: prod.price,
                            originalPrice = origPriceStr.toLongOrNull() ?: prod.originalPrice,
                            discountPercent = discountStr.toIntOrNull() ?: prod.discountPercent,
                            stockCount = stockStr.toIntOrNull() ?: prod.stockCount
                        )
                        viewModel.adminUpdateProduct(updated)
                        productToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SinaNavy)
                ) {
                    Text("ذخیره تغییرات")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToEdit = null }) { Text("انصراف") }
            }
        )
    }

    // Add Coupon Dialog
    if (showAddCouponDialog) {
        var code by remember { mutableStateOf("") }
        var percentStr by remember { mutableStateOf("15") }
        var maxDiscStr by remember { mutableStateOf("3000000") }
        var desc by remember { mutableStateOf("تخفیف ویژه جشنواره سینا کالا") }

        AlertDialog(
            onDismissRequest = { showAddCouponDialog = false },
            title = { Text("ایجاد کد تخفیف جدید", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        label = { Text("کد تخفیف (مثال: LUXURY20)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = percentStr,
                        onValueChange = { percentStr = it },
                        label = { Text("درصد تخفیف") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = maxDiscStr,
                        onValueChange = { maxDiscStr = it },
                        label = { Text("حداکثر سقف تخفیف (تومان)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("توضیحات کوپن") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (code.isNotBlank()) {
                            val newCoupon = DiscountCodeEntity(
                                code = code,
                                title = desc,
                                percent = percentStr.toIntOrNull() ?: 10,
                                maxDiscount = maxDiscStr.toLongOrNull() ?: 1000000L,
                                minOrder = 500000L,
                                expireDate = "۱۴۰۴/۱۲/۲۹"
                            )
                            viewModel.adminAddDiscountCode(newCoupon)
                            showAddCouponDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SinaNavy)
                ) {
                    Text("ایجاد کد")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddCouponDialog = false }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AdminProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = PersianUtils.formatPriceToman(product.price),
                        style = MaterialTheme.typography.labelSmall,
                        color = SinaRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "موجودی: ${PersianUtils.toPersianDigits(product.stockCount)} عدد",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (product.stockCount > 0) SinaSuccess else Color.Red
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "ویرایش", tint = SinaNavy)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun AdminOrderRow(
    order: OrderEntity,
    onStatusChange: (String, String) -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سفارش ${order.orderNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SinaNavy.copy(alpha = 0.15f))
                            .clickable { expandedMenu = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${order.statusTitle} ▼",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SinaNavy
                        )
                    }

                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("درحال پردازش") },
                            onClick = {
                                onStatusChange("PROCESSING", "درحال پردازش و آماده‌سازی")
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("تحویل به پست / پیک اکسپرس") },
                            onClick = {
                                onStatusChange("SHIPPED", "تحویل به پیک اکسپرس")
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("تحویل داده شد") },
                            onClick = {
                                onStatusChange("DELIVERED", "تحویل داده شده به مشتری")
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "گیرنده: ${order.recipientName} | مبلغ: ${PersianUtils.formatPriceToman(order.finalAmount)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
