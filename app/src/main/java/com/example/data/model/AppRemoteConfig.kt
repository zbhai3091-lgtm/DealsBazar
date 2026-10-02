package com.example.data.model

data class AppRemoteConfig(
    val announcement: String = "🎉 Deals Bazar Live: Multi-Platform Verified Deals from Daraz, AliExpress, Amazon & Markaz!",
    val showAnnouncement: Boolean = true,
    val isCommunityPostingAllowed: Boolean = true,
    val appVersion: String = "2.1.0",
    val supportWhatsapp: String = "923001234567",
    val adminEmail: String = "zbhai3091@gmail.com"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "announcement" to announcement,
        "showAnnouncement" to showAnnouncement,
        "isCommunityPostingAllowed" to isCommunityPostingAllowed,
        "appVersion" to appVersion,
        "supportWhatsapp" to supportWhatsapp,
        "adminEmail" to adminEmail
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): AppRemoteConfig {
            return AppRemoteConfig(
                announcement = (map["announcement"] as? String) ?: "🎉 Deals Bazar Live!",
                showAnnouncement = (map["showAnnouncement"] as? Boolean) ?: true,
                isCommunityPostingAllowed = (map["isCommunityPostingAllowed"] as? Boolean) ?: true,
                appVersion = (map["appVersion"] as? String) ?: "2.1.0",
                supportWhatsapp = (map["supportWhatsapp"] as? String) ?: "923001234567",
                adminEmail = (map["adminEmail"] as? String) ?: "zbhai3091@gmail.com"
            )
        }
    }
}
