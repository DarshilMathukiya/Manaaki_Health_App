package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.VitalsType
import com.example.presentation.components.CareServiceDetailDialog
import com.example.presentation.components.CareServiceType
import com.example.presentation.components.HoldToConfirmSOSButton
import com.example.presentation.components.PillIconView
import com.example.presentation.viewmodels.HomeViewModel
import com.example.presentation.viewmodels.MedicationViewModel
import com.example.presentation.voice.VoiceAssistantDialog
import com.example.presentation.voice.VoiceAssistantManager
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.GeoTrack
import com.example.ui.theme.HealthRedFlag
import com.example.ui.theme.HealthRedFlagBg
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.HealthWarningAmberBg

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    medicationViewModel: MedicationViewModel,
    voiceAssistantManager: VoiceAssistantManager,
    onNavigateToMedication: () -> Unit,
    onNavigateToVitals: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToFacilities: () -> Unit,
    onNavigateToSOS: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by homeViewModel.uiState.collectAsState()
    var selectedCareService by remember { mutableStateOf<CareServiceType?>(null) }
    var snoozeNoticeVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GeoBackground),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. FALL DETECTION ALERT (CRITICAL SENSORS)
            // ==========================================
            if (state.isFallDetected) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = HealthRedFlagBg),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, HealthRedFlag.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = HealthRedFlag,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "FALL DETECTED BY SENSORS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = HealthRedFlag,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "A sudden impact was recorded. If you need assistance, emergency contacts and 111 will be notified.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextPrimary)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onNavigateToSOS,
                                    colors = ButtonDefaults.buttonColors(containerColor = HealthRedFlag),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Emergency SOS", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { homeViewModel.dismissFallAlert() },
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, GeoBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("I'm Okay", color = GeoTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. LOW SUPPLY ALERT BANNER
            // ==========================================
            if (state.lowSupplyMedications.isNotEmpty()) {
                item {
                    val lowMed = state.lowSupplyMedications.first()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToFacilities() },
                        colors = CardDefaults.cardColors(containerColor = HealthWarningAmberBg),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, HealthWarningAmber.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = HealthWarningAmber,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "RUNNING LOW ON ${lowMed.name.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HealthWarningAmber,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Only ${lowMed.remainingSupply} doses left • Tap to request GP refill",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GeoTextPrimary,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = HealthWarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 3. DEDICATED HIGH-VISIBILITY MEDICATION REMINDER NOTIFICATION PANEL
            // =========================================================================
            item {
                val nextDose = state.nextDose
                val medName = nextDose?.name ?: "Atorvastatin"
                val medDosage = nextDose?.dosage ?: "20 mg"
                val medTime = state.nextDoseTime ?: "08:00 AM"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = Color(0x1A000000))
                        .testTag("medication_reminder_notification_panel"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, Color(0xFF86EFAC)) // Soft light-green accent border
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        // Header row with Bell / Pill reminder icon and status badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(GeoMintSelected),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Medication Reminder",
                                        tint = GeoPrimaryDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "MEDICATION REMINDER",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GeoPrimary,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "Time for your dose",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimaryDark,
                                            fontSize = 17.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = GeoSageContainer,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = medTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GeoPrimaryDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Medication Details Card Sub-container
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = GeoSageContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (nextDose != null) {
                                    PillIconView(iconType = nextDose.iconType, size = 38.dp)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(GeoMintSelected),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Medication,
                                            contentDescription = null,
                                            tint = GeoPrimaryDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "$medName • $medDosage",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimaryDark,
                                            fontSize = 15.5.sp
                                        )
                                    )
                                    Text(
                                        text = if (nextDose != null) nextDose.instructions else "Take with water after morning breakfast",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GeoTextSecondary,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Snooze feedback notification
                        AnimatedVisibility(
                            visible = snoozeNoticeVisible,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = HealthWarningAmberBg,
                                border = BorderStroke(1.dp, HealthWarningAmber.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Snooze,
                                        contentDescription = null,
                                        tint = HealthWarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Reminder snoozed for 15 minutes",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GeoTextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Action Buttons: "Take Now" and "Snooze"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    homeViewModel.markNextDoseTaken()
                                    snoozeNoticeVisible = false
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("medication_take_now_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Take Now",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    homeViewModel.snoozeNextDose()
                                    snoozeNoticeVisible = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("medication_snooze_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, GeoBorder),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = GeoSurface)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Snooze,
                                    contentDescription = null,
                                    tint = GeoTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Snooze",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextSecondary,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 4. "WE PROVIDE" SERVICES SECTION (2X2 RESPONSIVE GREEN THEME GRID)
            // =========================================================================
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "We Provide",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier.testTag("we_provide_section_title")
                        )

                        Text(
                            text = "Tap to explore",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GeoTextSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 1: Home Care & Memory Care
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ServiceCard(
                            service = CareServiceType.HOME_CARE,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedCareService = CareServiceType.HOME_CARE }
                        )

                        ServiceCard(
                            service = CareServiceType.MEMORY_CARE,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedCareService = CareServiceType.MEMORY_CARE }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 2: Specialised Care & Nursing Care
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ServiceCard(
                            service = CareServiceType.SPECIALISED_CARE,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedCareService = CareServiceType.SPECIALISED_CARE }
                        )

                        ServiceCard(
                            service = CareServiceType.NURSING_CARE,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedCareService = CareServiceType.NURSING_CARE }
                        )
                    }
                }
            }

            // =========================================================================
            // 5. CORE HEALTH SNAPSHOT: VITALS & ACTIVITY
            // =========================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Vitals Quick Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onNavigateToVitals() }
                            .testTag("dashboard_vitals_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = GeoSurface),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "VITALS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GeoTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        fontSize = 10.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val bp = state.latestVital
                            val bpDisplay = if (bp?.type == VitalsType.BLOOD_PRESSURE && bp.systolic != null) {
                                "${bp.systolic}/${bp.diastolic}"
                            } else if (bp?.value != null) {
                                "${bp.value}"
                            } else {
                                "128/82"
                            }

                            Text(
                                text = bpDisplay,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    color = GeoPrimaryDark,
                                    fontSize = 26.sp
                                )
                            )

                            Text(
                                text = "BLOOD PRESSURE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(GeoPrimary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "NORMAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GeoPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                        }
                    }

                    // Activity Quick Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onNavigateToActivity() }
                            .testTag("dashboard_activity_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = GeoSurface),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ACTIVITY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GeoTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        fontSize = 10.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${state.todaySteps}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    color = GeoPrimaryDark,
                                    fontSize = 26.sp
                                )
                            )

                            Text(
                                text = "TODAY'S STEPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val progress = (state.todaySteps.toFloat() / state.baselineAverageSteps.coerceAtLeast(1)).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(GeoTrack)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(GeoPrimary)
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 6. CAREGIVER & CLINIC SHORTCUT
            // =========================================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onNavigateToFacilities() }
                        .testTag("dashboard_facilities_shortcut_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GeoSageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = GeoPrimaryDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "HEALTHCARE & CLINIC VISITS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Auckland 24/7 ED, Urgent Care, & Book GP Visits",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GeoPrimaryDark,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = GeoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Direct Hold-to-Confirm SOS Component
            item {
                Spacer(modifier = Modifier.height(2.dp))
                HoldToConfirmSOSButton(
                    onConfirmed = onNavigateToSOS
                )
            }
        }

        // Service Details Modal Dialog
        selectedCareService?.let { service ->
            CareServiceDetailDialog(
                service = service,
                onDismiss = { selectedCareService = null },
                onRequestConsultation = {
                    selectedCareService = null
                    onNavigateToFacilities()
                }
            )
        }

        // Voice Assistant Modal Dialog
        if (state.isVoiceModalOpen) {
            Dialog(
                onDismissRequest = { homeViewModel.closeVoiceAssistant() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                VoiceAssistantDialog(
                    voiceAssistantManager = voiceAssistantManager,
                    onTakeMedication = { homeViewModel.markNextDoseTaken() },
                    getStepCountText = {
                        "${state.todaySteps} steps today, which is ${state.routinePercent}% of your baseline"
                    },
                    getVitalsText = {
                        val bp = state.latestVital
                        if (bp != null && bp.type == VitalsType.BLOOD_PRESSURE && bp.systolic != null) {
                            "${bp.systolic} over ${bp.diastolic} millimeters of mercury, recorded as normal"
                        } else {
                            "128 over 82 millimeters of mercury, in normal range"
                        }
                    },
                    onArmSOS = {
                        homeViewModel.closeVoiceAssistant()
                        onNavigateToSOS()
                    },
                    onOpenFacilities = {
                        homeViewModel.closeVoiceAssistant()
                        onNavigateToFacilities()
                    },
                    onClose = { homeViewModel.closeVoiceAssistant() }
                )
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: CareServiceType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("service_card_${service.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSageContainer.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = GeoMintSelected,
                border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.35f))
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = service.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimaryDark,
                    fontSize = 15.5.sp
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = service.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GeoTextSecondary,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2
            )
        }
    }
}
