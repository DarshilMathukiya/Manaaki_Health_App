package com.example.domain.model

data class UserSession(
    val userId: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val token: String,
    val expiresAt: Long,
    val isBiometricEnabled: Boolean = false,
    val caregiverName: String? = null,
    val caregiverPhone: String? = null
)

data class RegistrationData(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val caregiverName: String = "",
    val caregiverPhone: String = "",
    val consentAgreed: Boolean = false
)

sealed class AuthResult {
    data class Success(val session: UserSession) : AuthResult()
    data class Error(
        val message: String,
        val isUnknownAccount: Boolean = false,
        val isWrongPassword: Boolean = false,
        val isOfflineBlocked: Boolean = false
    ) : AuthResult()
}
