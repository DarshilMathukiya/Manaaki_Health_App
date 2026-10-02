package com.example.data.repository

import com.example.data.local.ActivityDao
import com.example.data.local.ActivityWindowEntity
import com.example.data.local.AppointmentDao
import com.example.data.local.AppointmentRequestEntity
import com.example.data.local.CaregiverTaskDao
import com.example.data.local.CaregiverTaskEntity
import com.example.data.local.MedicationDao
import com.example.data.local.MedicationLogEntity
import com.example.data.local.MedicationScheduleEntity
import com.example.data.local.SOSDao
import com.example.data.local.SOSEventEntity
import com.example.data.local.TriageDao
import com.example.data.local.TriageResultEntity
import com.example.data.local.VitalsDao
import com.example.data.local.VitalsRecordEntity
import com.example.domain.model.ActivityWindow
import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.AppointmentRequest
import com.example.domain.model.CareFacility
import com.example.domain.model.CaregiverTask
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
import com.example.domain.model.TaskType
import com.example.domain.model.TriageResult
import com.example.domain.model.VitalsRecord
import com.example.domain.model.VitalsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ---------------- Medication Repository ----------------

class MedicationRepository(private val dao: MedicationDao) {

    fun getAllSchedules(): Flow<List<MedicationSchedule>> = dao.getAllSchedules().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun getScheduleById(id: Long): MedicationSchedule? = dao.getScheduleById(id)?.toDomain()

    suspend fun addSchedule(schedule: MedicationSchedule): Long = dao.insertSchedule(schedule.toEntity())

    suspend fun updateSchedule(schedule: MedicationSchedule) = dao.updateSchedule(schedule.toEntity())

    suspend fun deleteSchedule(id: Long) = dao.deleteSchedule(id)

    suspend fun decrementSupply(id: Long) = dao.decrementSupply(id)

    suspend fun refillSupply(id: Long, count: Int) = dao.updateSupply(id, count)

    // Logs
    fun getLogsForDay(epochDay: Long): Flow<List<MedicationLog>> = dao.getLogsForDay(epochDay).map { list ->
        list.map { it.toDomain() }
    }

    fun getRecentLogs(): Flow<List<MedicationLog>> = dao.getRecentLogs().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun recordDoseTaken(scheduleId: Long, medicationName: String, scheduledTime: String, epochDay: Long) {
        val existing = dao.getLogForDose(scheduleId, epochDay, scheduledTime)
        if (existing != null) {
            dao.updateLog(existing.copy(status = DoseStatus.TAKEN.name, actualTime = System.currentTimeMillis()))
        } else {
            dao.insertLog(
                MedicationLogEntity(
                    scheduleId = scheduleId,
                    medicationName = medicationName,
                    scheduledTime = scheduledTime,
                    dateEpochDay = epochDay,
                    actualTime = System.currentTimeMillis(),
                    status = DoseStatus.TAKEN.name,
                    snoozeCount = 0,
                    syncStatus = SyncStatus.PENDING.name
                )
            )
        }
        dao.decrementSupply(scheduleId)
    }

    suspend fun recordDoseSnooze(scheduleId: Long, medicationName: String, scheduledTime: String, epochDay: Long) {
        val existing = dao.getLogForDose(scheduleId, epochDay, scheduledTime)
        if (existing != null) {
            dao.updateLog(
                existing.copy(
                    status = DoseStatus.SNOOZED.name,
                    actualTime = System.currentTimeMillis(),
                    snoozeCount = existing.snoozeCount + 1
                )
            )
        } else {
            dao.insertLog(
                MedicationLogEntity(
                    scheduleId = scheduleId,
                    medicationName = medicationName,
                    scheduledTime = scheduledTime,
                    dateEpochDay = epochDay,
                    actualTime = System.currentTimeMillis(),
                    status = DoseStatus.SNOOZED.name,
                    snoozeCount = 1,
                    syncStatus = SyncStatus.PENDING.name
                )
            )
        }
    }

