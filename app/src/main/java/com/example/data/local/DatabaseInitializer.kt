package com.example.data.local

import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.DoseStatus
import com.example.domain.model.PillIconType
import com.example.domain.model.SleepWakeState
import com.example.domain.model.SyncStatus
import com.example.domain.model.VitalsType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

object DatabaseInitializer {

    suspend fun populateInitialDataIfEmpty(database: ManaakiDatabase) = withContext(Dispatchers.IO) {
        val existingSchedules = database.medicationDao().getAllSchedules().first()
        if (existingSchedules.isNotEmpty()) return@withContext

        val todayEpochDay = LocalDate.now().toEpochDay()
        val now = System.currentTimeMillis()

        // 1. Initial Medication Schedules
        val schedules = listOf(
            MedicationScheduleEntity(
                name = "Metformin",
                iconType = PillIconType.ROUND_WHITE.name,
                dosage = "500 mg",
                timesPerDay = 2,
                reminderTimesJson = "08:00,20:00",
                startDate = now - (30L * 24 * 3600 * 1000),
                endDate = null,
                remainingSupply = 18,
                lowSupplyThreshold = 6,
                isCritical = true,
                instructions = "Take with morning & evening meals to manage blood sugar",
                syncStatus = SyncStatus.SYNCED.name
            ),
            MedicationScheduleEntity(
                name = "Lisinopril",
                iconType = PillIconType.OVAL_PINK.name,
                dosage = "10 mg",
                timesPerDay = 1,
                reminderTimesJson = "08:00",
                startDate = now - (60L * 24 * 3600 * 1000),
                endDate = null,
                remainingSupply = 4, // Intentionally low for demonstrating Low-Supply Alert banner!
                lowSupplyThreshold = 5,
                isCritical = true,
                instructions = "Take every morning with a glass of water for blood pressure",
                syncStatus = SyncStatus.SYNCED.name
            ),
            MedicationScheduleEntity(
                name = "Atorvastatin",
                iconType = PillIconType.TABLET_GREEN.name,
                dosage = "20 mg",
                timesPerDay = 1,
                reminderTimesJson = "20:00",
                startDate = now - (45L * 24 * 3600 * 1000),
                endDate = null,
                remainingSupply = 24,
                lowSupplyThreshold = 5,
                isCritical = false,
                instructions = "Take at bedtime for cholesterol control",
                syncStatus = SyncStatus.SYNCED.name
            ),
            MedicationScheduleEntity(
                name = "Paracetamol (Osteo)",
                iconType = PillIconType.CAPSULE_BLUE_WHITE.name,
                dosage = "665 mg",
                timesPerDay = 2,
                reminderTimesJson = "08:00,18:00",
                startDate = now - (14L * 24 * 3600 * 1000),
                endDate = null,
                remainingSupply = 12,
                lowSupplyThreshold = 6,
                isCritical = false,
                instructions = "Take for joint pain relief as directed",
                syncStatus = SyncStatus.SYNCED.name
            )
        )

        val insertedIds = schedules.map { database.medicationDao().insertSchedule(it) }

        // 2. Initial Medication Logs for Past 30 Days
        for (dayOffset in 0..29) {
            val epochDay = todayEpochDay - dayOffset
            val dayTime = now - (dayOffset * 24L * 3600 * 1000)

            // Morning Metformin
            database.medicationDao().insertLog(
                MedicationLogEntity(
                    scheduleId = insertedIds[0],
                    medicationName = "Metformin",
                    scheduledTime = "08:00",
                    dateEpochDay = epochDay,
                    actualTime = dayTime + (8L * 3600 * 1000) + 15 * 60 * 1000,
                    status = DoseStatus.TAKEN.name,
                    snoozeCount = 0,
                    syncStatus = SyncStatus.SYNCED.name
                )
            )

            // Evening Metformin (95% adherence)
            val eveningStatus = if (dayOffset == 7 || dayOffset == 21) DoseStatus.MISSED else DoseStatus.TAKEN
            database.medicationDao().insertLog(
                MedicationLogEntity(
                    scheduleId = insertedIds[0],
                    medicationName = "Metformin",
                    scheduledTime = "20:00",
                    dateEpochDay = epochDay,
                    actualTime = if (eveningStatus == DoseStatus.TAKEN) dayTime + (20L * 3600 * 1000) else null,
                    status = eveningStatus.name,
                    snoozeCount = if (dayOffset == 14) 1 else 0,
                    syncStatus = SyncStatus.SYNCED.name
                )
            )

            // Morning Lisinopril
            database.medicationDao().insertLog(
                MedicationLogEntity(
                    scheduleId = insertedIds[1],
                    medicationName = "Lisinopril",
                    scheduledTime = "08:00",
                    dateEpochDay = epochDay,
                    actualTime = dayTime + (8L * 3600 * 1000),
                    status = if (dayOffset == 15) DoseStatus.MISSED.name else DoseStatus.TAKEN.name,
                    snoozeCount = 0,
                    syncStatus = SyncStatus.SYNCED.name
                )
            )

            // Bedtime Atorvastatin
            database.medicationDao().insertLog(
                MedicationLogEntity(
                    scheduleId = insertedIds[2],
                    medicationName = "Atorvastatin",
                    scheduledTime = "20:00",
                    dateEpochDay = epochDay,
                    actualTime = dayTime + (20L * 3600 * 1000),
                    status = DoseStatus.TAKEN.name,
                    snoozeCount = 0,
                    syncStatus = SyncStatus.SYNCED.name
                )
            )
        }

        // 3. Initial Vitals History (Monthly Vitals Log)
        val vitalsList = mutableListOf<VitalsRecordEntity>()
        for (dayOffset in 0..29) {
            val dayTime = now - (dayOffset * 24L * 3600 * 1000)
            val bpVariation = (dayOffset % 5) - 2 // -2 to +2 variation
            val sys = 124 + bpVariation * 3
            val dia = 80 + bpVariation * 2

            // Daily Morning BP
            vitalsList.add(
                VitalsRecordEntity(
                    type = VitalsType.BLOOD_PRESSURE.name,
                    systolic = sys,
                    diastolic = dia,
                    value = sys.toDouble(),
                    unit = "mmHg",
                    recordedAt = dayTime - (4L * 3600 * 1000),
                    notes = if (dayOffset == 0) "Morning resting reading" else "Daily routine check",
                    isRedFlag = false,
                    severity = AdvisorySeverity.NORMAL.name,
                    syncStatus = SyncStatus.SYNCED.name
                )
            )

            // Daily Heart Rate
            val hr = 68 + (dayOffset % 7) * 2
            vitalsList.add(
                VitalsRecordEntity(
                    type = VitalsType.HEART_RATE.name,
                    systolic = null,
                    diastolic = null,
                    value = hr.toDouble(),
                    unit = "bpm",
                    recordedAt = dayTime - (4L * 3600 * 1000),
                    notes = "Resting pulse",
                    isRedFlag = false,
                    severity = AdvisorySeverity.NORMAL.name,
                    syncStatus = SyncStatus.SYNCED.name
                )
            )

            // Fasting Blood Glucose (every 2-3 days)
            if (dayOffset % 2 == 0) {
                val glu = 6.0 + ((dayOffset % 4) * 0.2)
                vitalsList.add(
                    VitalsRecordEntity(
                        type = VitalsType.BLOOD_GLUCOSE.name,
                        systolic = null,
                        diastolic = null,
                        value = (Math.round(glu * 10.0) / 10.0),
                        unit = "mmol/L",
                        recordedAt = dayTime - (5L * 3600 * 1000),
                        notes = "Fasting morning glucose",
                        isRedFlag = false,
                        severity = AdvisorySeverity.NORMAL.name,
                        syncStatus = SyncStatus.SYNCED.name
                    )
                )
            }
        }
        vitalsList.forEach { database.vitalsDao().insertVital(it) }

        // 4. Initial Activity Windows (for 7-day baseline)
        val activityList = listOf(
            ActivityWindowEntity(
                windowStart = now - (24L * 3600 * 1000),
                windowEnd = now - (16L * 3600 * 1000),
                stepCount = 3840,
                gaitCadence = 96.0,
                gaitSpeedMps = 0.92,
                sedentaryMinutes = 180,
                sleepWakeState = SleepWakeState.AWAKE.name,
                syncStatus = SyncStatus.SYNCED.name
            ),
            ActivityWindowEntity(
                windowStart = now - (48L * 3600 * 1000),
                windowEnd = now - (40L * 3600 * 1000),
                stepCount = 4120,
                gaitCadence = 98.0,
                gaitSpeedMps = 0.94,
                sedentaryMinutes = 150,
                sleepWakeState = SleepWakeState.AWAKE.name,
                syncStatus = SyncStatus.SYNCED.name
            ),
            ActivityWindowEntity(
                windowStart = now - (72L * 3600 * 1000),
                windowEnd = now - (64L * 3600 * 1000),
                stepCount = 3650,
                gaitCadence = 94.0,
                gaitSpeedMps = 0.90,
                sedentaryMinutes = 210,
                sleepWakeState = SleepWakeState.AWAKE.name,
                syncStatus = SyncStatus.SYNCED.name
            )
        )
        activityList.forEach { database.activityDao().insertWindow(it) }
    }
}
