package com.example.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CaregiverTask
import com.example.domain.model.TaskType
import com.example.domain.model.VitalsType
import com.example.presentation.viewmodels.CaregiverContact
import com.example.presentation.viewmodels.CaregiverSyncViewModel
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
import com.example.ui.theme.HealthSuccessGreenBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: CaregiverSyncViewModel,
    onLogoutRequested: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    var showMedicalIdDialog by remember { mutableStateOf(false) }
    var showHealthHistoryDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        containerColor = GeoBackground,
        modifier = modifier.fillMaxSize().testTag("account_screen_scaffold")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("account_screen_scroll"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // PROFILE HEADER
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("account_profile_header_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(GeoSageContainer)
                                        .border(2.dp, GeoPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Photo",
                                        tint = GeoPrimaryDark,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = state.userName,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimaryDark,
                                            fontSize = 19.sp
                                        )
                                    )
                                    Text(
                                        text = state.userEmail,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GeoTextSecondary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GeoMintSelected,
                                        border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "NHI: ${state.userNhiNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GeoPrimaryDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.openEditProfileDialog() },
                                modifier = Modifier.testTag("edit_profile_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Sync banner notification
                        state.syncSuccessBanner?.let { banner ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HealthSuccessGreenBg,
                                border = BorderStroke(1.dp, HealthSuccessGreen.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = banner,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = HealthSuccessGreen,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearSyncBanner() },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Check, null, tint = HealthSuccessGreen, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Caregiver Mode Active Sticky Banner (if mode active)
            if (state.isCaregiverModeActive) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("caregiver_mode_active_banner"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = GeoMintSelected),
                        border = BorderStroke(2.dp, GeoPrimary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = GeoPrimaryDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "CAREGIVER / ADMIN MODE",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GeoPrimaryDark,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        Text(
                                            text = "Logged in as: ${state.activeCaregiver?.name ?: "Caregiver"}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = GeoPrimaryDark,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.exitCaregiverMode() },
                                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimaryDark),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("exit_caregiver_mode_button")
                                ) {
                                    Text("EXIT MODE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // GROUP 1: CARE & SUPPORT
            // ==========================================
            item {
                AccountGroupHeader(
                    title = "CARE & SUPPORT",
                    subtitle = "Whānau links, task coordination, history & GP reports"
                )
            }

            // 1.1 Linked Caregivers / Patients Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("linked_caregivers_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, null, tint = GeoPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Linked Caregivers & Support",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoPrimaryDark
                                    )
                                )
                            }

                            TextButton(
                                onClick = { viewModel.openAddCaregiverDialog() },
                                modifier = Modifier.testTag("add_caregiver_button")
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add", fontWeight = FontWeight.Bold, color = GeoPrimary)
                            }
                        }

                        Text(
                            text = "Whānau members and clinical guardians with authorized access to your care.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        state.caregivers.forEach { caregiver ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = GeoBackground,
                                border = BorderStroke(1.dp, if (caregiver.isPrimary) GeoPrimary.copy(alpha = 0.5f) else GeoBorder.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("caregiver_row_${caregiver.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (caregiver.isPrimary) GeoMintSelected else GeoSageContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (caregiver.isPrimary) Icons.Default.Star else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = GeoPrimaryDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = caregiver.name,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = GeoPrimaryDark
                                                    )
                                                )
                                                if (caregiver.isPrimary) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(shape = RoundedCornerShape(4.dp), color = GeoMintSelected) {
                                                        Text("PRIMARY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GeoPrimaryDark, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                }
                                            }
                                            Text(
                                                text = "${caregiver.relationship} • ${caregiver.phoneNumber}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 11.sp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${caregiver.phoneNumber.replace(" ", "")}")
                                            }
                                            context.startActivity(dialIntent)
                                        },
                                        modifier = Modifier.size(32.dp).testTag("call_caregiver_${caregiver.id}")
                                    ) {
                                        Icon(Icons.Default.Call, "Call ${caregiver.name}", tint = GeoPrimary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 1.2 Task Center Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("task_center_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Assignment, null, tint = GeoPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Task Center",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoPrimaryDark
                                    )
                                )
                            }

                            Button(
                                onClick = { viewModel.openAssignTaskDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("account_assign_task_button")
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ASSIGN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "Tasks and reminders coordinated between you and your whānau.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (state.assignedTasks.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GeoBackground,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No active tasks. Tap 'Assign' to schedule a dose, GP visit, or check-in reminder.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.assignedTasks.forEach { task ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (task.isCompleted) GeoSageContainer.copy(alpha = 0.4f) else GeoBackground,
                                        border = BorderStroke(1.dp, if (task.isCompleted) HealthSuccessGreen.copy(alpha = 0.4f) else GeoBorder.copy(alpha = 0.6f)),
                                        modifier = Modifier.fillMaxWidth().testTag("account_task_${task.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                IconButton(
                                                    onClick = { viewModel.toggleTaskCompleted(task) },
                                                    modifier = Modifier.size(24.dp).testTag("account_toggle_task_${task.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                        contentDescription = "Toggle completion",
                                                        tint = if (task.isCompleted) HealthSuccessGreen else GeoTextSecondary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = task.title,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (task.isCompleted) GeoTextSecondary else GeoPrimaryDark
                                                        )
                                                    )
                                                    Text(
                                                        text = "${task.details} • Due: ${task.dueTime}",
                                                        style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 11.sp)
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteTask(task.id) },
                                                modifier = Modifier.size(28.dp).testTag("account_delete_task_${task.id}")
                                            ) {
                                                Icon(Icons.Default.Delete, "Delete task", tint = GeoTextSecondary.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 1.3 Activity & Health History Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("health_history_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null, tint = GeoPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Activity & Health History",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoPrimaryDark
                                    )
                                )
                            }
                            TextButton(
                                onClick = { showHealthHistoryDialog = true },
                                modifier = Modifier.testTag("view_history_button")
                            ) {
                                Text("View All", fontWeight = FontWeight.Bold, color = GeoPrimary)
                            }
                        }

                        Text(
                            text = "Historical vitals logs, medication compliance trends, and mobility logs.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GeoBackground,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Med Adherence", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 10.sp))
                                    Text("${state.seniorMedicationAdherence}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = HealthSuccessGreen))
                                    Text("7-day rolling", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 9.sp))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GeoBackground,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Steps Today", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 10.sp))
                                    Text("${state.seniorTodaySteps}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark))
                                    Text("Avg: ${state.seniorBaselineSteps}", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 9.sp))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GeoBackground,
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Vitals Status", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 10.sp))
                                    Text("STABLE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark, fontSize = 14.sp))
                                    Text("No red flags", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 9.sp))
                                }
                            }
                        }
                    }
                }
            }

            // 1.4 Health Reports for GP Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("health_reports_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GeoMintSelected),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, null, tint = GeoPrimaryDark, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Clinical Health Reports (PDF)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "Vitals logs & medication adherence audit",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 11.sp)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GeoSageContainer,
                                border = BorderStroke(1.dp, GeoBorder)
                            ) {
                                Text(
                                    text = "HIPC 2020",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoPrimaryDark,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Compiles 30-day continuous vitals telemetry (blood pressure, heart rate, fasting glucose) and medication logs into a certified clinical PDF for your GP.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextPrimary, lineHeight = 17.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Period Selector Chips
                        Text(
                            text = "Select Reporting Period:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Past 30 Days", "Current Calendar Month", "Previous Month").forEach { period ->
                                FilterChip(
                                    selected = state.selectedReportPeriod == period,
                                    onClick = { viewModel.setReportPeriod(period) },
                                    label = { Text(period, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GeoMintSelected,
                                        selectedLabelColor = GeoPrimaryDark
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live Compiled Metrics Preview Bar
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GeoBackground,
                            border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Med Adherence", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 10.sp))
                                    Text("${state.seniorMedicationAdherence}%", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = HealthSuccessGreen))
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(GeoBorder))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Vitals Logs", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 10.sp))
                                    Text("30+ Readings", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark))
                                }
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(GeoBorder))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Format", style = MaterialTheme.typography.labelSmall.copy(color = GeoTextSecondary, fontSize = 10.sp))
                                    Text("2-Page PDF", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimaryDark))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Generate / Convert Report into PDF Button
                        Button(
                            onClick = { viewModel.generateMonthlyPdfReport(state.selectedReportPeriod) },
                            enabled = !state.isGeneratingPdf,
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimaryDark),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("export_gp_report_button")
                        ) {
                            if (state.isGeneratingPdf) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("CONVERTING REPORT INTO PDF...", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                            } else {
                                Icon(Icons.Default.PictureAsPdf, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CONVERT REPORT INTO PDF", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, letterSpacing = 0.8.sp, color = Color.White)
                            }
                        }

                        // Direct Quick Actions if PDF already generated
                        if (state.generatedPdfFile != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.viewGeneratedPdf(context) },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, GeoPrimary),
                                    modifier = Modifier.weight(1f).height(40.dp).testTag("quick_view_pdf_button")
                                ) {
                                    Icon(Icons.Default.Visibility, null, tint = GeoPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open PDF", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.shareGeneratedPdf(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimaryDark),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(40.dp).testTag("quick_share_pdf_button")
                                ) {
                                    Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share with GP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // GROUP 2: ACCOUNT
            // ==========================================
            item {
                AccountGroupHeader(
                    title = "ACCOUNT",
                    subtitle = "Profile information, Emergency Medical ID & Mode switching"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("account_group_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // 2.1 Edit Profile
                        AccountActionRow(
                            icon = Icons.Default.Person,
                            title = "Edit Senior Profile",
                            subtitle = "Phone: ${state.userPhoneNumber} • Address: ${state.userAddress.take(24)}…",
                            onClick = { viewModel.openEditProfileDialog() },
                            testTag = "account_edit_profile_item"
                        )

                        // 2.2 Emergency & Medical ID
                        AccountActionRow(
                            icon = Icons.Default.Badge,
                            title = "Emergency & Medical ID",
                            subtitle = "NHI ${state.userNhiNumber} • Blood O+ • Allergies & Pacemaker",
                            onClick = { showMedicalIdDialog = true },
                            testTag = "account_medical_id_item"
                        )

                        // 2.3 Switch / Exit Caregiver Mode
                        AccountActionRow(
                            icon = Icons.Default.AdminPanelSettings,
                            title = if (state.isCaregiverModeActive) "Exit Caregiver Mode" else "Switch to Caregiver Mode",
                            subtitle = if (state.isCaregiverModeActive) "Return to ${state.userName}'s patient dashboard" else "Switch active view to an authorized whānau caregiver",
                            onClick = {
                                if (state.isCaregiverModeActive) {
                                    viewModel.exitCaregiverMode()
                                } else {
                                    val primary = state.caregivers.firstOrNull { it.isPrimary } ?: state.caregivers.firstOrNull()
                                    if (primary != null) {
                                        viewModel.openSwitchToCaregiverDialog(primary)
                                    }
                                }
                            },
                            testTag = "account_toggle_caregiver_mode_item"
                        )
                    }
                }
            }

            // ==========================================
            // GROUP 3: LOGOUT
            // ==========================================
            item {
                AccountGroupHeader(
                    title = "SESSION",
                    subtitle = "Sign out of Manaaki Health on this device"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("account_logout_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Logging out securely saves all offline medical and vitals records in encrypted storage.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onLogoutRequested,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("account_screen_logout_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HealthRedFlag.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = HealthRedFlagBg.copy(alpha = 0.15f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Log Out",
                                tint = HealthRedFlag,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOG OUT OF ACCOUNT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = HealthRedFlag,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOGS
    // ==========================================

    // 1. Export Report Dialog (PDF & Telemetry Summary)
    if (state.isExportDialogOpen) {
        val pdfFile = state.generatedPdfFile
        AlertDialog(
            onDismissRequest = { viewModel.closeExportDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GeoMintSelected),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = GeoPrimaryDark, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Clinical PDF Report Ready",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark
                            )
                        )
                        Text(
                            text = state.selectedReportPeriod,
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 11.sp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Document Badge & Summary Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GeoSageContainer,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2-PAGE CLINICAL TELEMETRY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoPrimaryDark,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "A4 FORMAT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GeoTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Text(
                                text = "• Patient: ${state.userName} (NHI: ${state.userNhiNumber})",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoPrimaryDark, fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "• 30-Day Medication Adherence: ${state.seniorMedicationAdherence}% (3 Regimens)",
                                style = MaterialTheme.typography.bodySmall.copy(color = HealthSuccessGreen, fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "• On-Device Vitals Log: Blood Pressure, Heart Rate, Glucose",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextPrimary)
                            )
                            Text(
                                text = "• Clinical Attestation & GP Sign-off block included",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                            )
                            if (pdfFile != null) {
                                Text(
                                    text = "File: ${pdfFile.name.take(32)}… (${pdfFile.length() / 1024} KB)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = GeoTextSecondary
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "The PDF is compiled offline and stored in app storage in compliance with the New Zealand Health Information Privacy Code 2020.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.viewGeneratedPdf(context) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GeoPrimary),
                        modifier = Modifier.testTag("dialog_view_pdf_button")
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = GeoPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open PDF", color = GeoPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.shareGeneratedPdf(context)
                            viewModel.closeExportDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("dialog_share_pdf_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share with GP", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeExportDialog() }) {
                    Text("Close", color = GeoTextSecondary)
                }
            }
        )
    }

    // 2. Add Caregiver Dialog
    if (state.isAddCaregiverDialogOpen) {
        var name by remember { mutableStateOf("") }
        var relationship by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var isPrimary by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.closeAddCaregiverDialog() },
            title = {
                Text(
                    text = "Add Caregiver or GP Contact",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        label = { Text("Relationship (e.g. Son, Nurse, GP)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text("Set as Primary Emergency Contact", style = MaterialTheme.typography.bodySmall)
                        Switch(
                            checked = isPrimary,
                            onCheckedChange = { isPrimary = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            viewModel.addCaregiver(name, relationship, phone, isPrimary, receivesRedFlags = true)
                        }
                    },
                    enabled = name.isNotBlank() && phone.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("Add Contact")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddCaregiverDialog() }) {
                    Text("Cancel", color = GeoTextSecondary)
                }
            }
        )
    }

    // 3. Edit Profile Dialog
    if (state.isEditProfileDialogOpen) {
        var phone by remember { mutableStateOf(state.userPhoneNumber) }
        var address by remember { mutableStateOf(state.userAddress) }
        var notes by remember { mutableStateOf(state.emergencyNotes) }

        AlertDialog(
            onDismissRequest = { viewModel.closeEditProfileDialog() },
            title = {
                Text(
                    text = "Edit Senior Profile",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Home Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Medical & Allergy Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveProfile(phone, address, notes) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeEditProfileDialog() }) {
                    Text("Cancel", color = GeoTextSecondary)
                }
            }
        )
    }

    // 4. Switch to Caregiver Mode Confirmation Dialog
    if (state.isCaregiverModeSwitchDialogOpen && state.selectedCaregiverForSwitch != null) {
        val selected = state.selectedCaregiverForSwitch!!
        AlertDialog(
            onDismissRequest = { viewModel.closeSwitchToCaregiverDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Switch to Caregiver Mode",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "You are switching to Caregiver Mode as:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GeoMintSelected,
                        border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = selected.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark
                                )
                            )
                            Text(
                                text = "${selected.relationship} • ${selected.phoneNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                            )
                        }
                    }
                    Text(
                        text = "This mode authorizes you to view ${state.userName}'s vital health records and assign medication/clinic tasks directly to their daily schedule.",
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextPrimary, lineHeight = 18.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.enterCaregiverMode(selected) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_enter_caregiver_mode_button")
                ) {
                    Text("ENTER CAREGIVER MODE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeSwitchToCaregiverDialog() }) {
                    Text("Cancel", color = GeoTextSecondary)
                }
            }
        )
    }

    // 5. Assign Caregiver Task Dialog
    if (state.isAssignTaskDialogOpen) {
        var selectedType by remember { mutableStateOf(TaskType.MEDICATION) }
        var title by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }
        var dueTime by remember { mutableStateOf("08:00 AM") }

        AlertDialog(
            onDismissRequest = { viewModel.closeAssignTaskDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Assign Patient Task / Reminder",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Select Task Category:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TaskType.values().take(2).forEach { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    label = { Text(type.displayName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GeoMintSelected,
                                        selectedLabelColor = GeoPrimaryDark
                                    )
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TaskType.values().drop(2).forEach { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    label = { Text(type.displayName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GeoMintSelected,
                                        selectedLabelColor = GeoPrimaryDark
                                    )
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = {
                                Text(
                                    when (selectedType) {
                                        TaskType.MEDICATION -> "Medication Name (e.g. Paracetamol 500mg)"
                                        TaskType.APPOINTMENT -> "Clinic / Doctor Visit Title"
                                        TaskType.CHECK_IN -> "Check-in Call Subject"
                                        TaskType.CUSTOM -> "Reminder Title"
                                    }
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("assign_task_title_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("Instructions / Dosage / Notes") },
                            modifier = Modifier.fillMaxWidth().testTag("assign_task_details_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = dueTime,
                            onValueChange = { dueTime = it },
                            label = { Text("Scheduled Time (e.g. 08:30 AM)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("assign_task_due_time_input")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.assignTask(selectedType, title, details, dueTime)
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("submit_assign_task_button")
                ) {
                    Text("ASSIGN & DISPATCH", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAssignTaskDialog() }) {
                    Text("Cancel", color = GeoTextSecondary)
                }
            }
        )
    }

    // 6. Emergency & Medical ID Dialog
    if (showMedicalIdDialog) {
        AlertDialog(
            onDismissRequest = { showMedicalIdDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, null, tint = GeoPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Emergency Medical ID", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Transmitted securely to 111 paramedics and emergency responders upon SOS activation:",
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GeoSageContainer,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Name: ${state.userName}", fontWeight = FontWeight.Bold, color = GeoPrimaryDark)
                            Text("NHI Number: ${state.userNhiNumber}", fontWeight = FontWeight.SemiBold, color = GeoPrimaryDark)
                            Text("DOB: 14 May 1948 (Age: 78) • Blood Type: O+", style = MaterialTheme.typography.bodySmall)
                            Text("Allergies: Penicillin", style = MaterialTheme.typography.bodySmall, color = HealthRedFlag, fontWeight = FontWeight.Bold)
                            Text("Medical Device: Cardiac Pacemaker (2021)", style = MaterialTheme.typography.bodySmall)
                            Text("Primary Caregiver: ${state.caregivers.firstOrNull { it.isPrimary }?.let { "${it.name} (${it.phoneNumber})" } ?: "David Te Aroha"}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMedicalIdDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("DONE", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 7. Health History Summary Dialog
    if (showHealthHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHealthHistoryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, tint = GeoPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Health & Vitals History", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Summary of local on-device telemetry logs for ${state.userName}:",
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GeoBackground,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("• 7-Day Medication Adherence: ${state.seniorMedicationAdherence}%", fontWeight = FontWeight.SemiBold, color = HealthSuccessGreen)
                            Text("• Today's Steps: ${state.seniorTodaySteps} (7-day baseline: ${state.seniorBaselineSteps})", fontWeight = FontWeight.Medium)
                            Text("• Latest Vitals: BP 124/80 mmHg (Normal, within baseline)", fontWeight = FontWeight.Medium)
                            Text("• Routine Deviation Index: 0.90 (Optimal routine integrity)", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHealthHistoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CLOSE", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ---------------- Helper Components for Account ----------------

@Composable
private fun AccountGroupHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimaryDark,
                letterSpacing = 1.sp,
                fontSize = 12.sp
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = GeoTextSecondary,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun AccountActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = GeoBackground,
        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GeoSageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}