    suspend fun recordDoseMissed(scheduleId: Long, medicationName: String, scheduledTime: String, epochDay: Long) {
        val existing = dao.getLogForDose(scheduleId, epochDay, scheduledTime)
        if (existing != null) {
            dao.updateLog(existing.copy(status = DoseStatus.MISSED.name))
        } else {
            dao.insertLog(
                MedicationLogEntity(
                    scheduleId = scheduleId,
                    medicationName = medicationName,
                    scheduledTime = scheduledTime,
                    dateEpochDay = epochDay,
                    actualTime = null,
                    status = DoseStatus.MISSED.name,
                    snoozeCount = 0,
                    syncStatus = SyncStatus.PENDING.name
                )
            )
        }
    }

    suspend fun calculate7DayAdherence(): Int {
        val startDay = LocalDate.now().minusDays(7).toEpochDay()
        val taken = dao.countTakenSince(startDay)
        val total = dao.countTotalCompletedOrMissedSince(startDay)
        return if (total == 0) 100 else ((taken.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
    }

    suspend fun calculateMonthlyAdherence(startEpochDay: Long, endEpochDay: Long): Int {
        val taken = dao.countTakenBetween(startEpochDay, endEpochDay)
        val total = dao.countTotalBetween(startEpochDay, endEpochDay)
        return if (total == 0) 100 else ((taken.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
    }

    suspend fun getLogsBetween(startEpochDay: Long, endEpochDay: Long): List<MedicationLog> {
        return dao.getLogsBetween(startEpochDay, endEpochDay).map { it.toDomain() }
    }

    private fun MedicationScheduleEntity.toDomain() = MedicationSchedule(
        id = id,
        name = name,
        iconType = runCatching { PillIconType.valueOf(iconType) }.getOrDefault(PillIconType.ROUND_WHITE),
        dosage = dosage,
        timesPerDay = timesPerDay,
        reminderTimes = if (reminderTimesJson.isBlank()) emptyList() else reminderTimesJson.split(","),
        startDate = startDate,
        endDate = endDate,
        remainingSupply = remainingSupply,
        lowSupplyThreshold = lowSupplyThreshold,
        isCritical = isCritical,
        instructions = instructions,
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.PENDING)
    )

    private fun MedicationSchedule.toEntity() = MedicationScheduleEntity(
        id = id,
        name = name,
        iconType = iconType.name,
        dosage = dosage,
        timesPerDay = timesPerDay,
        reminderTimesJson = reminderTimes.joinToString(","),
        startDate = startDate,
        endDate = endDate,
        remainingSupply = remainingSupply,
        lowSupplyThreshold = lowSupplyThreshold,
        isCritical = isCritical,
        instructions = instructions,
        syncStatus = syncStatus.name
    )

    private fun MedicationLogEntity.toDomain() = MedicationLog(
        id = id,
        scheduleId = scheduleId,
        medicationName = medicationName,
        scheduledTime = scheduledTime,
        dateEpochDay = dateEpochDay,
        actualTime = actualTime,
        status = runCatching { DoseStatus.valueOf(status) }.getOrDefault(DoseStatus.SCHEDULED),
        snoozeCount = snoozeCount,
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.PENDING)
    )
}

// ---------------- Vitals Repository ----------------

class VitalsRepository(private val dao: VitalsDao) {

    fun getAllVitals(): Flow<List<VitalsRecord>> = dao.getAllVitals().map { list ->
        list.map { it.toDomain() }
    }

    fun getVitalsByType(type: VitalsType, limit: Int = 20): Flow<List<VitalsRecord>> =
        dao.getVitalsByType(type.name, limit).map { list -> list.map { it.toDomain() } }

    fun getLatestVital(): Flow<VitalsRecord?> = dao.getLatestVital().map { it?.toDomain() }

    suspend fun getVitalsBetween(startTime: Long, endTime: Long): List<VitalsRecord> =
        dao.getVitalsBetween(startTime, endTime).map { it.toDomain() }

    suspend fun recordVitals(vitals: VitalsRecord): Long = dao.insertVital(vitals.toEntity())

    private fun VitalsRecordEntity.toDomain() = VitalsRecord(
        id = id,
        type = runCatching { VitalsType.valueOf(type) }.getOrDefault(VitalsType.BLOOD_PRESSURE),
        systolic = systolic,
        diastolic = diastolic,
        value = value,
        unit = unit,
        recordedAt = recordedAt,
        notes = notes,
        isRedFlag = isRedFlag,
        severity = runCatching { AdvisorySeverity.valueOf(severity) }.getOrDefault(AdvisorySeverity.NORMAL),
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.PENDING)
    )

    private fun VitalsRecord.toEntity() = VitalsRecordEntity(
        id = id,
        type = type.name,
        systolic = systolic,
        diastolic = diastolic,
        value = value,
        unit = unit,
        recordedAt = recordedAt,
        notes = notes,
        isRedFlag = isRedFlag,
        severity = severity.name,
        syncStatus = syncStatus.name
    )
}

// ---------------- Activity Repository ----------------

class ActivityRepository(private val dao: ActivityDao) {

