package com.example.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.DatabaseInitializer
import com.example.domain.model.SOSTrigger
import com.example.domain.model.SampleData
import com.example.presentation.components.LogoutConfirmDialog
import com.example.presentation.components.PersistentSOSFab
import com.example.presentation.components.SideNavDrawerSheet
import com.example.presentation.screens.AccountScreen
import com.example.presentation.screens.ActivityScreen
import com.example.presentation.screens.CaregiverSyncScreen
import com.example.presentation.screens.EmergencySOSScreen
import com.example.presentation.screens.HealthFacilitiesScreen
import com.example.presentation.screens.HomeScreen
import com.example.presentation.screens.MedicationScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.screens.VitalsScreen
import com.example.presentation.screens.auth.LoginScreen
import com.example.presentation.screens.auth.RegisterScreen
import com.example.presentation.viewmodels.ActivityViewModel
import com.example.presentation.viewmodels.AppointmentViewModel
import com.example.presentation.viewmodels.AuthScreenDestination
import com.example.presentation.viewmodels.AuthState
import com.example.presentation.viewmodels.AuthViewModel
import com.example.presentation.viewmodels.CaregiverSyncViewModel
import com.example.presentation.viewmodels.EmergencyViewModel
import com.example.presentation.viewmodels.HomeViewModel
import com.example.presentation.viewmodels.LoginRole
import com.example.presentation.viewmodels.ManaakiAppContainer
import com.example.presentation.viewmodels.MedicationViewModel
import com.example.presentation.viewmodels.SettingsViewModel
import com.example.presentation.viewmodels.VitalsViewModel
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextSecondary
import kotlinx.coroutines.launch

