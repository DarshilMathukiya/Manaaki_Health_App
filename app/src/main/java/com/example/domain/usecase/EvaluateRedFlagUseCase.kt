package com.example.domain.usecase

import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.TriageResult
import com.example.domain.model.VitalsRecord
import com.example.domain.model.VitalsType

/**
 * Deterministic Clinical Red-Flag Evaluation Engine.
 *
 * Rule-based red-flag checks run BEFORE and INDEPENDENTLY of any on-device machine learning model.
 * A low-confidence model output may soften an advisory rating shown to a caregiver,
 * but deterministic rule matches ALWAYS trigger an immediate emergency escalation.
 */
class EvaluateRedFlagUseCase {

    // CLINICAL_THRESHOLD: placeholder — must be reviewed against published guidance (e.g. Heart Foundation NZ, BPAC NZ)
    companion object {
        const val BP_SYSTOLIC_CRISIS = 180
        const val BP_DIASTOLIC_CRISIS = 120
        const val BP_SYSTOLIC_HIGH = 140
        const val BP_DIASTOLIC_HIGH = 90
        const val BP_SYSTOLIC_LOW_CRITICAL = 85
        const val BP_SYSTOLIC_LOW_WARNING = 95

        const val HR_LOW_CRITICAL = 42
        const val HR_LOW_WARNING = 50
        const val HR_HIGH_WARNING = 105
        const val HR_HIGH_CRITICAL = 130

        const val GLUCOSE_LOW_CRITICAL = 3.0 // mmol/L
        const val GLUCOSE_LOW_WARNING = 4.0  // mmol/L
        const val GLUCOSE_HIGH_WARNING = 11.1 // mmol/L
        const val GLUCOSE_HIGH_CRITICAL = 18.0 // mmol/L

        const val ACTIVITY_DEVIATION_CRITICAL_DROP = 0.25 // < 25% of 7-day baseline
        const val ACTIVITY_DEVIATION_WARNING_DROP = 0.50  // < 50% of 7-day baseline
    }

    fun evaluateVitals(vitals: VitalsRecord): TriageResult {
        return when (vitals.type) {
            VitalsType.BLOOD_PRESSURE -> evaluateBloodPressure(vitals.systolic ?: 0, vitals.diastolic ?: 0)
            VitalsType.HEART_RATE -> evaluateHeartRate(vitals.value.toInt())
            VitalsType.BLOOD_GLUCOSE -> evaluateBloodGlucose(vitals.value)
        }
    }

    fun evaluateBloodPressure(systolic: Int, diastolic: Int): TriageResult {
        // Deterministic Red Flag checks
        if (systolic >= BP_SYSTOLIC_CRISIS || diastolic >= BP_DIASTOLIC_CRISIS) {
            return TriageResult(
                sourceType = "VITALS_BP",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_HYPERTENSIVE_CRISIS",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 1.0,
                summary = "Severe High Blood Pressure ($systolic/$diastolic mmHg). Urgent clinical triage required."
            )
        }

        if (systolic > 0 && systolic <= BP_SYSTOLIC_LOW_CRITICAL) {
            return TriageResult(
                sourceType = "VITALS_BP",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_HYPOTENSION",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 1.0,
                summary = "Severe Low Blood Pressure ($systolic/$diastolic mmHg). Risk of collapse or shock."
            )
        }

        if (systolic >= BP_SYSTOLIC_HIGH || diastolic >= BP_DIASTOLIC_HIGH) {
            return TriageResult(
                sourceType = "VITALS_BP",
                isRedFlag = false,
                ruleTriggered = "ELEVATED_STAGE_1_2",
                advisorySeverity = AdvisorySeverity.ELEVATED,
                modelConfidence = 0.94,
                summary = "Elevated Blood Pressure ($systolic/$diastolic mmHg). Continue monitoring and notify caregiver."
            )
        }

        if (systolic <= BP_SYSTOLIC_LOW_WARNING) {
            return TriageResult(
                sourceType = "VITALS_BP",
                isRedFlag = false,
                ruleTriggered = "BORDERLINE_LOW_BP",
                advisorySeverity = AdvisorySeverity.ADVISORY_LOW,
                modelConfidence = 0.90,
                summary = "Borderline low Blood Pressure ($systolic/$diastolic mmHg). Rest and stay hydrated."
            )
        }

        return TriageResult(
            sourceType = "VITALS_BP",
            isRedFlag = false,
            ruleTriggered = "NORMAL_READING",
            advisorySeverity = AdvisorySeverity.NORMAL,
            modelConfidence = 0.99,
            summary = "Optimal Blood Pressure reading ($systolic/$diastolic mmHg)."
        )
    }

