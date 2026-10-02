package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DealCategory
import com.example.data.model.DealPlatform
import com.example.data.model.OwnerPaymentConfig
import com.example.data.model.ProductDeal
import com.example.data.model.PromotionalBanner
import com.example.ui.theme.FirebaseOrange
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminControlScreen(
    isAdminUnlocked: Boolean,
    adminPasswordInput: String,
    adminError: String?,
    paymentConfig: OwnerPaymentConfig,
    adBanner: PromotionalBanner,
    allDeals: List<ProductDeal>,
    isPublishing: Boolean,
    onAdminPasswordChange: (String) -> Unit,
    onVerifyAdmin: () -> Unit,
    onLockAdmin: () -> Unit,
    onSavePaymentConfig: (OwnerPaymentConfig) -> Unit,
    onSaveAdBanner: (PromotionalBanner) -> Unit,
    onToggleFeatureDeal: (String) -> Unit,
    onDeleteDeal: (String) -> Unit,
    onPasteFromClipboard: (Context, (String) -> Unit) -> Unit,
    onPublishAdminDeal: (title: String, category: DealCategory, platform: DealPlatform, orig: Double, disc: Double, img: String, aff: String, desc: String, isFeatured: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Payment state
    var jazzCashName by remember(paymentConfig) { mutableStateOf(paymentConfig.jazzCashName) }
    var jazzCashNum by remember(paymentConfig) { mutableStateOf(paymentConfig.jazzCashNumber) }
    var easyPaisaName by remember(paymentConfig) { mutableStateOf(paymentConfig.easypaisaName) }
    var easyPaisaNum by remember(paymentConfig) { mutableStateOf(paymentConfig.easypaisaNumber) }
    var whatsappNum by remember(paymentConfig) { mutableStateOf(paymentConfig.whatsappNumber) }

    // Ad Banner State
    var adTitle by remember(adBanner) { mutableStateOf(adBanner.title) }
    var adSubtitle by remember(adBanner) { mutableStateOf(adBanner.subtitle) }
    var adImageUrl by remember(adBanner) { mutableStateOf(adBanner.imageUrl) }
    var adTargetUrl by remember(adBanner) { mutableStateOf(adBanner.targetUrl) }
    var adSponsorName by remember(adBanner) { mutableStateOf(adBanner.sponsorName) }
    var isAdActive by remember(adBanner) { mutableStateOf(adBanner.isActive) }

    if (!isAdminUnlocked) {
        var passwordVisible by remember { mutableStateOf(false) }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(FirebaseOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = FirebaseOrange,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Owner / Admin Control Room",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Sirf authorized owner ke liye. Apna secret passcode enter karein.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = adminPasswordInput,
                            onValueChange = onAdminPasswordChange,
                            label = { Text("Secret Passcode") },
                            placeholder = { Text("••••••••") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { onVerifyAdmin() }),
                            isError = adminError != null,
                            modifier = Modifier.weight(1f).testTag("admin_pin_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 1-Click Paste Passcode Button
                        OutlinedButton(
                            onClick = {
                                onPasteFromClipboard(context) { onAdminPasswordChange(it) }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("📋 Paste")
                        }
                    }

                    if (adminError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = adminError,
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusError
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onVerifyAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = FirebaseOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_admin_btn")
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unlock Admin Control Room", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    // Unlocked State: Full Admin Suite
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Owner Status Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Boss Hassan (Admin Live)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusSuccess
                            )
                            Text(
                                text = "App Monetization, Ads, and Deals Control",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLockAdmin,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("lock_admin_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Section 1: Business & Earnings Analytics Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "📊 App Business & Analytics Dashboard",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = FirebaseOrange.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "${allDeals.size}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = FirebaseOrange)
                                Text(text = "Active Deals", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF7C3AED).copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "${allDeals.count { it.isCommunityPost }}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C3AED))
                                Text(text = "User Resellers", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Rs. 25k+", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = StatusSuccess)
                                Text(text = "Est. Volume", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: In-App Advertising & Sponsored Banner Setup
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "In-App Advertising & Banner Ads",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sponsor banner lagayein aur brands se fees charge karein.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isAdActive,
                            onCheckedChange = { isAdActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE11D48))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = adTitle,
                        onValueChange = { adTitle = it },
                        label = { Text("Ad Heading / Offer Title") },
                        placeholder = { Text("e.g. Mega Daraz Sale - 70% Off") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adSubtitle,
                        onValueChange = { adSubtitle = it },
                        label = { Text("Ad Subtitle / Details") },
                        placeholder = { Text("Limited time offers on all items") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = adImageUrl,
                            onValueChange = { adImageUrl = it },
                            label = { Text("Banner Image URL") },
                            placeholder = { Text("https://...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onPasteFromClipboard(context) { adImageUrl = it } },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("📋 Paste")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = adTargetUrl,
                            onValueChange = { adTargetUrl = it },
                            label = { Text("Ad Click Destination (Affiliate URL)") },
                            placeholder = { Text("https://...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onPasteFromClipboard(context) { adTargetUrl = it } },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("📋 Paste")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSaveAdBanner(
                                PromotionalBanner(
                                    title = adTitle,
                                    subtitle = adSubtitle,
                                    imageUrl = adImageUrl,
                                    targetUrl = adTargetUrl,
                                    sponsorName = adSponsorName,
                                    isActive = isAdActive
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_ad_banner_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Broadcast Ad Banner to All Users")
                    }
                }
            }
        }

        // Section 3: Owner Payment Methods Setup
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = FirebaseOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Aapke Personal Payment Receive Methods",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aapka Easypaisa aur JazzCash jahan customer direct paise bhejega.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = easyPaisaNum,
                            onValueChange = { easyPaisaNum = it },
                            label = { Text("Easypaisa Number") },
                            placeholder = { Text("03451234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = easyPaisaName,
                            onValueChange = { easyPaisaName = it },
                            label = { Text("Title") },
                            placeholder = { Text("Hassan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jazzCashNum,
                            onValueChange = { jazzCashNum = it },
                            label = { Text("JazzCash Number") },
                            placeholder = { Text("03001234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.2f)
                        )
                        OutlinedTextField(
                            value = jazzCashName,
                            onValueChange = { jazzCashName = it },
                            label = { Text("Title") },
                            placeholder = { Text("Hassan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = whatsappNum,
                        onValueChange = { whatsappNum = it },
                        label = { Text("WhatsApp Contact Number") },
                        placeholder = { Text("923001234567") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onSavePaymentConfig(
                                OwnerPaymentConfig(
                                    jazzCashName = jazzCashName,
                                    jazzCashNumber = jazzCashNum,
                                    easypaisaName = easyPaisaName,
                                    easypaisaNumber = easyPaisaNum,
                                    whatsappNumber = whatsappNum
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Payment Details")
                    }
                }
            }
        }

        // Section 4: Live Moderation (Pin to Top ⭐ or Delete)
        item {
            Text(
                text = "🛡️ Deals Moderation & Feature Control (${allDeals.size}):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(allDeals.size) { index ->
            val deal = allDeals[index]
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = deal.imageUrl,
                            contentDescription = deal.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = deal.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${deal.platform.iconEmoji} ${deal.platform.title} • By ${deal.posterName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Pin to Top / Feature Toggle
                    IconButton(onClick = { onToggleFeatureDeal(deal.id) }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Feature",
                            tint = if (deal.isFeatured) Color(0xFFF59E0B) else Color.LightGray
                        )
                    }

                    // Delete button
                    IconButton(onClick = { onDeleteDeal(deal.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError)
                    }
                }
            }
        }
    }
}
