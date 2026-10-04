package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.local.SecureStore
import com.example.domain.model.AuthResult
import com.example.domain.model.RegistrationData
import com.example.domain.model.SampleData
import com.example.domain.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * AuthRepository coordinates Clerk OIDC authentication and SecureStore token persistence.
 * Adheres strictly to offline-first principles:
 * - Session restoration from SecureStore works completely offline (0-tap path).
 * - First-time login/registration graceful handling with plain-language messaging.
 */
class AuthRepository(
    private val context: Context,
    private val secureStore: SecureStore
) {

    // Built-in registered permanent & demo accounts
    private val registeredAccounts = mutableMapOf<String, Pair<String, UserSession>>(
        "jeel12@gmail.com" to Pair(
            "jeel@12",
            UserSession(
                userId = "user_patient_jeel_nz",
                email = "jeel12@gmail.com",
                fullName = SampleData.PATIENT_FULL_NAME,
                phoneNumber = "+64 21 555 8392",
                token = "clerk_jwt_token_jeel_patient_nz_83921049",
                expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
                isBiometricEnabled = true,
                caregiverName = SampleData.CAREGIVER_NAME,
                caregiverPhone = SampleData.CAREGIVER_PHONE
            )
        ),
        "admin" to Pair(
            "Admin01234",
            UserSession(
                userId = "user_admin_caregiver_nz",
                email = "admin",
                fullName = SampleData.CAREGIVER_NAME,
                phoneNumber = SampleData.CAREGIVER_PHONE,
                token = "clerk_jwt_token_admin_caregiver_nz_910283",
                expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
                isBiometricEnabled = true,
                caregiverName = SampleData.CAREGIVER_NAME,
                caregiverPhone = SampleData.CAREGIVER_PHONE
            )
        ),
        "demo@example.com" to Pair(
            "demo123Password",
            UserSession(
                userId = "user_demo_jeel_nz",
                email = "demo@example.com",
                fullName = SampleData.PATIENT_FULL_NAME,
                phoneNumber = "+64 21 555 8392",
                token = "clerk_jwt_token_session_prod_nz_83921049",
                expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
                isBiometricEnabled = true,
                caregiverName = SampleData.CAREGIVER_NAME,
                caregiverPhone = SampleData.CAREGIVER_PHONE
            )
        )
    )

    var isSimulatedOffline: Boolean = false

    private fun isNetworkAvailable(): Boolean {
        if (isSimulatedOffline) return false
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return true
            val network = cm.activeNetwork ?: return true
            val caps = cm.getNetworkCapabilities(network) ?: return true
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Attempts to restore existing cached session from SecureStore.
     * Offline-first, 0 network required.
     */
    suspend fun restoreSession(): UserSession? = withContext(Dispatchers.IO) {
        secureStore.getSession()
    }

    /**
     * Authenticates with Clerk OIDC using email/phone and password.
     */
    suspend fun login(emailOrPhone: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedInput = emailOrPhone.trim().lowercase()

        // 1. Check network connectivity if no local cache exists
        if (!isNetworkAvailable()) {
            return@withContext AuthResult.Error(
                message = "You'll need an internet connection the first time you log in on this device.",
                isOfflineBlocked = true
            )
        }

        // Simulate secure Clerk OIDC network handshake latency
        delay(900)

        // 2. Lookup account credentials
        val matchedEntry = registeredAccounts.entries.find { 
            it.key.equals(trimmedInput, ignoreCase = true) ||
            it.value.second.phoneNumber.replace(" ", "").equals(trimmedInput.replace(" ", ""), ignoreCase = true)
        }

        if (matchedEntry == null) {
            // Unknown account
            return@withContext AuthResult.Error(
                message = "We couldn't find an account with that email. Would you like to create one?",
                isUnknownAccount = true
            )
        }

        val (correctPassword, session) = matchedEntry.value
        if (password != correctPassword) {
            // Wrong password
            return@withContext AuthResult.Error(
                message = "That password doesn't match. Try again or reset it below.",
                isWrongPassword = true
            )
        }

        // Success - persist session token in SecureStore
        val updatedSession = session.copy(
            token = "clerk_oidc_jwt_${UUID.randomUUID()}",
            expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
        )
        secureStore.saveSession(updatedSession)

        AuthResult.Success(updatedSession)
    }

    /**
     * Creates a new account via Clerk OIDC, caches session in SecureStore.
     */
    suspend fun register(data: RegistrationData): AuthResult = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext AuthResult.Error(
                message = "You'll need an internet connection to create your account.",
                isOfflineBlocked = true
            )
        }

        // Simulate Clerk OIDC registration & encryption provisioning
        delay(1200)

        val emailKey = data.email.trim().lowercase()
        val newUserId = "user_clerk_${UUID.randomUUID().toString().take(12)}"
        val newSession = UserSession(
            userId = newUserId,
            email = data.email.trim(),
            fullName = data.fullName.trim(),
            phoneNumber = data.phoneNumber.trim(),
            token = "clerk_oidc_jwt_${UUID.randomUUID()}",
            expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
            isBiometricEnabled = false,
            caregiverName = data.caregiverName.trim().takeIf { it.isNotEmpty() },
            caregiverPhone = data.caregiverPhone.trim().takeIf { it.isNotEmpty() }
        )

        // Store account locally for future logins in this session/app lifetime
        registeredAccounts[emailKey] = Pair(data.password, newSession)

        // Cache session token in SecureStore immediately
        secureStore.saveSession(newSession)

        AuthResult.Success(newSession)
    }

    /**
     * Biometric quick-unlock using cached SecureStore credentials.
     */
    suspend fun unlockWithBiometric(): AuthResult = withContext(Dispatchers.IO) {
        delay(400)
        val session = secureStore.getSession()
        if (session != null) {
            AuthResult.Success(session)
        } else {
            AuthResult.Error("Biometric session expired. Please log in with your password.")
        }
    }

    /**
     * Logs out the user by wiping the session token from SecureStore.
     * Preserves encrypted local SQLite health records.
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        secureStore.clearSession()
    }

    fun setBiometricOptIn(enabled: Boolean) {
        secureStore.setBiometricOptIn(enabled)
    }

    fun isBiometricOptIn(): Boolean {
        return secureStore.isBiometricOptIn()
    }

    fun hasLoggedInBefore(): Boolean {
        return secureStore.hasLoggedInBefore()
    }
}
