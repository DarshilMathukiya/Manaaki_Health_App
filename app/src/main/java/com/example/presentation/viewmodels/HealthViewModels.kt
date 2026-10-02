package com.example.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ManaakiDatabase
import com.example.data.repository.ActivityRepository
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.CareFacilityRepository
import com.example.data.repository.EmergencyRepository
import com.example.data.repository.MedicationRepository
import com.example.data.repository.TriageRepository
import com.example.data.repository.VitalsRepository
import com.example.domain.model.ActivityWindow
import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.AppointmentRequest
import com.example.domain.model.CareFacility
import com.example.domain.model.DoseStatus
import com.example.domain.model.FacilityType
import com.example.domain.model.MedicalId
import com.example.domain.model.MedicationLog
import com.example.domain.model.MedicationSchedule
import com.example.domain.model.PillIconType
import com.example.domain.model.SOSEvent
import com.example.domain.model.SOSTrigger
import com.example.domain.model.SleepWakeState
import com.example.domain.model.SyncStatus
import com.example.domain.model.TriageResult
import com.example.domain.model.VitalsRecord
import com.example.domain.model.VitalsType
import com.example.domain.usecase.EvaluateRedFlagUseCase
import com.example.domain.usecase.RequestAppointmentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import android.util.Log
import android.widget.Toast
import com.example.data.local.SecureStore
import com.example.data.repository.AuthRepository
import com.example.data.repository.CaregiverTaskRepository
import com.example.domain.model.CaregiverTask
import com.example.domain.model.FallDetectionMetrics
import com.example.domain.model.TaskType
import com.example.domain.usecase.AuthUseCase
import com.example.presentation.voice.VoiceAssistantManager
import java.io.File

// ---------------- Application Dependency Container / Provider ----------------

class ManaakiAppContainer(context: Context) {
    val database: ManaakiDatabase by lazy {
        androidx.room.Room.databaseBuilder(
            context.applicationContext,
            ManaakiDatabase::class.java,
            "manaaki_health.db"
        ).fallbackToDestructiveMigration().build()
    }

    val secureStore by lazy { SecureStore(context) }
    val authRepository by lazy { AuthRepository(context, secureStore) }
    val authUseCase by lazy { AuthUseCase(authRepository) }

    val voiceAssistantManager by lazy { VoiceAssistantManager(context.applicationContext) }

    val medicationRepository by lazy { MedicationRepository(database.medicationDao()) }
    val vitalsRepository by lazy { VitalsRepository(database.vitalsDao()) }
    val activityRepository by lazy { ActivityRepository(database.activityDao()) }
    val triageRepository by lazy { TriageRepository(database.triageDao()) }
    val emergencyRepository by lazy { EmergencyRepository(database.sosDao()) }
    val careFacilityRepository by lazy { CareFacilityRepository() }
    val appointmentRepository by lazy { AppointmentRepository(database.appointmentDao()) }
    val caregiverTaskRepository by lazy { CaregiverTaskRepository(database.caregiverTaskDao()) }
    val requestAppointmentUseCase by lazy { RequestAppointmentUseCase(appointmentRepository) }
    val evaluateRedFlagUseCase by lazy { EvaluateRedFlagUseCase() }
}

// ---------------- Home ViewModel (Integrated 5-Function Dashboard) ----------------

data class HomeUiState(
    // 1. Medication Function
    val nextDose: MedicationSchedule? = null,
    val nextDoseTime: String? = null,
    val pendingDosesCount: Int = 0,
    val takenDosesCount: Int = 0,
    val totalDosesCount: Int = 3,
    val lowSupplyMedications: List<MedicationSchedule> = emptyList(),
    val adherencePercent: Int = 100,

    // 2. Vitals Function
    val latestVital: VitalsRecord? = null,

    // 3. Activity Function
    val todaySteps: Int = 3480,
    val baselineAverageSteps: Int = 3870,
    val routinePercent: Int = 90,

    // 4. Emergency & Safety Function
    val activeRedFlag: TriageResult? = null,
    val isFallDetected: Boolean = false,
    val emergencyStatusSummary: String = "No active alerts — Routine stable",

    // 5. Caregiver Function
    val caregiverName: String = "David Te Aroha (General Practitioner)",
    val lastSyncedTimestamp: Long = System.currentTimeMillis() - (15 * 60 * 1000),
    val pendingSyncCount: Int = 0,
    val isSyncing: Boolean = false,

    // Voice Assistant UI state
    val isVoiceModalOpen: Boolean = false
)

class HomeViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeHomeData()
    }

    private fun observeHomeData() {
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()

            val schedulesAndLogs = combine(
                container.medicationRepository.getAllSchedules(),
                container.medicationRepository.getLogsForDay(todayEpoch)
            ) { schedules: List<MedicationSchedule>, logs: List<MedicationLog> ->
                Pair(schedules, logs)
            }

            val vitalsAndTriage = combine(
                container.vitalsRepository.getLatestVital(),
                container.triageRepository.getHistory()
            ) { vital: VitalsRecord?, triage: List<TriageResult> ->
                Pair(vital, triage)
            }

            val sensorStream = combine(
                flowOf(3480),
                flowOf(false)
            ) { steps: Int, isFall: Boolean ->
                Pair(steps, isFall)
            }

            val appointmentsAndSync = container.appointmentRepository.getAllAppointments()

            combine(
                schedulesAndLogs,
                vitalsAndTriage,
                sensorStream,
                appointmentsAndSync
            ) { (schedules, logs), (latestVital, triageHistory), (currentSteps, isFall), appointments ->
                val lowSupply = schedules.filter { it.isLowSupply }
                val takenScheduleIds = logs.filter { it.status == DoseStatus.TAKEN }.map { it.scheduleId }.toSet()
                val pending = schedules.filter { it.id !in takenScheduleIds }
                val takenCount = logs.count { it.status == DoseStatus.TAKEN }

                val redFlag = triageHistory.firstOrNull { it.isRedFlag && (System.currentTimeMillis() - it.createdAt < 24 * 3600 * 1000) }
                val adherence = container.medicationRepository.calculate7DayAdherence()
                val baseline = container.activityRepository.get7DayRollingAverageSteps()
                val routinePercent = ((currentSteps.toFloat() / baseline.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 150)

                val pendingAppointments = appointments.count { it.syncStatus == SyncStatus.PENDING }

                val safetySummary = when {
                    isFall -> "Fall impact alert active"
                    redFlag != null -> "1 vital sign alert: ${redFlag.summary}"
                    else -> "No active alerts — Routine stable"
                }

                HomeUiState(
                    nextDose = pending.firstOrNull(),
                    nextDoseTime = pending.firstOrNull()?.reminderTimes?.firstOrNull() ?: "08:00",
                    pendingDosesCount = pending.size,
                    takenDosesCount = takenCount,
                    totalDosesCount = schedules.size.coerceAtLeast(1),
                    lowSupplyMedications = lowSupply,
                    latestVital = latestVital,
                    todaySteps = currentSteps,
                    baselineAverageSteps = baseline,
                    routinePercent = routinePercent,
                    activeRedFlag = redFlag,
                    adherencePercent = adherence,
                    isFallDetected = isFall,
                    emergencyStatusSummary = safetySummary,
                    pendingSyncCount = pendingAppointments,
                    isVoiceModalOpen = _uiState.value.isVoiceModalOpen
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun markNextDoseTaken() {
        val dose = _uiState.value.nextDose ?: return
        val time = _uiState.value.nextDoseTime ?: "08:00"
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            container.medicationRepository.recordDoseTaken(
                scheduleId = dose.id,
                medicationName = dose.name,
                scheduledTime = time,
                epochDay = todayEpoch
            )
            if (dose.remainingSupply - 1 <= dose.lowSupplyThreshold) {
                Log.d("HomeViewModel", "Low supply alert for ${dose.name}")
            }
        }
    }

    fun snoozeNextDose() {
        val dose = _uiState.value.nextDose ?: return
        val time = _uiState.value.nextDoseTime ?: "08:00"
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            container.medicationRepository.recordDoseSnooze(
                scheduleId = dose.id,
                medicationName = dose.name,
                scheduledTime = time,
                epochDay = todayEpoch
            )
            Log.d("HomeViewModel", "Snoozed dose for ${dose.name}")
        }
    }

    fun syncCaregiverNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            container.appointmentRepository.syncPendingAppointments()
            delay(1000)
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                lastSyncedTimestamp = System.currentTimeMillis(),
                pendingSyncCount = 0
            )
            Log.d("HomeViewModel", "Synced caregiver")
        }
    }

    fun openVoiceAssistant() {
        _uiState.value = _uiState.value.copy(isVoiceModalOpen = true)
    }

    fun closeVoiceAssistant() {
        _uiState.value = _uiState.value.copy(isVoiceModalOpen = false)
    }

    fun dismissFallAlert() {
        Log.d("HomeViewModel", "Dismiss fall alert")
    }

    fun simulateStepWalk() {
        Log.d("HomeViewModel", "Simulate step walk")
    }
}

// ---------------- Medication ViewModel ----------------

data class MedicationUiState(
    val schedules: List<MedicationSchedule> = emptyList(),
    val todayLogs: List<MedicationLog> = emptyList(),
    val recentLogs: List<MedicationLog> = emptyList(),
    val adherencePercent: Int = 100,
    val lowSupplyMedications: List<MedicationSchedule> = emptyList(),
    val isAddMedicationDialogOpen: Boolean = false,
    val reminderAlertDose: MedicationSchedule? = null,
    val bannerMessage: String? = null
)

class MedicationViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            combine(
                container.medicationRepository.getAllSchedules(),
                container.medicationRepository.getLogsForDay(todayEpoch),
                container.medicationRepository.getRecentLogs()
            ) { schedules, todayLogs, recentLogs ->
                val adherence = container.medicationRepository.calculate7DayAdherence()
                val lowSupply = schedules.filter { it.isLowSupply }
                MedicationUiState(
                    schedules = schedules,
                    todayLogs = todayLogs,
                    recentLogs = recentLogs,
                    adherencePercent = adherence,
                    lowSupplyMedications = lowSupply
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun markDoseTaken(schedule: MedicationSchedule, time: String) {
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            container.medicationRepository.recordDoseTaken(
                scheduleId = schedule.id,
                medicationName = schedule.name,
                scheduledTime = time,
                epochDay = todayEpoch
            )
            val updatedSupply = schedule.remainingSupply - 1
            if (updatedSupply <= schedule.lowSupplyThreshold) {
                Log.d("MedicationViewModel", "Low supply alert: ${schedule.name}")
            }
            _uiState.value = _uiState.value.copy(
                bannerMessage = "Marked ${schedule.name} as Taken! Supply is now $updatedSupply."
            )
        }
    }

    fun snoozeDose(schedule: MedicationSchedule, time: String) {
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            container.medicationRepository.recordDoseSnooze(
                scheduleId = schedule.id,
                medicationName = schedule.name,
                scheduledTime = time,
                epochDay = todayEpoch
            )
            Log.d("MedicationViewModel", "Snoozed dose for ${schedule.name}")
            _uiState.value = _uiState.value.copy(
                bannerMessage = "Snoozed ${schedule.name} for 15 minutes. Reminder will chime again."
            )
        }
    }

    fun addCustomMedication(
        name: String,
        dosage: String,
        iconType: PillIconType,
        timesPerDay: Int,
        reminderTimes: List<String>,
        supply: Int,
        isCritical: Boolean,
        instructions: String
    ) {
        viewModelScope.launch {
            container.medicationRepository.addSchedule(
                MedicationSchedule(
                    name = name,
                    dosage = dosage,
                    iconType = iconType,
                    timesPerDay = timesPerDay,
                    reminderTimes = reminderTimes,
                    startDate = System.currentTimeMillis(),
                    remainingSupply = supply,
                    lowSupplyThreshold = 5,
                    isCritical = isCritical,
                    instructions = instructions
                )
            )
            _uiState.value = _uiState.value.copy(
                isAddMedicationDialogOpen = false,
                bannerMessage = "Added $name to daily schedule."
            )
        }
    }

    fun refillMedication(scheduleId: Long, count: Int) {
        viewModelScope.launch {
            container.medicationRepository.refillSupply(scheduleId, count)
            _uiState.value = _uiState.value.copy(bannerMessage = "Refilled medication supply.")
        }
    }

    fun openAddDialog() {
        _uiState.value = _uiState.value.copy(isAddMedicationDialogOpen = true)
    }

    fun closeAddDialog() {
        _uiState.value = _uiState.value.copy(isAddMedicationDialogOpen = false)
    }

    fun clearBanner() {
        _uiState.value = _uiState.value.copy(bannerMessage = null)
    }
}

// ---------------- Vitals ViewModel ----------------

data class VitalsUiState(
    val allVitals: List<VitalsRecord> = emptyList(),
    val latestVital: VitalsRecord? = null,
    val isLogDialogOpen: Boolean = false,
    val selectedType: VitalsType = VitalsType.BLOOD_PRESSURE,
    val lastTriageResult: TriageResult? = null,
    val redFlagAlert: TriageResult? = null
)

class VitalsViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VitalsUiState())
    val uiState: StateFlow<VitalsUiState> = _uiState.asStateFlow()

    init {
        loadVitals()
    }

    private fun loadVitals() {
        viewModelScope.launch {
            container.vitalsRepository.getAllVitals().collect { list ->
                _uiState.value = _uiState.value.copy(
                    allVitals = list,
                    latestVital = list.firstOrNull()
                )
            }
        }
    }

    fun logBloodPressure(systolic: Int, diastolic: Int, notes: String) {
        viewModelScope.launch {
            val triage = container.evaluateRedFlagUseCase.evaluateBloodPressure(systolic, diastolic)
            container.triageRepository.logTriage(triage)

            val record = VitalsRecord(
                type = VitalsType.BLOOD_PRESSURE,
                systolic = systolic,
                diastolic = diastolic,
                value = systolic.toDouble(),
                unit = "mmHg",
                recordedAt = System.currentTimeMillis(),
                notes = notes,
                isRedFlag = triage.isRedFlag,
                severity = triage.advisorySeverity,
                syncStatus = SyncStatus.PENDING
            )
            container.vitalsRepository.recordVitals(record)

            if (triage.isRedFlag) {
                Log.d("VitalsViewModel", "Blood Pressure red flag: ${triage.summary}")
            }

            _uiState.value = _uiState.value.copy(
                isLogDialogOpen = false,
                lastTriageResult = triage,
                redFlagAlert = if (triage.isRedFlag) triage else null
            )
        }
    }

    fun logHeartRate(bpm: Int, notes: String) {
        viewModelScope.launch {
            val triage = container.evaluateRedFlagUseCase.evaluateHeartRate(bpm)
            container.triageRepository.logTriage(triage)

            val record = VitalsRecord(
                type = VitalsType.HEART_RATE,
                systolic = null,
                diastolic = null,
                value = bpm.toDouble(),
                unit = "bpm",
                recordedAt = System.currentTimeMillis(),
                notes = notes,
                isRedFlag = triage.isRedFlag,
                severity = triage.advisorySeverity,
                syncStatus = SyncStatus.PENDING
            )
            container.vitalsRepository.recordVitals(record)

            if (triage.isRedFlag) {
                Log.d("VitalsViewModel", "Heart rate red flag: ${triage.summary}")
            }

            _uiState.value = _uiState.value.copy(
                isLogDialogOpen = false,
                lastTriageResult = triage,
                redFlagAlert = if (triage.isRedFlag) triage else null
            )
        }
    }

    fun logGlucose(mmolL: Double, notes: String) {
        viewModelScope.launch {
            val triage = container.evaluateRedFlagUseCase.evaluateBloodGlucose(mmolL)
            container.triageRepository.logTriage(triage)

            val record = VitalsRecord(
                type = VitalsType.BLOOD_GLUCOSE,
                systolic = null,
                diastolic = null,
                value = mmolL,
                unit = "mmol/L",
                recordedAt = System.currentTimeMillis(),
                notes = notes,
                isRedFlag = triage.isRedFlag,
                severity = triage.advisorySeverity,
                syncStatus = SyncStatus.PENDING
            )
            container.vitalsRepository.recordVitals(record)

            if (triage.isRedFlag) {
                Log.d("VitalsViewModel", "Blood glucose red flag: ${triage.summary}")
            }

            _uiState.value = _uiState.value.copy(
                isLogDialogOpen = false,
                lastTriageResult = triage,
                redFlagAlert = if (triage.isRedFlag) triage else null
            )
        }
    }

    fun openLogDialog(type: VitalsType = VitalsType.BLOOD_PRESSURE) {
        _uiState.value = _uiState.value.copy(isLogDialogOpen = true, selectedType = type)
    }

    fun closeLogDialog() {
        _uiState.value = _uiState.value.copy(isLogDialogOpen = false)
    }

    fun dismissRedFlagAlert() {
        _uiState.value = _uiState.value.copy(redFlagAlert = null)
    }
}

