package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SinaNavy
import com.example.ui.theme.SinaRed

data class BrandItem(
    val name: String,
    val nameEn: String,
    val brandQuery: String
)

@Composable
fun BrandLogosSection(
    onBrandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val brands = listOf(
        BrandItem("اپل", "Apple", "اپل"),
        BrandItem("سامسونگ", "Samsung", "سامسونگ"),
        BrandItem("سونی", "Sony", "سونی"),
        BrandItem("ایسوس", "ASUS", "ایسوس"),
        BrandItem("نایکی", "Nike", "نایکی"),
        BrandItem("دایسون", "Dyson", "دایسون"),
        BrandItem("بوش", "Bosch", "بوش"),
        BrandItem("مارشال", "Marshall", "مارشال"),
        BrandItem("ری‌بن", "Ray-Ban", "ری‌بن"),
        BrandItem("دلونگی", "DeLonghi", "دلونگی"),
        BrandItem("انکر", "Anker", "انکر"),
        BrandItem("ماکیتا", "Makita", "ماکیتا")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "برندهای معتبر و محبوب",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(brands) { brand ->
                Card(
                    modifier = Modifier
                        .width(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onBrandClick(brand.brandQuery) }
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = brand.nameEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = brand.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
