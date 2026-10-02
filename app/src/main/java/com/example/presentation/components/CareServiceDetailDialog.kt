package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

enum class CareServiceType(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val description: String,
    val features: List<String>,
    val staffQualification: String,
    val availability: String
) {
    HOME_CARE(
        title = "Home Care",
        subtitle = "Daily living, meal preparation & wellness checks",
        icon = Icons.Default.Home,
        description = "Empowering seniors to live independently in their own homes with compassionate daily assistance, nutritious meal support, medication prompting, and companionship.",
        features = listOf(
            "Personal hygiene & dressing assistance",
            "Nutritious meal preparation & hydration tracking",
            "Light housekeeping & mobility assistance",
            "Safe transport to appointments & community outings"
        ),
        staffQualification = "Certified New Zealand Healthcare Assistants (NZQA Level 3/4)",
        availability = "Available 7 days/week • 2 to 24-hour shift options"
    ),
    MEMORY_CARE(
        title = "Memory Care",
        subtitle = "Cognitive stimulation, routine adherence & gentle safety",
        icon = Icons.Default.Psychology,
        description = "Specialised support for individuals experiencing mild cognitive impairment, dementia, or Alzheimer’s, focused on dignity, sensory engagement, and routine structure.",
        features = listOf(
            "Structured daily cognitive exercises & reminiscence therapy",
            "Wandering prevention & GPS safe-zone alerts",
            "De-escalation & emotional reassurance techniques",
            "Family caregiver respite & routine synchronization"
        ),
        staffQualification = "Dementia-certified Clinical Specialists & Occupational Therapists",
        availability = "24/7 dedicated supervision & hourly respite available"
    ),
    SPECIALISED_CARE(
        title = "Specialised Care",
        subtitle = "Chronic disease management, cardiac & post-op rehab",
        icon = Icons.Default.WorkspacePremium,
        description = "Tailored clinical rehabilitation and care protocols for complex health conditions, including diabetes management, cardiac recovery, Parkinson’s, and stroke rehabilitation.",
        features = listOf(
            "Post-hospital discharge rehabilitation & mobility training",
            "Blood glucose & insulin titration monitoring",
            "Cardiac telemetry review & respiratory therapy support",
            "Specialist multi-disciplinary care coordination"
        ),
        staffQualification = "Senior Clinical Nurse Specialists & Physiotherapists",
        availability = "Scheduled home visits & continuous remote telemetry triage"
    ),
    NURSING_CARE(
        title = "Nursing Care",
        subtitle = "Registered nurse visits, wound triage & IV therapy",
        icon = Icons.Default.Healing,
        description = "Hospital-grade nursing services delivered in the comfort and privacy of your home by licensed New Zealand Registered Nurses under clinical governance.",
        features = listOf(
            "Complex wound care & post-surgical dressing changes",
            "Medication administration, injections & subcutaneous lines",
            "Catheter & stoma clinical management",
            "Palliative symptom control & pain management"
        ),
        staffQualification = "New Zealand Nursing Council Registered Nurses (RNs)",
        availability = "24/7 On-Call Urgent Response & Scheduled Clinical Visits"
    )
}

@Composable
fun CareServiceDetailDialog(
    service: CareServiceType,
    onDismiss: () -> Unit,
    onRequestConsultation: (CareServiceType) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("care_service_detail_modal"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, GeoBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row with consistent top-left Back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("care_service_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GeoPrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = GeoMintSelected,
                            border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = service.icon,
                                    contentDescription = null,
                                    tint = GeoPrimaryDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = GeoSageContainer
                            ) {
                                Text(
                                    text = "MANAAKI CARE SERVICES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GeoPrimaryDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = GeoBorder.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GeoTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "WHAT'S INCLUDED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                service.features.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GeoTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Qualifications & Availability Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSageContainer.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = GeoPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CLINICAL GOVERNANCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = service.staffQualification,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GeoTextPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = service.availability,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GeoTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Text(
                            text = "Dismiss",
                            color = GeoTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onRequestConsultation(service)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("book_care_service_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Book Care",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
