package com.example.presentation.voice

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceAssistantDialog(
    voiceAssistantManager: VoiceAssistantManager,
    onTakeMedication: () -> Unit,
    getStepCountText: () -> String,
    getVitalsText: () -> String,
    onArmSOS: () -> Unit,
    onOpenFacilities: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isListening by voiceAssistantManager.isListening.collectAsState()
    val transcript by voiceAssistantManager.spokenTranscript.collectAsState()
    val response by voiceAssistantManager.assistantResponse.collectAsState()
    val lastAction by voiceAssistantManager.lastActionType.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            voiceAssistantManager.stopListening()
            voiceAssistantManager.stopSpeaking()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("voice_assistant_modal"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        border = BorderStroke(1.5.dp, GeoBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GeoSageContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = GeoPrimaryDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "VOICE ASSISTANT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Manaaki Voice Command",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("voice_assistant_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close voice assistant",
                        tint = GeoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Microphone Action Center
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(GeoPrimary.copy(alpha = 0.15f))
                    )
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .scale(pulseScale * 0.9f)
                            .clip(CircleShape)
                            .background(GeoPrimary.copy(alpha = 0.25f))
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (isListening) {
                                voiceAssistantManager.stopListening()
                            } else {
                                voiceAssistantManager.startListening { spoken ->
                                    voiceAssistantManager.processVoiceCommand(
                                        query = spoken,
                                        onTakeMedication = onTakeMedication,
                                        getStepCountText = getStepCountText,
                                        getVitalsText = getVitalsText,
                                        onArmSOS = onArmSOS,
                                        onOpenFacilities = onOpenFacilities
                                    )
                                }
                            }
                        }
                        .testTag("voice_mic_toggle_button"),
                    shape = CircleShape,
                    color = if (isListening) GeoPrimary else GeoPrimaryDark,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isListening) "Listening to you..." else "Tap microphone to speak",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) GeoPrimary else GeoPrimaryDark
                )
            )

            // Spoken Transcript / Response Card
            if (transcript.isNotBlank() || response != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = GeoBackground,
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (transcript.isNotBlank()) {
                            Text(
                                text = "YOU SAID",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "\"$transcript\"",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GeoPrimaryDark,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        if (response != null) {
                            if (transcript.isNotBlank()) Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = response ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GeoTextPrimary,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Elder-Accessible Quick Voice Command Prompts
            Text(
                text = "OR TAP A QUICK VOICE COMMAND",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 1.sp,
                    fontSize = 11.sp
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoiceCommandChip(
                    icon = Icons.Default.Medication,
                    commandText = "Log my medication as taken",
                    tag = "voice_cmd_medication",
                    onClick = {
                        voiceAssistantManager.processVoiceCommand(
                            query = "Log my medication as taken",
                            onTakeMedication = onTakeMedication,
                            getStepCountText = getStepCountText,
                            getVitalsText = getVitalsText,
                            onArmSOS = onArmSOS,
                            onOpenFacilities = onOpenFacilities
                        )
                    }
                )

                VoiceCommandChip(
                    icon = Icons.Default.DirectionsWalk,
                    commandText = "What's my step count today?",
                    tag = "voice_cmd_steps",
                    onClick = {
                        voiceAssistantManager.processVoiceCommand(
                            query = "What's my step count today?",
                            onTakeMedication = onTakeMedication,
                            getStepCountText = getStepCountText,
                            getVitalsText = getVitalsText,
                            onArmSOS = onArmSOS,
                            onOpenFacilities = onOpenFacilities
                        )
                    }
                )

                VoiceCommandChip(
                    icon = Icons.Default.Favorite,
                    commandText = "What are my vitals?",
                    tag = "voice_cmd_vitals",
                    onClick = {
                        voiceAssistantManager.processVoiceCommand(
                            query = "What are my vitals?",
                            onTakeMedication = onTakeMedication,
                            getStepCountText = getStepCountText,
                            getVitalsText = getVitalsText,
                            onArmSOS = onArmSOS,
                            onOpenFacilities = onOpenFacilities
                        )
                    }
                )

                VoiceCommandChip(
                    icon = Icons.Default.Warning,
                    commandText = "Call for help (Arms SOS Screen)",
                    isEmergency = true,
                    tag = "voice_cmd_emergency",
                    onClick = {
                        voiceAssistantManager.processVoiceCommand(
                            query = "Call for help",
                            onTakeMedication = onTakeMedication,
                            getStepCountText = getStepCountText,
                            getVitalsText = getVitalsText,
                            onArmSOS = onArmSOS,
                            onOpenFacilities = onOpenFacilities
                        )
                    }
                )

                VoiceCommandChip(
                    icon = Icons.Default.LocalHospital,
                    commandText = "Book GP or Clinic appointment",
                    tag = "voice_cmd_appointment",
                    onClick = {
                        voiceAssistantManager.processVoiceCommand(
                            query = "Book appointment",
                            onTakeMedication = onTakeMedication,
                            getStepCountText = getStepCountText,
                            getVitalsText = getVitalsText,
                            onArmSOS = onArmSOS,
                            onOpenFacilities = onOpenFacilities
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onClose,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Dismiss Voice Assistant", color = GeoPrimaryDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VoiceCommandChip(
    icon: ImageVector,
    commandText: String,
    isEmergency: Boolean = false,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = if (isEmergency) HealthRedFlagBg else GeoBackground,
        border = BorderStroke(1.dp, if (isEmergency) HealthRedFlag.copy(alpha = 0.4f) else GeoBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isEmergency) HealthRedFlag else GeoPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = commandText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isEmergency) HealthRedFlag else GeoPrimaryDark,
                    fontSize = 14.sp
                )
            )
        }
    }
}
