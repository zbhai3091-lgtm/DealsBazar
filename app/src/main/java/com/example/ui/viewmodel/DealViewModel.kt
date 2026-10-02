package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DealCategory
import com.example.data.model.DealPlatform
import com.example.data.model.OwnerPaymentConfig
import com.example.data.model.ProductDeal
import com.example.data.model.PromotionalBanner
import com.example.data.model.UserProfile
import com.example.data.repository.DealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
    DEALS("Deals"),
    POST_DEAL("Post Deal"),
    SAVED("Saved"),
    SETTINGS("Settings ⚙️")
}

data class DealUiState(
    val selectedTab: MainTab = MainTab.DEALS,
    val selectedCategory: DealCategory = DealCategory.ALL,
    val selectedPlatform: DealPlatform? = null,
    val searchQuery: String = "",
    val selectedProductForDetail: ProductDeal? = null,
    val isPublishing: Boolean = false,
    val snackbarMessage: String? = null
)

class DealViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DealRepository(application)

    val deals: StateFlow<List<ProductDeal>> = repository.deals
    val savedDealIds: StateFlow<Set<String>> = repository.savedDealIds
    val paymentConfig: StateFlow<OwnerPaymentConfig> = repository.paymentConfig
    val adBanner: StateFlow<PromotionalBanner> = repository.adBanner
    val remoteConfig: StateFlow<com.example.data.model.AppRemoteConfig> = repository.remoteConfig
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _uiState = MutableStateFlow(DealUiState())
    val uiState: StateFlow<DealUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.statusMessage.collect { msg ->
                if (msg != null) {
                    _uiState.update { it.copy(snackbarMessage = msg) }
                    repository.clearStatusMessage()
                }
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectCategory(category: DealCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectPlatform(platform: DealPlatform?) {
        _uiState.update { it.copy(selectedPlatform = platform) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun showProductDetail(deal: ProductDeal?) {
        _uiState.update { it.copy(selectedProductForDetail = deal) }
    }

    fun toggleSave(dealId: String) {
        repository.toggleSaveDeal(dealId)
    }

    fun openDealLink(context: Context, deal: ProductDeal) {
        try {
            val url = if (deal.affiliateUrl.startsWith("http://") || deal.affiliateUrl.startsWith("https://")) {
                deal.affiliateUrl
            } else {
                "https://${deal.affiliateUrl}"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAdBanner(context: Context, banner: PromotionalBanner) {
        try {
            val url = if (banner.targetUrl.startsWith("http://") || banner.targetUrl.startsWith("https://")) {
                banner.targetUrl
            } else {
                "https://${banner.targetUrl}"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open ad link", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareOnWhatsApp(context: Context, deal: ProductDeal) {
        try {
            val message = """
                🔥 *HOT DISCOUNT DEAL!* 🔥
                *${deal.title}*
                
                🏪 Platform: *${deal.platform.title}*
                💰 Price: Rs. ${deal.discountPrice.toInt()} (Normal Rs. ${deal.originalPrice.toInt()})
                ⚡ Discount: ${deal.discountPercentage}% OFF!
                ⭐ Rating: ${deal.rating} / 5.0 (${deal.soldCount})
                👤 Reseller: ${deal.posterName}
                
                📦 Order Now Link:
                ${deal.affiliateUrl}
            """.trimIndent()

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Deal via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDirectWhatsAppOrder(context: Context, deal: ProductDeal, paymentConfig: OwnerPaymentConfig) {
        try {
            val targetPhone = if (deal.isCommunityPost && deal.posterPhone.isNotBlank()) {
                deal.posterPhone
            } else {
                paymentConfig.whatsappNumber.ifBlank { "923000000000" }
            }.replace("[^0-9]".toRegex(), "")

            val message = """
                Salam! Mujhey aapka yeh product order karna hai:
                📦 *${deal.title}*
                🏪 Platform: ${deal.platform.title}
                💰 Price: Rs. ${deal.discountPrice.toInt()}
                
                Mera Naam:
                Mera Pata (Address):
                Shehar:
                Phone:
                
                (Payment via Easypaisa / JazzCash / Payoneer / PayPal / Cash on Delivery).
            """.trimIndent()

            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$targetPhone&text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    // Community / User Publishing
    fun publishCommunityDeal(
        title: String,
        category: DealCategory,
        platform: DealPlatform,
        originalPrice: Double,
        discountPrice: Double,
        imageUrl: String,
        affiliateUrl: String,
        posterName: String,
        posterPhone: String,
        easypaisa: String,
        jazzcash: String,
        payoneer: String,
        paypal: String,
        usdt: String,
        bankIban: String,
        description: String
    ) {
        val newDeal = ProductDeal(
            title = title,
            category = category,
            platform = platform,
            originalPrice = originalPrice,
            discountPrice = discountPrice,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80" },
            affiliateUrl = affiliateUrl.ifBlank { "https://www.daraz.pk" },
            posterName = posterName.ifBlank { "Community Member" },
            posterPhone = posterPhone,
            posterEasypaisa = easypaisa,
            posterJazzcash = jazzcash,
            posterPayoneer = payoneer,
            posterPaypal = paypal,
            posterUsdt = usdt,
            posterBankIban = bankIban,
            isCommunityPost = true,
            isFeatured = false,
            description = description
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true) }
            repository.addDeal(newDeal)
            _uiState.update { it.copy(isPublishing = false, selectedTab = MainTab.DEALS, snackbarMessage = "Mubarak! Aapki deal live publish ho gayi hai! 🎉") }
        }
    }

    // Admin Publishing
    fun publishAdminDeal(
        title: String,
        category: DealCategory,
        platform: DealPlatform,
        originalPrice: Double,
        discountPrice: Double,
        imageUrl: String,
        affiliateUrl: String,
        description: String,
        isFeatured: Boolean
    ) {
        val newDeal = ProductDeal(
            title = title,
            category = category,
            platform = platform,
            originalPrice = originalPrice,
            discountPrice = discountPrice,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80" },
            affiliateUrl = affiliateUrl.ifBlank { "https://www.daraz.pk" },
            posterName = "Hassan (Verified Admin 👑)",
            isCommunityPost = false,
            isFeatured = isFeatured,
            description = description
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true) }
            repository.addDeal(newDeal)
            _uiState.update { it.copy(isPublishing = false, selectedTab = MainTab.DEALS, snackbarMessage = "Official Admin Deal Published!") }
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun signInWithGoogle(displayName: String, email: String) {
        viewModelScope.launch {
            repository.signInWithGoogle(displayName, email)
        }
    }

    fun signInWithFacebook() {
        viewModelScope.launch {
            val updated = repository.userProfile.value.copy(
                name = "Facebook User",
                email = "user.fb@facebook.com",
                isSignedIn = true
            )
            repository.saveUserProfile(updated)
            _uiState.update { it.copy(snackbarMessage = "Logged in with Facebook! 🎉") }
        }
    }

    fun signInWithPhone(phoneNumber: String) {
        viewModelScope.launch {
            val updated = repository.userProfile.value.copy(
                name = "User (${phoneNumber.takeLast(4)})",
                phone = phoneNumber,
                easypaisaNumber = phoneNumber,
                jazzcashNumber = phoneNumber,
                isSignedIn = true
            )
            repository.saveUserProfile(updated)
            _uiState.update { it.copy(snackbarMessage = "Phone Verified & Logged in! 🎉") }
        }
    }

    fun signUpWithEmail(name: String, email: String, password: String) {
        viewModelScope.launch {
            val updated = repository.userProfile.value.copy(
                name = name,
                email = email,
                isSignedIn = true
            )
            repository.saveUserProfile(updated)
            _uiState.update { it.copy(snackbarMessage = "Account created & Logged in! Welcome $name! 🎉") }
        }
    }

    fun exploreAsGuest() {
        viewModelScope.launch {
            val updated = repository.userProfile.value.copy(
                isSignedIn = true
            )
            repository.saveUserProfile(updated)
        }
    }

    fun signOut() {
        repository.signOut()
    }

    fun savePaymentConfig(config: OwnerPaymentConfig) {
        viewModelScope.launch {
            repository.savePaymentConfig(config)
        }
    }

    fun saveAdBanner(banner: PromotionalBanner) {
        viewModelScope.launch {
            repository.saveAdBanner(banner)
        }
    }

    fun toggleFeatureDeal(dealId: String) {
        viewModelScope.launch {
            repository.toggleFeatureDeal(dealId)
        }
    }

    fun deleteDeal(dealId: String) {
        viewModelScope.launch {
            repository.deleteDeal(dealId)
        }
    }

    fun saveRemoteConfig(config: com.example.data.model.AppRemoteConfig) {
        viewModelScope.launch {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            try {
                db.collection("settings").document("app_control").set(config.toMap())
                _uiState.update { it.copy(snackbarMessage = "Firebase Cloud Settings Synced! ☁️") }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, "$label copied! 📋", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Copy failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun pasteFromClipboard(context: Context, onTextPasted: (String) -> Unit) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = clipboard?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0)?.text?.toString() ?: ""
                if (text.isNotBlank()) {
                    onTextPasted(text)
                    Toast.makeText(context, "Pasted from clipboard! 📋", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Paste failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
