package com.example.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SampleData
import com.example.presentation.viewmodels.CaregiverSyncViewModel
import com.example.presentation.viewmodels.SettingsViewModel
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.HealthSuccessGreen
import com.example.ui.theme.HealthSuccessGreenBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    caregiverSyncViewModel: CaregiverSyncViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by settingsViewModel.uiState.collectAsState()
    val syncState by caregiverSyncViewModel.uiState.collectAsState()

    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Account",
                            tint = GeoPrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GeoBackground
                )
            )
        },
        containerColor = GeoBackground,
        modifier = modifier.fillMaxSize().testTag("settings_screen_scaffold")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("settings_screen_scroll"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // ==========================================
            // GROUP 1: GENERAL
            // ==========================================
            item {
                SettingsSectionHeader(
                    title = "GENERAL",
                    subtitle = "App preferences, data privacy & security management"
                )
            }

            // 1.1 Preferences Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("settings_preferences_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Preferences",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Text Size Selector
                        Text(
                            text = "Display Text Size",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = GeoPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Standard", "Large", "Extra Large").forEach { size ->
                                val isSelected = state.textSize == size
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        settingsViewModel.setTextSize(size)
                                        caregiverSyncViewModel.setTextSizePreference(size)
                                    },
                                    label = {
                                        Text(
                                            text = size,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GeoMintSelected,
                                        selectedLabelColor = GeoPrimaryDark
                                    ),
                                    modifier = Modifier.weight(1f).testTag("text_size_chip_$size")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Language Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, null, tint = GeoPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Language / Reo",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = GeoPrimaryDark
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("English (NZ)", "Te Reo Māori").forEach { lang ->
                                val isSelected = state.language == lang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { settingsViewModel.setLanguage(lang) },
                                    label = {
                                        Text(
                                            text = lang,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GeoMintSelected,
                                        selectedLabelColor = GeoPrimaryDark
                                    ),
                                    modifier = Modifier.weight(1f).testTag("lang_chip_${lang.take(4)}")
                                )
                            }
                        }
                    }
                }
            }

            // 1.2 Privacy & Consent Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("settings_privacy_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PrivacyTip,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Privacy & Consent",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoPrimaryDark
                                    )
                                )
                            }

                            TextButton(
                                onClick = { settingsViewModel.showPrivacyPolicy(true) },
                                modifier = Modifier.testTag("privacy_policy_button")
                            ) {
                                Text("Policy", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Text(
                            text = "Granular data-sharing controls in compliance with the NZ Health Information Privacy Code 2020.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sharing Toggles
                        SettingsToggleRow(
                            title = "Share Vitals with Caregivers",
                            subtitle = "Allows linked whānau and GPs to monitor BP and glucose readings",
                            checked = state.shareVitalsWithCaregiver,
                            onCheckedChange = { settingsViewModel.toggleShareVitals(it) },
                            testTag = "toggle_share_vitals"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingsToggleRow(
                            title = "Share Medication Adherence",
                            subtitle = "Notifies primary caregiver when doses are taken or missed",
                            checked = state.shareMedicationWithCaregiver,
                            onCheckedChange = { settingsViewModel.toggleShareMedication(it) },
                            testTag = "toggle_share_meds"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingsToggleRow(
                            title = "Emergency SOS GPS Coordinates",
                            subtitle = "Transmits precise offline geolocation when SOS is triggered",
                            checked = state.shareSosLocationWithCaregiver,
                            onCheckedChange = { settingsViewModel.toggleShareSosLocation(it) },
                            testTag = "toggle_share_sos_location"
                        )
                    }
                }
            }

            // 1.3 Account Management Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("settings_account_mgmt_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Account Management",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Biometric Toggle with inline status label
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Fingerprint, null, tint = GeoPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Biometric Quick Unlock",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GeoPrimaryDark
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (state.isBiometricEnabled) HealthSuccessGreenBg else GeoSageContainer
                                        ) {
                                            Text(
                                                text = if (state.isBiometricEnabled) "ON" else "OFF",
                                                color = if (state.isBiometricEnabled) HealthSuccessGreen else GeoTextSecondary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Fingerprint / Face ID for instant offline sign-in",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                                    )
                                }
                            }
                            Switch(
                                checked = state.isBiometricEnabled,
                                onCheckedChange = {
                                    settingsViewModel.toggleBiometric(it)
                                    caregiverSyncViewModel.toggleBiometricLogin(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = GeoPrimary,
                                    uncheckedThumbColor = GeoBorder,
                                    uncheckedTrackColor = GeoSageContainer
                                ),
                                modifier = Modifier.testTag("settings_biometric_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Change Password Button
                        OutlinedButton(
                            onClick = { settingsViewModel.showChangePassword(true) },
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("change_password_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Icon(Icons.Default.Lock, null, tint = GeoPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CHANGE PASSWORD",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    letterSpacing = 0.6.sp
                                )
                            )
                        }
                    }
                }
            }

            // ==========================================
            // GROUP 2: NOTIFICATIONS & APPEARANCE
            // ==========================================
            item {
                SettingsSectionHeader(
                    title = "NOTIFICATIONS & APPEARANCE",
                    subtitle = "Alert triggers, SMS fallback, and visual styling"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("settings_notifications_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Master Notifications Toggle
                        SettingsToggleRow(
                            title = "Push Notifications",
                            subtitle = "Health reminders, red flags, and appointment alerts",
                            checked = state.isNotificationsMasterEnabled,
                            onCheckedChange = { settingsViewModel.toggleMasterNotifications(it) },
                            testTag = "toggle_master_notifications"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Notification Categories Dialog Trigger
                        OutlinedButton(
                            onClick = { settingsViewModel.showNotificationCategories(true) },
                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("notification_categories_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Icon(Icons.Default.NotificationsActive, null, tint = GeoPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CUSTOMIZE ALERT CATEGORIES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    letterSpacing = 0.6.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // SMS Notification Toggle
                        SettingsToggleRow(
                            title = "SMS Emergency Fallback",
                            subtitle = "Dispatches SMS alerts if offline or unable to reach caregiver",
                            checked = state.isSmsEmergencyFallbackEnabled,
                            onCheckedChange = { settingsViewModel.toggleSmsFallback(it) },
                            testTag = "toggle_sms_fallback"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Theme Mode Toggle (Light / Dark - WCAG 2.2 AAA Compliance)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GeoSageContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(38.dp),
                                            shape = CircleShape,
                                            color = GeoMintSelected,
                                            border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.5f))
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (state.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                                    contentDescription = null,
                                                    tint = GeoPrimaryDark,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "System-Wide Dark Mode",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GeoPrimaryDark,
                                                    fontSize = 15.sp
                                                )
                                            )
                                            Text(
                                                text = if (state.isDarkTheme) "High-contrast dark palette active" else "Daylight warm neutral active",
                                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = state.isDarkTheme,
                                        onCheckedChange = { settingsViewModel.toggleDarkTheme(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = GeoPrimary,
                                            uncheckedThumbColor = GeoBorder,
                                            uncheckedTrackColor = GeoSageContainer
                                        ),
                                        modifier = Modifier.testTag("settings_dark_theme_switch")
                                    )
                                }

                                // WCAG 2.2 AAA Contrast Standard certification tag
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HealthSuccessGreenBg,
                                    border = BorderStroke(1.dp, HealthSuccessGreen.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = HealthSuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "WCAG 2.2 AAA Contrast Standard (≥ 7:1 ratio)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = HealthSuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // GROUP 3: SUPPORT & ACCESSIBILITY
            // ==========================================
            item {
                SettingsSectionHeader(
                    title = "SUPPORT & ACCESSIBILITY",
                    subtitle = "Assistive tools, QR pairing, and institutional info"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("settings_support_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // 3.1 Accessibility Customizer
                        SettingsNavigationItem(
                            icon = Icons.Default.AccessibilityNew,
                            title = "Accessibility & Reading Mode",
                            subtitle = "Dynamic text zoom slider with live preview & contrast boosts",
                            onClick = { settingsViewModel.showAccessibility(true) },
                            testTag = "settings_accessibility_item"
                        )

                        // 3.2 Scan QR Code / Pair
                        SettingsNavigationItem(
                            icon = Icons.Default.QrCodeScanner,
                            title = "Scan QR Code / Whānau Pairing",
                            subtitle = "Pair caregiver phone or generate senior linking QR code",
                            onClick = { settingsViewModel.openQrPairing(initialTab = 0) },
                            testTag = "settings_qr_pairing_item"
                        )

                        // 3.3 About Us
                        SettingsNavigationItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            title = "About Manaaki Health",
                            subtitle = "Version, COMP826 Auckland University of Technology & Te Whatu Ora info",
                            onClick = { settingsViewModel.showAboutUs(true) },
                            testTag = "settings_about_us_item"
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOGS & MODALS
    // ==========================================

    // 1. Accessibility Customizer Dialog with Live Preview
    if (state.showAccessibilityDialog) {
        var sliderValue by remember {
            mutableFloatStateOf(
                when (state.textSize) {
                    "Standard" -> 1f
                    "Large" -> 2f
                    else -> 3f
                }
            )
        }

        AlertDialog(
            onDismissRequest = { settingsViewModel.showAccessibility(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessibilityNew, null, tint = GeoPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accessibility & Text Zoom", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Adjust the text size slider below to calibrate all cards and alerts for comfortable reading:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                    )

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 1f..3f,
                        steps = 1,
                        colors = SliderDefaults.colors(
                            thumbColor = GeoPrimary,
                            activeTrackColor = GeoPrimary,
                            inactiveTrackColor = GeoSageContainer
                        ),
                        modifier = Modifier.testTag("accessibility_text_slider")
                    )

                    val previewSizeLabel = when {
                        sliderValue <= 1.4f -> "Standard"
                        sliderValue <= 2.4f -> "Large"
                        else -> "Extra Large"
                    }

                    val previewFontSize = when {
                        sliderValue <= 1.4f -> 16.sp
                        sliderValue <= 2.4f -> 19.sp
                        else -> 23.sp
                    }

                    Text(
                        text = "Current Scale: $previewSizeLabel",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )

                    // Live Interactive Preview Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GeoSageContainer,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "LIVE PREVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kia ora ${SampleData.PATIENT_FIRST_NAME}, take your morning medication with water.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = previewFontSize,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GeoPrimaryDark,
                                    lineHeight = (previewFontSize.value * 1.35).sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    SettingsToggleRow(
                        title = "High Contrast Mode (WCAG AAA)",
                        subtitle = "Elevates text contrast across all vitals and graphs",
                        checked = state.isHighContrastEnabled,
                        onCheckedChange = { settingsViewModel.toggleHighContrast(it) },
                        testTag = "toggle_high_contrast"
                    )

                    SettingsToggleRow(
                        title = "Voice Assistance Speech Prompts",
                        subtitle = "Spoken bilingual voice prompts in English & Te Reo Māori",
                        checked = state.isVoiceAssistanceEnabled,
                        onCheckedChange = { settingsViewModel.toggleVoiceAssistance(it) },
                        testTag = "toggle_voice_assistance"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosen = when {
                            sliderValue <= 1.4f -> "Standard"
                            sliderValue <= 2.4f -> "Large"
                            else -> "Extra Large"
                        }
                        settingsViewModel.setTextSize(chosen)
                        caregiverSyncViewModel.setTextSizePreference(chosen)
                        settingsViewModel.showAccessibility(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("APPLY SETTINGS", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.showAccessibility(false) }) {
                    Text("CLOSE", color = GeoTextSecondary)
                }
            }
        )
    }

    // 2. QR Code Pairing Dialog (Two Tabs: "My Pairing QR Code" and "Scan / Enter Invite Code")
    if (state.showQrPairingDialog) {
        var activeTab by remember { mutableIntStateOf(state.qrPairingInitialTab) }

        AlertDialog(
            onDismissRequest = { settingsViewModel.closeQrPairing() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, null, tint = GeoPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Caregiver Pairing & Links", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = GeoBackground,
                        contentColor = GeoPrimary
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("My QR Code", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            modifier = Modifier.testTag("tab_my_qr")
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("Enter / Scan Code", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            modifier = Modifier.testTag("tab_scan_qr")
                        )
                    }

                    if (activeTab == 0) {
                        // Show QR Code Representation for Elder
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(2.dp, GeoPrimary),
                                modifier = Modifier.size(160.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = "Caregiver Linking QR Code",
                                            tint = GeoPrimaryDark,
                                            modifier = Modifier.size(110.dp)
                                        )
                                        Text(
                                            text = "PAIR: ${syncState.userNhiNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GeoPrimaryDark,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${syncState.userName} • NHI: ${syncState.userNhiNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark
                                )
                            )
                            Text(
                                text = "Have your whānau or nurse scan this screen to link as an authorized caregiver.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GeoTextSecondary,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    } else {
                        // Scan / Enter Code Flow
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Enter the 6-character Caregiver Invite Code or NHI provided by the patient:",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                            )

                            OutlinedTextField(
                                value = state.pairingInviteCodeInput,
                                onValueChange = { settingsViewModel.onPairingCodeChanged(it) },
                                label = { Text("Invite Token or NHI (e.g. ABC1234)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("pairing_code_input")
                            )

                            Button(
                                onClick = {
                                    settingsViewModel.pairWithCode(state.pairingInviteCodeInput) { contact ->
                                        caregiverSyncViewModel.addCaregiver(
                                            name = contact.name,
                                            relationship = contact.relationship,
                                            phoneNumber = contact.phoneNumber,
                                            isPrimary = false,
                                            receivesRedFlags = true
                                        )
                                    }
                                },
                                enabled = state.pairingInviteCodeInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("submit_pair_code_button")
                            ) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("VERIFY & LINK CAREGIVER", fontWeight = FontWeight.Bold)
                            }

                            state.pairingSuccessMessage?.let { successMsg ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = HealthSuccessGreenBg,
                                    border = BorderStroke(1.dp, HealthSuccessGreen.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, null, tint = HealthSuccessGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = successMsg,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = HealthSuccessGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { settingsViewModel.closeQrPairing() }) {
                    Text("DONE", color = GeoPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 3. Change Password Dialog
    if (state.showChangePasswordDialog) {
        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { settingsViewModel.showChangePassword(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Account Password", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password (min 8 characters)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    errorMsg?.let { err ->
                        Text(err, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    state.changePasswordSuccessMessage?.let { succ ->
                        Text(succ, color = HealthSuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 8) {
                            errorMsg = "Password must be at least 8 characters long."
                        } else if (newPass != confirmPass) {
                            errorMsg = "Passwords do not match."
                        } else {
                            val success = settingsViewModel.submitChangePassword(oldPass, newPass, confirmPass)
                            if (success) {
                                errorMsg = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("UPDATE PASSWORD", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.showChangePassword(false) }) {
                    Text("CANCEL", color = GeoTextSecondary)
                }
            }
        )
    }

    // 4. Notification Categories Customization Dialog
    if (state.showNotificationCategoriesDialog) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.showNotificationCategories(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Alert Categories", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsToggleRow(
                        title = "Medication Dose Reminders",
                        subtitle = "Morning, midday, evening, and night audio alerts",
                        checked = state.notifyMedicationReminders,
                        onCheckedChange = { settingsViewModel.toggleMedicationNotifications(it) },
                        testTag = "toggle_medication_alerts"
                    )

                    SettingsToggleRow(
                        title = "Emergency Red Flag & SOS Alerts",
                        subtitle = "Critical blood pressure, heart rate, and fall detection triggers",
                        checked = state.notifyEmergencyAlerts,
                        onCheckedChange = { settingsViewModel.toggleEmergencyAlerts(it) },
                        testTag = "toggle_emergency_alerts"
                    )

                    SettingsToggleRow(
                        title = "Caregiver Assigned Tasks",
                        subtitle = "Direct reminders created by linked caregivers & GPs",
                        checked = state.notifyCaregiverTasks,
                        onCheckedChange = { settingsViewModel.toggleCaregiverTaskNotifications(it) },
                        testTag = "toggle_caregiver_tasks"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { settingsViewModel.showNotificationCategories(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("SAVE", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 5. Privacy Policy Dialog
    if (state.showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.showPrivacyPolicy(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PrivacyTip, null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("NZ Health Privacy Statement", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text(
                            text = "Manaaki Health is designed to uphold the New Zealand Health Information Privacy Code 2020 and Privacy Act 2020.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextPrimary, fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Local-First Storage: All personal health records (NHI, medication schedules, vitals logs, fall incident metrics) are stored encrypted on-device via Room & Android Keystore.\n\n2. Consensual Sharing: Caregiver syncing only occurs with explicitly authorized whānau members or registered GPs using cryptographic pairing keys.\n\n3. Emergency Dispatch: Emergency SOS coordinates are transmitted over secure TLS 1.3 or cellular SMS directly to designated contacts and emergency responders.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, lineHeight = 18.sp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { settingsViewModel.showPrivacyPolicy(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("I UNDERSTAND", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 6. About Us Dialog
    if (state.showAboutUsDialog) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.showAboutUs(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("About Manaaki Health", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Manaaki Health v2.4.0 (Build 2026.08)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )
                    Text(
                        text = "Developed for COMP826 at Auckland University of Technology (AUT) in partnership with Te Whatu Ora Health NZ.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GeoTextPrimary,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Empowering elderly New Zealanders and their whānau with offline-first, bilingual health monitoring, automated red-flag triage, and rapid emergency response.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { settingsViewModel.showAboutUs(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CLOSE", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ---------------- Helper Components for Settings ----------------

@Composable
private fun SettingsSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimaryDark,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = GeoTextSecondary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GeoPrimaryDark
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GeoTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GeoPrimary,
                uncheckedThumbColor = GeoBorder,
                uncheckedTrackColor = GeoSageContainer
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = GeoBackground,
        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GeoSageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}