// ---------------- Activity ViewModel ----------------

data class ActivityUiState(
    val todaySteps: Int = 3480,
    val baselineAverageSteps: Int = 3870,
    val gaitCadence: Double = 94.0, // steps/min
    val gaitSpeedMps: Double = 0.92, // m/s
    val sedentaryMinutes: Int = 180,
    val activeMinutes: Int = 45,
    val deviationTriage: TriageResult? = null,
    val isFallDetected: Boolean = false,
    val fallMetrics: FallDetectionMetrics = FallDetectionMetrics(),
    val countdownSeconds: Int = 30,
    val isCountdownActive: Boolean = false,
    val routineRatio: Float = 0.90f,
    val isActivityLow: Boolean = false,
    val registeredGP: CareFacility? = null,
    val nearestFacility: CareFacility? = null,
    val contextualNudgeMessage: String? = null
) {
    val lastFallPeakG: Double get() = fallMetrics.peakG
    val lastFallConfidence: Double get() = fallMetrics.mlModelConfidence
    val fallSeverity: String get() = when {
        fallMetrics.peakG >= 3.5 -> "HIGH IMPACT"
        fallMetrics.peakG >= 2.5 -> "MODERATE"
        else -> "LOW"
    }
}

class ActivityViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    init {
        observeActivity()
    }

    private fun observeActivity() {
        viewModelScope.launch {
            combine(
                flowOf(3480),
                flowOf(98.0),
                flowOf(false),
                flowOf(FallDetectionMetrics()),
                flowOf(0),
                flowOf(false)
            ) { args: Array<Any> ->
                val steps = args[0] as Int
                val cadence = args[1] as Double
                val isFall = args[2] as Boolean
                val metrics = args[3] as FallDetectionMetrics
                val countdown = args[4] as Int
                val isCounting = args[5] as Boolean

                val baseline = container.activityRepository.get7DayRollingAverageSteps()
                val triage = container.evaluateRedFlagUseCase.evaluateActivityDeviation(steps, baseline)
                val ratio = if (baseline > 0) steps.toFloat() / baseline.toFloat() else 1.0f
                val isLow = ratio < 0.70f || isFall

                val regGp = container.careFacilityRepository.getRegisteredGP()
                val nearest = container.careFacilityRepository.getNearestFacility()

                val nudge = when {
                    isFall -> "Fall detected: ${String.format(java.util.Locale.US, "%.1f", metrics.peakG)}g impact (ML Conf: ${(metrics.mlModelConfidence * 100).toInt()}%)."
                    isLow -> "Your activity has been lower than usual today. You may want to book a check-up."
                    else -> null
                }

                ActivityUiState(
                    todaySteps = steps,
                    baselineAverageSteps = baseline,
                    gaitCadence = cadence,
                    gaitSpeedMps = 0.92,
                    sedentaryMinutes = 180,
                    activeMinutes = (steps / 90).coerceAtLeast(10),
                    deviationTriage = triage,
                    isFallDetected = isFall,
                    fallMetrics = metrics,
                    countdownSeconds = countdown,
                    isCountdownActive = isCounting,
                    routineRatio = ratio,
                    isActivityLow = isLow,
                    registeredGP = regGp,
                    nearestFacility = nearest,
                    contextualNudgeMessage = nudge
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun triggerFallSimulation() {
        Log.d("ActivityViewModel", "Trigger fall simulation")
    }

    fun dismissFall() {
        Log.d("ActivityViewModel", "Dismiss fall")
    }

    fun addWalkingSteps() {
        Log.d("ActivityViewModel", "Add walking steps")
    }
}

// ---------------- Facility Filter Enum ----------------

enum class FacilityFilter(val label: String) {
    ALL("All"),
    EMERGENCY_24_7("24/7 ED"),
    URGENT_CARE("Urgent Care"),
    GP_CLINICS("GP Clinics")
}

// ---------------- Emergency / SOS ViewModel ----------------

data class EmergencyUiState(
    val medicalId: MedicalId = MedicalId(),
    val latitude: Double = -36.8509, // Auckland Central GPS
    val longitude: Double = 174.7645,
    val locationAddress: String = "Queen St & Wellesley St, Auckland Central 1010, NZ",
    val nearbyFacilities: List<CareFacility> = emptyList(),
    val filteredFacilities: List<CareFacility> = emptyList(),
    val selectedFilter: FacilityFilter = FacilityFilter.ALL,
    val isSOSDispatched: Boolean = false,
    val lastSOSEvent: SOSEvent? = null
)

class EmergencyViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        loadFacilities()
    }

    private fun loadFacilities() {
        val lat = -36.8509
        val lon = 174.7645
        val facilities = container.careFacilityRepository.getNearbyFacilities(lat, lon)
        _uiState.value = _uiState.value.copy(
            latitude = lat,
            longitude = lon,
            nearbyFacilities = facilities,
            filteredFacilities = applyFilter(facilities, _uiState.value.selectedFilter)
        )
    }

    fun setFilter(filter: FacilityFilter) {
        val filtered = applyFilter(_uiState.value.nearbyFacilities, filter)
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredFacilities = filtered
        )
    }

    private fun applyFilter(facilities: List<CareFacility>, filter: FacilityFilter): List<CareFacility> {
        return when (filter) {
            FacilityFilter.ALL -> facilities
            FacilityFilter.EMERGENCY_24_7 -> facilities.filter { it.type == FacilityType.HOSPITAL }
            FacilityFilter.URGENT_CARE -> facilities.filter { it.type == FacilityType.URGENT_CARE }
            FacilityFilter.GP_CLINICS -> facilities.filter { it.type == FacilityType.GP_CLINIC }
        }
    }

    fun triggerSOS(trigger: SOSTrigger = SOSTrigger.MANUAL) {
        viewModelScope.launch {
            val medId = _uiState.value.medicalId
            val summary = "NHI: ${medId.nhiNumber} | ${medId.fullName} (DOB: ${medId.dateOfBirth}) | Allergies: ${medId.knownAllergies} | Conditions: ${medId.chronicConditions}"

            val event = SOSEvent(
                triggeredBy = trigger,
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude,
                locationAddress = _uiState.value.locationAddress,
                medicalIdSummary = summary,
                timestamp = System.currentTimeMillis(),
                emergencyPhoneDialed = "111",
                status = "DISPATCHED_TO_111_AND_CAREGIVERS",
                syncStatus = SyncStatus.PENDING
            )

            container.emergencyRepository.recordSOSEvent(event)
            container.triageRepository.logTriage(
                TriageResult(
                    sourceType = "SOS_DISPATCH",
                    isRedFlag = true,
                    ruleTriggered = "MANUAL_SOS_HELD",
                    advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                    modelConfidence = 1.0,
                    summary = "Emergency 111 SOS Dispatched at ${_uiState.value.locationAddress}"
                )
            )

            Log.d("EmergencyViewModel", "Emergency 111 SOS Triggered")

            _uiState.value = _uiState.value.copy(
                isSOSDispatched = true,
                lastSOSEvent = event
            )

            // Safely open phone dialer with 111 pre-filled
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:111")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                getApplication<Application>().startActivity(dialIntent)
            } catch (e: Exception) {
                // Dial intent handled gracefully
            }
        }
    }

    fun dismissSOSDialog() {
        _uiState.value = _uiState.value.copy(isSOSDispatched = false)
    }

    fun callFacility(phone: String) {
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${phone.replace(" ", "")}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(dialIntent)
        } catch (e: Exception) {
            // Handled
        }
    }

    fun openMapNavigation(facility: CareFacility) {
        try {
            val uri = Uri.parse("geo:${facility.latitude},${facility.longitude}?q=${Uri.encode(facility.name + ", " + facility.address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(mapIntent)
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${facility.latitude},${facility.longitude}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                getApplication<Application>().startActivity(webIntent)
            } catch (e2: Exception) {
                // Ignored
            }
        }
    }
}

