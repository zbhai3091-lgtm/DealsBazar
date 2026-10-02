package com.example.data.model

import com.google.firebase.auth.FirebaseUser

data class FirebaseProjectInfo(
    val projectId: String = "creatorhub-721ac",
    val projectNumber: String = "954883172278",
    val storageBucket: String = "creatorhub-721ac.firebasestorage.app",
    val packageName: String = "com.example",
    val mobileSdkAppId: String = "1:954883172278:android:1e00e52f4d54f6b4f38317",
    val databaseId: String = "ai-studio-android-c696480d-e669-45d5-bf61-7d25c10d14ce",
    val isConfigured: Boolean = true
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AccentPalette(val displayName: String, val hexCode: String) {
    FIREBASE_AMBER("Firebase Amber", "#FF8F00"),
    DEEP_INDIGO("Deep Indigo", "#3F51B5"),
    EMERALD_GREEN("Emerald", "#10B981"),
    CYAN_BLUE("Electric Cyan", "#06B6D4"),
    ROSE_CRIMSON("Rose Crimson", "#F43F5E")
}

enum class SyncFrequency(val displayName: String) {
    REALTIME("Real-time Live Sync"),
    HOURLY("Hourly Auto-Sync"),
    DAILY("Daily Auto-Sync"),
    MANUAL("Manual Sync Only")
}

data class CloudUserSettings(
    val displayName: String = "Firebase Explorer",
    val themeMode: String = ThemeMode.SYSTEM.name,
    val accentPalette: String = AccentPalette.FIREBASE_AMBER.name,
    val cloudSyncEnabled: Boolean = true,
    val syncFrequency: String = SyncFrequency.REALTIME.name,
    val offlineCacheEnabled: Boolean = true,
    val notifySyncComplete: Boolean = true,
    val notifySecurityAlerts: Boolean = true,
    val notifyQuotaWarnings: Boolean = false,
    val developerMode: Boolean = true,
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.0"
) {
    fun toMap(): Map<String, Any> = mapOf(
        "displayName" to displayName,
        "themeMode" to themeMode,
        "accentPalette" to accentPalette,
        "cloudSyncEnabled" to cloudSyncEnabled,
        "syncFrequency" to syncFrequency,
        "offlineCacheEnabled" to offlineCacheEnabled,
        "notifySyncComplete" to notifySyncComplete,
        "notifySecurityAlerts" to notifySecurityAlerts,
        "notifyQuotaWarnings" to notifyQuotaWarnings,
        "developerMode" to developerMode,
        "lastSyncedTimestamp" to lastSyncedTimestamp,
        "appVersion" to appVersion
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): CloudUserSettings {
            return CloudUserSettings(
                displayName = (map["displayName"] as? String) ?: "Firebase Explorer",
                themeMode = (map["themeMode"] as? String) ?: ThemeMode.SYSTEM.name,
                accentPalette = (map["accentPalette"] as? String) ?: AccentPalette.FIREBASE_AMBER.name,
                cloudSyncEnabled = (map["cloudSyncEnabled"] as? Boolean) ?: true,
                syncFrequency = (map["syncFrequency"] as? String) ?: SyncFrequency.REALTIME.name,
                offlineCacheEnabled = (map["offlineCacheEnabled"] as? Boolean) ?: true,
                notifySyncComplete = (map["notifySyncComplete"] as? Boolean) ?: true,
                notifySecurityAlerts = (map["notifySecurityAlerts"] as? Boolean) ?: true,
                notifyQuotaWarnings = (map["notifyQuotaWarnings"] as? Boolean) ?: false,
                developerMode = (map["developerMode"] as? Boolean) ?: true,
                lastSyncedTimestamp = (map["lastSyncedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                appVersion = (map["appVersion"] as? String) ?: "1.0.0"
            )
        }
    }
}

enum class LogLevel {
    SUCCESS, INFO, WARNING, ERROR
}

data class DiagnosticLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO,
    val latencyMs: Long? = null
)

data class AuthUserState(
    val isAuthenticated: Boolean = false,
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val isAnonymous: Boolean = false,
    val creationTime: Long? = null,
    val lastSignInTime: Long? = null
) {
    companion object {
        fun fromFirebaseUser(user: FirebaseUser?): AuthUserState {
            return if (user == null) {
                AuthUserState()
            } else {
                AuthUserState(
                    isAuthenticated = true,
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName ?: if (user.isAnonymous) "Anonymous User" else user.email?.substringBefore('@'),
                    isAnonymous = user.isAnonymous,
                    creationTime = user.metadata?.creationTimestamp,
                    lastSignInTime = user.metadata?.lastSignInTimestamp
                )
            }
        }
    }
}
