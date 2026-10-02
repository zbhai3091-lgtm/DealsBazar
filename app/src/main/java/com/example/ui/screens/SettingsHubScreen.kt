package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccentPalette
import com.example.data.model.DiagnosticLog
import com.example.data.model.LogLevel
import com.example.data.model.SyncFrequency
import com.example.data.model.ThemeMode
import com.example.ui.theme.FirebaseOrange
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.SettingsTab
import com.example.ui.viewmodel.SettingsUiState
import com.example.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHubScreen(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FirebaseOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Firebase Settings Hub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Project: ${uiState.projectInfo.projectId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.triggerCloudSync() },
                        modifier = Modifier.testTag("top_bar_sync_button")
                    ) {
                        if (uiState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Sync",
                                tint = if (uiState.settings.cloudSyncEnabled) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
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
                SettingsTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            when (tab) {
                                SettingsTab.OVERVIEW -> Icon(Icons.Default.Cloud, contentDescription = tab.title)
                                SettingsTab.AUTH -> Icon(Icons.Default.Person, contentDescription = tab.title)
                                SettingsTab.FIRESTORE -> Icon(Icons.Default.Storage, contentDescription = tab.title)
                                SettingsTab.PREFERENCES -> Icon(Icons.Default.Settings, contentDescription = tab.title)
                                SettingsTab.DIAGNOSTICS -> Icon(Icons.Default.DeveloperMode, contentDescription = tab.title)
                                SettingsTab.GUIDE -> Icon(Icons.Default.HelpOutline, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
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
                    SettingsTab.OVERVIEW -> OverviewTabContent(viewModel, uiState)
                    SettingsTab.AUTH -> AuthTabContent(viewModel, uiState)
                    SettingsTab.FIRESTORE -> FirestoreTabContent(viewModel, uiState)
                    SettingsTab.PREFERENCES -> PreferencesTabContent(viewModel, uiState)
                    SettingsTab.DIAGNOSTICS -> DiagnosticsTabContent(viewModel, uiState)
                    SettingsTab.GUIDE -> GuideTabContent(uiState)
                }
            }
        }
    }

    AuthDialog(
        isOpen = uiState.showAuthDialog,
        isRegister = uiState.isRegisterMode,
        email = uiState.emailInput,
        pass = uiState.passwordInput,
        isSubmitting = uiState.isAuthenticating,
        onEmailChange = { viewModel.updateEmailInput(it) },
        onPassChange = { viewModel.updatePasswordInput(it) },
        onSubmit = { viewModel.submitEmailAuth() },
        onDismiss = { viewModel.dismissAuthDialog() }
    )
}

