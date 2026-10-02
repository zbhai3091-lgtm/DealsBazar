package com.example.data.model

data class UserProfile(
    val uid: String = "guest_user",
    val name: String = "Guest Reseller",
    val email: String = "user@example.com",
    val phone: String = "",
    val photoUrl: String = "",
    val isSignedIn: Boolean = false,
    val isGoogleUser: Boolean = false,
    // Pakistan Local Methods
    val easypaisaNumber: String = "",
    val easypaisaName: String = "",
    val jazzcashNumber: String = "",
    val jazzcashName: String = "",
    val sadapayNumber: String = "",
    val bankName: String = "",
    val bankIban: String = "",
    // International Payment Methods
    val payoneerEmail: String = "",
    val paypalEmail: String = "",
    val usdtTrc20Wallet: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "photoUrl" to photoUrl,
        "isSignedIn" to isSignedIn,
        "isGoogleUser" to isGoogleUser,
        "easypaisaNumber" to easypaisaNumber,
        "easypaisaName" to easypaisaName,
        "jazzcashNumber" to jazzcashNumber,
        "jazzcashName" to jazzcashName,
        "sadapayNumber" to sadapayNumber,
        "bankName" to bankName,
        "bankIban" to bankIban,
        "payoneerEmail" to payoneerEmail,
        "paypalEmail" to paypalEmail,
        "usdtTrc20Wallet" to usdtTrc20Wallet
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): UserProfile {
            return UserProfile(
                uid = (map["uid"] as? String) ?: "guest_user",
                name = (map["name"] as? String) ?: "Guest Reseller",
                email = (map["email"] as? String) ?: "",
                phone = (map["phone"] as? String) ?: "",
                photoUrl = (map["photoUrl"] as? String) ?: "",
                isSignedIn = (map["isSignedIn"] as? Boolean) ?: false,
                isGoogleUser = (map["isGoogleUser"] as? Boolean) ?: false,
                easypaisaNumber = (map["easypaisaNumber"] as? String) ?: "",
                easypaisaName = (map["easypaisaName"] as? String) ?: "",
                jazzcashNumber = (map["jazzcashNumber"] as? String) ?: "",
                jazzcashName = (map["jazzcashName"] as? String) ?: "",
                sadapayNumber = (map["sadapayNumber"] as? String) ?: "",
                bankName = (map["bankName"] as? String) ?: "",
                bankIban = (map["bankIban"] as? String) ?: "",
                payoneerEmail = (map["payoneerEmail"] as? String) ?: "",
                paypalEmail = (map["paypalEmail"] as? String) ?: "",
                usdtTrc20Wallet = (map["usdtTrc20Wallet"] as? String) ?: ""
            )
        }
    }
}
