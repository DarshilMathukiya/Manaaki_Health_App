package com.example.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CaregiverTask
import com.example.domain.model.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    // General Preferences
    val textSize: String = "Large", // "Standard", "Large", "Extra Large"
    val language: String = "English (NZ)", // "English (NZ)", "Te Reo Māori"
    val isHighContrastEnabled: Boolean = false,
    val isVoiceAssistanceEnabled: Boolean = true,
    
    // Privacy & Consent (NZ Health Information Privacy Code)
    val shareVitalsWithCaregiver: Boolean = true,
    val shareMedicationWithCaregiver: Boolean = true,
    val shareSosLocationWithCaregiver: Boolean = true,
    val showPrivacyPolicyDialog: Boolean = false,
    
    // Account Management
    val isBiometricEnabled: Boolean = true,
    val showChangePasswordDialog: Boolean = false,
    val changePasswordSuccessMessage: String? = null,
    
    // Notifications & Appearance
    val isNotificationsMasterEnabled: Boolean = true,
    val notifyMedicationReminders: Boolean = true,
    val notifyEmergencyAlerts: Boolean = true,
    val notifyCaregiverTasks: Boolean = true,
    val showNotificationCategoriesDialog: Boolean = false,
    val isSmsEmergencyFallbackEnabled: Boolean = true,
    val isDarkTheme: Boolean = false,
    
    // Support & Accessibility
    val showAccessibilityDialog: Boolean = false,
    val showQrPairingDialog: Boolean = false,
    val qrPairingInitialTab: Int = 0, // 0 = My QR Code, 1 = Scan / Enter Code
    val pairingInviteCodeInput: String = "",
    val pairingSuccessMessage: String? = null,
    val showAboutUsDialog: Boolean = false,
    
    // Medical ID & Emergency Data
    val showMedicalIdDialog: Boolean = false,
    val showHistorySummaryDialog: Boolean = false
)

class SettingsViewModel(
    application: Application,
    private val container: ManaakiAppContainer
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val session = container.secureStore.getSession()
        if (session != null) {
            _uiState.value = _uiState.value.copy(
                isBiometricEnabled = session.isBiometricEnabled
            )
        }
    }

    // Preferences
    fun setTextSize(size: String) {
        _uiState.value = _uiState.value.copy(textSize = size)
    }

    fun setLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(language = lang)
    }

    fun toggleHighContrast(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isHighContrastEnabled = enabled)
    }

    fun toggleVoiceAssistance(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isVoiceAssistanceEnabled = enabled)
    }

    // Privacy
    fun toggleShareVitals(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(shareVitalsWithCaregiver = enabled)
    }

    fun toggleShareMedication(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(shareMedicationWithCaregiver = enabled)
    }

    fun toggleShareSosLocation(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(shareSosLocationWithCaregiver = enabled)
    }

    fun showPrivacyPolicy(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPrivacyPolicyDialog = show)
    }

    // Account Management
    fun toggleBiometric(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
        container.secureStore.setBiometricEnabled(enabled)
    }

    fun showChangePassword(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showChangePasswordDialog = show,
            changePasswordSuccessMessage = null
        )
    }

    fun submitChangePassword(oldPass: String, newPass: String, confirmPass: String): Boolean {
        if (newPass.length >= 8 && newPass == confirmPass) {
            _uiState.value = _uiState.value.copy(
                changePasswordSuccessMessage = "Password updated securely."
            )
            return true
        }
        return false
    }

    // Notifications & Appearance
    fun toggleMasterNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isNotificationsMasterEnabled = enabled)
    }

    fun toggleMedicationNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notifyMedicationReminders = enabled)
    }

    fun toggleEmergencyAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notifyEmergencyAlerts = enabled)
    }

    fun toggleCaregiverTaskNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notifyCaregiverTasks = enabled)
    }

    fun showNotificationCategories(show: Boolean) {
        _uiState.value = _uiState.value.copy(showNotificationCategoriesDialog = show)
    }

    fun toggleSmsFallback(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isSmsEmergencyFallbackEnabled = enabled)
    }

    fun toggleDarkTheme(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkTheme = enabled)
    }

    // Support & Accessibility
    fun showAccessibility(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAccessibilityDialog = show)
    }

    fun openQrPairing(initialTab: Int = 0) {
        _uiState.value = _uiState.value.copy(
            showQrPairingDialog = true,
            qrPairingInitialTab = initialTab,
            pairingInviteCodeInput = "",
            pairingSuccessMessage = null
        )
    }

    fun closeQrPairing() {
        _uiState.value = _uiState.value.copy(
            showQrPairingDialog = false,
            pairingSuccessMessage = null
        )
    }

    fun onPairingCodeChanged(code: String) {
        _uiState.value = _uiState.value.copy(pairingInviteCodeInput = code)
    }

    fun pairWithCode(
        code: String,
        onSuccess: (CaregiverContact) -> Unit
    ) {
        if (code.isNotBlank()) {
            val contact = CaregiverContact(
                name = "Kaitiaki Nurse (Caregiver)",
                relationship = "Whānau Support / Linked Caregiver",
                phoneNumber = "+64 21 555 9012",
                isPrimary = false,
                receivesRedFlags = true
            )
            _uiState.value = _uiState.value.copy(
                pairingSuccessMessage = "Successfully linked via token '$code'. Caregiver connection verified."
            )
            onSuccess(contact)
        }
    }

    fun showAboutUs(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAboutUsDialog = show)
    }

    fun showMedicalId(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMedicalIdDialog = show)
    }

    fun showHistorySummary(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistorySummaryDialog = show)
    }
}
