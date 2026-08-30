package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.SinaGold
import com.example.ui.theme.SinaNavy
import com.example.ui.theme.SinaRed

@Composable
fun AmazingOfferSection(
    products: List<ProductEntity>,
    countdownSeconds: Long,
    wishlistProductIds: Set<Long>,
    onProductClick: (Long) -> Unit,
    onAddToCart: (Long) -> Unit,
    onToggleWishlist: (Long) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (hours, minutes, seconds) = PersianUtils.formatTimeRemaining(countdownSeconds)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SinaRed,
                        Color(0xFFBE123C),
                        Color(0xFF881337)
                    )
                )
            )
            .padding(vertical = 16.dp)
    ) {
        // Section Header Row: Title & Countdown Timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title & Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = SinaGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "پیشنهاد شگفت‌انگیز",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "تخفیف‌های استثنایی با مدت زمان محدود",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }

            // Realtime Countdown Timer Boxes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                CountdownDigitBox(seconds)
                Text(text = ":", color = Color.White, fontWeight = FontWeight.Bold)
                CountdownDigitBox(minutes)
                Text(text = ":", color = Color.White, fontWeight = FontWeight.Bold)
                CountdownDigitBox(hours)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Product List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First item: "See all" card
            item {
                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .height(290.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onViewAllClick() },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "مشاهده همه",
                                tint = SinaRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "مشاهده\nهمه محصولات",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(products) { product ->
                ProductCard(
                    product = product,
                    isInWishlist = wishlistProductIds.contains(product.id),
                    onProductClick = { onProductClick(product.id) },
                    onAddToCart = { onAddToCart(product.id) },
                    onToggleWishlist = { onToggleWishlist(product.id) },
                    modifier = Modifier.width(200.dp),
                    isCompact = true
                )
            }
        }
    }
}

@Composable
private fun CountdownDigitBox(value: String) {
    Box(
        modifier = Modifier
            .size(width = 28.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