// ---------------- Appointment Booking ViewModel ----------------

data class AppointmentUiState(
    val selectedFacility: CareFacility? = null,
    val selectedReason: String = "General check-up",
    val availableReasons: List<String> = listOf(
        "General check-up",
        "Follow-up",
        "Medication review",
        "Mobility & pain check",
        "Other"
    ),
    val selectedDate: String = "Tomorrow",
    val availableDates: List<String> = listOf(
        "Today",
        "Tomorrow",
        "In 2 days",
        "Next available (Mon)"
    ),
    val selectedTimeSlot: String = "10:00 AM",
    val availableTimeSlots: List<String> = listOf(
        "9:00 AM",
        "10:00 AM",
        "11:30 AM",
        "2:00 PM",
        "3:30 PM",
        "4:30 PM"
    ),
    val patientName: String = "Margaret Te Aroha",
    val patientContact: String = "+64 21 555 0192",
    val isSubmitting: Boolean = false,
    val isBookingModalOpen: Boolean = false,
    val isConfirmationOpen: Boolean = false,
    val confirmedAppointment: AppointmentRequest? = null,
    val errorMessage: String? = null
)

class AppointmentViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AppointmentUiState())
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    fun openBooking(facility: CareFacility) {
        _uiState.value = _uiState.value.copy(
            selectedFacility = facility,
            selectedReason = "General check-up",
            selectedDate = "Tomorrow",
            selectedTimeSlot = "10:00 AM",
            isBookingModalOpen = true,
            isConfirmationOpen = false,
            errorMessage = null
        )
    }

    fun closeBooking() {
        _uiState.value = _uiState.value.copy(
            isBookingModalOpen = false,
            errorMessage = null
        )
    }

    fun selectReason(reason: String) {
        _uiState.value = _uiState.value.copy(selectedReason = reason)
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun selectTimeSlot(time: String) {
        _uiState.value = _uiState.value.copy(selectedTimeSlot = time)
    }

    fun submitAppointmentRequest(): kotlinx.coroutines.Job = viewModelScope.launch {
        val facility = _uiState.value.selectedFacility ?: return@launch
        val reason = _uiState.value.selectedReason
        val dateTime = "${_uiState.value.selectedDate} • ${_uiState.value.selectedTimeSlot}"
        val patientName = _uiState.value.patientName
        val patientContact = _uiState.value.patientContact

        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
        val result = container.requestAppointmentUseCase(
            facilityId = facility.id,
            facilityName = facility.name,
            reasonCategory = reason,
            requestedDateTime = dateTime,
            patientName = patientName,
            patientContact = patientContact
        )

        result.onSuccess { request ->
            Log.d("AppointmentViewModel", "Appointment requested for ${facility.name}")
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                isBookingModalOpen = false,
                isConfirmationOpen = true,
                confirmedAppointment = request
            )
        }.onFailure { ex ->
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                errorMessage = ex.message ?: "Failed to submit appointment request."
            )
        }
    }

    fun dismissConfirmation() {
        _uiState.value = _uiState.value.copy(
            isConfirmationOpen = false,
            confirmedAppointment = null
        )
    }

    fun callClinic(phone: String) {
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${phone.replace(" ", "")}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(dialIntent)
        } catch (e: Exception) {
            // Handled
        }
    }
}

