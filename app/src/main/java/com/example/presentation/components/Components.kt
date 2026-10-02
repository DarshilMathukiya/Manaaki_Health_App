package com.example.presentation.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.PillIconType
import com.example.ui.theme.GeoBluePill
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoDarkSlate
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSosCoral
import com.example.ui.theme.GeoTrack
import com.example.ui.theme.HealthBlueInfo
import com.example.ui.theme.HealthBlueInfoBg
import com.example.ui.theme.HealthRedFlag
import com.example.ui.theme.HealthRedFlagBg
import com.example.ui.theme.HealthSuccessGreen
import com.example.ui.theme.HealthSuccessGreenBg
import com.example.ui.theme.HealthWarningAmber
import com.example.ui.theme.HealthWarningAmberBg
import kotlinx.coroutines.delay

/**
 * Geometric visual pill representations (shape/colour icons) to help seniors recognise their medicine at a glance.
 */
@Composable
fun PillIconView(
    iconType: PillIconType,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val bluePillColor = GeoBluePill
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(GeoPrimary),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val w = this.size.width
            val h = this.size.height

            when (iconType) {
                PillIconType.ROUND_WHITE -> {
                    // White Scored Tablet at 45 degree angle
                    rotate(45f, center) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(w * 0.15f, h * 0.32f),
                            size = Size(w * 0.7f, h * 0.36f),
                            cornerRadius = CornerRadius(h * 0.18f, h * 0.18f)
                        )
                        drawLine(
                            color = Color(0xFFB0BEC5),
                            start = Offset(w * 0.5f, h * 0.32f),
                            end = Offset(w * 0.5f, h * 0.68f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                PillIconType.OVAL_PINK -> {
                    // Oval Pink Tablet
                    rotate(45f, center) {
                        drawRoundRect(
                            color = Color(0xFFF48FB1),
                            topLeft = Offset(w * 0.12f, h * 0.3f),
                            size = Size(w * 0.76f, h * 0.4f),
                            cornerRadius = CornerRadius(h * 0.2f, h * 0.2f)
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(w * 0.12f, h * 0.3f),
                            size = Size(w * 0.76f, h * 0.4f),
                            cornerRadius = CornerRadius(h * 0.2f, h * 0.2f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
                PillIconType.CAPSULE_BLUE_WHITE -> {
                    // Geometric Blue & White Capsule
                    rotate(45f, center) {
                        val capsuleRect = Size(w * 0.8f, h * 0.4f)
                        val topL = Offset(w * 0.1f, h * 0.3f)
                        // Blue Left Half
                        drawRoundRect(
                            color = bluePillColor,
                            topLeft = topL,
                            size = Size(capsuleRect.width * 0.5f, capsuleRect.height),
                            cornerRadius = CornerRadius(capsuleRect.height / 2, capsuleRect.height / 2)
                        )
                        // White Right Half
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(topL.x + capsuleRect.width * 0.5f, topL.y),
                            size = Size(capsuleRect.width * 0.5f, capsuleRect.height),
                            cornerRadius = CornerRadius(capsuleRect.height / 2, capsuleRect.height / 2)
                        )
                    }
                }
                PillIconType.CAPSULE_RED_YELLOW -> {
                    // Red and Amber Gold Capsule
                    rotate(45f, center) {
                        val capsuleRect = Size(w * 0.8f, h * 0.4f)
                        val topL = Offset(w * 0.1f, h * 0.3f)
                        drawRoundRect(
                            color = Color(0xFFD32F2F),
                            topLeft = topL,
                            size = Size(capsuleRect.width * 0.5f, capsuleRect.height),
                            cornerRadius = CornerRadius(capsuleRect.height / 2, capsuleRect.height / 2)
                        )
                        drawRoundRect(
                            color = Color(0xFFFFB300),
                            topLeft = Offset(topL.x + capsuleRect.width * 0.5f, topL.y),
                            size = Size(capsuleRect.width * 0.5f, capsuleRect.height),
                            cornerRadius = CornerRadius(capsuleRect.height / 2, capsuleRect.height / 2)
                        )
                    }
                }
                PillIconType.TABLET_GREEN -> {
                    // Green Round Tablet
                    drawCircle(
                        color = Color(0xFFA5D6A7),
                        radius = w * 0.38f,
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.38f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                PillIconType.DROP_AMBER -> {
                    // Amber Liquid Dropper
                    drawCircle(
                        color = Color(0xFFFFB74D),
                        radius = w * 0.36f,
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.36f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * Geometric Balance 3-Second Hold-To-Confirm Emergency SOS Button.
 * Dark Slate container with Coral accent rings and crisp uppercase typography.
 */
@Composable
fun HoldToConfirmSOSButton(
    modifier: Modifier = Modifier,
    holdDurationMillis: Long = 3000L,
    onConfirmed: () -> Unit
) {
    val view = LocalView.current
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            val stepMs = 50L
            val totalSteps = holdDurationMillis / stepMs
            var step = 0
            while (step < totalSteps && isHolding) {
                delay(stepMs)
                step++
                holdProgress = step.toFloat() / totalSteps.toFloat()
                if (step % 4 == 0) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
            }
            if (isHolding && holdProgress >= 0.98f) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onConfirmed()
                isHolding = false
                holdProgress = 0f
            }
        } else {
            holdProgress = 0f
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = holdProgress,
        label = "sos_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(GeoDarkSlate)
            .border(1.dp, Color(0xFF2E332E), RoundedCornerShape(28.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        tryAwaitRelease()
                        isHolding = false
                        holdProgress = 0f
                    }
                )
            }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isHolding) GeoSosCoral.copy(alpha = 0.2f) else Color.Transparent)
                    .border(4.dp, GeoSosCoral, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isHolding) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(80.dp),
                        color = GeoSosCoral,
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                }
                Text(
                    text = if (isHolding) "${((1f - animatedProgress) * 3).toInt() + 1}s" else "SOS",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = GeoSosCoral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isHolding) "KEEP HOLDING TO CONFIRM..." else "HOLD TO CONFIRM",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = GeoSosCoral,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "GPS location will be shared with Emergency Services & Family",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = GeoTrack,
                    fontSize = 12.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Geometric Balance High-Contrast Severity Badge
 */
@Composable
fun UrgencyBadge(
    severity: AdvisorySeverity,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text, icon) = when (severity) {
        AdvisorySeverity.NORMAL -> Quad(GeoMintSelected, GeoPrimaryDark, "NORMAL", Icons.Default.CheckCircle)
        AdvisorySeverity.ADVISORY_LOW -> Quad(HealthBlueInfoBg, HealthBlueInfo, "ADVISORY", Icons.Default.Info)
        AdvisorySeverity.ELEVATED -> Quad(HealthWarningAmberBg, HealthWarningAmber, "ELEVATED", Icons.Default.Warning)
        AdvisorySeverity.CRITICAL_RED_FLAG -> Quad(HealthRedFlagBg, HealthRedFlag, "CRITICAL RED FLAG", Icons.Default.Error)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = bgColor,
        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Senior Accessible Primary Large Action Button (minimum 56dp height)
 */
@Composable
fun AccessibleActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    containerColor: Color? = null,
    contentColor: Color? = null
) {
    val defaultContainer = if (isSecondary) GeoSageContainer else GeoPrimary
    val defaultContent = if (isSecondary) GeoPrimaryDark else Color.White

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp),
        shape = RoundedCornerShape(18.dp),
        border = if (isSecondary) BorderStroke(1.dp, GeoBorder) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor ?: defaultContainer,
            contentColor = contentColor ?: defaultContent
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

