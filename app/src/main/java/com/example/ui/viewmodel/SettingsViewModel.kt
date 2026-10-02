package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AccentPalette
import com.example.data.model.AuthUserState
import com.example.data.model.CloudUserSettings
import com.example.data.model.DiagnosticLog
import com.example.data.model.FirebaseProjectInfo
import com.example.data.model.LogLevel
import com.example.data.model.SyncFrequency
import com.example.data.model.ThemeMode
import com.example.data.repository.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SettingsTab(val title: String) {
    OVERVIEW("Overview"),
    AUTH("Account"),
    FIRESTORE("Cloud Sync"),
    PREFERENCES("Preferences"),
    DIAGNOSTICS("Diagnostics"),
    GUIDE("Setup Guide")
}

data class SettingsUiState(
    val selectedTab: SettingsTab = SettingsTab.OVERVIEW,
    val projectInfo: FirebaseProjectInfo = FirebaseProjectInfo(),
    val userState: AuthUserState = AuthUserState(),
    val settings: CloudUserSettings = CloudUserSettings(),
    val isConnected: Boolean = true,
    val isSyncing: Boolean = false,
    val isAuthenticating: Boolean = false,
    val isTesting: Boolean = false,
    val lastLatencyMs: Long? = null,
    val logs: List<DiagnosticLog> = emptyList(),
    val snackbarMessage: String? = null,
    val showAuthDialog: Boolean = false,
    val isRegisterMode: Boolean = false,
    val emailInput: String = "",
    val passwordInput: String = ""
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseManager = FirebaseManager(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Collect reactive state flows from repository
        viewModelScope.launch {
            firebaseManager.projectInfo.collect { info ->
                _uiState.update { it.copy(projectInfo = info) }
            }
        }
        viewModelScope.launch {
            firebaseManager.userState.collect { user ->
                _uiState.update { it.copy(userState = user) }
            }
        }
        viewModelScope.launch {
            firebaseManager.cloudSettings.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
        viewModelScope.launch {
            firebaseManager.logs.collect { logs ->
                _uiState.update { it.copy(logs = logs) }
            }
        }
        viewModelScope.launch {
            firebaseManager.isConnected.collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
        }
        viewModelScope.launch {
            firebaseManager.isSyncing.collect { syncing ->
                _uiState.update { it.copy(isSyncing = syncing) }
            }
        }
        viewModelScope.launch {
            firebaseManager.lastLatencyMs.collect { latency ->
                _uiState.update { it.copy(lastLatencyMs = latency) }
            }
        }
    }

    fun selectTab(tab: SettingsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateDisplayName(name: String) {
        val updated = _uiState.value.settings.copy(displayName = name)
        updateSettingsInternal(updated)
    }

    fun setThemeMode(mode: ThemeMode) {
        val updated = _uiState.value.settings.copy(themeMode = mode.name)
        updateSettingsInternal(updated)
        showSnackbar("Theme set to ${mode.name}")
    }

    fun setAccentPalette(palette: AccentPalette) {
        val updated = _uiState.value.settings.copy(accentPalette = palette.name)
        updateSettingsInternal(updated)
        showSnackbar("Accent changed to ${palette.displayName}")
    }

    fun toggleCloudSync(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(cloudSyncEnabled = enabled)
        updateSettingsInternal(updated)
        showSnackbar(if (enabled) "Cloud sync activated" else "Cloud sync paused")
    }

    fun setSyncFrequency(freq: SyncFrequency) {
        val updated = _uiState.value.settings.copy(syncFrequency = freq.name)
        updateSettingsInternal(updated)
        showSnackbar("Sync frequency: ${freq.displayName}")
    }

    fun toggleOfflineCache(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(offlineCacheEnabled = enabled)
        updateSettingsInternal(updated)
        showSnackbar(if (enabled) "Offline cache enabled" else "Offline cache disabled")
    }

    fun toggleNotifySync(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(notifySyncComplete = enabled)
        updateSettingsInternal(updated)
    }

    fun toggleNotifySecurity(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(notifySecurityAlerts = enabled)
        updateSettingsInternal(updated)
    }

    fun toggleNotifyQuota(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(notifyQuotaWarnings = enabled)
        updateSettingsInternal(updated)
    }

    fun toggleDeveloperMode(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(developerMode = enabled)
        updateSettingsInternal(updated)
    }

    private fun updateSettingsInternal(newSettings: CloudUserSettings) {
        _uiState.update { it.copy(settings = newSettings) }
        firebaseManager.updateLocalSettings(newSettings)
        if (newSettings.cloudSyncEnabled) {
            viewModelScope.launch {
                firebaseManager.saveSettingsToCloud(newSettings)
            }
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            val result = firebaseManager.saveSettingsToCloud(_uiState.value.settings)
            if (result.isSuccess) {
                showSnackbar("Settings successfully synced to Firebase Firestore!")
            } else {
                showSnackbar("Sync error: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun triggerFetchCloudSettings() {
        viewModelScope.launch {
            val result = firebaseManager.fetchSettingsFromCloud()
            if (result.isSuccess) {
                showSnackbar("Successfully fetched settings from Firebase Firestore!")
            } else {
                showSnackbar("Fetch failed: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthenticating = true) }
            val result = firebaseManager.signInAnonymously()
            _uiState.update { it.copy(isAuthenticating = false) }
            if (result.isSuccess) {
                showSnackbar("Signed in anonymously!")
            } else {
                showSnackbar("Sign in error: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun openAuthDialog(registerMode: Boolean) {
        _uiState.update {
            it.copy(
                showAuthDialog = true,
                isRegisterMode = registerMode,
                emailInput = "",
                passwordInput = ""
            )
        }
    }

    fun dismissAuthDialog() {
        _uiState.update { it.copy(showAuthDialog = false) }
    }

    fun updateEmailInput(email: String) {
        _uiState.update { it.copy(emailInput = email) }
    }

    fun updatePasswordInput(password: String) {
        _uiState.update { it.copy(passwordInput = password) }
    }

    fun submitEmailAuth() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput
        if (email.isEmpty() || !email.contains("@")) {
            showSnackbar("Please enter a valid email address")
            return
        }
        if (password.length < 6) {
            showSnackbar("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAuthenticating = true) }
            val isRegister = _uiState.value.isRegisterMode
            val result = if (isRegister) {
                firebaseManager.registerWithEmail(email, password)
            } else {
                firebaseManager.signInWithEmail(email, password)
            }
            _uiState.update { it.copy(isAuthenticating = false, showAuthDialog = !result.isSuccess) }

            if (result.isSuccess) {
                showSnackbar(if (isRegister) "Account created & signed in!" else "Signed in successfully!")
            } else {
                showSnackbar("Authentication error: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun signOut() {
        firebaseManager.signOut()
        showSnackbar("Signed out of Firebase")
    }

    fun runDiagnosticsWrite() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true) }
            val result = firebaseManager.testFirestoreWrite()
            _uiState.update { it.copy(isTesting = false) }
            if (result.isSuccess) {
                showSnackbar("Cloud write succeeded in ${result.getOrNull()}ms!")
            } else {
                showSnackbar("Write failed: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun runDiagnosticsRead() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true) }
            val result = firebaseManager.testFirestoreRead()
            _uiState.update { it.copy(isTesting = false) }
            if (result.isSuccess) {
                showSnackbar("Cloud read succeeded in ${result.getOrNull()}ms!")
            } else {
                showSnackbar("Read failed: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun pingConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true) }
            val latency = firebaseManager.pingFirestore()
            _uiState.update { it.copy(isTesting = false) }
            if (latency != null) {
                showSnackbar("Firestore ping latency: ${latency}ms")
            } else {
                showSnackbar("Firestore ping completed")
            }
        }
    }

    fun clearLogs() {
        firebaseManager.clearLogs()
        showSnackbar("Logs cleared")
    }

    fun resetSettingsToDefault() {
        val defaults = CloudUserSettings()
        updateSettingsInternal(defaults)
        showSnackbar("Settings reset to defaults")
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