    fun getAllWindows(): Flow<List<ActivityWindow>> = dao.getAllWindows().map { list -> list.map { it.toDomain() } }

    suspend fun recordWindow(window: ActivityWindow): Long = dao.insertWindow(window.toEntity())

    suspend fun getTodaySteps(): Int {
        val startOfToday = LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getStepCountSince(startOfToday) ?: 0
    }

    suspend fun get7DayRollingAverageSteps(): Int {
        val sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val totalSteps = dao.getStepCountSince(sevenDaysAgo) ?: 0
        return (totalSteps / 7).coerceAtLeast(3000) // Realistic senior baseline fallback
    }

    private fun ActivityWindowEntity.toDomain() = ActivityWindow(
        id = id,
        windowStart = windowStart,
        windowEnd = windowEnd,
        stepCount = stepCount,
        gaitCadence = gaitCadence,
        gaitSpeedMps = gaitSpeedMps,
        sedentaryMinutes = sedentaryMinutes,
        sleepWakeState = runCatching { SleepWakeState.valueOf(sleepWakeState) }.getOrDefault(SleepWakeState.AWAKE),
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.PENDING)
    )

    private fun ActivityWindow.toEntity() = ActivityWindowEntity(
        id = id,
        windowStart = windowStart,
        windowEnd = windowEnd,
        stepCount = stepCount,
        gaitCadence = gaitCadence,
        gaitSpeedMps = gaitSpeedMps,
        sedentaryMinutes = sedentaryMinutes,
        sleepWakeState = sleepWakeState.name,
        syncStatus = syncStatus.name
    )
}

// ---------------- Triage & SOS Repository ----------------

class TriageRepository(private val dao: TriageDao) {
    fun getHistory(): Flow<List<TriageResult>> = dao.getTriageHistory().map { list -> list.map { it.toDomain() } }

    suspend fun logTriage(result: TriageResult): Long = dao.insertTriageResult(result.toEntity())

    private fun TriageResultEntity.toDomain() = TriageResult(
        id = id,
        sourceType = sourceType,
        isRedFlag = isRedFlag,
        ruleTriggered = ruleTriggered,
        advisorySeverity = runCatching { AdvisorySeverity.valueOf(advisorySeverity) }.getOrDefault(AdvisorySeverity.NORMAL),
        modelConfidence = modelConfidence,
        summary = summary,
        createdAt = createdAt
    )

    private fun TriageResult.toEntity() = TriageResultEntity(
        id = id,
        sourceType = sourceType,
        isRedFlag = isRedFlag,
        ruleTriggered = ruleTriggered,
        advisorySeverity = advisorySeverity.name,
        modelConfidence = modelConfidence,
        summary = summary,
        createdAt = createdAt
    )
}

class EmergencyRepository(private val dao: SOSDao) {
    fun getAllEvents(): Flow<List<SOSEvent>> = dao.getAllSOSEvents().map { list -> list.map { it.toDomain() } }

    suspend fun recordSOSEvent(event: SOSEvent): Long = dao.insertSOSEvent(event.toEntity())

