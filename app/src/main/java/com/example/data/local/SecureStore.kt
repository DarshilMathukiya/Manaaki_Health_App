package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.UserSession

/**
 * SecureStore handles persistent caching of session tokens and user credentials metadata.
 * 
 * CRITICAL ARCHITECTURAL POLICY:
 * - We NEVER store plain-text passwords in SecureStore.
 * - Calling [clearSession] only clears authentication tokens and session credentials.
 *   Encrypted on-device health data (Medication, Vitals, Activity, SOS records in Room)
 *   remains safely preserved on-device under device-level encryption.
 */
class SecureStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "manaaki_secure_auth_store",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_EMAIL = "auth_email"
        private const val KEY_FULL_NAME = "auth_full_name"
        private const val KEY_PHONE = "auth_phone"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EXPIRES_AT = "auth_expires_at"
        private const val KEY_BIOMETRIC_OPT_IN = "auth_biometric_opt_in"
        private const val KEY_CAREGIVER_NAME = "auth_caregiver_name"
        private const val KEY_CAREGIVER_PHONE = "auth_caregiver_phone"
        private const val KEY_HAS_LOGGED_IN_BEFORE = "auth_has_logged_in_before"
    }

    fun saveSession(session: UserSession) {
        prefs.edit()
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_FULL_NAME, session.fullName)
            .putString(KEY_PHONE, session.phoneNumber)
            .putString(KEY_TOKEN, session.token)
            .putLong(KEY_EXPIRES_AT, session.expiresAt)
            .putBoolean(KEY_BIOMETRIC_OPT_IN, session.isBiometricEnabled)
            .putString(KEY_CAREGIVER_NAME, session.caregiverName)
            .putString(KEY_CAREGIVER_PHONE, session.caregiverPhone)
            .putBoolean(KEY_HAS_LOGGED_IN_BEFORE, true)
            .apply()
    }

    fun getSession(): UserSession? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val fullName = prefs.getString(KEY_FULL_NAME, "Margaret Te Aroha") ?: "Margaret Te Aroha"
        val phone = prefs.getString(KEY_PHONE, "+64 21 555 8392") ?: "+64 21 555 8392"
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        val isBiometric = prefs.getBoolean(KEY_BIOMETRIC_OPT_IN, false)
        val caregiverName = prefs.getString(KEY_CAREGIVER_NAME, null)
        val caregiverPhone = prefs.getString(KEY_CAREGIVER_PHONE, null)

        // Session validation (e.g., 30 days rolling expiration)
        if (expiresAt > 0 && System.currentTimeMillis() > expiresAt) {
            clearSession()
            return null
        }

        return UserSession(
            userId = userId,
            email = email,
            fullName = fullName,
            phoneNumber = phone,
            token = token,
            expiresAt = expiresAt,
            isBiometricEnabled = isBiometric,
            caregiverName = caregiverName,
            caregiverPhone = caregiverPhone
        )
    }

    /**
     * Clears authentication tokens upon logout.
     * Note: Does NOT delete encrypted SQLite/Room health database.
     */
    fun clearSession() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_EMAIL)
            .remove(KEY_FULL_NAME)
            .remove(KEY_PHONE)
            .remove(KEY_TOKEN)
            .remove(KEY_EXPIRES_AT)
            .remove(KEY_CAREGIVER_NAME)
            .remove(KEY_CAREGIVER_PHONE)
            // Note: We retain KEY_BIOMETRIC_OPT_IN and KEY_HAS_LOGGED_IN_BEFORE for user convenience
            .apply()
    }

    fun setBiometricOptIn(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_OPT_IN, enabled).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        setBiometricOptIn(enabled)
    }

    fun isBiometricOptIn(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_OPT_IN, false)
    }

    fun hasLoggedInBefore(): Boolean {
        return prefs.getBoolean(KEY_HAS_LOGGED_IN_BEFORE, false)
    }
}
