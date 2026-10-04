package com.example.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AuthResult
import com.example.domain.model.RegistrationData
import com.example.domain.model.SampleData
import com.example.domain.model.UserSession
import com.example.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthScreenDestination {
    object Welcome : AuthScreenDestination()
    object Login : AuthScreenDestination()
    object Register : AuthScreenDestination()
}

enum class LoginRole {
    PATIENT,
    CAREGIVER_ADMIN
}

sealed class AuthState {
    object Initializing : AuthState()
    data class Authenticated(val session: UserSession, val targetRole: LoginRole = LoginRole.PATIENT) : AuthState()
    data class Unauthenticated(val errorMessage: String? = null) : AuthState()
}

data class LoginUiState(
    val emailOrPhone: String = "demo@example.com",
    val password: String = "demo123Password",
    val selectedRole: LoginRole = LoginRole.PATIENT,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUnknownAccount: Boolean = false,
    val isWrongPassword: Boolean = false,
    val isOfflineBlocked: Boolean = false,
    val showForgotPasswordDialog: Boolean = false,
    val showNoCaregiverLinkDialog: Boolean = false,
    val showBiometricPrompt: Boolean = false,
    val showBiometricOptInDialog: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val cachedAvatarUrl: String? = null,
    val cachedUserName: String = SampleData.PATIENT_FULL_NAME
)

data class RegisterUiState(
    val currentStep: Int = 1,
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val caregiverName: String = "",
    val caregiverPhone: String = "",
    val consentAgreed: Boolean = false,
    val isRegistering: Boolean = false,
    val errorMessage: String? = null,
    val showPrivacyPolicyModal: Boolean = false,
    val errors: Map<String, String> = emptyMap()
)

