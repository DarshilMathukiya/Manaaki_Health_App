package com.example.domain.model

enum class PillIconType {
    ROUND_WHITE,
    OVAL_PINK,
    CAPSULE_BLUE_WHITE,
    CAPSULE_RED_YELLOW,
    DROP_AMBER,
    TABLET_GREEN
}

enum class SyncStatus {
    PENDING,
    SYNCED
}

enum class DoseStatus {
    SCHEDULED,
    TAKEN,
    SNOOZED,
    MISSED
}

enum class VitalsType {
    BLOOD_PRESSURE,
    HEART_RATE,
    BLOOD_GLUCOSE
}

enum class SleepWakeState {
    AWAKE,
    RESTING,
    ASLEEP
}

enum class AdvisorySeverity {
    NORMAL,
    ADVISORY_LOW,
    ELEVATED,
    CRITICAL_RED_FLAG
}

enum class SOSTrigger {
    MANUAL,
    AUTO_FALL,
    AUTO_ROUTINE
}

enum class FacilityType {
    HOSPITAL,
    URGENT_CARE,
    GP_CLINIC,
    PHARMACY
}

data class MedicationSchedule(
    val id: Long = 0,
    val name: String,
    val iconType: PillIconType = PillIconType.ROUND_WHITE,
    val dosage: String,
    val timesPerDay: Int,
    val reminderTimes: List<String>, // e.g. ["08:00", "20:00"]
    val startDate: Long,
    val endDate: Long? = null,
    val remainingSupply: Int,
    val lowSupplyThreshold: Int = 5,
    val isCritical: Boolean = false,
    val instructions: String = "Take with water after meals",
    val syncStatus: SyncStatus = SyncStatus.PENDING
) {
    val isLowSupply: Boolean get() = remainingSupply <= lowSupplyThreshold
}

data class MedicationLog(
    val id: Long = 0,
    val scheduleId: Long,
    val medicationName: String,
    val scheduledTime: String, // e.g. "08:00"
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val actualTime: Long? = null, // epoch millis when taken/snoozed
    val status: DoseStatus = DoseStatus.SCHEDULED,
    val snoozeCount: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

data class VitalsRecord(
    val id: Long = 0,
    val type: VitalsType,
    val systolic: Int? = null,
    val diastolic: Int? = null,
    val value: Double, // Heart rate bpm, or glucose mmol/L, or systolic
    val unit: String,
    val recordedAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isRedFlag: Boolean = false,
    val severity: AdvisorySeverity = AdvisorySeverity.NORMAL,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

data class ActivityWindow(
    val id: Long = 0,
    val windowStart: Long,
    val windowEnd: Long,
    val stepCount: Int,
    val gaitCadence: Double, // steps per minute
    val gaitSpeedMps: Double, // meters per second
    val sedentaryMinutes: Int,
    val sleepWakeState: SleepWakeState = SleepWakeState.AWAKE,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

data class TriageResult(
    val id: Long = 0,
    val sourceType: String, // "VITALS", "ACTIVITY", "FALL_DETECTED", "ROUTINE_DEVIATION"
    val isRedFlag: Boolean,
    val ruleTriggered: String,
    val advisorySeverity: AdvisorySeverity,
    val modelConfidence: Double = 0.95,
    val summary: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class SOSEvent(
    val id: Long = 0,
    val triggeredBy: SOSTrigger,
    val latitude: Double,
    val longitude: Double,
    val locationAddress: String,
    val medicalIdSummary: String,
    val timestamp: Long = System.currentTimeMillis(),
    val emergencyPhoneDialed: String = "111", // NZ Emergency Number
    val status: String = "DISPATCHED",
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

data class CareFacility(
    val id: String,
    val name: String,
    val type: FacilityType,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String,
    val distanceKm: Double = 0.0,
    val isAfterHoursEmergency: Boolean = false,
    val isRegisteredGP: Boolean = false
)

data class AppointmentRequest(
    val id: Long = 0,
    val facilityId: String,
    val facilityName: String,
    val reasonCategory: String, // e.g. "General check-up", "Follow-up", "Medication review", "Other"
    val requestedDateTime: String, // e.g. "Tomorrow • 10:00 AM"
    val patientName: String,
    val patientContact: String,
    val status: String = "REQUESTED", // REQUESTED, CONFIRMED, DECLINED
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

data class EmergencyContact(
    val name: String,
    val relationship: String,
    val phone: String,
    val isPrimary: Boolean = true
)

data class MedicalId(
    val fullName: String = "Margaret Te Aroha",
    val dateOfBirth: String = "14/05/1948",
    val nhiNumber: String = "ABC1234", // NZ National Health Index
    val bloodType: String = "O+",
    val knownAllergies: String = "Penicillin, NSAIDs (mild)",
    val chronicConditions: String = "Hypertension, Type 2 Diabetes, Mild Osteoarthritis",
    val emergencyContacts: List<EmergencyContact> = listOf(
        EmergencyContact("David Te Aroha", "Son (Caregiver)", "+64 21 555 0192", true),
        EmergencyContact("Sarah Jenkins", "Daughter", "+64 22 555 0834", false),
        EmergencyContact("Dr. Alistair Ross", "GP (Greenlane Medical)", "+64 9 555 1200", false)
    )
)

enum class TaskType(val displayName: String) {
    MEDICATION("Medication"),
    APPOINTMENT("Appointment"),
    CHECK_IN("Check-in"),
    CUSTOM("Custom Reminder")
}

data class CaregiverTask(
    val id: Long = 0,
    val patientId: String = "user_clerk_margaret_nz",
    val caregiverName: String,
    val type: TaskType,
    val title: String,
    val details: String,
    val dueTime: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class FallDetectionMetrics(
    val peakG: Double = 0.0,
    val freeFallDurationMs: Long = 0,
    val postImpactStillnessScore: Double = 0.0,
    val isDeterministicTriggered: Boolean = false,
    val mlModelConfidence: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

