package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.AccentPalette
import com.example.data.model.ThemeMode

fun getCustomColorScheme(palette: AccentPalette, dark: Boolean): ColorScheme {
    return if (dark) {
        when (palette) {
            AccentPalette.FIREBASE_AMBER -> darkColorScheme(
                primary = AmberPrimaryDark,
                secondary = AmberSecondaryDark,
                tertiary = FirebaseOrange,
                background = Color(0xFF0F172A),
                surface = Color(0xFF1E293B),
                surfaceVariant = Color(0xFF334155),
                onPrimary = Color(0xFF451A03),
                onSecondary = Color(0xFF451A03),
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF8FAFC)
            )
            AccentPalette.DEEP_INDIGO -> darkColorScheme(
                primary = IndigoPrimaryDark,
                secondary = IndigoSecondaryDark,
                tertiary = CyanPrimaryDark,
                background = Color(0xFF0A0F1D),
                surface = Color(0xFF172033),
                surfaceVariant = Color(0xFF263352),
                onPrimary = Color(0xFF1E1B4B),
                onSecondary = Color(0xFF1E1B4B),
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF8FAFC)
            )
            AccentPalette.EMERALD_GREEN -> darkColorScheme(
                primary = EmeraldPrimaryDark,
                secondary = EmeraldSecondaryDark,
                tertiary = AmberPrimaryDark,
                background = Color(0xFF061A14),
                surface = Color(0xFF0E3025),
                surfaceVariant = Color(0xFF1A4738),
                onPrimary = Color(0xFF022C22),
                onSecondary = Color(0xFF022C22),
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF8FAFC)
            )
            AccentPalette.CYAN_BLUE -> darkColorScheme(
                primary = CyanPrimaryDark,
                secondary = CyanSecondaryDark,
                tertiary = IndigoPrimaryDark,
                background = Color(0xFF081822),
                surface = Color(0xFF0F2E40),
                surfaceVariant = Color(0xFF1C455E),
                onPrimary = Color(0xFF082F49),
                onSecondary = Color(0xFF082F49),
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF8FAFC)
            )
            AccentPalette.ROSE_CRIMSON -> darkColorScheme(
                primary = RosePrimaryDark,
                secondary = RoseSecondaryDark,
                tertiary = AmberPrimaryDark,
                background = Color(0xFF1A0A10),
                surface = Color(0xFF2E131E),
                surfaceVariant = Color(0xFF451F2F),
                onPrimary = Color(0xFF4C0519),
                onSecondary = Color(0xFF4C0519),
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF8FAFC)
            )
        }
    } else {
        when (palette) {
            AccentPalette.FIREBASE_AMBER -> lightColorScheme(
                primary = AmberPrimaryLight,
                secondary = AmberSecondaryLight,
                tertiary = FirebaseOrange,
                background = Color(0xFFFFFBEB),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFEF3C7),
                onPrimary = Color.White,
                onSecondary = Color.White
            )
            AccentPalette.DEEP_INDIGO -> lightColorScheme(
                primary = IndigoPrimaryLight,
                secondary = IndigoSecondaryLight,
                tertiary = CyanPrimaryLight,
                background = Color(0xFFF5F3FF),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFEDE9FE),
                onPrimary = Color.White,
                onSecondary = Color.White
            )
            AccentPalette.EMERALD_GREEN -> lightColorScheme(
                primary = EmeraldPrimaryLight,
                secondary = EmeraldSecondaryLight,
                tertiary = AmberPrimaryLight,
                background = Color(0xFFF0FDF4),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFDCFCE7),
                onPrimary = Color.White,
                onSecondary = Color.White
            )
            AccentPalette.CYAN_BLUE -> lightColorScheme(
                primary = CyanPrimaryLight,
                secondary = CyanSecondaryLight,
                tertiary = IndigoPrimaryLight,
                background = Color(0xFFECFEFF),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFCFFAFE),
                onPrimary = Color.White,
                onSecondary = Color.White
            )
            AccentPalette.ROSE_CRIMSON -> lightColorScheme(
                primary = RosePrimaryLight,
                secondary = RoseSecondaryLight,
                tertiary = AmberPrimaryLight,
                background = Color(0xFFFFF1F2),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFFE4E6),
                onPrimary = Color.White,
                onSecondary = Color.White
            )
        }
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentPalette: AccentPalette = AccentPalette.FIREBASE_AMBER,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = getCustomColorScheme(accentPalette, isDark)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