    fun evaluateHeartRate(bpm: Int): TriageResult {
        if (bpm <= HR_LOW_CRITICAL) {
            return TriageResult(
                sourceType = "VITALS_HR",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_BRADYCARDIA",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 1.0,
                summary = "Critically low resting pulse ($bpm bpm). Risk of syncope."
            )
        }

        if (bpm >= HR_HIGH_CRITICAL) {
            return TriageResult(
                sourceType = "VITALS_HR",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_TACHYCARDIA",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 1.0,
                summary = "Critically high resting pulse ($bpm bpm). Emergency evaluation indicated."
            )
        }

        if (bpm >= HR_HIGH_WARNING) {
            return TriageResult(
                sourceType = "VITALS_HR",
                isRedFlag = false,
                ruleTriggered = "ELEVATED_HR",
                advisorySeverity = AdvisorySeverity.ELEVATED,
                modelConfidence = 0.92,
                summary = "Elevated resting pulse ($bpm bpm). Monitor for palpitations or dizziness."
            )
        }

        if (bpm <= HR_LOW_WARNING) {
            return TriageResult(
                sourceType = "VITALS_HR",
                isRedFlag = false,
                ruleTriggered = "MILD_BRADYCARDIA",
                advisorySeverity = AdvisorySeverity.ADVISORY_LOW,
                modelConfidence = 0.90,
                summary = "Mildly low resting pulse ($bpm bpm)."
            )
        }

        return TriageResult(
            sourceType = "VITALS_HR",
            isRedFlag = false,
            ruleTriggered = "NORMAL_HR",
            advisorySeverity = AdvisorySeverity.NORMAL,
            modelConfidence = 0.99,
            summary = "Resting pulse within normal range ($bpm bpm)."
        )
    }

    fun evaluateBloodGlucose(mmolL: Double): TriageResult {
        if (mmolL <= GLUCOSE_LOW_CRITICAL) {
            return TriageResult(
                sourceType = "VITALS_GLUCOSE",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_HYPOGLYCEMIA",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 1.0,
                summary = "Severe Hypoglycemia ($mmolL mmol/L). Immediate fast-acting carbohydrate & urgent care."
            )
        }

        if (mmolL >= GLUCOSE_HIGH_CRITICAL) {
            return TriageResult(
                sourceType = "VITALS_GLUCOSE",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_HYPERGLYCEMIA",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 1.0,
                summary = "Severe Hyperglycemia ($mmolL mmol/L). Danger of hyperosmolar / ketoacidosis state."
            )
        }

        if (mmolL <= GLUCOSE_LOW_WARNING) {
            return TriageResult(
                sourceType = "VITALS_GLUCOSE",
                isRedFlag = false,
                ruleTriggered = "LOW_GLUCOSE_WARNING",
                advisorySeverity = AdvisorySeverity.ELEVATED,
                modelConfidence = 0.95,
                summary = "Low Blood Glucose ($mmolL mmol/L). Have a snack and recheck in 15 minutes."
            )
        }

        if (mmolL >= GLUCOSE_HIGH_WARNING) {
            return TriageResult(
                sourceType = "VITALS_GLUCOSE",
                isRedFlag = false,
                ruleTriggered = "ELEVATED_GLUCOSE",
                advisorySeverity = AdvisorySeverity.ADVISORY_LOW,
                modelConfidence = 0.92,
                summary = "Elevated Blood Glucose ($mmolL mmol/L). Verify insulin / medication timing."
            )
        }

        return TriageResult(
            sourceType = "VITALS_GLUCOSE",
            isRedFlag = false,
            ruleTriggered = "NORMAL_GLUCOSE",
            advisorySeverity = AdvisorySeverity.NORMAL,
            modelConfidence = 0.98,
            summary = "Blood glucose reading on target ($mmolL mmol/L)."
        )
    }

    fun evaluateActivityDeviation(todaySteps: Int, baseline7DayAvgSteps: Int): TriageResult {
        if (baseline7DayAvgSteps < 500) {
            // Not enough baseline accumulated yet
            return TriageResult(
                sourceType = "ACTIVITY",
                isRedFlag = false,
                ruleTriggered = "BASELINE_CALIBRATING",
                advisorySeverity = AdvisorySeverity.NORMAL,
                modelConfidence = 0.85,
                summary = "Activity baseline is currently calibrating."
            )
        }

        val ratio = todaySteps.toDouble() / baseline7DayAvgSteps.toDouble()

        if (ratio <= ACTIVITY_DEVIATION_CRITICAL_DROP) {
            return TriageResult(
                sourceType = "ACTIVITY",
                isRedFlag = true,
                ruleTriggered = "CRITICAL_MOBILITY_COLLAPSE",
                advisorySeverity = AdvisorySeverity.CRITICAL_RED_FLAG,
                modelConfidence = 0.96,
                summary = "Severe activity drop (${(ratio * 100).toInt()}% of 7-day baseline). Check senior wellbeing immediately."
            )
        }

        if (ratio <= ACTIVITY_DEVIATION_WARNING_DROP) {
            return TriageResult(
                sourceType = "ACTIVITY",
                isRedFlag = false,
                ruleTriggered = "REDUCED_MOBILITY_TREND",
                advisorySeverity = AdvisorySeverity.ELEVATED,
                modelConfidence = 0.88,
                summary = "Noticeable reduction in daily steps (${(ratio * 100).toInt()}% of rolling baseline)."
            )
        }

        return TriageResult(
            sourceType = "ACTIVITY",
            isRedFlag = false,
            ruleTriggered = "NORMAL_ACTIVITY",
            advisorySeverity = AdvisorySeverity.NORMAL,
            modelConfidence = 0.98,
            summary = "Activity level consistent with 7-day personal routine (${(ratio * 100).toInt()}% of baseline)."
        )
    }
}
