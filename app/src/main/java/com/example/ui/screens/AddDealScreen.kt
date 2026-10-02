package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
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
import com.example.ui.theme.FirebaseOrange
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDealScreen(
    isAdminUnlocked: Boolean,
    adminPasswordInput: String,
    adminError: String?,
    paymentConfig: OwnerPaymentConfig,
    isPublishing: Boolean,
    onAdminPasswordChange: (String) -> Unit,
    onVerifyAdmin: () -> Unit,
    onLockAdmin: () -> Unit,
    onSavePaymentConfig: (OwnerPaymentConfig) -> Unit,
    onPublish: (title: String, category: DealCategory, platform: DealPlatform, origPrice: Double, discPrice: Double, imgUrl: String, affiliateUrl: String, desc: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DealCategory.TECH) }
    var selectedPlatform by remember { mutableStateOf(DealPlatform.DARAZ) }
    var origPriceText by remember { mutableStateOf("") }
    var discPriceText by remember { mutableStateOf("") }
    var affiliateLink by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Payment state
    var jazzCashName by remember(paymentConfig) { mutableStateOf(paymentConfig.jazzCashName) }
    var jazzCashNum by remember(paymentConfig) { mutableStateOf(paymentConfig.jazzCashNumber) }
    var easyPaisaName by remember(paymentConfig) { mutableStateOf(paymentConfig.easypaisaName) }
    var easyPaisaNum by remember(paymentConfig) { mutableStateOf(paymentConfig.easypaisaNumber) }
    var whatsappNum by remember(paymentConfig) { mutableStateOf(paymentConfig.whatsappNumber) }

    val presetImages = listOf(
        "Smart Watch" to "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
        "Airpods" to "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
        "Kitchen Gadget" to "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=600&auto=format&fit=crop&q=80",
        "Projector Lamp" to "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
        "Lawn Suit" to "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80",
        "Web Hosting" to "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=600&auto=format&fit=crop&q=80"
    )

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
                        text = "Owner / Admin Access",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Nayi deals add karne aur payment settings badalne ke liye apna secret passcode enter karein.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = onAdminPasswordChange,
                        label = { Text("Enter Secret Passcode") },
                        placeholder = { Text("Code yahan likhein") },
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )

                    if (adminError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
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
                        Text("Unlock Owner Panel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

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
                        Text(text = "👑", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Owner: Hassan (Admin Active)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusSuccess
                            )
                            Text(
                                text = "Aap multi-platform deals publish aur control kar rahe hain.",
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

        // Section: Payment Receiving Methods Setup
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
                                text = "Aapke Payment Receive Methods",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Direct orders par customer in accounts mein payment karega.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Easypaisa
                    Text(text = "🟢 Easypaisa Account Details:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = easyPaisaNum,
                            onValueChange = { easyPaisaNum = it },
                            label = { Text("Easypaisa Number") },
                            placeholder = { Text("03451234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.2f).testTag("input_easypaisa_num")
                        )
                        OutlinedTextField(
                            value = easyPaisaName,
                            onValueChange = { easyPaisaName = it },
                            label = { Text("Title / Naam") },
                            placeholder = { Text("Hassan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_easypaisa_name")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // JazzCash
                    Text(text = "🔴 JazzCash Account Details:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jazzCashNum,
                            onValueChange = { jazzCashNum = it },
                            label = { Text("JazzCash Number") },
                            placeholder = { Text("03001234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.2f).testTag("input_jazzcash_num")
                        )
                        OutlinedTextField(
                            value = jazzCashName,
                            onValueChange = { jazzCashName = it },
                            label = { Text("Title / Naam") },
                            placeholder = { Text("Hassan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_jazzcash_name")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // WhatsApp Number
                    Text(text = "📲 WhatsApp Contact Number (Orders Ke Liye):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = whatsappNum,
                        onValueChange = { whatsappNum = it },
                        label = { Text("WhatsApp Number") },
                        placeholder = { Text("923001234567") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("input_whatsapp_num")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                        modifier = Modifier.fillMaxWidth().testTag("save_payments_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Payment Details to Cloud")
                    }
                }
            }
        }

        // Section: Add Deal Form with Platform Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Nayi Affiliate Deal Publish Karein",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Platform Selector
                    Text(
                        text = "1. Platform Chunein (Kahan Ki Deal Hai?):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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
                        placeholder = { Text("e.g. M10 TWS Wireless Earbuds Gaming") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_deal_title")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Selector
                    Text(
                        text = "2. Category Chunein:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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

                    // Prices row
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

                    // Affiliate URL
                    OutlinedTextField(
                        value = affiliateLink,
                        onValueChange = { affiliateLink = it },
                        label = { Text("Aapka Affiliate Link (${selectedPlatform.title})") },
                        placeholder = { Text("https://... affiliate link paste karein") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_affiliate_link")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Image URL
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Product Photo Link (Image URL)") },
                        placeholder = { Text("https://...") },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_image_url")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Presets
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Khasoosiyaat / Features (Optional)") },
                        placeholder = { Text("Heavy battery, water resistant, fast delivery...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("input_description")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val isValid = title.isNotBlank() && (discPriceText.toDoubleOrNull() ?: 0.0) > 0

                    Button(
                        onClick = {
                            val orig = origPriceText.toDoubleOrNull() ?: (discPriceText.toDoubleOrNull() ?: 1000.0) * 1.5
                            val disc = discPriceText.toDoubleOrNull() ?: 1000.0
                            onPublish(title, selectedCategory, selectedPlatform, orig, disc, imageUrl, affiliateLink, description)
                        },
                        enabled = isValid && !isPublishing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(selectedPlatform.brandColorHex)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_deal_btn")
                    ) {
                        if (isPublishing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Publish, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish ${selectedPlatform.title} Deal to App", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
