package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.domain.model.CareFacility
import com.example.domain.model.FacilityType
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.HealthRedFlag

@Composable
fun HealthFacilityCard(
    facility: CareFacility,
    onBookAppointment: (CareFacility) -> Unit,
    onCallClinic: (String) -> Unit,
    onMapNav: (CareFacility) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmergencyOnly = facility.type == FacilityType.HOSPITAL || facility.isAfterHoursEmergency && facility.type != FacilityType.GP_CLINIC && facility.type != FacilityType.URGENT_CARE

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("facility_card_${facility.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        border = BorderStroke(1.dp, GeoBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Name, Type Icon, Distance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = if (isEmergencyOnly) HealthRedFlag else GeoPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = facility.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark,
                                fontSize = 17.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = facility.address,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (facility.isRegisteredGP) GeoPrimary.copy(alpha = 0.15f) else GeoSageContainer,
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (facility.isRegisteredGP) "Registered GP" else "${facility.distanceKm} km",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark,
                            fontSize = 11.sp,
                            letterSpacing = 0.6.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hours & Emergency indicator
            Text(
                text = facility.openingHours,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (facility.isAfterHoursEmergency) HealthRedFlag else GeoTextSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row
            if (isEmergencyOnly) {
                // Emergency Department: Call Hospital as primary action, Map & Nav secondary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onCallClinic(facility.phone) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("call_emergency_button_${facility.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HealthRedFlag)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CALL HOSPITAL", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.6.sp)
                    }

                    OutlinedButton(
                        onClick = { onMapNav(facility) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("nav_button_${facility.id}"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = null, tint = GeoPrimaryDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NAV", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            } else {
                // GP / Urgent Care / Pharmacy: Book Appointment as primary action
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onBookAppointment(facility) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("book_appointment_button_${facility.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Book Appointment",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onCallClinic(facility.phone) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("call_clinic_button_${facility.id}"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = GeoPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Clinic", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { onMapNav(facility) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("map_nav_button_${facility.id}"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Icon(imageVector = Icons.Default.Navigation, contentDescription = null, tint = GeoPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Map & Nav", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
