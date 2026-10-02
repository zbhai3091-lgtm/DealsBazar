package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductDeal
import com.example.ui.components.ProductCard

@Composable
fun SavedDealsScreen(
    allDeals: List<ProductDeal>,
    savedDealIds: Set<String>,
    onCardClick: (ProductDeal) -> Unit,
    onBuyDaraz: (Context, ProductDeal) -> Unit,
    onShareWhatsApp: (Context, ProductDeal) -> Unit,
    onToggleSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedDeals = allDeals.filter { savedDealIds.contains(it.id) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Aapki Mehfooz Ki Gayi Deals (${savedDeals.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (savedDeals.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "❤️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Abhi tak koi deal save nahi ki",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Deals tab par ja kar kisi bhi product ke dil ❤️ icon par click karein.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(savedDeals, key = { it.id }) { deal ->
                ProductCard(
                    deal = deal,
                    isSaved = true,
                    onCardClick = { onCardClick(deal) },
                    onBuyDarazClick = { onBuyDaraz(context, deal) },
                    onShareWhatsAppClick = { onShareWhatsApp(context, deal) },
                    onToggleSave = { onToggleSave(deal.id) }
                )
            }
        }
    }
}
