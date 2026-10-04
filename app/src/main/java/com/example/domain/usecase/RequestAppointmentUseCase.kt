package com.example.domain.usecase

import com.example.data.repository.AppointmentRepository
import com.example.domain.model.AppointmentRequest
import com.example.domain.model.SampleData
import com.example.domain.model.SyncStatus

class RequestAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(
        facilityId: String,
        facilityName: String,
        reasonCategory: String,
        requestedDateTime: String,
        patientName: String,
        patientContact: String
    ): Result<AppointmentRequest> {
        if (facilityId.isBlank() || facilityName.isBlank()) {
            return Result.failure(IllegalArgumentException("Facility information is required."))
        }
        if (reasonCategory.isBlank()) {
            return Result.failure(IllegalArgumentException("Please select a reason for your visit."))
        }
        if (requestedDateTime.isBlank()) {
            return Result.failure(IllegalArgumentException("Please choose a preferred date and time."))
        }

        val request = AppointmentRequest(
            facilityId = facilityId,
            facilityName = facilityName,
            reasonCategory = reasonCategory,
            requestedDateTime = requestedDateTime,
            patientName = patientName.ifBlank { SampleData.PATIENT_FULL_NAME },
            patientContact = patientContact.ifBlank { SampleData.CAREGIVER_PHONE },
            status = "REQUESTED",
            syncStatus = SyncStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )

        val id = appointmentRepository.createAppointmentRequest(request)
        return Result.success(request.copy(id = id))
    }
}
