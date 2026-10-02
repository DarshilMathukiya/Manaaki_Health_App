package com.example.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.presentation.viewmodels.CaregiverSyncViewModel

@Composable
fun CaregiverSyncScreen(
    viewModel: CaregiverSyncViewModel,
    onLogoutRequested: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AccountScreen(
        viewModel = viewModel,
        onLogoutRequested = onLogoutRequested,
        onNavigateToSettings = onNavigateToSettings,
        onBack = onBack,
        modifier = modifier
    )
}
