package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DealCategory
import com.example.data.model.DealPlatform
import com.example.data.model.UserProfile
import com.example.ui.theme.FirebaseOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostCommunityDealScreen(
    userProfile: UserProfile,
    isPublishing: Boolean,
    onPasteFromClipboard: (Context, (String) -> Unit) -> Unit,
    onPublish: (
        title: String,
        category: DealCategory,
        platform: DealPlatform,
        orig: Double,
        disc: Double,
        img: String,
        aff: String,
        name: String,
        phone: String,
        easypaisa: String,
        jazzcash: String,
        payoneer: String,
        paypal: String,
        usdt: String,
        bankIban: String,
        desc: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DealCategory.TECH) }
    var selectedPlatform by remember { mutableStateOf(DealPlatform.DARAZ) }
    var origPriceText by remember { mutableStateOf("") }
    var discPriceText by remember { mutableStateOf("") }
    var affiliateLink by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var sellerName by remember(userProfile) { mutableStateOf(userProfile.name) }
    var sellerPhone by remember(userProfile) { mutableStateOf(userProfile.phone.ifBlank { userProfile.easypaisaNumber }) }

    // Reseller Payouts for this deal (auto-filled from profile!)
    var easypaisa by remember(userProfile) { mutableStateOf(userProfile.easypaisaNumber) }
    var jazzcash by remember(userProfile) { mutableStateOf(userProfile.jazzcashNumber) }
    var payoneer by remember(userProfile) { mutableStateOf(userProfile.payoneerEmail) }
    var paypal by remember(userProfile) { mutableStateOf(userProfile.paypalEmail) }
    var usdt by remember(userProfile) { mutableStateOf(userProfile.usdtTrc20Wallet) }
    var bankIban by remember(userProfile) { mutableStateOf(userProfile.bankIban) }

    var description by remember { mutableStateOf("") }

    val presetImages = listOf(
        "Smart Watch" to "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
        "Airpods" to "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
        "Kitchen Chopper" to "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=600&auto=format&fit=crop&q=80",
        "Astronaut Projector" to "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
        "Lawn Suit" to "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80",
        "Leather Wallet" to "https://images.unsplash.com/photo-1627123424574-724758594e93?w=600&auto=format&fit=crop&q=80"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Hero Card for Users
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📢", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Apni Deal Post Karein (Free for All)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Apna Easypaisa, JazzCash ya International PayPal/USDT dalein aur direct munafa kamayein!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Seller Details Row
                    Text(text = "1. Aapki Pehchan (Seller Profile):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sellerName,
                            onValueChange = { sellerName = it },
                            label = { Text("Aapka Naam / Shop") },
                            placeholder = { Text("e.g. Ali Traders") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_seller_name")
                        )

                        OutlinedTextField(
                            value = sellerPhone,
                            onValueChange = { sellerPhone = it },
                            label = { Text("WhatsApp Number") },
                            placeholder = { Text("03001234567") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.1f).testTag("input_seller_phone")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Platform Selection
                    Text(text = "2. Platform Chunein:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DealPlatform.values().forEach { plat ->
                            val isSelected = selectedPlatform == plat
                            val platColor = Color(plat.brandColorHex)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPlatform = plat },
                                label = { Text("${plat.iconEmoji} ${plat.title}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = platColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product Title (Naam)") },
                        placeholder = { Text("e.g. Wireless Bluetooth Earbuds Pro 6") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { onPasteFromClipboard(context) { title = it } }) {
                                Icon(Icons.Default.ContentPaste, contentDescription = "Paste")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("input_deal_title")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Selection
                    Text(text = "3. Category Chunein:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DealCategory.values().filter { it != DealCategory.ALL }.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text("${cat.iconEmoji} ${cat.title}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FirebaseOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prices
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = origPriceText,
                            onValueChange = { origPriceText = it.filter { c -> c.isDigit() } },
                            label = { Text("Asli Price (Rs.)") },
                            placeholder = { Text("3000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_orig_price")
                        )

                        OutlinedTextField(
                            value = discPriceText,
                            onValueChange = { discPriceText = it.filter { c -> c.isDigit() } },
                            label = { Text("Sale Price (Rs.)") },
                            placeholder = { Text("1499") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("input_disc_price")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Affiliate URL with 1-Click Clipboard Paste
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = affiliateLink,
                            onValueChange = { affiliateLink = it },
                            label = { Text("Affiliate / Product Link") },
                            placeholder = { Text("Link paste karein") },
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_affiliate_link")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onPasteFromClipboard(context) { affiliateLink = it } },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp).testTag("btn_paste_link")
                        ) {
                            Text("📋 Paste")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Photo URL
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Photo Link (URL)") },
                            placeholder = { Text("https://...") },
                            leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_image_url")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onPasteFromClipboard(context) { imageUrl = it } },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("📋 Paste")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick select image presets
                    Text(
                        text = "Ya photo chunein (Quick Presets):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetImages.forEach { (name, url) ->
                            Surface(
                                onClick = { imageUrl = url },
                                shape = RoundedCornerShape(8.dp),
                                color = if (imageUrl == url) FirebaseOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (imageUrl == url) androidx.compose.foundation.BorderStroke(1.dp, FirebaseOrange) else null
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    if (imageUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payout Methods for this Deal
                    Text(text = "4. Aapke Payment Receive Accounts (Customer In Par Pay Karega):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = easypaisa,
                            onValueChange = { easypaisa = it },
                            label = { Text("Easypaisa Number") },
                            placeholder = { Text("03451234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = jazzcash,
                            onValueChange = { jazzcash = it },
                            label = { Text("JazzCash Number") },
                            placeholder = { Text("03001234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = payoneer,
                            onValueChange = { payoneer = it },
                            label = { Text("Payoneer Email (USD)") },
                            placeholder = { Text("email@payoneer.com") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = paypal,
                            onValueChange = { paypal = it },
                            label = { Text("PayPal Email (USD)") },
                            placeholder = { Text("email@paypal.com") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = usdt,
                        onValueChange = { usdt = it },
                        label = { Text("🪙 USDT TRC-20 Wallet Address (Crypto)") },
                        placeholder = { Text("TX7...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Features / Description (Optional)") },
                        placeholder = { Text("Quality, warranty, color options...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("input_description")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val isValid = title.isNotBlank() && (discPriceText.toDoubleOrNull() ?: 0.0) > 0

                    Button(
                        onClick = {
                            val orig = origPriceText.toDoubleOrNull() ?: (discPriceText.toDoubleOrNull() ?: 1000.0) * 1.5
                            val disc = discPriceText.toDoubleOrNull() ?: 1000.0
                            onPublish(
                                title,
                                selectedCategory,
                                selectedPlatform,
                                orig,
                                disc,
                                imageUrl,
                                affiliateLink,
                                sellerName.ifBlank { "Verified Reseller" },
                                sellerPhone,
                                easypaisa,
                                jazzcash,
                                payoneer,
                                paypal,
                                usdt,
                                bankIban,
                                description
                            )
                        },
                        enabled = isValid && !isPublishing,
                        colors = ButtonDefaults.buttonColors(containerColor = FirebaseOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_community_deal_btn")
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Publish, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Post Deal Now (Sub Ko Dikhayein)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
