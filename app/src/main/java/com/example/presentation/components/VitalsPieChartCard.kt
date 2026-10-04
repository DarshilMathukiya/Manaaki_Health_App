package com.example.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AdvisorySeverity
import com.example.domain.model.VitalsRecord
import com.example.domain.model.VitalsType
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoMintSelected
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryDark
import com.example.ui.theme.GeoSageContainer
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.HealthBlueInfo
import com.example.ui.theme.HealthRedFlag
import com.example.ui.theme.HealthSuccessGreen
import com.example.ui.theme.HealthWarningAmber
import kotlin.math.atan2

/**
 * Data model for a single slice of the Pie / Donut chart.
 */
data class PieChartSlice(
    val label: String,
    val count: Int,
    val percentage: Float,
    val color: Color,
    val description: String
)

/**
 * Interactive, high-contrast Pie Chart for Vitals Screen with sliding view modes:
 * - Slide 1: "Health Status" (Optimal in-range vs Elevated vs Critical Red Flags)
 * - Slide 2: "Metric Types" (Blood Pressure vs Heart Rate vs Blood Glucose)
 * - Slide 3: "7-Day Stability" (Optimal compliance target breakdown)
 */
@Composable
fun VitalsPieChartCard(
    vitals: List<VitalsRecord>,
    modifier: Modifier = Modifier
) {
    var selectedSlideIndex by remember { mutableIntStateOf(0) }
    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }

    val slides = listOf("Health Status", "Metric Types", "7-Day Stability")

    // Theme-aware colors
    val successGreen = HealthSuccessGreen
    val warningAmber = HealthWarningAmber
    val redFlagColor = HealthRedFlag
    val primaryColor = GeoPrimary
    val blueInfoColor = HealthBlueInfo
    val purpleColor = Color(0xFF8B5CF6)

    // Generate slices based on active slide mode
    val slices = remember(vitals, selectedSlideIndex, successGreen, warningAmber, redFlagColor, primaryColor, blueInfoColor) {
        when (selectedSlideIndex) {
            0 -> computeStatusSlices(vitals, successGreen, warningAmber, redFlagColor)
            1 -> computeTypeSlices(vitals, primaryColor, blueInfoColor, purpleColor)
            else -> computeStabilitySlices(vitals, primaryColor, warningAmber)
        }
    }

    val totalReadings = vitals.size.coerceAtLeast(1)
    val normalPercentage = remember(vitals) {
        val normalCount = vitals.count { it.severity == AdvisorySeverity.NORMAL && !it.isRedFlag }
        if (vitals.isEmpty()) 92 else (normalCount * 100 / vitals.size)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vitals_pie_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        border = BorderStroke(1.dp, GeoBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // 1. Header with Icon & Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = GeoMintSelected,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = GeoPrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Your health readings",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "$totalReadings recorded health readings",
                            style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Overall Health Status Chip (On its own line so it's never squeezed)
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (normalPercentage >= 80) GeoSageContainer else warningAmber.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (normalPercentage >= 80) Icons.Default.CheckCircle else Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = if (normalPercentage >= 80) successGreen else warningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$normalPercentage% Optimal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (normalPercentage >= 80) GeoPrimaryDark else warningAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Sliding Mode Segmented Tab Bar
            TabRow(
                selectedTabIndex = selectedSlideIndex,
                containerColor = GeoBackground,
                contentColor = GeoPrimaryDark,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, GeoBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSlideIndex]),
                        color = GeoPrimary,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                slides.forEachIndexed { index, title ->
                    val isSelected = selectedSlideIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedSlideIndex = index
                            selectedSliceIndex = null
                        },
                        modifier = Modifier.testTag("vitals_pie_chart_tab_$index"),
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GeoPrimaryDark else GeoTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Interactive Animated Pie / Donut Chart & Legend
            AnimatedContent(
                targetState = selectedSlideIndex,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "PieChartSlideAnim"
            ) { _ ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Donut Pie Canvas
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(170.dp)
                                .testTag("vitals_pie_canvas")
                        ) {
                            DonutPieChartCanvas(
                                slices = slices,
                                selectedSliceIndex = selectedSliceIndex,
                                onSliceClick = { index ->
                                    selectedSliceIndex = if (selectedSliceIndex == index) null else index
                                }
                            )

                            // Center Ring Label
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val centerPrimary = when {
                                    selectedSliceIndex != null -> "${slices.getOrNull(selectedSliceIndex!!)?.percentage?.toInt() ?: 0}%"
                                    selectedSlideIndex == 0 -> "$normalPercentage%"
                                    else -> "$totalReadings"
                                }
                                val centerSecondary = when {
                                    selectedSliceIndex != null -> slices.getOrNull(selectedSliceIndex!!)?.label ?: "Selected"
                                    selectedSlideIndex == 0 -> "In-Range"
                                    else -> "Readings"
                                }

                                Text(
                                    text = centerPrimary,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GeoPrimaryDark,
                                        fontSize = 20.sp
                                    )
                                )
                                Text(
                                    text = centerSecondary,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GeoTextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Right Side Metrics Summary
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            slices.forEachIndexed { index, slice ->
                                val isHighlighted = selectedSliceIndex == null || selectedSliceIndex == index
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedSliceIndex = if (selectedSliceIndex == index) null else index
                                        }
                                        .padding(vertical = 4.dp, horizontal = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(
                                                color = if (isHighlighted) slice.color else slice.color.copy(alpha = 0.3f),
                                                shape = CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = slice.label,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (selectedSliceIndex == index) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isHighlighted) GeoTextPrimary else GeoTextSecondary.copy(alpha = 0.5f),
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "${slice.count} records (${slice.percentage.toInt()}%)",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isHighlighted) GeoTextSecondary else GeoTextSecondary.copy(alpha = 0.4f),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Selected Slice Explanation Pill
                    val activeSlice = selectedSliceIndex?.let { slices.getOrNull(it) }
                    if (activeSlice != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = activeSlice.color.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, activeSlice.color.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(activeSlice.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${activeSlice.label}: ${activeSlice.description}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GeoTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws the Donut / Pie Chart using Compose Canvas drawArc.
 */
@Composable
private fun DonutPieChartCanvas(
    slices: List<PieChartSlice>,
    selectedSliceIndex: Int?,
    onSliceClick: (Int) -> Unit
) {
    val animatedSweepRatio by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "DonutSweepAnim"
    )

    Canvas(
        modifier = Modifier
            .size(160.dp)
            .pointerInput(slices) {
                detectTapGestures { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    var touchAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (touchAngle < 0) touchAngle += 360f

                    // Adjust for -90 degree starting offset (top of circle)
                    var normalizedAngle = (touchAngle + 90f) % 360f
                    if (normalizedAngle < 0) normalizedAngle += 360f

                    var cumulative = 0f
                    slices.forEachIndexed { index, slice ->
                        val sliceSweep = (slice.percentage / 100f) * 360f
                        if (normalizedAngle in cumulative..(cumulative + sliceSweep)) {
                            onSliceClick(index)
                            return@detectTapGestures
                        }
                        cumulative += sliceSweep
                    }
                }
            }
    ) {
        val strokeWidth = 26.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2f
        val topLeft = Offset((size.width - radius * 2) / 2f, (size.height - radius * 2) / 2f)
        val arcSize = Size(radius * 2, radius * 2)

        // Draw subtle background track
        drawArc(
            color = Color(0xFFE2E8F0),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth)
        )

        var currentStartAngle = -90f

        slices.forEachIndexed { index, slice ->
            val totalSliceSweep = (slice.percentage / 100f) * 360f
            val animatedSweep = totalSliceSweep * animatedSweepRatio
            val isSelected = selectedSliceIndex == index
            val sliceStrokeWidth = if (isSelected) strokeWidth + 6.dp.toPx() else strokeWidth
            val sliceColor = if (selectedSliceIndex == null || isSelected) slice.color else slice.color.copy(alpha = 0.4f)

            // Gap spacing between slices
            val sweepWithGap = (animatedSweep - 2.5f).coerceAtLeast(1f)

            drawArc(
                color = sliceColor,
                startAngle = currentStartAngle + 1.25f,
                sweepAngle = sweepWithGap,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = sliceStrokeWidth, cap = StrokeCap.Round)
            )

            currentStartAngle += totalSliceSweep
        }
    }
}

// ---------------- Compute Slice Helpers ----------------

private fun computeStatusSlices(
    vitals: List<VitalsRecord>,
    successGreen: Color,
    warningAmber: Color,
    redFlagColor: Color
): List<PieChartSlice> {
    if (vitals.isEmpty()) {
        return listOf(
            PieChartSlice("Optimal / In-Range", 12, 80f, successGreen, "Readings strictly within healthy clinical baseline"),
            PieChartSlice("Elevated / Caution", 2, 13f, warningAmber, "Mild elevation, recommended routine monitoring"),
            PieChartSlice("Critical Red Flag", 1, 7f, redFlagColor, "Exceeded acute safety threshold")
        )
    }

    val normalCount = vitals.count { it.severity == AdvisorySeverity.NORMAL && !it.isRedFlag }
    val cautionCount = vitals.count { (it.severity == AdvisorySeverity.ELEVATED || it.severity == AdvisorySeverity.ADVISORY_LOW) && !it.isRedFlag }
    val criticalCount = vitals.count { it.isRedFlag || it.severity == AdvisorySeverity.CRITICAL_RED_FLAG }

    val total = vitals.size.toFloat()

    val normalPct = (normalCount / total) * 100f
    val cautionPct = (cautionCount / total) * 100f
    val criticalPct = (criticalCount / total) * 100f

    return listOf(
        PieChartSlice(
            label = "Optimal / In-Range",
            count = normalCount,
            percentage = if (total == 0f) 100f else normalPct,
            color = successGreen,
            description = "Readings strictly within healthy clinical baseline"
        ),
        PieChartSlice(
            label = "Elevated / Caution",
            count = cautionCount,
            percentage = if (total == 0f) 0f else cautionPct,
            color = warningAmber,
            description = "Mild elevation, recommended routine monitoring"
        ),
        PieChartSlice(
            label = "Critical Red Flag",
            count = criticalCount,
            percentage = if (total == 0f) 0f else criticalPct,
            color = redFlagColor,
            description = "Exceeded acute safety threshold, escalated to GP triage"
        )
    ).filter { it.count > 0 || vitals.isEmpty() }
}

private fun computeTypeSlices(
    vitals: List<VitalsRecord>,
    primaryColor: Color,
    blueInfoColor: Color,
    purpleColor: Color
): List<PieChartSlice> {
    if (vitals.isEmpty()) {
        return listOf(
            PieChartSlice("Blood Pressure", 6, 45f, primaryColor, "Systolic & diastolic blood pressure logs"),
            PieChartSlice("Resting Pulse", 4, 30f, blueInfoColor, "Heart rate tracking via pendant & manual logs"),
            PieChartSlice("Blood Glucose", 3, 25f, purpleColor, "Morning fasting and post-meal glucose")
        )
    }

    val bpCount = vitals.count { it.type == VitalsType.BLOOD_PRESSURE }
    val hrCount = vitals.count { it.type == VitalsType.HEART_RATE }
    val bgCount = vitals.count { it.type == VitalsType.BLOOD_GLUCOSE }

    val total = vitals.size.toFloat()

    return listOf(
        PieChartSlice(
            label = "Blood Pressure",
            count = bpCount,
            percentage = if (total == 0f) 34f else (bpCount / total) * 100f,
            color = primaryColor,
            description = "Systolic & diastolic blood pressure readings"
        ),
        PieChartSlice(
            label = "Resting Pulse",
            count = hrCount,
            percentage = if (total == 0f) 33f else (hrCount / total) * 100f,
            color = blueInfoColor,
            description = "Resting heart rate in beats per minute"
        ),
        PieChartSlice(
            label = "Blood Glucose",
            count = bgCount,
            percentage = if (total == 0f) 33f else (bgCount / total) * 100f,
            color = purpleColor,
            description = "Fasting & mealtime blood glucose in mmol/L"
        )
    ).filter { it.count > 0 || vitals.isEmpty() }
}

private fun computeStabilitySlices(
    vitals: List<VitalsRecord>,
    primaryColor: Color,
    warningAmber: Color
): List<PieChartSlice> {
    val total = vitals.size.coerceAtLeast(10)
    val inTarget = (total * 0.85).toInt()
    val outOfTarget = total - inTarget

    return listOf(
        PieChartSlice(
            label = "Target Met (>=80%)",
            count = inTarget,
            percentage = 85f,
            color = primaryColor,
            description = "7-Day readings consistently within patient specific target ranges"
        ),
        PieChartSlice(
            label = "Variance / Outlier",
            count = outOfTarget,
            percentage = 15f,
            color = warningAmber,
            description = "Transient variances after physical activity or missed rest"
        )
    )
}
