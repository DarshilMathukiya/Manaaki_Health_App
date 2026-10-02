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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.HealthRedFlag
import com.example.ui.theme.HealthRedFlagBg

/**
 * Logout confirmation modal dialog / bottom sheet.
 * 
 * DATA RETENTION POLICY:
 * Logging out clears the authentication session token from SecureStore.
 * It does NOT delete or wipe the encrypted local SQLite/Room health database.
 * Your prescription logs, vitals history, and sensor baseline stay securely preserved on this device.
 */
@Composable
fun LogoutConfirmDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = GeoSurface,
        icon = {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = GeoSageContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Log out icon",
                        tint = GeoPrimaryDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Are you sure you want to log out?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimaryDark,
                    textAlign = TextAlign.Center
                )
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Logging out requires you to sign in again with your password or biometrics to see your active schedule.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                )

                // Data retention in-app reassurance notice
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GeoSageContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, GeoBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your health data stays safely stored on this device.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GeoPrimaryDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            // Stay Logged In is the default, primary low-risk button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("stay_logged_in_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
            ) {
                Text(
                    text = "STAY LOGGED IN",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        },
        dismissButton = {
            // Log Out is the secondary / outlined style button
            OutlinedButton(
                onClick = onConfirmLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_logout_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HealthRedFlag.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = HealthRedFlagBg.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "LOG OUT",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = HealthRedFlag,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }
    )
}

@Composable
private fun Box(
    contentAlignment: Alignment,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = contentAlignment
    ) {
        content()
    }
}
