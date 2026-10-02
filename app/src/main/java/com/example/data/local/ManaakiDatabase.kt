package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.DoseStatus
import com.example.domain.model.PillIconType
import com.example.domain.model.SleepWakeState
import com.example.domain.model.SOSTrigger
import com.example.domain.model.SyncStatus
import com.example.domain.model.VitalsType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// ---------------- Entities ----------------

@Entity(tableName = "medication_schedules")
data class MedicationScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconType: String,
    val dosage: String,
    val timesPerDay: Int,
    val reminderTimesJson: String, // comma-separated e.g. "08:00,20:00"
    val startDate: Long,
    val endDate: Long?,
    val remainingSupply: Int,
    val lowSupplyThreshold: Int,
    val isCritical: Boolean,
    val instructions: String,
    val syncStatus: String
)

@Entity(tableName = "medication_logs")
data class MedicationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleId: Long,
    val medicationName: String,
    val scheduledTime: String,
    val dateEpochDay: Long,
    val actualTime: Long?,
    val status: String,
    val snoozeCount: Int,
    val syncStatus: String
)

@Entity(tableName = "vitals_records")
data class VitalsRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val systolic: Int?,
    val diastolic: Int?,
    val value: Double,
    val unit: String,
    val recordedAt: Long,
    val notes: String,
    val isRedFlag: Boolean,
    val severity: String,
    val syncStatus: String
)

@Entity(tableName = "activity_windows")
data class ActivityWindowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val windowStart: Long,
    val windowEnd: Long,
    val stepCount: Int,
    val gaitCadence: Double,
    val gaitSpeedMps: Double,
    val sedentaryMinutes: Int,
    val sleepWakeState: String,
    val syncStatus: String
)

@Entity(tableName = "triage_results")
data class TriageResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceType: String,
    val isRedFlag: Boolean,
    val ruleTriggered: String,
    val advisorySeverity: String,
    val modelConfidence: Double,
    val summary: String,
    val createdAt: Long
)

@Entity(tableName = "sos_events")
data class SOSEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val triggeredBy: String,
    val latitude: Double,
    val longitude: Double,
    val locationAddress: String,
    val medicalIdSummary: String,
    val timestamp: Long,
    val emergencyPhoneDialed: String,
    val status: String,
    val syncStatus: String
)

@Entity(tableName = "appointment_requests")
data class AppointmentRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: String,
    val facilityName: String,
    val reasonCategory: String,
    val requestedDateTime: String,
    val patientName: String,
    val patientContact: String,
    val status: String,
    val syncStatus: String,
    val createdAt: Long
)

@Entity(tableName = "caregiver_tasks")
data class CaregiverTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: String,
    val caregiverName: String,
    val type: String,
    val title: String,
    val details: String,
    val dueTime: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val syncStatus: String
)

