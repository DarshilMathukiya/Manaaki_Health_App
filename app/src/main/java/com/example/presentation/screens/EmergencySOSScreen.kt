package com.example.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SOSTrigger
import com.example.presentation.components.AppointmentConfirmationDialog
import com.example.presentation.components.BookAppointmentModal
import com.example.presentation.components.HealthFacilityCard
import com.example.presentation.components.HoldToConfirmSOSButton
import com.example.presentation.viewmodels.AppointmentViewModel
import com.example.presentation.viewmodels.EmergencyViewModel
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.HealthRedFlag
import com.example.ui.theme.HealthRedFlagBg

@Composable
fun EmergencySOSScreen(
    viewModel: EmergencyViewModel,
    modifier: Modifier = Modifier,
    appointmentViewModel: AppointmentViewModel? = null,
    onClose: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val appointmentState = appointmentViewModel?.uiState?.collectAsState()?.value

    BackHandler(enabled = onClose != null) {
        onClose?.invoke()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .testTag("emergency_sos_screen"),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Emergency Header with consistent top-left Back button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onClose != null) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(44.dp)
                            .testTag("emergency_sos_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = HealthRedFlag,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Emergency SOS & Care Locator",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthRedFlag,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hold button below for 3 seconds to instantly call 111 & dispatch GPS Medical ID.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                    )
                }
            }
        }

        // 2. Main Hold-To-Confirm SOS Dispatch Button
        item {
            HoldToConfirmSOSButton(
                holdDurationMillis = 3000L,
                onConfirmed = { viewModel.triggerSOS(SOSTrigger.MANUAL) }
            )
        }

        // 3. Current GPS Location Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CURRENT GPS LOCATION (NZ)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GeoTextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = state.locationAddress,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark
                            )
                        )
                    }
                }
            }
        }

        // 4. Senior Medical ID Summary Card
        item {
            Text(
                text = "SENIOR MEDICAL ID (TRANSMITTED ON SOS)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.medicalId.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = GeoMintSelected,
                            border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "NHI: ${state.medicalId.nhiNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = GeoPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "DOB: ${state.medicalId.dateOfBirth} • Blood Group: ${state.medicalId.bloodType}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = GeoTextPrimary)
                    )
                    Text(
                        text = "Known Allergies: ${state.medicalId.knownAllergies}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = HealthRedFlag, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Conditions: ${state.medicalId.chronicConditions}",
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "EMERGENCY CONTACTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoTextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    state.medicalId.emergencyContacts.forEach { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark, fontSize = 15.sp)
                                )
                                Text(
                                    text = contact.relationship,
                                    style = MaterialTheme.typography.bodySmall.copy(color = GeoPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                                )
                                Text(
                                    text = contact.phone,
                                    style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.callFacility(contact.phone) },
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                modifier = Modifier.defaultMinSize(minWidth = 90.dp, minHeight = 44.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CALL", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 5. Nearby Care Facilities & Urgent Care Locator (FR5)
        item {
            Text(
                text = "NEARBY CARE FACILITIES & CLINICS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(state.nearbyFacilities, key = { it.id }) { facility ->
            HealthFacilityCard(
                facility = facility,
                onBookAppointment = { fac -> appointmentViewModel?.openBooking(fac) },
                onCallClinic = { phone -> viewModel.callFacility(phone) },
                onMapNav = { fac -> viewModel.openMapNavigation(fac) }
            )
        }
    }

    // Booking Dialog Modal (if appointmentViewModel provided)
    if (appointmentViewModel != null && appointmentState != null && appointmentState.isBookingModalOpen && appointmentState.selectedFacility != null) {
        val fac = appointmentState.selectedFacility!!
        BookAppointmentModal(
            facility = fac,
            selectedReason = appointmentState.selectedReason,
            availableReasons = appointmentState.availableReasons,
            onReasonSelected = { appointmentViewModel.selectReason(it) },
            selectedDate = appointmentState.selectedDate,
            availableDates = appointmentState.availableDates,
            onDateSelected = { appointmentViewModel.selectDate(it) },
            selectedTimeSlot = appointmentState.selectedTimeSlot,
            availableTimeSlots = appointmentState.availableTimeSlots,
            onTimeSlotSelected = { appointmentViewModel.selectTimeSlot(it) },
            patientName = appointmentState.patientName,
            patientContact = appointmentState.patientContact,
            isSubmitting = appointmentState.isSubmitting,
            errorMessage = appointmentState.errorMessage,
            onRequestAppointment = { appointmentViewModel.submitAppointmentRequest() },
            onDismiss = { appointmentViewModel.closeBooking() }
        )
    }

    // Confirmation Dialog
    if (appointmentViewModel != null && appointmentState != null && appointmentState.isConfirmationOpen && appointmentState.confirmedAppointment != null) {
        val appt = appointmentState.confirmedAppointment!!
        val clinicPhone = state.nearbyFacilities.find { it.id == appt.facilityId }?.phone ?: "+64 9 376 4467"
        AppointmentConfirmationDialog(
            appointment = appt,
            onDismiss = { appointmentViewModel.dismissConfirmation() },
            onCallClinic = {
                appointmentViewModel.callClinic(clinicPhone)
            }
        )
    }

    // SOS Dispatched Notification Modal
    if (state.isSOSDispatched) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSOSDialog() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = null,
                    tint = HealthRedFlag,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "111 EMERGENCY DISPATCHED",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = HealthRedFlag,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "• Dialing New Zealand 111 Emergency Services.\n• GPS Coordinates (${state.latitude}, ${state.longitude}) transmitted.\n• Medical ID & Allergy packet sent to primary caregiver David Te Aroha.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissSOSDialog() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRedFlag)
                ) {
                    Text("UNDERSTOOD", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            }
        )
    }
}

