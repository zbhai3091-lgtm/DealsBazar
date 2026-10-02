package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppRemoteConfig
import com.example.data.model.UserProfile
import com.example.ui.theme.FirebaseOrange
import com.example.ui.theme.StatusSuccess

@Composable
fun ProfileSettingsScreen(
    userProfile: UserProfile,
    remoteConfig: AppRemoteConfig,
    onSaveProfile: (UserProfile) -> Unit,
    onGoogleSignIn: (String, String) -> Unit,
    onSignOut: () -> Unit,
    onPasteFromClipboard: (Context, (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var name by remember(userProfile) { mutableStateOf(userProfile.name) }
    var email by remember(userProfile) { mutableStateOf(userProfile.email) }
    var phone by remember(userProfile) { mutableStateOf(userProfile.phone) }

    // Pakistan Local Methods
    var easypaisaNum by remember(userProfile) { mutableStateOf(userProfile.easypaisaNumber) }
    var easypaisaName by remember(userProfile) { mutableStateOf(userProfile.easypaisaName) }
    var jazzcashNum by remember(userProfile) { mutableStateOf(userProfile.jazzcashNumber) }
    var jazzcashName by remember(userProfile) { mutableStateOf(userProfile.jazzcashName) }
    var sadapayNum by remember(userProfile) { mutableStateOf(userProfile.sadapayNumber) }
    var bankName by remember(userProfile) { mutableStateOf(userProfile.bankName) }
    var bankIban by remember(userProfile) { mutableStateOf(userProfile.bankIban) }

    // International Methods
    var payoneerEmail by remember(userProfile) { mutableStateOf(userProfile.payoneerEmail) }
    var paypalEmail by remember(userProfile) { mutableStateOf(userProfile.paypalEmail) }
    var usdtWallet by remember(userProfile) { mutableStateOf(userProfile.usdtTrc20Wallet) }

    val isOwner = userProfile.email.equals(remoteConfig.adminEmail, ignoreCase = true) || userProfile.email == "zbhai3091@gmail.com"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile & Google Sign-In Card
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
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(FirebaseOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👤", fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (userProfile.isSignedIn) userProfile.name else "Guest Reseller",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (userProfile.isSignedIn) userProfile.email else "Not Signed In",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (userProfile.isSignedIn) {
                            Surface(
                                color = StatusSuccess.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isOwner) "👑 Owner" else "Verified", style = MaterialTheme.typography.labelSmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onGoogleSignIn("Hassan (Owner)", "zbhai3091@gmail.com")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("G Switch Account", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign Out", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Firebase Cloud Control Card (Owner Control via Google Firebase)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = FirebaseOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "☁️ Firebase Cloud Control (Admin)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Admin control is managed directly on Firebase Cloud (console.firebase.google.com).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "🔥 Real-Time Cloud Synced Status:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "• App Version: ${remoteConfig.appVersion}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Community Posting: ${if (remoteConfig.isCommunityPostingAllowed) "Active & Open 🟢" else "Paused 🔴"}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Cloud Database: Google Firebase Firestore", style = MaterialTheme.typography.bodySmall)
                            Text(text = "• Feature Changing: Edit 'settings' document in Firebase Console to update ads, banners or features instantly!", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Section: Pakistan Local Payment Receive Methods
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🇵🇰 Pakistan Payout Methods",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Jab aap koi deal post karenge to customers in accounts mein payment bhejenge.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Easypaisa
                    Text(text = "🟢 Easypaisa Account:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = easypaisaNum,
                            onValueChange = { easypaisaNum = it },
                            label = { Text("Easypaisa Number") },
                            placeholder = { Text("03451234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.2f).testTag("profile_easypaisa_num")
                        )
                        OutlinedTextField(
                            value = easypaisaName,
                            onValueChange = { easypaisaName = it },
                            label = { Text("Title") },
                            placeholder = { Text("Hassan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("profile_easypaisa_name")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // JazzCash
                    Text(text = "🔴 JazzCash Account:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jazzcashNum,
                            onValueChange = { jazzcashNum = it },
                            label = { Text("JazzCash Number") },
                            placeholder = { Text("03001234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.2f).testTag("profile_jazzcash_num")
                        )
                        OutlinedTextField(
                            value = jazzcashName,
                            onValueChange = { jazzcashName = it },
                            label = { Text("Title") },
                            placeholder = { Text("Hassan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("profile_jazzcash_name")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SadaPay / NayaPay
                    Text(text = "🟣 SadaPay / NayaPay Number:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sadapayNum,
                        onValueChange = { sadapayNum = it },
                        label = { Text("SadaPay / NayaPay Mobile") },
                        placeholder = { Text("03001234567") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("profile_sadapay_num")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bank Account & IBAN
                    Text(text = "🏦 Pakistani Bank IBAN (All Banks):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank Name") },
                            placeholder = { Text("Meezan / HBL / UBL") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bankIban,
                            onValueChange = { bankIban = it },
                            label = { Text("24-Digit IBAN") },
                            placeholder = { Text("PK78...") },
                            singleLine = true,
                            modifier = Modifier.weight(1.3f)
                        )
                    }
                }
            }
        }

        // Section: International Payment Receive Methods
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🌍 International Payout Methods",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bahar ke mulkon se Dollars ($) aur Crypto mein payment lene ke liye.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payoneer
                    Text(text = "💳 Payoneer Account (USD / EUR / GBP):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = payoneerEmail,
                        onValueChange = { payoneerEmail = it },
                        label = { Text("Payoneer Email Address") },
                        placeholder = { Text("your.email@example.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("profile_payoneer_email")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PayPal
                    Text(text = "🌍 PayPal (Overseas Orders):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = paypalEmail,
                        onValueChange = { paypalEmail = it },
                        label = { Text("PayPal Email Address") },
                        placeholder = { Text("paypal.id@example.com") },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("profile_paypal_email")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // USDT TRC20 Crypto
                    Text(text = "🪙 Crypto / USDT TRC-20 Wallet Address (Zero Fee):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = usdtWallet,
                            onValueChange = { usdtWallet = it },
                            label = { Text("USDT Address (Binance / OKX)") },
                            placeholder = { Text("TX7...") },
                            leadingIcon = { Icon(Icons.Default.CurrencyBitcoin, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("profile_usdt_wallet")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onPasteFromClipboard(context) { usdtWallet = it } },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("📋 Paste")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save Profile Button
                    Button(
                        onClick = {
                            onSaveProfile(
                                userProfile.copy(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    easypaisaNumber = easypaisaNum,
                                    easypaisaName = easypaisaName,
                                    jazzcashNumber = jazzcashNum,
                                    jazzcashName = jazzcashName,
                                    sadapayNumber = sadapayNum,
                                    bankName = bankName,
                                    bankIban = bankIban,
                                    payoneerEmail = payoneerEmail,
                                    paypalEmail = paypalEmail,
                                    usdtTrc20Wallet = usdtWallet
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FirebaseOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_user_payouts_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save My Payment & Payout Methods", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
