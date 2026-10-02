package com.example.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.domain.model.CareFacility
import com.example.presentation.components.AppointmentConfirmationDialog
import com.example.presentation.components.BookAppointmentModal
import com.example.presentation.components.HealthFacilityCard
import com.example.presentation.viewmodels.AppointmentViewModel
import com.example.presentation.viewmodels.EmergencyViewModel
import com.example.presentation.viewmodels.FacilityFilter
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextSecondary

@Composable
fun HealthFacilitiesScreen(
    emergencyViewModel: EmergencyViewModel,
    appointmentViewModel: AppointmentViewModel,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null
) {
    val emergencyState by emergencyViewModel.uiState.collectAsState()
    val appointmentState by appointmentViewModel.uiState.collectAsState()

    BackHandler(enabled = onClose != null) {
        onClose?.invoke()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .testTag("health_facilities_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header with consistent top-left Back button
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
                            .testTag("close_facilities_screen_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = GeoPrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NZ Health Facilities",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Distance-sorted Auckland care network & appointment booking",
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                    )
                }
            }
        }

        // 2. Filter Chips Row (All / 24/7 ED / Urgent Care / GP Clinics)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("facility_filter_row")
            ) {
                items(items = FacilityFilter.entries) { filter: FacilityFilter ->
                    val isSelected = emergencyState.selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) GeoPrimary else GeoSurface,
                        border = BorderStroke(1.dp, if (isSelected) GeoPrimary else GeoBorder),
                        modifier = Modifier
                            .clickable { emergencyViewModel.setFilter(filter) }
                            .testTag("filter_chip_${filter.name}")
                    ) {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color.White else GeoPrimaryDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // 3. Facility Cards
        val facilities: List<CareFacility> = emergencyState.filteredFacilities.ifEmpty { emergencyState.nearbyFacilities }
        items(items = facilities, key = { facilityItem: CareFacility -> facilityItem.id }) { facility: CareFacility ->
            HealthFacilityCard(
                facility = facility,
                onBookAppointment = { appointmentViewModel.openBooking(it) },
                onCallClinic = { phone -> emergencyViewModel.callFacility(phone) },
                onMapNav = { emergencyViewModel.openMapNavigation(it) }
            )
        }
    }

    // Booking Dialog Modal
    if (appointmentState.isBookingModalOpen && appointmentState.selectedFacility != null) {
        val selectedFacility = appointmentState.selectedFacility!!
        BookAppointmentModal(
            facility = selectedFacility,
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
    if (appointmentState.isConfirmationOpen && appointmentState.confirmedAppointment != null) {
        val confirmedAppointment = appointmentState.confirmedAppointment!!
        val clinicPhone = emergencyState.nearbyFacilities.find { it.id == confirmedAppointment.facilityId }?.phone ?: "+64 9 376 4467"
        AppointmentConfirmationDialog(
            appointment = confirmedAppointment,
            onDismiss = { appointmentViewModel.dismissConfirmation() },
            onCallClinic = {
                appointmentViewModel.callClinic(clinicPhone)
            }
        )
    }
}
