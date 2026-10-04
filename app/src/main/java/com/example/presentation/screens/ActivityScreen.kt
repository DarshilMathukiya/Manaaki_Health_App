package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.NearbyCareCard
import com.example.presentation.viewmodels.ActivityViewModel
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
fun ActivityScreen(
    viewModel: ActivityViewModel,
    onNavigateToSOS: () -> Unit,
    onNavigateToFacilities: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val progress = (state.todaySteps.toFloat() / state.baselineAverageSteps.toFloat()).coerceIn(0f, 1.5f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Title
        item {
            Column {
                Text(
                    text = "Activity & Routine Monitor",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compared against your personal 7-day rolling baseline.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                )
            }
        }

        // 2. Fall Alert & Countdown Dialog / Banner (if triggered)
        if (state.isFallDetected || state.isCountdownActive) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("fall_detection_alert_card"),
                    colors = CardDefaults.cardColors(containerColor = HealthRedFlagBg),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(2.dp, HealthRedFlag)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = HealthRedFlag, modifier = Modifier.size(26.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "FALL DETECTED — SOS COUNTDOWN",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = HealthRedFlag,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                            if (state.isCountdownActive) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HealthRedFlag
                                ) {
                                    Text(
                                        text = "${state.countdownSeconds}s",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Wearable accelerometer registered sudden free-fall followed by impact (${state.lastFallPeakG}g). Emergency contacts will receive your GPS coordinates automatically when countdown expires.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextPrimary, lineHeight = 20.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ML Verification & Severity pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GeoSurface,
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "On-Device ML Confidence: ${(state.lastFallConfidence * 100).toInt()}% • Severity: ${state.fallSeverity}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GeoPrimaryDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onNavigateToSOS,
                                colors = ButtonDefaults.buttonColors(containerColor = HealthRedFlag),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f).testTag("call_sos_immediate_button")
                            ) {
                                Text("CALL 111 SOS", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.dismissFall() },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, GeoPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = GeoSurface),
                                modifier = Modifier.weight(1f).testTag("dismiss_fall_safe_button")
                            ) {
                                Text("I AM SAFE", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Step Progress Dial vs 7-Day Baseline
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(170.dp),
                            color = GeoSageContainer,
                            strokeWidth = 14.dp
                        )
                        CircularProgressIndicator(
                            progress = { progress.coerceAtMost(1f) },
                            modifier = Modifier.size(170.dp),
                            color = if (progress >= 0.7f) GeoPrimary else Color(0xFFD97706),
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.todaySteps}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    fontSize = 36.sp
                                )
                            )
                            Text(
                                text = "TODAY'S STEPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.baselineAverageSteps}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark)
                            )
                            Text(
                                text = "7-Day Baseline",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${((state.todaySteps.toFloat() / state.baselineAverageSteps) * 100).toInt()}%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GeoPrimary)
                            )
                            Text(
                                text = "Routine Ratio",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                            )
                        }
                    }
                }
            }
        }

        // 4. Biomechanical Gait & Sedentary Metrics
        item {
            Text(
                text = "MOBILITY & CADENCE BREAKDOWN",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = GeoPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.gaitCadence.toInt()} spm",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark)
                        )
                        Text(text = "Gait Cadence (Stable)", style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.sedentaryMinutes / 60}h ${state.sedentaryMinutes % 60}m",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark)
                        )
                        Text(text = "Resting / Seated", style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary))
                    }
                }
            }
        }

        // 5. Sensor Simulation & Testing Actions
        item {
            Text(
                text = "SENSOR INGESTION CONTROLS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.addWalkingSteps() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GeoBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = GeoSurface)
                ) {
                    Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WALK (+250)", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.triggerFallSimulation() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, HealthRedFlag.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = HealthRedFlagBg)
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = HealthRedFlag)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SIMULATE FALL", color = HealthRedFlag, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            }
        }

        // 6. Nearby Care & Registered GP Teaser with Booking Action
        item {
            NearbyCareCard(
                facility = state.registeredGP ?: state.nearestFacility,
                nudgeMessage = state.contextualNudgeMessage,
                onFindAndBookClick = onNavigateToFacilities
            )
        }
    }
}

