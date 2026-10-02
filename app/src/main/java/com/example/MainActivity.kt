package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.presentation.ManaakiApp
import com.example.presentation.viewmodels.ActivityViewModel
import com.example.presentation.viewmodels.AppointmentViewModel
import com.example.presentation.viewmodels.AuthViewModel
import com.example.presentation.viewmodels.CaregiverSyncViewModel
import com.example.presentation.viewmodels.EmergencyViewModel
import com.example.presentation.viewmodels.HomeViewModel
import com.example.presentation.viewmodels.ManaakiAppContainer
import com.example.presentation.viewmodels.MedicationViewModel
import com.example.presentation.viewmodels.SettingsViewModel
import com.example.presentation.viewmodels.VitalsViewModel
import com.example.ui.theme.ManaakiTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { ManaakiAppContainer(applicationContext) }

    private val authViewModel by viewModels<AuthViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(application, container.authUseCase) as T
            }
        }
    }

    private val homeViewModel by viewModels<HomeViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(application, container) as T
            }
        }
    }

    private val medicationViewModel by viewModels<MedicationViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MedicationViewModel(application, container) as T
            }
        }
    }

    private val vitalsViewModel by viewModels<VitalsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VitalsViewModel(application, container) as T
            }
        }
    }

    private val activityViewModel by viewModels<ActivityViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ActivityViewModel(application, container) as T
            }
        }
    }

    private val emergencyViewModel by viewModels<EmergencyViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EmergencyViewModel(application, container) as T
            }
        }
    }

    private val appointmentViewModel by viewModels<AppointmentViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AppointmentViewModel(application, container) as T
            }
        }
    }

    private val caregiverSyncViewModel by viewModels<CaregiverSyncViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CaregiverSyncViewModel(application, container) as T
            }
        }
    }

    private val settingsViewModel by viewModels<SettingsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(application, container) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsState by settingsViewModel.uiState.collectAsState()
            ManaakiTheme(darkTheme = settingsState.isDarkTheme) {
                ManaakiApp(
                    container = container,
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel,
                    medicationViewModel = medicationViewModel,
                    vitalsViewModel = vitalsViewModel,
                    activityViewModel = activityViewModel,
                    emergencyViewModel = emergencyViewModel,
                    appointmentViewModel = appointmentViewModel,
                    caregiverSyncViewModel = caregiverSyncViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