    private fun SOSEventEntity.toDomain() = SOSEvent(
        id = id,
        triggeredBy = runCatching { SOSTrigger.valueOf(triggeredBy) }.getOrDefault(SOSTrigger.MANUAL),
        latitude = latitude,
        longitude = longitude,
        locationAddress = locationAddress,
        medicalIdSummary = medicalIdSummary,
        timestamp = timestamp,
        emergencyPhoneDialed = emergencyPhoneDialed,
        status = status,
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.PENDING)
    )

    private fun SOSEvent.toEntity() = SOSEventEntity(
        id = id,
        triggeredBy = triggeredBy.name,
        latitude = latitude,
        longitude = longitude,
        locationAddress = locationAddress,
        medicalIdSummary = medicalIdSummary,
        timestamp = timestamp,
        emergencyPhoneDialed = emergencyPhoneDialed,
        status = status,
        syncStatus = syncStatus.name
    )
}

// ---------------- Appointment Repository ----------------

class AppointmentRepository(private val dao: AppointmentDao) {
    fun getAllAppointments(): Flow<List<AppointmentRequest>> = dao.getAllAppointments().map { list ->
        list.map { it.toDomain() }
    }

    fun getPendingAppointments(): Flow<List<AppointmentRequest>> = dao.getPendingAppointments().map { list ->
        list.map { it.toDomain() }
    }

    fun getPendingCount(): Flow<Int> = dao.getPendingCount()

    suspend fun createAppointmentRequest(request: AppointmentRequest): Long {
        return dao.insertAppointment(request.toEntity())
    }

    suspend fun updateAppointmentStatus(id: Long, status: String, syncStatus: SyncStatus = SyncStatus.PENDING) {
        dao.updateStatus(id, status, syncStatus.name)
    }

    suspend fun syncPendingAppointments() {
        dao.markAllSynced()
    }

    suspend fun deleteAppointment(id: Long) {
        dao.deleteAppointment(id)
    }

    private fun AppointmentRequestEntity.toDomain() = AppointmentRequest(
        id = id,
        facilityId = facilityId,
        facilityName = facilityName,
        reasonCategory = reasonCategory,
        requestedDateTime = requestedDateTime,
        patientName = patientName,
        patientContact = patientContact,
        status = status,
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.PENDING),
        createdAt = createdAt
    )

    private fun AppointmentRequest.toEntity() = AppointmentRequestEntity(
        id = id,
        facilityId = facilityId,
        facilityName = facilityName,
        reasonCategory = reasonCategory,
        requestedDateTime = requestedDateTime,
        patientName = patientName,
        patientContact = patientContact,
        status = status,
        syncStatus = syncStatus.name,
        createdAt = createdAt
    )
}

// ---------------- NZ Care Facility Dataset & Repository ----------------

class CareFacilityRepository {