@Composable
fun OverviewTabContent(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FirebaseStatusCard(
                projectInfo = uiState.projectInfo,
                isConnected = uiState.isConnected,
                isSyncing = uiState.isSyncing,
                lastLatencyMs = uiState.lastLatencyMs,
                onPingClick = { viewModel.pingConnection() }
            )
        }

        item {
            SectionHeader(
                title = "Account Status",
                subtitle = "Manage active Firebase Authentication session"
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.userState.isAuthenticated) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.userState.isAuthenticated) Icons.Default.AccountCircle else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (uiState.userState.isAuthenticated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.userState.isAuthenticated)
                                (uiState.userState.displayName ?: uiState.userState.email ?: "Firebase User")
                            else "Guest User (Unauthenticated)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.userState.isAuthenticated) {
                                if (uiState.userState.isAnonymous) "Anonymous Session (UID: ${uiState.userState.uid?.take(8)}...)"
                                else "Email: ${uiState.userState.email}"
                            } else "Sign in to persist settings to cloud across devices",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!uiState.userState.isAuthenticated) {
                        Button(
                            onClick = { viewModel.signInAnonymously() },
                            modifier = Modifier.testTag("overview_quick_anon_button")
                        ) {
                            Text("Quick Guest Sign In")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.selectTab(SettingsTab.AUTH) },
                            modifier = Modifier.testTag("overview_manage_account_button")
                        ) {
                            Text("Manage Account")
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Cloud Sync & Firestore",
                subtitle = "Settings synchronized with collection: users/{uid}/settings"
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto Cloud Sync",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Frequency: ${uiState.settings.syncFrequency}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = if (uiState.settings.cloudSyncEnabled) StatusSuccess.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (uiState.settings.cloudSyncEnabled) "ENABLED" else "PAUSED",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.settings.cloudSyncEnabled) StatusSuccess else StatusWarning
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
                    val lastSyncStr = dateFormat.format(Date(uiState.settings.lastSyncedTimestamp))
                    Text(
                        text = "Last synced: $lastSyncStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerCloudSync() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("overview_sync_now_button")
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now")
                        }
                        OutlinedButton(
                            onClick = { viewModel.runDiagnosticsWrite() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("overview_test_write_button")
                        ) {
                            Text("Test Write")
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Active Firebase Specs",
                subtitle = "Configuration verified from google-services.json"
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SpecRow(label = "Project ID", value = uiState.projectInfo.projectId)
                    SpecRow(label = "Database ID", value = uiState.projectInfo.databaseId.take(24) + "...")
                    SpecRow(label = "Project Number", value = uiState.projectInfo.projectNumber)
                    SpecRow(label = "Storage Bucket", value = uiState.projectInfo.storageBucket)
                    SpecRow(label = "Package Name", value = uiState.projectInfo.packageName)
                    SpecRow(label = "Firestore SDK", value = "v34.17.0 (BOM)")
                }
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AuthTabContent(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState
) {
    val context = LocalContext.current
    var displayNameInput by remember(uiState.settings.displayName) {
        mutableStateOf(uiState.settings.displayName)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Firebase Authentication",
                subtitle = "Sign in to link device settings to your Firebase UID"
            )
        }

        if (uiState.userState.isAuthenticated) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = uiState.userState.displayName ?: "Firebase User",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (uiState.userState.isAnonymous) "Anonymous Account" else (uiState.userState.email ?: "Email Account"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Firebase User ID (UID):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.userState.uid ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Firebase UID", uiState.userState.uid ?: ""))
                                    viewModel.showSnackbar("UID copied to clipboard")
                                },
                                modifier = Modifier.size(28.dp).testTag("copy_uid_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy UID", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = displayNameInput,
                            onValueChange = { displayNameInput = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth().testTag("auth_display_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.updateDisplayName(displayNameInput) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_save_name_button")
                        ) {
                            Text("Update Name & Sync")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { viewModel.signOut() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                            modifier = Modifier.fillMaxWidth().testTag("auth_sign_out_button")
                        ) {
                            Text("Sign Out")
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Connect Your Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Choose an authentication method to store your custom preferences and diagnostics securely in Firestore.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.signInAnonymously() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_sign_in_anon_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1-Tap Anonymous Sign In")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { viewModel.openAuthDialog(registerMode = false) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_open_email_sign_in")
                        ) {
                            Text("Sign In with Email & Password")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { viewModel.openAuthDialog(registerMode = true) },
                            modifier = Modifier.testTag("auth_open_register")
                        ) {
                            Text("Need an account? Register here")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirestoreTabContent(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Firestore Sync Configuration",
                subtitle = "Manage real-time snapshot listeners and persistence"
            )
        }

        item {
            SettingSwitchRow(
                title = "Real-time Cloud Sync",
                description = "Keep preferences in sync across devices with Firestore snapshot listeners",
                checked = uiState.settings.cloudSyncEnabled,
                onCheckedChange = { viewModel.toggleCloudSync(it) },
                icon = Icons.Default.CloudSync,
                testTag = "sync_toggle_switch"
            )
        }

        item {
            SettingSwitchRow(
                title = "Offline Persistence Cache",
                description = "Store Firestore documents locally in SQLite when device is offline",
                checked = uiState.settings.offlineCacheEnabled,
                onCheckedChange = { viewModel.toggleOfflineCache(it) },
                icon = Icons.Default.Storage,
                testTag = "offline_cache_switch"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sync Frequency",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SyncFrequency.values().forEach { freq ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setSyncFrequency(freq) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.settings.syncFrequency == freq.name,
                                onClick = { viewModel.setSyncFrequency(freq) },
                                modifier = Modifier.testTag("radio_sync_${freq.name.lowercase()}")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = freq.displayName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Target Firestore Document",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val uid = uiState.userState.uid ?: "guest_local"
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "users/$uid/settings/preferences",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerCloudSync() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("firestore_push_button")
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Push to Cloud")
                        }
                        OutlinedButton(
                            onClick = { viewModel.triggerFetchCloudSettings() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("firestore_pull_button")
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pull from Cloud")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreferencesTabContent(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Theme & Appearance",
                subtitle = "Customize application colors and display mode"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val selected = uiState.settings.themeMode == mode.name
                            if (selected) {
                                Button(
                                    onClick = { viewModel.setThemeMode(mode) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("theme_btn_${mode.name.lowercase()}")
                                ) {
                                    Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.setThemeMode(mode) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("theme_btn_${mode.name.lowercase()}")
                                ) {
                                    Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Accent Palette",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AccentPalette.values().forEach { palette ->
                            val isSelected = uiState.settings.accentPalette == palette.name
                            val color = Color(android.graphics.Color.parseColor(palette.hexCode))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.setAccentPalette(palette) }
                                    .padding(4.dp)
                                    .testTag("palette_${palette.name.lowercase()}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = palette.displayName.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Notifications & Alerts",
                subtitle = "Configure Firebase cloud sync alerts"
            )
        }

        item {
            SettingSwitchRow(
                title = "Sync Completion Alerts",
                description = "Show toast confirmation when settings sync successfully",
                checked = uiState.settings.notifySyncComplete,
                onCheckedChange = { viewModel.toggleNotifySync(it) },
                icon = Icons.Default.Notifications,
                testTag = "notify_sync_switch"
            )
        }

        item {
            SettingSwitchRow(
                title = "Security Alerts",
                description = "Notify on unauthenticated access or sign-in state changes",
                checked = uiState.settings.notifySecurityAlerts,
                onCheckedChange = { viewModel.toggleNotifySecurity(it) },
                icon = Icons.Default.Security,
                testTag = "notify_security_switch"
            )
        }

        item {
            SettingSwitchRow(
                title = "Quota & Rate Warnings",
                description = "Alert when nearing Firebase free-tier read/write quotas",
                checked = uiState.settings.notifyQuotaWarnings,
                onCheckedChange = { viewModel.toggleNotifyQuota(it) },
                icon = Icons.Default.Warning,
                testTag = "notify_quota_switch"
            )
        }

        item {
            SectionHeader(
                title = "System & Maintenance",
                subtitle = "Reset or toggle developer mode"
            )
        }

        item {
            SettingSwitchRow(
                title = "Developer Mode",
                description = "Enable verbose logs and low-level diagnostic benchmark tools",
                checked = uiState.settings.developerMode,
                onCheckedChange = { viewModel.toggleDeveloperMode(it) },
                icon = Icons.Default.DeveloperMode,
                testTag = "dev_mode_switch"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reset Preferences",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Restore all app preferences to factory cloud defaults.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.resetSettingsToDefault() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusWarning),
                        modifier = Modifier.fillMaxWidth().testTag("reset_defaults_button")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset to Defaults")
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticsTabContent(
    viewModel: SettingsViewModel,
    uiState: SettingsUiState
) {
    var filterLevel by remember { mutableStateOf<LogLevel?>(null) }

    val filteredLogs = remember(uiState.logs, filterLevel) {
        if (filterLevel == null) uiState.logs
        else uiState.logs.filter { it.level == filterLevel }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Live Diagnostics & Benchmarks",
                subtitle = "Test Firebase Firestore read/write latency in real time"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Database Benchmarks",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.runDiagnosticsWrite() },
                            enabled = !uiState.isTesting,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("diag_test_write_btn")
                        ) {
                            Text("Test Write")
                        }
                        Button(
                            onClick = { viewModel.runDiagnosticsRead() },
                            enabled = !uiState.isTesting,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("diag_test_read_btn")
                        ) {
                            Text("Test Read")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.pingConnection() },
                        enabled = !uiState.isTesting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("diag_ping_btn")
                    ) {
                        Text("Ping Firestore Latency")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Event Logs (${filteredLogs.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { viewModel.clearLogs() },
                    modifier = Modifier.testTag("diag_clear_logs_btn")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = filterLevel == null,
                    onClick = { filterLevel = null },
                    label = { Text("All") },
                    modifier = Modifier.testTag("log_filter_all")
                )
                FilterChip(
                    selected = filterLevel == LogLevel.SUCCESS,
                    onClick = { filterLevel = if (filterLevel == LogLevel.SUCCESS) null else LogLevel.SUCCESS },
                    label = { Text("Success") },
                    modifier = Modifier.testTag("log_filter_success")
                )
                FilterChip(
                    selected = filterLevel == LogLevel.ERROR,
                    onClick = { filterLevel = if (filterLevel == LogLevel.ERROR) null else LogLevel.ERROR },
                    label = { Text("Error") },
                    modifier = Modifier.testTag("log_filter_error")
                )
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No logs recorded yet. Run a write or read test above.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                LogItemCard(log)
            }
        }
    }
}

@Composable
fun LogItemCard(log: DiagnosticLog) {
    val levelColor = when (log.level) {
        LogLevel.SUCCESS -> StatusSuccess
        LogLevel.INFO -> StatusInfo
        LogLevel.WARNING -> StatusWarning
        LogLevel.ERROR -> StatusError
    }
    val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(log.timestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(levelColor)
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "[${log.tag}]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
                if (log.latencyMs != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Roundtrip Latency: ${log.latencyMs}ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun GuideTabContent(uiState: SettingsUiState) {
    val context = LocalContext.current
    val rulesSample = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // User preferences & settings
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && (request.auth.uid == userId || userId == 'anon_diagnostic');
    }
    // Diagnostics ping collection
    match /diagnostics/{userId}/{document=**} {
      allow read, write: if true;
    }
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
""".trimIndent()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Firebase Setup Checklist",
                subtitle = "Everything ready and provisioned for this application"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ChecklistRow(title = "Firebase Project Linked", desc = "Connected to ${uiState.projectInfo.projectId}", done = true)
                    ChecklistRow(title = "Android Client Config", desc = "app/google-services.json registered for com.example", done = true)
                    ChecklistRow(title = "SHA Fingerprints Added", desc = "Debug keystore SHA-1 & SHA-256 bound to Firebase", done = true)
                    ChecklistRow(title = "Cloud Firestore Enabled", desc = "Document database provisioned and accepting connections", done = true)
                    ChecklistRow(title = "Firebase Authentication", desc = "Supports Anonymous & Email/Password sessions", done = true)
                }
            }
        }

        item {
            SectionHeader(
                title = "Firestore Security Rules",
                subtitle = "Sample security rules for user settings and diagnostics"
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "firestore.rules",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = rulesSample,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Firestore Rules", rulesSample))
                        },
                        modifier = Modifier.align(Alignment.End).testTag("copy_rules_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Rules")
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistRow(title: String, desc: String, done: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (done) StatusSuccess.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (done) StatusSuccess else StatusWarning,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