// ---------------- Caregiver Sync & Account ViewModel ----------------

data class CaregiverContact(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val isPrimary: Boolean = false,
    val receivesRedFlags: Boolean = true
)

data class PairedBleDevice(
    val id: String,
    val name: String,
    val type: String,
    val isConnected: Boolean,
    val lastSyncTime: String
)

data class CaregiverSyncUiState(
    val pendingRecordsCount: Int = 3,
    val lastSyncedTimestamp: Long = System.currentTimeMillis() - (15 * 60 * 1000), // 15 mins ago
    val isSyncing: Boolean = false,
    val syncSuccessBanner: String? = null,
    val caregiverName: String = "David Miller (General Practitioner)",
    val caregiverContact: String = "+64 21 555 0192",
    val appointmentRequests: List<AppointmentRequest> = emptyList(),
    val caregivers: List<CaregiverContact> = listOf(
        CaregiverContact(
            id = "1",
            name = "David Miller",
            relationship = "General Practitioner",
            phoneNumber = "+64 21 555 0192",
            isPrimary = true,
            receivesRedFlags = true
        ),
        CaregiverContact(
            id = "2",
            name = "Sarah Miller",
            relationship = "General Practitioner",
            phoneNumber = "+64 21 555 0831",
            isPrimary = false,
            receivesRedFlags = true
        ),
        CaregiverContact(
            id = "3",
            name = "Dr. Hemi Taylor",
            relationship = "General Practitioner",
            phoneNumber = "+64 9 307 4949",
            isPrimary = false,
            receivesRedFlags = false
        )
    ),
    // Caregiver / Admin Mode state
    val isCaregiverModeActive: Boolean = false,
    val activeCaregiver: CaregiverContact? = null,
    val isCaregiverModeSwitchDialogOpen: Boolean = false,
    val selectedCaregiverForSwitch: CaregiverContact? = null,
    val isAssignTaskDialogOpen: Boolean = false,
    val assignedTasks: List<CaregiverTask> = emptyList(),
    val seniorMedicationAdherence: Int = 94,
    val seniorLatestVital: VitalsRecord? = null,
    val seniorTodaySteps: Int = 3480,
    val seniorBaselineSteps: Int = 3870,
    val seniorLowSupplyMeds: List<MedicationSchedule> = emptyList(),
    val seniorActiveAlerts: List<TriageResult> = emptyList(),

    val pairedDevices: List<PairedBleDevice> = listOf(
        PairedBleDevice("1", "Omron Complete BP & ECG", "Blood Pressure", isConnected = true, lastSyncTime = "Today, 08:30 AM"),
        PairedBleDevice("2", "Beurer PO80 Pulse Oximeter", "SpO2 & Pulse", isConnected = true, lastSyncTime = "Today, 08:15 AM"),
        PairedBleDevice("3", "Accu-Chek Guide Me", "Blood Glucose", isConnected = false, lastSyncTime = "Yesterday, 06:45 PM")
    ),
    val isScanningBle: Boolean = false,
    val bleScanStatusMessage: String? = null,
    val textSizePreference: String = "Large", // "Standard", "Large", "Extra Large"
    val morningReminderTime: String = "08:00 AM",
    val middayReminderTime: String = "12:30 PM",
    val eveningReminderTime: String = "06:00 PM",
    val nightReminderTime: String = "09:00 PM",
    val isBiometricLoginEnabled: Boolean = true,
    val generatedReportText: String? = null,
    val isExportDialogOpen: Boolean = false,
    val isAddCaregiverDialogOpen: Boolean = false,
    val isEditProfileDialogOpen: Boolean = false,
    val isEditRemindersDialogOpen: Boolean = false,
    val isHealthReportModalOpen: Boolean = false,
    val isGeneratingPdf: Boolean = false,
    val generatedPdfFile: File? = null,
    val pdfGenerationError: String? = null,
    val selectedReportPeriod: String = "Past 30 Days",
    val userName: String = "Jaseline Miller",
    val userEmail: String = "jaseline@manaaki.health.nz",
    val userPhoneNumber: String = "+64 21 555 8392",
    val userAddress: String = "42 Karangahape Road, Auckland 1010",
    val userNhiNumber: String = "ABC9876",
    val emergencyNotes: String = "Penicillin allergy. Pacemaker fitted in 2021."
)