// ---------------- DAOs ----------------

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication_schedules ORDER BY name ASC")
    fun getAllSchedules(): Flow<List<MedicationScheduleEntity>>

    @Query("SELECT * FROM medication_schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): MedicationScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MedicationScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: MedicationScheduleEntity)

    @Query("UPDATE medication_schedules SET remainingSupply = remainingSupply - 1 WHERE id = :id AND remainingSupply > 0")
    suspend fun decrementSupply(id: Long)

    @Query("UPDATE medication_schedules SET remainingSupply = :newCount WHERE id = :id")
    suspend fun updateSupply(id: Long, newCount: Int)

    @Query("DELETE FROM medication_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)

    // Logs
    @Query("SELECT * FROM medication_logs WHERE dateEpochDay = :epochDay ORDER BY scheduledTime ASC")
    fun getLogsForDay(epochDay: Long): Flow<List<MedicationLogEntity>>

    @Query("SELECT * FROM medication_logs ORDER BY dateEpochDay DESC, scheduledTime DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<MedicationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicationLogEntity): Long

    @Update
    suspend fun updateLog(log: MedicationLogEntity)

    @Query("SELECT * FROM medication_logs WHERE scheduleId = :scheduleId AND dateEpochDay = :epochDay AND scheduledTime = :time LIMIT 1")
    suspend fun getLogForDose(scheduleId: Long, epochDay: Long, time: String): MedicationLogEntity?

    @Query("SELECT COUNT(*) FROM medication_logs WHERE status = 'TAKEN' AND dateEpochDay >= :startEpochDay")
    suspend fun countTakenSince(startEpochDay: Long): Int

    @Query("SELECT COUNT(*) FROM medication_logs WHERE (status = 'TAKEN' OR status = 'MISSED') AND dateEpochDay >= :startEpochDay")
    suspend fun countTotalCompletedOrMissedSince(startEpochDay: Long): Int

    @Query("SELECT * FROM medication_logs WHERE dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay ORDER BY dateEpochDay DESC, scheduledTime DESC")
    suspend fun getLogsBetween(startEpochDay: Long, endEpochDay: Long): List<MedicationLogEntity>

    @Query("SELECT COUNT(*) FROM medication_logs WHERE status = 'TAKEN' AND dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay")
    suspend fun countTakenBetween(startEpochDay: Long, endEpochDay: Long): Int

    @Query("SELECT COUNT(*) FROM medication_logs WHERE (status = 'TAKEN' OR status = 'MISSED') AND dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay")
    suspend fun countTotalBetween(startEpochDay: Long, endEpochDay: Long): Int
}

@Dao
interface VitalsDao {
    @Query("SELECT * FROM vitals_records ORDER BY recordedAt DESC")
    fun getAllVitals(): Flow<List<VitalsRecordEntity>>

    @Query("SELECT * FROM vitals_records WHERE type = :type ORDER BY recordedAt DESC LIMIT :limit")
    fun getVitalsByType(type: String, limit: Int = 20): Flow<List<VitalsRecordEntity>>

    @Query("SELECT * FROM vitals_records WHERE recordedAt >= :startTime AND recordedAt <= :endTime ORDER BY recordedAt DESC")
    suspend fun getVitalsBetween(startTime: Long, endTime: Long): List<VitalsRecordEntity>

    @Query("SELECT * FROM vitals_records ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestVital(): Flow<VitalsRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vital: VitalsRecordEntity): Long

    @Query("SELECT * FROM vitals_records WHERE isRedFlag = 1 ORDER BY recordedAt DESC LIMIT 20")
    fun getRedFlagVitals(): Flow<List<VitalsRecordEntity>>
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_windows ORDER BY windowEnd DESC")
    fun getAllWindows(): Flow<List<ActivityWindowEntity>>

    @Query("SELECT * FROM activity_windows WHERE windowStart >= :startTime ORDER BY windowStart ASC")
    fun getWindowsSince(startTime: Long): Flow<List<ActivityWindowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWindow(window: ActivityWindowEntity): Long

    @Query("SELECT SUM(stepCount) FROM activity_windows WHERE windowStart >= :startTime")
    suspend fun getStepCountSince(startTime: Long): Int?

    @Query("SELECT AVG(stepCount) FROM activity_windows WHERE windowStart >= :startTime")
    suspend fun getAverageStepsPerWindow(startTime: Long): Double?
}

@Dao
interface TriageDao {
    @Query("SELECT * FROM triage_results ORDER BY createdAt DESC LIMIT 50")
    fun getTriageHistory(): Flow<List<TriageResultEntity>>

    @Query("SELECT * FROM triage_results WHERE isRedFlag = 1 ORDER BY createdAt DESC LIMIT 10")
    fun getActiveRedFlags(): Flow<List<TriageResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriageResult(triage: TriageResultEntity): Long
}

@Dao
interface SOSDao {
    @Query("SELECT * FROM sos_events ORDER BY timestamp DESC")
    fun getAllSOSEvents(): Flow<List<SOSEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSOSEvent(event: SOSEventEntity): Long

    @Query("UPDATE sos_events SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointment_requests ORDER BY createdAt DESC")
    fun getAllAppointments(): Flow<List<AppointmentRequestEntity>>

    @Query("SELECT * FROM appointment_requests WHERE syncStatus = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingAppointments(): Flow<List<AppointmentRequestEntity>>

    @Query("SELECT COUNT(*) FROM appointment_requests WHERE syncStatus = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentRequestEntity): Long

    @Query("UPDATE appointment_requests SET status = :status, syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, syncStatus: String)

    @Query("UPDATE appointment_requests SET syncStatus = 'SYNCED' WHERE syncStatus = 'PENDING'")
    suspend fun markAllSynced()

    @Query("DELETE FROM appointment_requests WHERE id = :id")
    suspend fun deleteAppointment(id: Long)
}

@Dao
interface CaregiverTaskDao {
    @Query("SELECT * FROM caregiver_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<CaregiverTaskEntity>>

    @Query("SELECT * FROM caregiver_tasks WHERE isCompleted = 0 ORDER BY createdAt ASC")
    fun getPendingTasks(): Flow<List<CaregiverTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CaregiverTaskEntity): Long

    @Query("UPDATE caregiver_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, completed: Boolean)

    @Query("DELETE FROM caregiver_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}

// ---------------- Room Database ----------------

@Database(
    entities = [
        MedicationScheduleEntity::class,
        MedicationLogEntity::class,
        VitalsRecordEntity::class,
        ActivityWindowEntity::class,
        TriageResultEntity::class,
        SOSEventEntity::class,
        AppointmentRequestEntity::class,
        CaregiverTaskEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ManaakiDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun activityDao(): ActivityDao
    abstract fun triageDao(): TriageDao
    abstract fun sosDao(): SOSDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun caregiverTaskDao(): CaregiverTaskDao
}
