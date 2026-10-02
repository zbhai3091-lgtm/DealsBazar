package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.DealCategory
import com.example.data.model.DealPlatform
import com.example.data.model.OwnerPaymentConfig
import com.example.data.model.ProductDeal
import com.example.data.model.PromotionalBanner
import com.example.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DealRepository(private val context: Context) {

    private val tag = "DealRepository"
    private val scope = CoroutineScope(Dispatchers.IO)

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var dealsListener: ListenerRegistration? = null

    private val _deals = MutableStateFlow<List<ProductDeal>>(emptyList())
    val deals: StateFlow<List<ProductDeal>> = _deals.asStateFlow()

    private val _savedDealIds = MutableStateFlow<Set<String>>(emptySet())
    val savedDealIds: StateFlow<Set<String>> = _savedDealIds.asStateFlow()

    private val _paymentConfig = MutableStateFlow(OwnerPaymentConfig())
    val paymentConfig: StateFlow<OwnerPaymentConfig> = _paymentConfig.asStateFlow()

    private val _adBanner = MutableStateFlow(PromotionalBanner())
    val adBanner: StateFlow<PromotionalBanner> = _adBanner.asStateFlow()

    private val _remoteConfig = MutableStateFlow(com.example.data.model.AppRemoteConfig())
    val remoteConfig: StateFlow<com.example.data.model.AppRemoteConfig> = _remoteConfig.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            uid = "user_me",
            name = "Guest User",
            email = "",
            phone = "",
            isSignedIn = false,
            isGoogleUser = false
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        initFirebase()
    }

    private fun initFirebase() {
        try {
            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                FirebaseApp.getInstance()
            }

            auth = FirebaseAuth.getInstance()

            val customDbId = try {
                val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
                if (resId != 0) context.getString(resId) else null
            } catch (e: Exception) {
                null
            }

            firestore = if (!customDbId.isNullOrEmpty() && app != null) {
                try {
                    FirebaseFirestore.getInstance(app, customDbId)
                } catch (e: Exception) {
                    FirebaseFirestore.getInstance()
                }
            } else {
                FirebaseFirestore.getInstance()
            }

            if (auth?.currentUser == null) {
                auth?.signInAnonymously()?.addOnCompleteListener {
                    listenToDeals()
                }
            } else {
                listenToDeals()
            }

        } catch (e: Exception) {
            Log.e(tag, "Firebase init error: ${e.message}", e)
            _deals.value = getInitialStarterDeals()
            _isLoading.value = false
        }
    }

    private fun listenToDeals() {
        val db = firestore
        if (db == null) {
            _deals.value = getInitialStarterDeals()
            _isLoading.value = false
            return
        }

        try {
            dealsListener?.remove()
            val collection = db.collection("daraz_deals")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            dealsListener = collection.addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error != null) {
                    Log.w(tag, "Deals listener error: ${error.message}")
                    if (_deals.value.isEmpty()) {
                        _deals.value = getInitialStarterDeals()
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { ProductDeal.fromMap(it) }
                    }
                    if (list.isEmpty()) {
                        seedStarterDeals()
                    } else {
                        _deals.value = list
                    }
                }
            }

            // Payment settings listener
            db.collection("settings").document("owner_payments")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                        _paymentConfig.value = OwnerPaymentConfig.fromMap(snapshot.data!!)
                    }
                }

            // Ad banner listener
            db.collection("settings").document("ad_banner")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                        _adBanner.value = PromotionalBanner.fromMap(snapshot.data!!)
                    }
                }

            // Remote App Feature Control listener from Firebase Cloud
            db.collection("settings").document("app_control")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                        _remoteConfig.value = com.example.data.model.AppRemoteConfig.fromMap(snapshot.data!!)
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Error setting snapshot listener", e)
            _deals.value = getInitialStarterDeals()
            _isLoading.value = false
        }
    }

    private fun seedStarterDeals() {
        val starters = getInitialStarterDeals()
        _deals.value = starters
        val db = firestore ?: return
        scope.launch {
            starters.forEach { deal ->
                try {
                    db.collection("daraz_deals").document(deal.id).set(deal.toMap()).await()
                } catch (e: Exception) {
                    Log.w(tag, "Error seeding deal: ${e.message}")
                }
            }
        }
    }

    private fun getInitialStarterDeals(): List<ProductDeal> {
        val now = System.currentTimeMillis()
        return listOf(
            ProductDeal(
                id = "deal_1",
                title = "T800 Ultra Smart Watch - Bluetooth Call & Heart Rate",
                category = DealCategory.TECH,
                platform = DealPlatform.DARAZ,
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                originalPrice = 3500.0,
                discountPrice = 1899.0,
                affiliateUrl = "https://www.daraz.pk/products/t800-ultra-smart-watch-i428000000.html",
                rating = 4.9f,
                soldCount = "4,200+ Sold",
                isFeatured = true,
                posterName = "Hassan (Verified Admin 👑)",
                isCommunityPost = false,
                posterEasypaisa = "03451234567",
                posterJazzcash = "03001234567",
                posterPayoneer = "zbhai3091@gmail.com",
                posterUsdt = "TX7abc123DEF456ghi789JKL012mno",
                description = "Full HD Touchscreen, Wireless Charging, 2 Straps included, Water resistant.",
                timestamp = now
            ),
            ProductDeal(
                id = "deal_2",
                title = "AliExpress Smart Galaxy Star Astronaut Projector 360°",
                category = DealCategory.HOME,
                platform = DealPlatform.ALIEXPRESS,
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
                originalPrice = 4500.0,
                discountPrice = 2490.0,
                affiliateUrl = "https://www.aliexpress.com",
                rating = 4.8f,
                soldCount = "12k+ Global Orders",
                isFeatured = true,
                posterName = "Ali Tech Store (Reseller)",
                isCommunityPost = true,
                posterPhone = "03001234567",
                posterEasypaisa = "03459876543",
                posterJazzcash = "03001234567",
                posterPaypal = "ali.store@paypal.com",
                posterUsdt = "TY8def456GHI789jkl012PQR345stu",
                description = "AliExpress Choice Direct Shipping to Pakistan. Nebula starry night lamp with remote control.",
                timestamp = now - 3600000
            ),
            ProductDeal(
                id = "deal_3",
                title = "Pro 6 Wireless Bluetooth Earbuds Deep Bass",
                category = DealCategory.TECH,
                platform = DealPlatform.DARAZ,
                imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
                originalPrice = 2200.0,
                discountPrice = 999.0,
                affiliateUrl = "https://www.daraz.pk/products/pro-6-wireless-earbuds-i429000000.html",
                rating = 4.7f,
                soldCount = "3,150+ Sold",
                isFeatured = true,
                posterName = "Hassan (Verified Admin 👑)",
                isCommunityPost = false,
                posterEasypaisa = "03451234567",
                posterJazzcash = "03001234567",
                description = "Deep Bass, Noise Cancellation, 24 Hours Battery with Power Case.",
                timestamp = now - 7200000
            ),
            ProductDeal(
                id = "deal_4",
                title = "Amazon Basics RGB Gaming Mechanical Keyboard",
                category = DealCategory.TECH,
                platform = DealPlatform.AMAZON,
                imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80",
                originalPrice = 7500.0,
                discountPrice = 4990.0,
                affiliateUrl = "https://www.amazon.com",
                rating = 4.9f,
                soldCount = "8,400+ Sold",
                isFeatured = false,
                posterName = "Gaming Hub PK (Community)",
                isCommunityPost = true,
                posterPhone = "03217654321",
                posterBankIban = "PK55HABB0009876543210123",
                posterPayoneer = "gaminghub.pk@payoneer.com",
                description = "Amazon Associates Global Deal. Blue mechanical switches with customizable RGB lighting.",
                timestamp = now - 10800000
            ),
            ProductDeal(
                id = "deal_5",
                title = "Markaz 3-Piece Stitched Summer Lawn Suit Embroidered",
                category = DealCategory.FASHION,
                platform = DealPlatform.MARKAZ,
                imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80",
                originalPrice = 3200.0,
                discountPrice = 1950.0,
                affiliateUrl = "https://www.markaz.app",
                rating = 4.9f,
                soldCount = "1,800+ Delivered",
                isFeatured = true,
                posterName = "Zainab Boutique (Markaz Reseller)",
                isCommunityPost = true,
                posterPhone = "03459876543",
                posterEasypaisa = "03459876543",
                posterJazzcash = "03009876543",
                description = "Markaz Wholesale Direct: Cash On Delivery all over Pakistan, Rs. 0 investment, Rs. 600 reseller profit.",
                timestamp = now - 14400000
            ),
            ProductDeal(
                id = "deal_6",
                title = "Hostinger Premium Web Hosting + Free Domain (.com)",
                category = DealCategory.TECH,
                platform = DealPlatform.DIGITAL,
                imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=600&auto=format&fit=crop&q=80",
                originalPrice = 9000.0,
                discountPrice = 3200.0,
                affiliateUrl = "https://www.hostinger.com",
                rating = 4.9f,
                soldCount = "5k+ Registrations",
                isFeatured = false,
                posterName = "Hassan (Verified Admin 👑)",
                isCommunityPost = false,
                posterPayoneer = "zbhai3091@gmail.com",
                posterPaypal = "zbhai3091@gmail.com",
                posterUsdt = "TX7abc123DEF456ghi789JKL012mno",
                description = "Huge Affiliate Payout! Earn up to $40 (Rs. 11,000) per referral sale from Hostinger Affiliate.",
                timestamp = now - 18000000
            )
        )
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        _userProfile.value = profile
        val db = firestore
        if (db != null) {
            return try {
                db.collection("users").document(profile.uid).set(profile.toMap()).await()
                _statusMessage.value = "Profile & Payout methods saved!"
                Result.success(Unit)
            } catch (e: Exception) {
                _statusMessage.value = "Profile saved locally"
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    suspend fun signInWithGoogle(displayName: String, email: String): Result<Unit> {
        val updated = _userProfile.value.copy(
            name = displayName.ifBlank { "Google User" },
            email = email.ifBlank { "user@gmail.com" },
            isSignedIn = true,
            isGoogleUser = true
        )
        return saveUserProfile(updated)
    }

    fun signOut() {
        _userProfile.update { it.copy(isSignedIn = false) }
        _statusMessage.value = "Signed out successfully"
    }

    suspend fun savePaymentConfig(config: OwnerPaymentConfig): Result<Unit> {
        _paymentConfig.value = config
        val db = firestore
        if (db != null) {
            return try {
                db.collection("settings").document("owner_payments").set(config.toMap()).await()
                _statusMessage.value = "Admin payment details updated!"
                Result.success(Unit)
            } catch (e: Exception) {
                _statusMessage.value = "Payment details saved locally"
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    suspend fun saveAdBanner(banner: PromotionalBanner): Result<Unit> {
        _adBanner.value = banner
        val db = firestore
        if (db != null) {
            return try {
                db.collection("settings").document("ad_banner").set(banner.toMap()).await()
                _statusMessage.value = "Promotional Ad Banner updated on all devices!"
                Result.success(Unit)
            } catch (e: Exception) {
                _statusMessage.value = "Ad banner saved"
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    suspend fun addDeal(deal: ProductDeal): Result<Unit> {
        _deals.update { current -> listOf(deal) + current.filter { it.id != deal.id } }
        val db = firestore
        if (db != null) {
            return try {
                db.collection("daraz_deals").document(deal.id).set(deal.toMap()).await()
                _statusMessage.value = if (deal.isCommunityPost) "Your deal is now live for all users!" else "Admin deal published to Cloud!"
                Result.success(Unit)
            } catch (e: Exception) {
                _statusMessage.value = "Deal saved locally"
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    suspend fun toggleFeatureDeal(dealId: String): Result<Unit> {
        val target = _deals.value.firstOrNull { it.id == dealId } ?: return Result.success(Unit)
        val updated = target.copy(isFeatured = !target.isFeatured)
        _deals.update { current -> current.map { if (it.id == dealId) updated else it } }
        val db = firestore
        if (db != null) {
            try {
                db.collection("daraz_deals").document(dealId).update("isFeatured", updated.isFeatured).await()
                _statusMessage.value = if (updated.isFeatured) "Deal marked as Featured ⭐" else "Deal unfeatured"
            } catch (e: Exception) {
                // local update done
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteDeal(id: String): Result<Unit> {
        _deals.update { current -> current.filter { it.id != id } }
        val db = firestore
        if (db != null) {
            return try {
                db.collection("daraz_deals").document(id).delete().await()
                _statusMessage.value = "Deal removed"
                Result.success(Unit)
            } catch (e: Exception) {
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    fun toggleSaveDeal(dealId: String) {
        _savedDealIds.update { set ->
            if (set.contains(dealId)) set - dealId else set + dealId
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