class CaregiverSyncViewModel(application: Application, private val container: ManaakiAppContainer) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CaregiverSyncUiState())
    val uiState: StateFlow<CaregiverSyncUiState> = _uiState.asStateFlow()

    init {
        // Load initial state if stored
        val session = container.secureStore.getSession()
        if (session != null) {
            _uiState.value = _uiState.value.copy(
                userName = session.fullName,
                userEmail = session.email,
                userPhoneNumber = session.phoneNumber,
                isBiometricLoginEnabled = session.isBiometricEnabled
            )
        }

        observeAppointments()
        observeCaregiverTasks()
        observeSeniorSnapshot()
    }

    private fun observeAppointments() {
        viewModelScope.launch {
            container.appointmentRepository.getAllAppointments().collect { list ->
                val pending = list.count { it.syncStatus == SyncStatus.PENDING }
                _uiState.value = _uiState.value.copy(
                    appointmentRequests = list,
                    pendingRecordsCount = pending
                )
            }
        }
    }
    private fun observeCaregiverTasks() {
        viewModelScope.launch {
            container.caregiverTaskRepository.getAllTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(assignedTasks = tasks)
            }
        }
    }

    private fun observeSeniorSnapshot() {
        viewModelScope.launch {
            combine(
                container.medicationRepository.getAllSchedules(),
                container.vitalsRepository.getLatestVital(),
                container.activityRepository.getAllWindows(),
                container.triageRepository.getHistory()
            ) { schedules, latestVital, _, triageHistory ->
                val adherence = container.medicationRepository.calculate7DayAdherence()
                val todaySteps = container.activityRepository.getTodaySteps()
                val baseline = container.activityRepository.get7DayRollingAverageSteps()
                val lowSupply = schedules.filter { it.isLowSupply }
                val redFlags = triageHistory.filter { it.isRedFlag }

                _uiState.value = _uiState.value.copy(
                    seniorMedicationAdherence = adherence,
                    seniorLatestVital = latestVital,
                    seniorTodaySteps = todaySteps,
                    seniorBaselineSteps = baseline,
                    seniorLowSupplyMeds = lowSupply,
                    seniorActiveAlerts = redFlags
                )
            }.collect {}
        }
    }

    fun openSwitchToCaregiverDialog(caregiver: CaregiverContact) {
        _uiState.value = _uiState.value.copy(
            isCaregiverModeSwitchDialogOpen = true,
            selectedCaregiverForSwitch = caregiver
        )
    }

    fun closeSwitchToCaregiverDialog() {
        _uiState.value = _uiState.value.copy(
            isCaregiverModeSwitchDialogOpen = false,
            selectedCaregiverForSwitch = null
        )
    }

    fun enterCaregiverMode(caregiver: CaregiverContact) {
        _uiState.value = _uiState.value.copy(
            isCaregiverModeActive = true,
            activeCaregiver = caregiver,
            isCaregiverModeSwitchDialogOpen = false,
            selectedCaregiverForSwitch = null,
            syncSuccessBanner = "Switched to Caregiver Mode: Viewing ${_uiState.value.userName} (NHI: ${_uiState.value.userNhiNumber})"
        )
        Log.d("CaregiverSyncViewModel", "Caregiver sync alert for ${caregiver.name}")
    }

    fun exitCaregiverMode() {
        _uiState.value = _uiState.value.copy(
            isCaregiverModeActive = false,
            activeCaregiver = null,
            syncSuccessBanner = "Returned to Senior Patient View."
        )
    }

    fun openAssignTaskDialog() {
        _uiState.value = _uiState.value.copy(isAssignTaskDialogOpen = true)
    }

    fun closeAssignTaskDialog() {
        _uiState.value = _uiState.value.copy(isAssignTaskDialogOpen = false)
    }

    fun assignTask(type: TaskType, title: String, details: String, dueTime: String) {
        viewModelScope.launch {
            val caregiver = _uiState.value.activeCaregiver ?: _uiState.value.caregivers.first { it.isPrimary }
            val task = CaregiverTask(
                caregiverName = caregiver.name,
                type = type,
                title = title.trim(),
                details = details.trim(),
                dueTime = dueTime.trim(),
                isCompleted = false,
                syncStatus = SyncStatus.SYNCED
            )

            container.caregiverTaskRepository.addTask(task)

            Log.d("CaregiverSyncViewModel", "Assigned task '$title' to ${caregiver.name}")

            _uiState.value = _uiState.value.copy(
                isAssignTaskDialogOpen = false,
                syncSuccessBanner = "Assigned task '$title' to ${_uiState.value.userName}. Reminder dispatched."
            )
        }
    }

    fun toggleTaskCompleted(task: CaregiverTask) {
        viewModelScope.launch {
            container.caregiverTaskRepository.setTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            container.caregiverTaskRepository.deleteTask(taskId)
        }
    }

    fun triggerSyncNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, syncSuccessBanner = null)
            container.appointmentRepository.syncPendingAppointments()
            delay(1200) // Simulated secure TLS 1.3 sync to caregiver backend & GP Cloud
            Log.d("CaregiverSyncViewModel", "Synced caregiver records")
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                pendingRecordsCount = 0,
                lastSyncedTimestamp = System.currentTimeMillis(),
                syncSuccessBanner = "Synced all health records & appointment requests with Caregiver Portal & GP Cloud."
            )
        }
    }

    fun clearSyncBanner() {
        _uiState.value = _uiState.value.copy(syncSuccessBanner = null)
    }

    fun addCaregiver(name: String, relationship: String, phoneNumber: String, isPrimary: Boolean, receivesRedFlags: Boolean) {
        val newCaregiver = CaregiverContact(
            name = name.trim(),
            relationship = relationship.trim(),
            phoneNumber = phoneNumber.trim(),
            isPrimary = isPrimary,
            receivesRedFlags = receivesRedFlags
        )
        val updated = if (isPrimary) {
            _uiState.value.caregivers.map { it.copy(isPrimary = false) } + newCaregiver
        } else {
            _uiState.value.caregivers + newCaregiver
        }
        _uiState.value = _uiState.value.copy(
            caregivers = updated,
            isAddCaregiverDialogOpen = false,
            syncSuccessBanner = "Added $name as caregiver."
        )
    }

    fun removeCaregiver(id: String) {
        val caregiver = _uiState.value.caregivers.find { it.id == id }
        _uiState.value = _uiState.value.copy(
            caregivers = _uiState.value.caregivers.filter { it.id != id },
            syncSuccessBanner = caregiver?.let { "Removed ${it.name} from caregivers." }
        )
    }

    fun setPrimaryCaregiver(id: String) {
        _uiState.value = _uiState.value.copy(
            caregivers = _uiState.value.caregivers.map {
                it.copy(isPrimary = it.id == id)
            }
        )
    }

    fun openAddCaregiverDialog() {
        _uiState.value = _uiState.value.copy(isAddCaregiverDialogOpen = true)
    }

    fun closeAddCaregiverDialog() {
        _uiState.value = _uiState.value.copy(isAddCaregiverDialogOpen = false)
    }

    fun openEditProfileDialog() {
        _uiState.value = _uiState.value.copy(isEditProfileDialogOpen = true)
    }

    fun closeEditProfileDialog() {
        _uiState.value = _uiState.value.copy(isEditProfileDialogOpen = false)
    }

    fun saveProfile(phone: String, address: String, notes: String) {
        _uiState.value = _uiState.value.copy(
            userPhoneNumber = phone.trim(),
            userAddress = address.trim(),
            emergencyNotes = notes.trim(),
            isEditProfileDialogOpen = false,
            syncSuccessBanner = "Profile details updated successfully."
        )
    }

    fun openEditRemindersDialog() {
        _uiState.value = _uiState.value.copy(isEditRemindersDialogOpen = true)
    }

    fun closeEditRemindersDialog() {
        _uiState.value = _uiState.value.copy(isEditRemindersDialogOpen = false)
    }

    fun saveReminderTimes(morning: String, midday: String, evening: String, night: String) {
        _uiState.value = _uiState.value.copy(
            morningReminderTime = morning,
            middayReminderTime = midday,
            eveningReminderTime = evening,
            nightReminderTime = night,
            isEditRemindersDialogOpen = false,
            syncSuccessBanner = "Medication reminder schedule updated."
        )
    }

    fun setTextSizePreference(size: String) {
        _uiState.value = _uiState.value.copy(textSizePreference = size)
    }

    fun toggleBiometricLogin(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricLoginEnabled = enabled)
        container.secureStore.setBiometricEnabled(enabled)
    }

    fun scanBleDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanningBle = true, bleScanStatusMessage = "Scanning nearby Bluetooth medical peripherals…")
            delay(2000)
            _uiState.value = _uiState.value.copy(
                isScanningBle = false,
                bleScanStatusMessage = "Found 3 devices. Omron BP and Beurer SpO2 connected."
            )
        }
    }

    fun openHealthReportModal() {
        _uiState.value = _uiState.value.copy(
            isHealthReportModalOpen = true,
            pdfGenerationError = null
        )
    }

    fun closeHealthReportModal() {
        _uiState.value = _uiState.value.copy(
            isHealthReportModalOpen = false,
            isGeneratingPdf = false,
            pdfGenerationError = null
        )
    }

    fun setReportPeriod(period: String) {
        _uiState.value = _uiState.value.copy(selectedReportPeriod = period)
    }

    fun generateMonthlyPdfReport(selectedPeriod: String = _uiState.value.selectedReportPeriod) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingPdf = true,
                pdfGenerationError = null,
                selectedReportPeriod = selectedPeriod
            )
            try {
                val pdfFile = File(getApplication<Application>().cacheDir, "health_report_summary.pdf")
                if (!pdfFile.exists()) {
                    pdfFile.createNewFile()
                }

                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    generatedPdfFile = pdfFile,
                    isExportDialogOpen = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    pdfGenerationError = e.message ?: "Failed to generate PDF report."
                )
            }
        }
    }

    fun viewGeneratedPdf(context: Context) {
        Toast.makeText(context, "Viewing PDF Health Report placeholder", Toast.LENGTH_SHORT).show()
    }

    fun shareGeneratedPdf(context: Context) {
        Toast.makeText(context, "Sharing PDF Health Report placeholder", Toast.LENGTH_SHORT).show()
    }

    fun generateHealthSummaryReport() {
        generateMonthlyPdfReport(_uiState.value.selectedReportPeriod)
    }

    fun closeExportDialog() {
        _uiState.value = _uiState.value.copy(isExportDialogOpen = false)
    }

    fun shareReport(context: Context, report: String) {
        val file = _uiState.value.generatedPdfFile
        if (file != null) {
            shareGeneratedPdf(context)
        } else {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Manaaki Health Summary — ${_uiState.value.userName} (NHI ${_uiState.value.userNhiNumber})")
                putExtra(Intent.EXTRA_TEXT, report)
            }
            context.startActivity(Intent.createChooser(intent, "Share Health Summary for GP"))
        }
    }
}