enum class ManaakiNavDestination(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    MEDICATION("Meds", Icons.Default.Medication),
    VITALS("Vitals", Icons.Default.Favorite),
    ACTIVITY("Activity", Icons.AutoMirrored.Filled.DirectionsWalk),
    CARE("Care", Icons.Default.LocalHospital)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManaakiApp(
    container: ManaakiAppContainer,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    medicationViewModel: MedicationViewModel,
    vitalsViewModel: VitalsViewModel,
    activityViewModel: ActivityViewModel,
    emergencyViewModel: EmergencyViewModel,
    appointmentViewModel: AppointmentViewModel,
    caregiverSyncViewModel: CaregiverSyncViewModel,
    settingsViewModel: SettingsViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val currentAuthScreen by authViewModel.currentScreen.collectAsState()
    val showLogoutSheet by authViewModel.showLogoutSheet.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var navBackStack by remember { mutableStateOf(listOf(0)) }
    var showSOSModal by remember { mutableStateOf(false) }
    var showFacilitiesModal by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showAccountModal by remember { mutableStateOf(false) }

    fun navigateToTab(targetIndex: Int) {
        if (selectedIndex != targetIndex) {
            navBackStack = navBackStack.filter { it != targetIndex } + targetIndex
            selectedIndex = targetIndex
        }
    }

    fun navigateBack() {
        if (navBackStack.size > 1) {
            val updated = navBackStack.dropLast(1)
            navBackStack = updated
            selectedIndex = updated.last()
        } else {
            selectedIndex = 0
            navBackStack = listOf(0)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Hardware/System Back Handler for hierarchical navigation
    BackHandler(enabled = showFacilitiesModal || showSOSModal || showSettingsScreen || showAccountModal || drawerState.isOpen || selectedIndex != 0) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (showFacilitiesModal) {
            showFacilitiesModal = false
        } else if (showSOSModal) {
            showSOSModal = false
        } else if (showSettingsScreen) {
            showSettingsScreen = false
        } else if (showAccountModal) {
            showAccountModal = false
        } else if (selectedIndex != 0) {
            navigateBack()
        }
    }

    LaunchedEffect(Unit) {
        DatabaseInitializer.populateInitialDataIfEmpty(container.database)
    }

    // Role-based post-login navigation effect
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val authenticatedState = authState as AuthState.Authenticated
            if (authenticatedState.targetRole == LoginRole.CAREGIVER_ADMIN) {
                showAccountModal = true
                val syncState = caregiverSyncViewModel.uiState.value
                val primaryCaregiver = syncState.caregivers.firstOrNull { it.isPrimary }
                    ?: syncState.caregivers.firstOrNull()
                if (primaryCaregiver != null && !syncState.isCaregiverModeActive) {
                    caregiverSyncViewModel.enterCaregiverMode(primaryCaregiver)
                }
            }
        }
    }

    // 1. Initializing State (Auto session restoration check)
    when (authState) {
        is AuthState.Initializing -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GeoBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = GeoMintSelected,
                        border = BorderStroke(2.dp, GeoPrimaryDark)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "M",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimaryDark,
                                    fontSize = 36.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "MANAAKI HEALTH",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark,
                            letterSpacing = 2.sp
                        )
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = GeoPrimary,
                        strokeWidth = 3.dp
                    )
                }
            }
        }

        // 2. Unauthenticated State (Login or Register Flow)
        is AuthState.Unauthenticated -> {
            when (currentAuthScreen) {
                is AuthScreenDestination.Welcome,
                is AuthScreenDestination.Login -> {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateBack = null
                    )
                }
                is AuthScreenDestination.Register -> {
                    RegisterScreen(viewModel = authViewModel)
                }
            }
        }

        // 3. Authenticated State (Main App with Drawer, Header & Bottom Bar)
        is AuthState.Authenticated -> {
            val displayName = SampleData.PATIENT_FIRST_NAME

            if (showSettingsScreen) {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    caregiverSyncViewModel = caregiverSyncViewModel,
                    onBack = { showSettingsScreen = false }
                )
            } else if (showAccountModal) {
                AccountScreen(
                    viewModel = caregiverSyncViewModel,
                    onLogoutRequested = { authViewModel.requestLogout() },
                    onNavigateToSettings = {
                        showAccountModal = false
                        showSettingsScreen = true
                    },
                    onBack = { showAccountModal = false }
                )
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        SideNavDrawerSheet(
                            userName = displayName,
                            userNhi = SampleData.PATIENT_NHI,
                            caregiverName = "${SampleData.GP_NAME} (${SampleData.GP_ROLE})",
                            onNavigateToProfile = {
                                coroutineScope.launch { drawerState.close() }
                                showAccountModal = true
                            },
                            onNavigateToCaregivers = {
                                coroutineScope.launch { drawerState.close() }
                                showAccountModal = true
                            },
                            onNavigateToHealthHistory = {
                                coroutineScope.launch { drawerState.close() }
                                showFacilitiesModal = true
                            },
                            onNavigateToMedication = {
                                coroutineScope.launch { drawerState.close() }
                                selectedIndex = 1
                            },
                            onNavigateToSettings = {
                                coroutineScope.launch { drawerState.close() }
                                showSettingsScreen = true
                            },
                            onLogoutRequested = {
                                coroutineScope.launch { drawerState.close() }
                                authViewModel.requestLogout()
                            }
                        )
                    }
                ) {
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                navigationIcon = {
                                    if (selectedIndex == 0) {
                                        // Root Home Screen: Left Hamburger Menu Button
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                                }
                                            },
                                            modifier = Modifier
                                                .padding(start = 6.dp)
                                                .testTag("app_bar_hamburger_menu")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = "Open Navigation Menu",
                                                tint = GeoPrimaryDark,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    } else {
                                        // Consistent 'Back' button for non-root screens / tabs
                                        IconButton(
                                            onClick = { navigateBack() },
                                            modifier = Modifier
                                                .padding(start = 6.dp)
                                                .testTag("app_bar_back_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back to Home",
                                                tint = GeoPrimaryDark,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }
                                },
                                title = {
                                    val screenTitle = when (selectedIndex) {
                                        0 -> "Kia Ora, $displayName"
                                        1 -> "Medication Schedule"
                                        2 -> "Vitals Monitor"
                                        3 -> "Activity & Routine"
                                        4 -> "Care & Health Records"
                                        else -> "Kia Ora, $displayName"
                                    }
                                    Text(
                                        text = screenTitle,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimaryDark,
                                            fontSize = 20.sp
                                        ),
                                        modifier = Modifier.testTag("app_bar_greeting_title")
                                    )
                                },
                                actions = {
                                    // 1. TOP APP BAR: Right Section (Dark Mode Toggle & Profile Avatar)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        // Dark Mode Toggle Button
                                        IconButton(
                                            onClick = { settingsViewModel.toggleDarkTheme(!settingsState.isDarkTheme) },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .testTag("app_bar_dark_mode_toggle")
                                        ) {
                                            Icon(
                                                imageVector = if (settingsState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = if (settingsState.isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                                                tint = GeoPrimaryDark,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Profile Avatar Image (Circular crop with subtle border)
                                        Surface(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .clickable { showAccountModal = true }
                                                .border(1.5.dp, GeoPrimary, CircleShape)
                                                .testTag("app_bar_profile_avatar"),
                                            shape = CircleShape,
                                            color = GeoMintSelected
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = displayName.take(1).uppercase(),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = GeoPrimaryDark,
                                                        fontSize = 16.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = GeoBackground
                                )
                            )
                        },
                        bottomBar = {
                            // =========================================================================
                            // 4. BOTTOM NAVIGATION BAR (5 TABS: Home, Meds, Vitals, Activity, Care)
                            // =========================================================================
                            Surface(
                                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)),
                                color = GeoSurface
                            ) {
                                NavigationBar(
                                    containerColor = GeoSurface,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.height(72.dp)
                                ) {
                                    ManaakiNavDestination.entries.forEachIndexed { index, destination ->
                                        val isSelected = selectedIndex == index

                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { navigateToTab(index) },
                                            icon = {
                                                Icon(
                                                    imageVector = destination.icon,
                                                    contentDescription = destination.label,
                                                    tint = if (isSelected) GeoPrimaryDark else GeoTextSecondary.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = destination.label.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp,
                                                        letterSpacing = 0.8.sp,
                                                        color = if (isSelected) GeoPrimaryDark else GeoTextSecondary.copy(alpha = 0.7f)
                                                    )
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = GeoPrimaryDark,
                                                selectedTextColor = GeoPrimaryDark,
                                                indicatorColor = GeoMintSelected
                                            ),
                                            modifier = Modifier.testTag("bottom_nav_${destination.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GeoBackground)
                                .padding(innerPadding)
                        ) {
                            when (selectedIndex) {
                                0 -> HomeScreen(
                                    homeViewModel = homeViewModel,
                                    medicationViewModel = medicationViewModel,
                                    voiceAssistantManager = container.voiceAssistantManager,
                                    onNavigateToMedication = { navigateToTab(1) },
                                    onNavigateToVitals = { navigateToTab(2) },
                                    onNavigateToActivity = { navigateToTab(3) },
                                    onNavigateToAccount = { navigateToTab(4) },
                                    onNavigateToFacilities = { showFacilitiesModal = true },
                                    onNavigateToSOS = { showSOSModal = true }
                                )
                                1 -> MedicationScreen(
                                    viewModel = medicationViewModel
                                )
                                2 -> VitalsScreen(
                                    viewModel = vitalsViewModel,
                                    onNavigateToSOS = { showSOSModal = true }
                                )
                                3 -> ActivityScreen(
                                    viewModel = activityViewModel,
                                    onNavigateToSOS = { showSOSModal = true },
                                    onNavigateToFacilities = { showFacilitiesModal = true }
                                )
                                4 -> {
                                    CaregiverSyncScreen(
                                        viewModel = caregiverSyncViewModel,
                                        onLogoutRequested = { authViewModel.requestLogout() },
                                        onNavigateToSettings = { showSettingsScreen = true }
                                    )
                                }
                            }

                            // Persistent Floating Action Button (SOS)
                            PersistentSOSFab(
                                onClick = { showSOSModal = true },
                                onEmergencyTriggered = {
                                    emergencyViewModel.triggerSOS(SOSTrigger.MANUAL)
                                    showSOSModal = true
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, bottom = 16.dp)
                            )
                        }
                    }
                }
            }

            // NZ Health Facilities & Appointment Booking Modal / Screen
            if (showFacilitiesModal) {
                Dialog(
                    onDismissRequest = { showFacilitiesModal = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = GeoBackground
                    ) {
                        HealthFacilitiesScreen(
                            emergencyViewModel = emergencyViewModel,
                            appointmentViewModel = appointmentViewModel,
                            onClose = { showFacilitiesModal = false }
                        )
                    }
                }
            }

            // Emergency SOS Full-Screen / Modal Dialog
            if (showSOSModal) {
                Dialog(
                    onDismissRequest = { showSOSModal = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = GeoBackground
                    ) {
                        EmergencySOSScreen(
                            viewModel = emergencyViewModel,
                            appointmentViewModel = appointmentViewModel,
                            onClose = { showSOSModal = false }
                        )
                    }
                }
            }

            // Mandatory Logout Confirmation Dialog
            if (showLogoutSheet) {
                LogoutConfirmDialog(
                    onConfirmLogout = {
                        showAccountModal = false
                        authViewModel.confirmLogout()
                    },
                    onDismiss = { authViewModel.dismissLogoutSheet() }
                )
            }
        }
    }
}
