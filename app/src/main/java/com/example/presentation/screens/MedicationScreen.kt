package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DoseStatus
import com.example.domain.model.PillIconType
import com.example.presentation.components.AccessibleActionButton
import com.example.presentation.components.PillIconView
import com.example.presentation.viewmodels.MedicationViewModel
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
import com.example.ui.theme.HealthSuccessGreen
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.HealthWarningAmberBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    viewModel: MedicationViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Today's Schedule, 1: Adherence History

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        // Tab Selector (Geometric Balance)
        Surface(
            color = Color.White,
            border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GeoPrimaryDark,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GeoPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "TODAY'S SCHEDULE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = if (selectedTab == 0) GeoPrimaryDark else GeoTextSecondary
                            ),
                            modifier = Modifier.padding(vertical = 14.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "7-DAY ADHERENCE (${state.adherencePercent}%)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = if (selectedTab == 1) GeoPrimaryDark else GeoTextSecondary
                            ),
                            modifier = Modifier.padding(vertical = 14.dp)
                        )
                    }
                )
            }
        }

        // Notification Banner (if any)
        state.bannerMessage?.let { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = GeoSageContainer),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GeoPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GeoPrimaryDark,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearBanner() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = GeoPrimaryDark)
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Tab 0: Today's Schedule & Reminders
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Low Supply Banners
                if (state.lowSupplyMedications.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = HealthWarningAmberBg),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, HealthWarningAmber.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = HealthWarningAmber
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "LOW SUPPLY ALERT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = HealthWarningAmber,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                state.lowSupplyMedications.forEach { med ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "• ${med.name}: ${med.remainingSupply} doses left",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = GeoTextPrimary)
                                        )
                                        TextButton(onClick = { viewModel.refillMedication(med.id, 30) }) {
                                            Text("+ REFILL (30)", color = HealthWarningAmber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // List of Active Medication Schedules
                items(state.schedules) { schedule ->
                    val isTakenToday = state.todayLogs.any { it.scheduleId == schedule.id && it.status == DoseStatus.TAKEN }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTakenToday) GeoSageContainer.copy(alpha = 0.6f) else GeoSurface
                        ),
                        border = BorderStroke(1.dp, if (isTakenToday) GeoMintSelected else GeoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PillIconView(iconType = schedule.iconType, size = 48.dp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = schedule.name,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GeoPrimaryDark,
                                                fontSize = 19.sp
                                            )
                                        )
                                        if (schedule.isCritical) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = HealthRedFlagBg,
                                                border = BorderStroke(1.dp, HealthRedFlag.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = "CRITICAL",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = HealthRedFlag,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp,
                                                        letterSpacing = 0.8.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${schedule.dosage} • ${schedule.reminderTimes.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = GeoTextSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = schedule.instructions,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GeoTextSecondary.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Supply status tag
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Supply: ${schedule.remainingSupply} doses remaining",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (schedule.isLowSupply) HealthWarningAmber else GeoTextSecondary,
                                        fontWeight = if (schedule.isLowSupply) FontWeight.Bold else FontWeight.Normal
                                    )
                                )

                                if (isTakenToday) {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = GeoMintSelected
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = GeoPrimaryDark, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "TAKEN TODAY",
                                                color = GeoPrimaryDark,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.8.sp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirmation Buttons (Taken / Snooze)
                            if (!isTakenToday) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.markDoseTaken(schedule, schedule.reminderTimes.firstOrNull() ?: "08:00")
                                        },
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Mark Taken", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.snoozeDose(schedule, schedule.reminderTimes.firstOrNull() ?: "08:00")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                                        border = BorderStroke(1.dp, GeoBorder),
                                        colors = ButtonDefaults.outlinedButtonColors(containerColor = GeoSurface)
                                    ) {
                                        Icon(imageVector = Icons.Default.Snooze, contentDescription = null, tint = GeoTextSecondary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Snooze", color = GeoTextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Add Medication CTA Button
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    AccessibleActionButton(
                        text = "+ ADD NEW MEDICATION SCHEDULE",
                        onClick = { viewModel.openAddDialog() },
                        icon = Icons.Default.Add,
                        isSecondary = true
                    )
                }
            }
        } else {
            // Tab 1: 7-Day Adherence History
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GeoSageContainer),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "7-Day Adherence Rate: ${state.adherencePercent}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = GeoPrimaryDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Consistent medication protects heart & blood sugar control.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "RECENT DOSE HISTORY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoTextSecondary,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (state.recentLogs.isEmpty()) {
                    item {
                        Text(
                            text = "No history recorded yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                        )
                    }
                } else {
                    items(state.recentLogs) { log ->
                        val (statusColor, statusBg, statusText) = when (log.status) {
                            DoseStatus.TAKEN -> Triple(GeoPrimaryDark, GeoMintSelected, "TAKEN")
                            DoseStatus.SNOOZED -> Triple(HealthWarningAmber, HealthWarningAmberBg, "SNOOZED (${log.snoozeCount}x)")
                            DoseStatus.MISSED -> Triple(HealthRedFlag, HealthRedFlagBg, "MISSED")
                            DoseStatus.SCHEDULED -> Triple(GeoPrimaryDark, GeoSageContainer, "SCHEDULED")
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = GeoSurface),
                            border = BorderStroke(1.dp, GeoBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = log.medicationName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "Scheduled at ${log.scheduledTime}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = statusBg,
                                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.8.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Medication Dialog
    if (state.isAddMedicationDialogOpen) {
        var medName by remember { mutableStateOf("") }
        var dosage by remember { mutableStateOf("") }
        var selectedPillIcon by remember { mutableStateOf(PillIconType.ROUND_WHITE) }
        var reminderTime by remember { mutableStateOf("08:00") }
        var supplyCount by remember { mutableStateOf("30") }
        var isCritical by remember { mutableStateOf(false) }
        var instructions by remember { mutableStateOf("Take with water after food") }

        AlertDialog(
            onDismissRequest = { viewModel.closeAddDialog() },
            title = {
                Text(
                    text = "Add Medication",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark
                    )
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = medName,
                            onValueChange = { medName = it },
                            label = { Text("Medication Name (e.g. Aspirin)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text("Dosage (e.g. 100 mg)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Text(
                            text = "Pill Shape & Colour Icon",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark)
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(PillIconType.values()) { iconType ->
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            width = if (selectedPillIcon == iconType) 3.dp else 1.dp,
                                            color = if (selectedPillIcon == iconType) GeoPrimary else GeoBorder,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { selectedPillIcon = iconType },
                                    contentAlignment = Alignment.Center
                                ) {
                                    PillIconView(iconType = iconType, size = 42.dp)
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = reminderTime,
                            onValueChange = { reminderTime = it },
                            label = { Text("Daily Reminder Time (e.g. 08:00, 20:00)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = supplyCount,
                            onValueChange = { supplyCount = it },
                            label = { Text("Initial Bottle Supply Count (e.g. 30)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Instructions (e.g. Take with morning meal)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (medName.isNotBlank()) {
                            viewModel.addCustomMedication(
                                name = medName.trim(),
                                dosage = if (dosage.isBlank()) "1 dose" else dosage.trim(),
                                iconType = selectedPillIcon,
                                timesPerDay = 1,
                                reminderTimes = reminderTime.split(",").map { it.trim() },
                                supply = supplyCount.toIntOrNull() ?: 30,
                                isCritical = isCritical,
                                instructions = instructions.trim()
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("SAVE SCHEDULE", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddDialog() }) {
                    Text("CANCEL", color = GeoTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                }
            }
        )
    }
}

