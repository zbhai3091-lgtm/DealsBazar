package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DealCategory
import com.example.data.model.DealPlatform
import com.example.data.model.ProductDeal
import com.example.data.model.PromotionalBanner
import com.example.ui.components.ProductCard
import com.example.ui.theme.FirebaseOrange

@Composable
fun DealsFeedScreen(
    deals: List<ProductDeal>,
    savedDealIds: Set<String>,
    selectedCategory: DealCategory,
    selectedPlatform: DealPlatform?,
    adBanner: PromotionalBanner,
    searchQuery: String,
    isLoading: Boolean,
    onCategorySelect: (DealCategory) -> Unit,
    onPlatformSelect: (DealPlatform?) -> Unit,
    onSearchChange: (String) -> Unit,
    onAdBannerClick: (PromotionalBanner) -> Unit,
    onCardClick: (ProductDeal) -> Unit,
    onBuyDeal: (Context, ProductDeal) -> Unit,
    onShareWhatsApp: (Context, ProductDeal) -> Unit,
    onToggleSave: (String) -> Unit,
    onDeleteDeal: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val filteredDeals = remember(deals, selectedCategory, selectedPlatform, searchQuery) {
        deals.filter { deal ->
            val matchCategory = selectedCategory == DealCategory.ALL || deal.category == selectedCategory
            val matchPlatform = selectedPlatform == null || deal.platform == selectedPlatform
            val matchSearch = searchQuery.isBlank() ||
                    deal.title.contains(searchQuery, ignoreCase = true) ||
                    deal.description.contains(searchQuery, ignoreCase = true) ||
                    deal.posterName.contains(searchQuery, ignoreCase = true) ||
                    deal.platform.title.contains(searchQuery, ignoreCase = true)
            matchCategory && matchPlatform && matchSearch
        }.sortedWith(compareByDescending<ProductDeal> { it.isFeatured }.thenByDescending { it.timestamp })
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sponsored Ad Banner (Admin Controlled)
        if (adBanner.isActive) {
            item {
                SponsoredAdBanner(banner = adBanner, onClick = { onAdBannerClick(adBanner) })
            }
        }

        // Hero Flash Banner
        item {
            HeroMultiPlatformBanner()
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search Daraz, AliExpress, Amazon, Resellers...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_deals_input")
            )
        }

        // Platform Filter Row
        item {
            Column {
                Text(
                    text = "Select Platform (Dukaan / Website):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPlatform == null,
                        onClick = { onPlatformSelect(null) },
                        label = { Text("🌐 All Platforms") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )

                    DealPlatform.values().forEach { platform ->
                        val isSelected = selectedPlatform == platform
                        val platColor = Color(platform.brandColorHex)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPlatformSelect(platform) },
                            label = { Text("${platform.iconEmoji} ${platform.title}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = platColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Horizontal Category Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DealCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(category) },
                        label = { Text("${category.iconEmoji} ${category.title}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FirebaseOrange,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_cat_${category.name.lowercase()}")
                    )
                }
            }
        }

        // Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Deals (${filteredDeals.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⭐ Top Deals Pinned",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FirebaseOrange)
                }
            }
        } else if (filteredDeals.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🔍", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Koi deal nahi mili",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "'Post Deal' tab se apni deal lagayein!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredDeals, key = { it.id }) { deal ->
                ProductCard(
                    deal = deal,
                    isSaved = savedDealIds.contains(deal.id),
                    onCardClick = { onCardClick(deal) },
                    onBuyDarazClick = { onBuyDeal(context, deal) },
                    onShareWhatsAppClick = { onShareWhatsApp(context, deal) },
                    onToggleSave = { onToggleSave(deal.id) },
                    onDelete = if (onDeleteDeal != null) { { onDeleteDeal.invoke(deal.id) } } else null
                )
            }
        }
    }
}

@Composable
fun SponsoredAdBanner(
    banner: PromotionalBanner,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sponsored_ad_banner")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFF0F172A))
            ) {
                if (banner.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = "Ad Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 10.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SPONSORED AD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = banner.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = banner.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Surface(
                    color = FirebaseOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Check Offer", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HeroMultiPlatformBanner() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFEA580C),
                            Color(0xFF9333EA),
                            Color(0xFF2563EB)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "🌐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MULTI-PLATFORM DEALS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "Highest Commissions",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFEF08A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Daraz • AliExpress • Amazon • Markaz",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Dunya ke best affiliate platforms ki verified deals! Har koi apna link laga kar commission kama sakta hai.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}
