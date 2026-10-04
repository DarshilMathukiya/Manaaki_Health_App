package com.example.presentation.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.VitalsType
import com.example.presentation.components.AccessibleActionButton
import com.example.presentation.components.UrgencyBadge
import com.example.presentation.components.VitalsPieChartCard
import com.example.presentation.viewmodels.VitalsViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VitalsScreen(
    viewModel: VitalsViewModel,
    onNavigateToSOS: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Title
        item {
            Column {
                Text(
                    text = "Vitals & Health Readings",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tracked 100% locally on your device — zero cloud required.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                )
            }
        }

        // 2. Interactive Vitals Pie Chart & Trend Breakdown
        item {
            VitalsPieChartCard(vitals = state.allVitals)
        }

        // 3. Primary Vitals Overview Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Blood Pressure Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openLogDialog(VitalsType.BLOOD_PRESSURE) },
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
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = GeoPrimary)
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = GeoMintSelected,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    "BP",
                                    color = GeoPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val bp = state.allVitals.firstOrNull { it.type == VitalsType.BLOOD_PRESSURE }
                        Text(
                            text = if (bp != null) "${bp.systolic}/${bp.diastolic}" else "126/82",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark, fontSize = 22.sp)
                        )
                        Text(text = "mmHg (Target <140/90)", style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary))
                    }
                }

                // Blood Glucose Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.openLogDialog(VitalsType.BLOOD_GLUCOSE) },
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
                            Icon(imageVector = Icons.Default.Bloodtype, contentDescription = null, tint = GeoPrimary)
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = GeoSageContainer,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    "GLUCOSE",
                                    color = GeoPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val glucose = state.allVitals.firstOrNull { it.type == VitalsType.BLOOD_GLUCOSE }
                        Text(
                            text = if (glucose != null) "${glucose.value}" else "6.4",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark, fontSize = 22.sp)
                        )
                        Text(text = "mmol/L (Target 4.0–8.0)", style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary))
                    }
                }
            }
        }

        // 3. Heart Rate Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openLogDialog(VitalsType.HEART_RATE) },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonitorHeart,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            val hr = state.allVitals.firstOrNull { it.type == VitalsType.HEART_RATE }
                            Text(
                                text = "${hr?.value?.toInt() ?: 72} bpm",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    fontSize = 22.sp
                                )
                            )
                            Text(
                                text = "Resting Pulse (Pendant BLE)",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                            )
                        }
                    }
                    UrgencyBadge(severity = AdvisorySeverity.NORMAL)
                }
            }
        }

        // 4. Record New Vital CTA
        item {
            AccessibleActionButton(
                text = "+ RECORD NEW VITAL READING",
                onClick = { viewModel.openLogDialog() },
                icon = Icons.Default.Add,
                isSecondary = true
            )
        }

        // 5. Recent Vitals Log History
        item {
            Text(
                text = "PAST READINGS LOG",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(state.allVitals) { record ->
            val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val title = when (record.type) {
                            VitalsType.BLOOD_PRESSURE -> "Blood Pressure: ${record.systolic}/${record.diastolic} mmHg"
                            VitalsType.HEART_RATE -> "Heart Rate: ${record.value.toInt()} bpm"
                            VitalsType.BLOOD_GLUCOSE -> "Blood Glucose: ${record.value} mmol/L"
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (record.isRedFlag) HealthRedFlag else GeoPrimaryDark
                            )
                        )
                        Text(
                            text = "${dateFormat.format(Date(record.recordedAt))} • ${record.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )
                    }

                    UrgencyBadge(severity = record.severity)
                }
            }
        }
    }

    // Vitals Entry Dialog
    if (state.isLogDialogOpen) {
        var selectedTypeIndex by remember {
            mutableIntStateOf(
                when (state.selectedType) {
                    VitalsType.BLOOD_PRESSURE -> 0
                    VitalsType.HEART_RATE -> 1
                    VitalsType.BLOOD_GLUCOSE -> 2
                }
            )
        }
        var systolicText by remember { mutableStateOf("128") }
        var diastolicText by remember { mutableStateOf("84") }
        var pulseText by remember { mutableStateOf("72") }
        var glucoseText by remember { mutableStateOf("6.2") }
        var notesText by remember { mutableStateOf("Routine check") }

        AlertDialog(
            onDismissRequest = { viewModel.closeLogDialog() },
            title = {
                Text(
                    text = "Record Vital Reading",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTypeIndex,
                        containerColor = Color.White,
                        contentColor = GeoPrimaryDark,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTypeIndex]),
                                color = GeoPrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTypeIndex == 0,
                            onClick = { selectedTypeIndex = 0 },
                            text = { Text("BP", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
                        )
                        Tab(
                            selected = selectedTypeIndex == 1,
                            onClick = { selectedTypeIndex = 1 },
                            text = { Text("PULSE", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
                        )
                        Tab(
                            selected = selectedTypeIndex == 2,
                            onClick = { selectedTypeIndex = 2 },
                            text = { Text("GLUCOSE", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
                        )
                    }

                    when (selectedTypeIndex) {
                        0 -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = systolicText,
                                    onValueChange = { systolicText = it },
                                    label = { Text("Systolic (top)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = diastolicText,
                                    onValueChange = { diastolicText = it },
                                    label = { Text("Diastolic (bot)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        1 -> {
                            OutlinedTextField(
                                value = pulseText,
                                onValueChange = { pulseText = it },
                                label = { Text("Heart Rate (bpm)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        2 -> {
                            OutlinedTextField(
                                value = glucoseText,
                                onValueChange = { glucoseText = it },
                                label = { Text("Blood Glucose (mmol/L)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notes (e.g. after lunch, feeling fine)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (selectedTypeIndex) {
                            0 -> viewModel.logBloodPressure(
                                systolicText.toIntOrNull() ?: 120,
                                diastolicText.toIntOrNull() ?: 80,
                                notesText
                            )
                            1 -> viewModel.logHeartRate(
                                pulseText.toIntOrNull() ?: 72,
                                notesText
                            )
                            2 -> viewModel.logGlucose(
                                glucoseText.toDoubleOrNull() ?: 6.0,
                                notesText
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("SAVE & EVALUATE", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeLogDialog() }) {
                    Text("CANCEL", color = GeoTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            }
        )
    }

    // Red Flag Alert Dialog (Deterministic Escalation)
    state.redFlagAlert?.let { alert ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissRedFlagAlert() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = HealthRedFlag,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "CRITICAL RED FLAG DETECTED",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = HealthRedFlag,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = alert.summary,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Deterministic triage safety rule triggered. Please sit down, remain calm, and seek medical attention or activate 111 emergency SOS.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissRedFlagAlert()
                        onNavigateToSOS()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRedFlag)
                ) {
                    Text("OPEN EMERGENCY SOS (111)", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRedFlagAlert() }) {
                    Text("I'M OKAY / RECHECK", color = GeoTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            }
        )
    }
}

