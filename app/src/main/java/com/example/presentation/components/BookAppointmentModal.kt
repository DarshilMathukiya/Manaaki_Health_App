package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.AppointmentRequest
import com.example.domain.model.CareFacility
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookAppointmentModal(
    facility: CareFacility,
    selectedReason: String,
    availableReasons: List<String>,
    onReasonSelected: (String) -> Unit,
    selectedDate: String,
    availableDates: List<String>,
    onDateSelected: (String) -> Unit,
    selectedTimeSlot: String,
    availableTimeSlots: List<String>,
    onTimeSlotSelected: (String) -> Unit,
    patientName: String,
    patientContact: String,
    isSubmitting: Boolean,
    errorMessage: String?,
    onRequestAppointment: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .testTag("book_appointment_modal"),
            shape = RoundedCornerShape(28.dp),
            color = GeoSurface,
            border = BorderStroke(1.dp, GeoBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Top Bar with consistent top-left Back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .testTag("close_booking_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GeoPrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "REQUEST APPOINTMENT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoTextSecondary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = facility.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "${facility.address} • ${facility.openingHours}",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error banner if any
                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, Color(0xFFEF5350))
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 1. Reason for Visit Chips
                Text(
                    text = "1. REASON FOR VISIT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoTextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableReasons.forEach { reason ->
                        val isSelected = reason == selectedReason
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) GeoPrimary else GeoBackground,
                            border = BorderStroke(1.5.dp, if (isSelected) GeoPrimary else GeoBorder),
                            modifier = Modifier
                                .clickable { onReasonSelected(reason) }
                                .testTag("reason_chip_$reason")
                        ) {
                            Text(
                                text = reason,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isSelected) Color.White else GeoPrimaryDark,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Preferred Date Selection
                Text(
                    text = "2. PREFERRED DATE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoTextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableDates.forEach { date ->
                        val isSelected = date == selectedDate
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) GeoPrimary else GeoBackground,
                            border = BorderStroke(1.5.dp, if (isSelected) GeoPrimary else GeoBorder),
                            modifier = Modifier
                                .clickable { onDateSelected(date) }
                                .testTag("date_chip_$date")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else GeoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) Color.White else GeoPrimaryDark,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Preferred Time Slot Selection
                Text(
                    text = "3. PREFERRED TIME SLOT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoTextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTimeSlots.forEach { slot ->
                        val isSelected = slot == selectedTimeSlot
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) GeoPrimary else GeoBackground,
                            border = BorderStroke(1.5.dp, if (isSelected) GeoPrimary else GeoBorder),
                            modifier = Modifier
                                .clickable { onTimeSlotSelected(slot) }
                                .testTag("time_chip_$slot")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else GeoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) Color.White else GeoPrimaryDark,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Auto-filled Patient Details Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoBackground),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PATIENT DETAILS (AUTO-FILLED)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            Text(
                                text = "$patientName • $patientContact",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark
                                )
                            )
                            Text(
                                text = "Details auto-filled from your profile. Editable in Account.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GeoTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = onRequestAppointment,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_appointment_request_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Request Appointment",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Text(
                        text = "Cancel",
                        color = GeoTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentConfirmationDialog(
    appointment: AppointmentRequest,
    onDismiss: () -> Unit,
    onCallClinic: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = GeoSurface,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = GeoPrimary,
                modifier = Modifier.size(44.dp)
            )
        },
        title = {
            Text(
                text = "Appointment Requested",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimaryDark,
                    fontSize = 20.sp
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Your appointment request has been submitted to ${appointment.facilityName}.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GeoTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GeoBackground,
                    border = BorderStroke(1.dp, GeoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "• Requested For: ${appointment.requestedDateTime}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark)
                        )
                        Text(
                            text = "• Reason: ${appointment.reasonCategory}",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )
                        Text(
                            text = "• Patient: ${appointment.patientName} (${appointment.patientContact})",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )
                    }
                }

                Text(
                    text = "The clinic team will review availability and call you shortly to confirm your booking. Please note this is an appointment request, not an immediate guaranteed slot.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GeoTextSecondary,
                        lineHeight = 18.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GeoSageContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Saved locally & queued. Sync status is visible in the Account tab.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GeoPrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("appointment_confirm_done_button")
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCallClinic,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = GeoPrimaryDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call Clinic Directly", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    )
}
