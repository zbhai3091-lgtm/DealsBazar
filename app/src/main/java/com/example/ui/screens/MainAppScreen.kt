package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppRemoteConfig
import com.example.data.model.DealPlatform
import com.example.data.model.OwnerPaymentConfig
import com.example.data.model.ProductDeal
import com.example.data.model.PromotionalBanner
import com.example.data.model.UserProfile
import com.example.ui.theme.FirebaseOrange
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.DealUiState
import com.example.ui.viewmodel.DealViewModel
import com.example.ui.viewmodel.MainTab
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: DealViewModel,
    uiState: DealUiState,
    deals: List<ProductDeal>,
    savedDealIds: Set<String>,
    paymentConfig: OwnerPaymentConfig,
    adBanner: PromotionalBanner,
    remoteConfig: AppRemoteConfig,
    userProfile: UserProfile,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissSnackbar()
        }
    }

    if (!userProfile.isSignedIn) {
        AuthScreen(
            onGoogleSignIn = { viewModel.signInWithGoogle("Google User", "zbhai3091@gmail.com") },
            onFacebookSignIn = { viewModel.signInWithFacebook() },
            onPhoneSignIn = { phone -> viewModel.signInWithPhone(phone) },
            onEmailSignUp = { name, email, pass -> viewModel.signUpWithEmail(name, email, pass) },
            onExploreAsGuest = { viewModel.exploreAsGuest() }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FirebaseOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛒", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Deals Bazar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Affiliate Reseller & Deals Hub",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        color = StatusSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = StatusSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Firebase Cloud Live",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusSuccess
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                MainTab.values().forEach { tab ->
                    val isSelected = uiState.selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            when (tab) {
                                MainTab.DEALS -> Icon(Icons.Default.LocalOffer, contentDescription = tab.title)
                                MainTab.POST_DEAL -> Icon(Icons.Default.AddCircle, contentDescription = tab.title)
                                MainTab.SAVED -> Icon(Icons.Default.Bookmark, contentDescription = tab.title)
                                MainTab.SETTINGS -> Icon(Icons.Default.Settings, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(text = tab.title, style = MaterialTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FirebaseOrange,
                            selectedTextColor = FirebaseOrange,
                            indicatorColor = FirebaseOrange.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.widthIn(max = 640.dp)) {
                when (uiState.selectedTab) {
                    MainTab.DEALS -> DealsFeedScreen(
                        deals = deals,
                        savedDealIds = savedDealIds,
                        selectedCategory = uiState.selectedCategory,
                        selectedPlatform = uiState.selectedPlatform,
                        adBanner = adBanner,
                        searchQuery = uiState.searchQuery,
                        isLoading = isLoading,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onPlatformSelect = { viewModel.selectPlatform(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onAdBannerClick = { viewModel.openAdBanner(context, it) },
                        onCardClick = { viewModel.showProductDetail(it) },
                        onBuyDeal = { ctx, deal -> viewModel.openDealLink(ctx, deal) },
                        onShareWhatsApp = { ctx, deal -> viewModel.shareOnWhatsApp(ctx, deal) },
                        onToggleSave = { viewModel.toggleSave(it) },
                        onDeleteDeal = null
                    )
                    MainTab.POST_DEAL -> PostCommunityDealScreen(
                        userProfile = userProfile,
                        isPublishing = uiState.isPublishing,
                        onPasteFromClipboard = { ctx, onText -> viewModel.pasteFromClipboard(ctx, onText) },
                        onPublish = { title, cat, plat, orig, disc, img, aff, name, phone, ep, jc, payo, pyp, usdt, iban, desc ->
                            viewModel.publishCommunityDeal(title, cat, plat, orig, disc, img, aff, name, phone, ep, jc, payo, pyp, usdt, iban, desc)
                        }
                    )
                    MainTab.SAVED -> SavedDealsScreen(
                        allDeals = deals,
                        savedDealIds = savedDealIds,
                        onCardClick = { viewModel.showProductDetail(it) },
                        onBuyDaraz = { ctx, deal -> viewModel.openDealLink(ctx, deal) },
                        onShareWhatsApp = { ctx, deal -> viewModel.shareOnWhatsApp(ctx, deal) },
                        onToggleSave = { viewModel.toggleSave(it) }
                    )
                    MainTab.SETTINGS -> SettingsScreen(
                        userProfile = userProfile,
                        remoteConfig = remoteConfig,
                        onSaveProfile = { viewModel.saveUserProfile(it) },
                        onGoogleSignIn = { name, email -> viewModel.signInWithGoogle(name, email) },
                        onSignOut = { viewModel.signOut() },
                        onPasteFromClipboard = { ctx, onText -> viewModel.pasteFromClipboard(ctx, onText) }
                    )
                }
            }
        }
    }

    // Product Detail Dialog with Local & International Payout details
    uiState.selectedProductForDetail?.let { deal: ProductDeal ->
        ProductDetailModal(
            deal = deal,
            paymentConfig = paymentConfig,
            onDismiss = { viewModel.showProductDetail(null) },
            onBuyDeal = { viewModel.openDealLink(context, deal) },
            onDirectOrder = { viewModel.openDirectWhatsAppOrder(context, deal, paymentConfig) },
            onShareWhatsApp = { viewModel.shareOnWhatsApp(context, deal) },
            onCopy = { label, text -> viewModel.copyToClipboard(context, label, text) }
        )
    }
}

@Composable
fun ProductDetailModal(
    deal: ProductDeal,
    paymentConfig: OwnerPaymentConfig,
    onDismiss: () -> Unit,
    onBuyDeal: () -> Unit,
    onDirectOrder: () -> Unit,
    onShareWhatsApp: () -> Unit,
    onCopy: (String, String) -> Unit
) {
    val platColor = Color(deal.platform.brandColorHex)
    val buttonLabel = when (deal.platform) {
        DealPlatform.DARAZ -> "Buy on Daraz"
        DealPlatform.ALIEXPRESS -> "Buy on AliExpress"
        DealPlatform.AMAZON -> "Buy on Amazon"
        DealPlatform.MARKAZ -> "Order on Markaz"
        DealPlatform.DIGITAL -> "Claim Offer"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onBuyDeal,
                colors = ButtonDefaults.buttonColors(containerColor = platColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(buttonLabel)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDirectOrder,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "📲", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (deal.isCommunityPost) "WhatsApp Reseller" else "WhatsApp Owner")
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = deal.imageUrl,
                        contentDescription = deal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        color = platColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${deal.platform.iconEmoji} ${deal.platform.title}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${deal.category.iconEmoji} ${deal.category.title} • By ${deal.posterName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = deal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rs. ${String.format(Locale.getDefault(), "%,.0f", deal.discountPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = platColor
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

                val ep = deal.posterEasypaisa.ifBlank { if (!deal.isCommunityPost) paymentConfig.easypaisaNumber else "" }
                val jc = deal.posterJazzcash.ifBlank { if (!deal.isCommunityPost) paymentConfig.jazzCashNumber else "" }
                val payo = deal.posterPayoneer
                val pyp = deal.posterPaypal
                val usdt = deal.posterUsdt
                val iban = deal.posterBankIban

                val hasAnyPayout = ep.isNotBlank() || jc.isNotBlank() || payo.isNotBlank() || pyp.isNotBlank() || usdt.isNotBlank() || iban.isNotBlank()

                if (hasAnyPayout) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "💳 Seller Payment Methods (Local & International):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            if (ep.isNotBlank()) {
                                PayoutRow(title = "🟢 Easypaisa", value = ep, onCopy = { onCopy("Easypaisa", ep) })
                            }
                            if (jc.isNotBlank()) {
                                PayoutRow(title = "🔴 JazzCash", value = jc, onCopy = { onCopy("JazzCash", jc) })
                            }
                            if (iban.isNotBlank()) {
                                PayoutRow(title = "🏦 Bank IBAN", value = iban, onCopy = { onCopy("Bank IBAN", iban) })
                            }
                            if (payo.isNotBlank()) {
                                PayoutRow(title = "💳 Payoneer", value = payo, onCopy = { onCopy("Payoneer", payo) })
                            }
                            if (pyp.isNotBlank()) {
                                PayoutRow(title = "🌍 PayPal", value = pyp, onCopy = { onCopy("PayPal", pyp) })
                            }
                            if (usdt.isNotBlank()) {
                                PayoutRow(title = "🪙 USDT TRC20", value = usdt, onCopy = { onCopy("USDT Wallet", usdt) })
                            }
                        }
                    }
                }

                if (deal.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Features / Description:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
fun PayoutRow(title: String, value: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
        }
    }
}