class AuthViewModel(
    application: Application,
    private val authUseCase: AuthUseCase
) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentScreen = MutableStateFlow<AuthScreenDestination>(AuthScreenDestination.Login)
    val currentScreen: StateFlow<AuthScreenDestination> = _currentScreen.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _showLogoutSheet = MutableStateFlow(false)
    val showLogoutSheet: StateFlow<Boolean> = _showLogoutSheet.asStateFlow()

    init {
        restoreSessionOnLaunch()
    }

    /**
     * Checks SecureStore on launch for zero-tap offline session restoration.
     */
    fun restoreSessionOnLaunch() {
        viewModelScope.launch {
            val cachedSession = authUseCase.restoreSession()
            if (cachedSession != null) {
                _authState.value = AuthState.Authenticated(cachedSession)
            } else {
                val hasBiometric = authUseCase.isBiometricOptIn()
                _loginState.value = _loginState.value.copy(
                    isBiometricAvailable = hasBiometric
                )
                _authState.value = AuthState.Unauthenticated()
            }
        }
    }

    // ---------------- Login Actions ----------------

    fun onEmailOrPhoneChange(input: String) {
        _loginState.value = _loginState.value.copy(
            emailOrPhone = input,
            errorMessage = null,
            isUnknownAccount = false,
            isWrongPassword = false,
            isOfflineBlocked = false
        )
    }

    fun onPasswordChange(input: String) {
        _loginState.value = _loginState.value.copy(
            password = input,
            errorMessage = null,
            isWrongPassword = false
        )
    }

    fun togglePasswordVisibility() {
        _loginState.value = _loginState.value.copy(
            isPasswordVisible = !_loginState.value.isPasswordVisible
        )
    }

    fun selectRole(role: LoginRole) {
        val current = _loginState.value
        val (newEmail, newPassword) = when (role) {
            LoginRole.PATIENT -> {
                if (current.emailOrPhone == "admin" || current.emailOrPhone.isBlank()) {
                    "demo@example.com" to "demo123Password"
                } else {
                    current.emailOrPhone to current.password
                }
            }
            LoginRole.CAREGIVER_ADMIN -> {
                if (current.emailOrPhone == "demo@example.com" || current.emailOrPhone.isBlank()) {
                    "admin" to "Admin01234"
                } else {
                    current.emailOrPhone to current.password
                }
            }
        }

        _loginState.value = current.copy(
            selectedRole = role,
            emailOrPhone = newEmail,
            password = newPassword,
            errorMessage = null,
            isUnknownAccount = false,
            isWrongPassword = false
        )
    }

    fun showNoCaregiverLink(show: Boolean) {
        _loginState.value = _loginState.value.copy(
            showNoCaregiverLinkDialog = show
        )
    }

    fun performLogin(hasLinkedCaregivers: Boolean = true, onNoCaregiverLink: () -> Unit = {}) {
        val current = _loginState.value
        if (current.emailOrPhone.isBlank()) {
            _loginState.value = current.copy(
                errorMessage = "Please enter your email or phone number."
            )
            return
        }
        if (current.password.isBlank()) {
            _loginState.value = current.copy(
                errorMessage = "Please enter your password."
            )
            return
        }

        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authUseCase.login(current.emailOrPhone, current.password)) {
                is AuthResult.Success -> {
                    val hadBiometricBefore = authUseCase.isBiometricOptIn()
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        showBiometricOptInDialog = !hadBiometricBefore
                    )
                    if (current.selectedRole == LoginRole.CAREGIVER_ADMIN && !hasLinkedCaregivers) {
                        _loginState.value = _loginState.value.copy(showNoCaregiverLinkDialog = true)
                        onNoCaregiverLink()
                    } else {
                        _authState.value = AuthState.Authenticated(
                            session = result.session,
                            targetRole = current.selectedRole
                        )
                    }
                }
                is AuthResult.Error -> {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isUnknownAccount = result.isUnknownAccount,
                        isWrongPassword = result.isWrongPassword,
                        isOfflineBlocked = result.isOfflineBlocked
                    )
                }
            }
        }
    }

    fun unlockWithBiometric(hasLinkedCaregivers: Boolean = true, onNoCaregiverLink: () -> Unit = {}) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true)
            when (val result = authUseCase.unlockWithBiometric()) {
                is AuthResult.Success -> {
                    _loginState.value = _loginState.value.copy(isLoading = false)
                    val current = _loginState.value
                    if (current.selectedRole == LoginRole.CAREGIVER_ADMIN && !hasLinkedCaregivers) {
                        _loginState.value = _loginState.value.copy(showNoCaregiverLinkDialog = true)
                        onNoCaregiverLink()
                    } else {
                        _authState.value = AuthState.Authenticated(
                            session = result.session,
                            targetRole = current.selectedRole
                        )
                    }
                }
                is AuthResult.Error -> {
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun setBiometricOptIn(enabled: Boolean) {
        authUseCase.setBiometricOptIn(enabled)
        _loginState.value = _loginState.value.copy(
            showBiometricOptInDialog = false,
            isBiometricAvailable = enabled
        )
    }

    fun dismissBiometricOptIn() {
        _loginState.value = _loginState.value.copy(showBiometricOptInDialog = false)
    }

    fun showForgotPassword(show: Boolean) {
        _loginState.value = _loginState.value.copy(showForgotPasswordDialog = show)
    }

    fun updateAvatar(url: String?) {
        _loginState.value = _loginState.value.copy(cachedAvatarUrl = url)
    }

    // ---------------- Register Stepper Actions ----------------

    fun navigateToWelcome() {
        _currentScreen.value = AuthScreenDestination.Login
    }

    fun navigateToRegister() {
        _registerState.value = RegisterUiState()
        _currentScreen.value = AuthScreenDestination.Register
    }

    fun navigateToLogin() {
        _loginState.value = _loginState.value.copy(errorMessage = null)
        _currentScreen.value = AuthScreenDestination.Login
    }

    fun onRegisterFullNameChange(name: String) {
        val errors = _registerState.value.errors.toMutableMap().apply { remove("fullName") }
        _registerState.value = _registerState.value.copy(fullName = name, errors = errors)
    }

    fun onRegisterEmailChange(email: String) {
        val errors = _registerState.value.errors.toMutableMap().apply { remove("email") }
        _registerState.value = _registerState.value.copy(email = email, errors = errors)
    }

    fun onRegisterPhoneChange(phone: String) {
        val errors = _registerState.value.errors.toMutableMap().apply { remove("phone") }
        _registerState.value = _registerState.value.copy(phoneNumber = phone, errors = errors)
    }

    fun onRegisterPasswordChange(password: String) {
        val errors = _registerState.value.errors.toMutableMap().apply { remove("password") }
        _registerState.value = _registerState.value.copy(password = password, errors = errors)
    }

    fun onRegisterConfirmPasswordChange(confirm: String) {
        val errors = _registerState.value.errors.toMutableMap().apply { remove("confirmPassword") }
        _registerState.value = _registerState.value.copy(confirmPassword = confirm, errors = errors)
    }

    fun toggleRegisterPasswordVisibility() {
        _registerState.value = _registerState.value.copy(
            isPasswordVisible = !_registerState.value.isPasswordVisible
        )
    }

    fun onRegisterCaregiverNameChange(name: String) {
        _registerState.value = _registerState.value.copy(caregiverName = name)
    }

    fun onRegisterCaregiverPhoneChange(phone: String) {
        _registerState.value = _registerState.value.copy(caregiverPhone = phone)
    }

    fun onConsentAgreedChange(agreed: Boolean) {
        val errors = _registerState.value.errors.toMutableMap().apply { remove("consent") }
        _registerState.value = _registerState.value.copy(consentAgreed = agreed, errors = errors)
    }

    fun showPrivacyModal(show: Boolean) {
        _registerState.value = _registerState.value.copy(showPrivacyPolicyModal = show)
    }

    fun nextRegisterStep() {
        val current = _registerState.value
        val errors = mutableMapOf<String, String>()

        when (current.currentStep) {
            1 -> {
                if (current.fullName.isBlank()) {
                    errors["fullName"] = "Please enter your full name."
                }
                if (current.email.isBlank()) {
                    errors["email"] = "Please enter your email address."
                } else if (!current.email.contains("@") || !current.email.contains(".")) {
                    errors["email"] = "Please enter a valid email (e.g. name@example.com)."
                }
                if (current.phoneNumber.isBlank()) {
                    errors["phone"] = "Please enter your mobile or home phone."
                }
            }
            2 -> {
                if (current.password.length < 8) {
                    errors["password"] = "Password must be at least 8 characters long."
                } else if (!current.password.any { it.isDigit() }) {
                    errors["password"] = "Password must include at least one number."
                }
                if (current.confirmPassword != current.password) {
                    errors["confirmPassword"] = "Passwords do not match. Please retype."
                }
            }
            3 -> {
                // Caregiver is optional, no blocking errors
            }
            4 -> {
                if (!current.consentAgreed) {
                    errors["consent"] = "Please agree to the privacy statement to create your account."
                }
            }
        }

        if (errors.isNotEmpty()) {
            _registerState.value = current.copy(errors = errors)
            return
        }

        if (current.currentStep < 4) {
            _registerState.value = current.copy(
                currentStep = current.currentStep + 1,
                errors = emptyMap()
            )
        } else {
            performRegister()
        }
    }

    fun previousRegisterStep() {
        val current = _registerState.value
        if (current.currentStep > 1) {
            _registerState.value = current.copy(
                currentStep = current.currentStep - 1,
                errors = emptyMap()
            )
        } else {
            navigateToLogin()
        }
    }

    private fun performRegister() {
        val current = _registerState.value
        val data = RegistrationData(
            fullName = current.fullName,
            email = current.email,
            phoneNumber = current.phoneNumber,
            password = current.password,
            caregiverName = current.caregiverName,
            caregiverPhone = current.caregiverPhone,
            consentAgreed = current.consentAgreed
        )

        viewModelScope.launch {
            _registerState.value = current.copy(isRegistering = true, errorMessage = null)
            when (val result = authUseCase.register(data)) {
                is AuthResult.Success -> {
                    _registerState.value = current.copy(isRegistering = false)
                    // Seamless route directly into app with cached session
                    _authState.value = AuthState.Authenticated(result.session)
                }
                is AuthResult.Error -> {
                    _registerState.value = current.copy(
                        isRegistering = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    // ---------------- Logout Flow ----------------

    fun requestLogout() {
        _showLogoutSheet.value = true
    }

    fun dismissLogoutSheet() {
        _showLogoutSheet.value = false
    }

    fun confirmLogout() {
        viewModelScope.launch {
            _showLogoutSheet.value = false
            authUseCase.logout()
            _authState.value = AuthState.Unauthenticated()
            _currentScreen.value = AuthScreenDestination.Login
            _loginState.value = LoginUiState(
                emailOrPhone = "",
                password = "",
                isBiometricAvailable = authUseCase.isBiometricOptIn()
            )
        }
    }
}
