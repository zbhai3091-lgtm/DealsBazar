package com.example.data.model

import java.util.UUID

enum class DealPlatform(val title: String, val iconEmoji: String, val brandColorHex: Long) {
    DARAZ("Daraz", "🛒", 0xFFEA580C),
    ALIEXPRESS("AliExpress", "🚀", 0xFFE11D48),
    AMAZON("Amazon", "📦", 0xFFD97706),
    MARKAZ("Markaz", "🛍️", 0xFF7C3AED),
    DIGITAL("Digital & Tech", "🌐", 0xFF0284C7);

    companion object {
        fun fromName(name: String?): DealPlatform {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: DARAZ
        }
    }
}

enum class DealCategory(val title: String, val iconEmoji: String) {
    ALL("Sab Deals", "🔥"),
    TECH("Mobiles & Tech", "📱"),
    FASHION("Fashion & Clothes", "👗"),
    HOME("Home & Kitchen", "🏠"),
    BEAUTY("Beauty & Care", "💄"),
    FLASH("Under Rs. 999", "⚡");

    companion object {
        fun fromName(name: String?): DealCategory {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: TECH
        }
    }
}

data class PromotionalBanner(
    val id: String = "main_ad",
    val title: String = "🔥 Super Summer Sale - 70% Off",
    val subtitle: String = "Get Exclusive Deals on Daraz & AliExpress",
    val imageUrl: String = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=1000&auto=format&fit=crop&q=80",
    val targetUrl: String = "https://www.daraz.pk",
    val sponsorName: String = "Daraz Official",
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "subtitle" to subtitle,
        "imageUrl" to imageUrl,
        "targetUrl" to targetUrl,
        "sponsorName" to sponsorName,
        "isActive" to isActive
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): PromotionalBanner {
            return PromotionalBanner(
                id = (map["id"] as? String) ?: "main_ad",
                title = (map["title"] as? String) ?: "Special Promotion",
                subtitle = (map["subtitle"] as? String) ?: "Limited Time Deals",
                imageUrl = (map["imageUrl"] as? String) ?: "",
                targetUrl = (map["targetUrl"] as? String) ?: "https://www.daraz.pk",
                sponsorName = (map["sponsorName"] as? String) ?: "Sponsored Ad",
                isActive = (map["isActive"] as? Boolean) ?: true
            )
        }
    }
}

data class ProductDeal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: DealCategory,
    val platform: DealPlatform = DealPlatform.DARAZ,
    val imageUrl: String,
    val originalPrice: Double,
    val discountPrice: Double,
    val affiliateUrl: String,
    val rating: Float = 4.8f,
    val soldCount: String = "1,500+ Sold",
    val isFeatured: Boolean = false,
    val isVerifiedDaraz: Boolean = true,
    val posterName: String = "Hassan (Admin)",
    val posterPhone: String = "",
    val isCommunityPost: Boolean = false,
    // Seller Payout Methods (Local & International)
    val posterEasypaisa: String = "",
    val posterJazzcash: String = "",
    val posterBankIban: String = "",
    val posterPayoneer: String = "",
    val posterPaypal: String = "",
    val posterUsdt: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val discountPercentage: Int
        get() {
            return if (originalPrice > discountPrice && originalPrice > 0) {
                (((originalPrice - discountPrice) / originalPrice) * 100).toInt()
            } else 0
        }

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "category" to category.name,
        "platform" to platform.name,
        "imageUrl" to imageUrl,
        "originalPrice" to originalPrice,
        "discountPrice" to discountPrice,
        "affiliateUrl" to affiliateUrl,
        "rating" to rating.toDouble(),
        "soldCount" to soldCount,
        "isFeatured" to isFeatured,
        "isVerifiedDaraz" to isVerifiedDaraz,
        "posterName" to posterName,
        "posterPhone" to posterPhone,
        "isCommunityPost" to isCommunityPost,
        "posterEasypaisa" to posterEasypaisa,
        "posterJazzcash" to posterJazzcash,
        "posterBankIban" to posterBankIban,
        "posterPayoneer" to posterPayoneer,
        "posterPaypal" to posterPaypal,
        "posterUsdt" to posterUsdt,
        "description" to description,
        "timestamp" to timestamp
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): ProductDeal {
            val cat = DealCategory.fromName(map["category"] as? String)
            val plat = DealPlatform.fromName(map["platform"] as? String)
            return ProductDeal(
                id = (map["id"] as? String) ?: UUID.randomUUID().toString(),
                title = (map["title"] as? String) ?: "Smart Deal",
                category = cat,
                platform = plat,
                imageUrl = (map["imageUrl"] as? String) ?: "",
                originalPrice = (map["originalPrice"] as? Number)?.toDouble() ?: 0.0,
                discountPrice = (map["discountPrice"] as? Number)?.toDouble() ?: 0.0,
                affiliateUrl = (map["affiliateUrl"] as? String) ?: "https://www.daraz.pk",
                rating = (map["rating"] as? Number)?.toFloat() ?: 4.8f,
                soldCount = (map["soldCount"] as? String) ?: "1k+ Sold",
                isFeatured = (map["isFeatured"] as? Boolean) ?: false,
                isVerifiedDaraz = (map["isVerifiedDaraz"] as? Boolean) ?: true,
                posterName = (map["posterName"] as? String) ?: "Hassan (Admin)",
                posterPhone = (map["posterPhone"] as? String) ?: "",
                isCommunityPost = (map["isCommunityPost"] as? Boolean) ?: false,
                posterEasypaisa = (map["posterEasypaisa"] as? String) ?: "",
                posterJazzcash = (map["posterJazzcash"] as? String) ?: "",
                posterBankIban = (map["posterBankIban"] as? String) ?: "",
                posterPayoneer = (map["posterPayoneer"] as? String) ?: "",
                posterPaypal = (map["posterPaypal"] as? String) ?: "",
                posterUsdt = (map["posterUsdt"] as? String) ?: "",
                description = (map["description"] as? String) ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
