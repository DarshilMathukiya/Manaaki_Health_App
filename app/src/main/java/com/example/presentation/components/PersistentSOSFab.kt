package com.example.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HealthRedFlag
import com.example.ui.theme.HealthRedFlagBg
import kotlinx.coroutines.delay

@Composable
fun PersistentSOSFab(
    onClick: () -> Unit,
    onEmergencyTriggered: () -> Unit,
    modifier: Modifier = Modifier,
    holdDurationMillis: Long = 3000L
) {
    var isPressed by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            val startTime = System.currentTimeMillis()
            while (isPressed) {
                val elapsed = System.currentTimeMillis() - startTime
                val fraction = (elapsed.toFloat() / holdDurationMillis).coerceIn(0f, 1f)
                holdProgress = fraction
                if (fraction >= 1f) {
                    onEmergencyTriggered()
                    isPressed = false
                    holdProgress = 0f
                    break
                }
                delay(16)
            }
        } else {
            holdProgress = 0f
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = holdProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "sos_fab_progress"
    )

    Box(
        modifier = modifier
            .testTag("floating_sos_fab")
            .size(68.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Progress ring when pressing & holding
        if (animatedProgress > 0f) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(68.dp),
                color = HealthRedFlag,
                strokeWidth = 4.dp,
                trackColor = HealthRedFlagBg,
                strokeCap = StrokeCap.Round
            )
        }

        Surface(
            modifier = Modifier
                .size(60.dp)
                .shadow(8.dp, CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            val released = tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            onClick()
                        }
                    )
                },
            shape = CircleShape,
            color = if (isPressed) HealthRedFlag.copy(alpha = 0.9f) else HealthRedFlag,
            shadowElevation = 6.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "Emergency SOS - Tap to open or hold 3 seconds to dispatch 111",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "SOS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