    // Cached New Zealand Health Facilities (Auckland & Regional NZ)
    private val cachedFacilities = listOf(
        CareFacility(
            id = "nz_fac_gp_1",
            name = "Ponsonby Medical Centre (Your Registered GP)",
            type = FacilityType.GP_CLINIC,
            address = "182 Ponsonby Road, Ponsonby, Auckland 1011",
            phone = "+64 9 376 4467",
            latitude = -36.8540,
            longitude = 174.7460,
            openingHours = "Mon–Fri 8:00 AM – 5:30 PM",
            isAfterHoursEmergency = false,
            isRegisteredGP = true
        ),
        CareFacility(
            id = "nz_fac_1",
            name = "Auckland City Hospital (Emergency Dept)",
            type = FacilityType.HOSPITAL,
            address = "2 Park Road, Grafton, Auckland 1023",
            phone = "+64 9 367 0000",
            latitude = -36.8606,
            longitude = 174.7708,
            openingHours = "Open 24/7 (Emergency & Trauma)",
            isAfterHoursEmergency = true,
            isRegisteredGP = false
        ),
        CareFacility(
            id = "nz_fac_2",
            name = "White Cross Urgent Care — St Lukes",
            type = FacilityType.URGENT_CARE,
            address = "52 St Lukes Road, Mount Albert, Auckland 1025",
            phone = "+64 9 815 3111",
            latitude = -36.8856,
            longitude = 174.7335,
            openingHours = "Open 8:00 AM – 10:00 PM Daily",
            isAfterHoursEmergency = true,
            isRegisteredGP = false
        ),
        CareFacility(
            id = "nz_fac_3",
            name = "Middlemore Hospital (Emergency Dept)",
            type = FacilityType.HOSPITAL,
            address = "100 Hospital Road, Otahuhu, Auckland 2025",
            phone = "+64 9 276 0000",
            latitude = -36.9634,
            longitude = 174.8466,
            openingHours = "Open 24/7 (Emergency Services)",
            isAfterHoursEmergency = true,
            isRegisteredGP = false
        ),
        CareFacility(
            id = "nz_fac_4",
            name = "Greenlane Clinical Centre & After Hours",
            type = FacilityType.URGENT_CARE,
            address = "214 Green Lane West, Epsom, Auckland 1051",
            phone = "+64 9 307 4949",
            latitude = -36.8943,
            longitude = 174.7801,
            openingHours = "Open Mon–Sun 8:00 AM – 8:00 PM",
            isAfterHoursEmergency = false,
            isRegisteredGP = false
        ),
        CareFacility(
            id = "nz_fac_5",
            name = "Te Whare Tapa Whā Community Health Clinic",
            type = FacilityType.GP_CLINIC,
            address = "18 Dominion Road, Mount Eden, Auckland 1024",
            phone = "+64 9 630 8421",
            latitude = -36.8722,
            longitude = 174.7540,
            openingHours = "Mon–Fri 8:30 AM – 5:00 PM",
            isAfterHoursEmergency = false,
            isRegisteredGP = false
        ),
        CareFacility(
            id = "nz_fac_6",
            name = "Life Pharmacy 24/7 Urgent Care Dispensary",
            type = FacilityType.PHARMACY,
            address = "Queen Street Medical Plaza, Auckland 1010",
            phone = "+64 9 309 4567",
            latitude = -36.8520,
            longitude = 174.7640,
            openingHours = "Open 24/7 for prescriptions & emergency supply",
            isAfterHoursEmergency = true,
            isRegisteredGP = false
        )
    )

    fun getNearbyFacilities(userLat: Double, userLon: Double): List<CareFacility> {
        return cachedFacilities.map { facility ->
            val dist = calculateHaversineDistanceKm(userLat, userLon, facility.latitude, facility.longitude)
            facility.copy(distanceKm = dist)
        }.sortedBy { it.distanceKm }
    }

    fun getRegisteredGP(userLat: Double = -36.8509, userLon: Double = 174.7645): CareFacility {
        val list = getNearbyFacilities(userLat, userLon)
        return list.firstOrNull { it.isRegisteredGP } ?: list.first { it.type == FacilityType.GP_CLINIC }
    }

    fun getNearestFacility(userLat: Double = -36.8509, userLon: Double = 174.7645): CareFacility {
        return getNearbyFacilities(userLat, userLon).first()
    }

    private fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of earth in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return Math.round(r * c * 10.0) / 10.0
    }
}

// ---------------- Caregiver Task Repository ----------------

class CaregiverTaskRepository(private val dao: CaregiverTaskDao) {

    fun getAllTasks(): Flow<List<CaregiverTask>> = dao.getAllTasks().map { list ->
        list.map { it.toDomain() }
    }

    fun getPendingTasks(): Flow<List<CaregiverTask>> = dao.getPendingTasks().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun addTask(task: CaregiverTask): Long = dao.insertTask(task.toEntity())

    suspend fun setTaskCompleted(id: Long, completed: Boolean) = dao.updateTaskStatus(id, completed)

    suspend fun deleteTask(id: Long) = dao.deleteTask(id)

    private fun CaregiverTaskEntity.toDomain() = CaregiverTask(
        id = id,
        patientId = patientId,
        caregiverName = caregiverName,
        type = runCatching { TaskType.valueOf(type) }.getOrDefault(TaskType.CUSTOM),
        title = title,
        details = details,
        dueTime = dueTime,
        isCompleted = isCompleted,
        createdAt = createdAt,
        syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.SYNCED)
    )

    private fun CaregiverTask.toEntity() = CaregiverTaskEntity(
        id = id,
        patientId = patientId,
        caregiverName = caregiverName,
        type = type.name,
        title = title,
        details = details,
        dueTime = dueTime,
        isCompleted = isCompleted,
        createdAt = createdAt,
        syncStatus = syncStatus.name
    )
}

