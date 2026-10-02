package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DealPlatform
import com.example.data.model.ProductDeal
import com.example.ui.theme.FirebaseOrange
import com.example.ui.theme.StatusError
import java.util.Locale

@Composable
fun ProductCard(
    deal: ProductDeal,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onBuyDarazClick: () -> Unit,
    onShareWhatsAppClick: () -> Unit,
    onToggleSave: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val platformColor = Color(deal.platform.brandColorHex)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("deal_card_${deal.id.take(8)}")
    ) {
        Column {
            // Product Image Container with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                AsyncImage(
                    model = deal.imageUrl,
                    contentDescription = deal.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Badge (e.g. -46% OFF)
                if (deal.discountPercentage > 0) {
                    Surface(
                        color = StatusError,
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "-${deal.discountPercentage}% OFF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Platform Badge (Daraz / AliExpress / Amazon / Markaz / Digital)
                Surface(
                    color = platformColor.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(text = deal.platform.iconEmoji, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = deal.platform.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bookmark Icon
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .testTag("save_deal_${deal.id.take(6)}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save Deal",
                        tint = if (isSaved) StatusError else Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Info Column
            Column(modifier = Modifier.padding(14.dp)) {
                // Category & Rating Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${deal.category.iconEmoji} ${deal.category.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${deal.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${deal.soldCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = deal.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Reseller / Poster Name Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (deal.isCommunityPost) "👤 Reseller: ${deal.posterName}" else "👑 Official Deal by ${deal.posterName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (deal.isCommunityPost) MaterialTheme.colorScheme.onSurfaceVariant else FirebaseOrange,
                        fontWeight = if (deal.isCommunityPost) FontWeight.Normal else FontWeight.Bold
                    )
                    if (deal.isFeatured) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "⭐ Featured", fontSize = 10.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rs. ${String.format(Locale.getDefault(), "%,.0f", deal.discountPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = platformColor
                    )

                    if (deal.originalPrice > deal.discountPrice) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rs. ${String.format(Locale.getDefault(), "%,.0f", deal.originalPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val buttonTitle = when (deal.platform) {
                        DealPlatform.DARAZ -> "Buy on Daraz"
                        DealPlatform.ALIEXPRESS -> "Buy AliExpress"
                        DealPlatform.AMAZON -> "Buy on Amazon"
                        DealPlatform.MARKAZ -> "Order Markaz"
                        DealPlatform.DIGITAL -> "Claim Deal"
                    }

                    Button(
                        onClick = onBuyDarazClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = platformColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("buy_deal_${deal.id.take(6)}")
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = buttonTitle,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share on WhatsApp Button
                    OutlinedButton(
                        onClick = onShareWhatsAppClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("share_whatsapp_${deal.id.take(6)}")
                    ) {
                        Text(text = "📲", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Share", fontWeight = FontWeight.Bold)
                    }

                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("delete_deal_${deal.id.take(6)}")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
