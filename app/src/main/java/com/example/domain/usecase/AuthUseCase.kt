package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.domain.model.AuthResult
import com.example.domain.model.RegistrationData
import com.example.domain.model.UserSession

/**
 * AuthUseCase encapsulates all domain logic for authentication, session verification,
 * biometric unlock, and secure logout.
 */
class AuthUseCase(
    private val authRepository: AuthRepository
) {

    suspend fun restoreSession(): UserSession? {
        return authRepository.restoreSession()
    }

    suspend fun login(emailOrPhone: String, password: String): AuthResult {
        if (emailOrPhone.isBlank()) {
            return AuthResult.Error("Please enter your email address or phone number.")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Please enter your password.")
        }
        return authRepository.login(emailOrPhone, password)
    }

    suspend fun register(data: RegistrationData): AuthResult {
        if (data.fullName.isBlank()) {
            return AuthResult.Error("Please enter your full name.")
        }
        if (data.email.isBlank() || !data.email.contains("@")) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        if (data.password.length < 8) {
            return AuthResult.Error("Password must be at least 8 characters.")
        }
        if (!data.password.any { it.isDigit() }) {
            return AuthResult.Error("Password must include at least one number.")
        }
        if (!data.consentAgreed) {
            return AuthResult.Error("Please agree to the health privacy policy to proceed.")
        }
        return authRepository.register(data)
    }

    suspend fun unlockWithBiometric(): AuthResult {
        return authRepository.unlockWithBiometric()
    }

    suspend fun logout() {
        authRepository.logout()
    }

    fun setBiometricOptIn(enabled: Boolean) {
        authRepository.setBiometricOptIn(enabled)
    }

    fun isBiometricOptIn(): Boolean {
        return authRepository.isBiometricOptIn()
    }

    fun hasLoggedInBefore(): Boolean {
        return authRepository.hasLoggedInBefore()
    }
}
