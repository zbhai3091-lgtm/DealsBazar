package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AccentPalette
import com.example.data.model.ThemeMode
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dealViewModel: DealViewModel = viewModel()
            val uiState by dealViewModel.uiState.collectAsStateWithLifecycle()
            val deals by dealViewModel.deals.collectAsStateWithLifecycle()
            val savedDealIds by dealViewModel.savedDealIds.collectAsStateWithLifecycle()
            val paymentConfig by dealViewModel.paymentConfig.collectAsStateWithLifecycle()
            val adBanner by dealViewModel.adBanner.collectAsStateWithLifecycle()
            val remoteConfig by dealViewModel.remoteConfig.collectAsStateWithLifecycle()
            val userProfile by dealViewModel.userProfile.collectAsStateWithLifecycle()
            val isLoading by dealViewModel.isLoading.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = ThemeMode.SYSTEM,
                accentPalette = AccentPalette.FIREBASE_AMBER
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen(
                        viewModel = dealViewModel,
                        uiState = uiState,
                        deals = deals,
                        savedDealIds = savedDealIds,
                        paymentConfig = paymentConfig,
                        adBanner = adBanner,
                        remoteConfig = remoteConfig,
                        userProfile = userProfile,
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}
