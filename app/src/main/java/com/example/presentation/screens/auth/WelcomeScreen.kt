package com.example.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =============================================================================
// MANAAKI HEALTH DESIGN TOKENS
// =============================================================================
val WelcomeBgColor = Color(0xFFFBFDFB)           // Pure / very light off-white (#FBFDFB)
val BrandForestGreen = Color(0xFF0E4D2A)         // Primary Brand Deep Forest Green (#0E4D2A)
val BrandMintOuterRing = Color(0xFFA3E4D7)       // Secondary Accent Mint Green (#A3E4D7)
val BrandMintTeal = Color(0xFF7DE2D1)            // Secondary Accent Teal (#7DE2D1)
val BrandSoftGreenBorder = Color(0xFFD1E7DD)     // Subtle soft green border (#D1E7DD)
val BrandSoftGreenTint = Color(0xFFEBF6F0)       // Light green tint for secondary button (#EBF6F0)
val BrandHeadlineDark = Color(0xFF111111)        // Clean dark title text (#111111)
val BrandTaglineGray = Color(0xFF666666)         // Neutral muted body subtext (#666666)
val BrandFooterMuted = Color(0xFF8E8E93)         // Footer micro-copy gray (#8E8E93)
val BrandHeartYellow = Color(0xFFFACC15)         // Small soft-yellow heart accent (#FACC15)

/**
 * Modern, clean, mobile-first Welcome/Landing Screen for "Manaaki Health"
 * that seamlessly leads into the existing Login and Register flows.
 */
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnimationVisible by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimationVisible = true
    }

    // Smooth entry animations: gentle fade-in and scale-up
    val centralAlpha by animateFloatAsState(
        targetValue = if (isAnimationVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "central_fade"
    )
    val centralScale by animateFloatAsState(
        targetValue = if (isAnimationVisible) 1f else 0.88f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "central_scale"
    )
    val bottomAlpha by animateFloatAsState(
        targetValue = if (isAnimationVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 750, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "bottom_fade"
    )
    val bottomTranslationY by animateFloatAsState(
        targetValue = if (isAnimationVisible) 0f else 28f,
        animationSpec = tween(durationMillis = 750, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "bottom_slide"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WelcomeBgColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Subtle ambient background gradient for aesthetic depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BrandSoftGreenTint.copy(alpha = 0.55f),
                            WelcomeBgColor,
                            WelcomeBgColor
                        )
                    )
                )
        )

        // Main content column structured for mobile-first viewports
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // =================================================================
            // SECTION A: CENTER VISUAL IDENTITY (Logo, App Name, Headline, Tagline)
            // =================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 24.dp)
                    .graphicsLayer {
                        alpha = centralAlpha
                        scaleX = centralScale
                        scaleY = centralScale
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Manaaki Health Badge Logo
                ManaakiLogoBadge(
                    modifier = Modifier.testTag("welcome_logo_badge")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // App Name: Uppercase, letter-spacing 1.5px, font-size 14px, bold, color #0E4D2A
                Text(
                    text = "MANAAKI HEALTH",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandForestGreen,
                        letterSpacing = 1.5.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Main Headline: Font-size 26px, bold, centered, color #111111, max-width 280px
                Text(
                    text = "Your Health & Wellness, Simplified",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandHeadlineDark,
                        lineHeight = 32.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tagline/Subtext: Font-size 14px, regular, centered, color #666666, line-height 1.5, max-width 310px
                Text(
                    text = "Effortlessly manage your daily medications, reminders, and health vitals in one safe place.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = BrandTaglineGray,
                        lineHeight = 21.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 310.dp)
                )
            }

            // =================================================================
            // SECTION B: BOTTOM ACTION SECTION (Pinned to bottom with 32px padding)
            // =================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .graphicsLayer {
                        alpha = bottomAlpha
                        translationY = bottomTranslationY
                    }
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary CTA Button: "Get Started"
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = BrandForestGreen.copy(alpha = 0.25f)
                        )
                        .testTag("welcome_get_started_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary CTA Button: "Create an Account"
                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("welcome_create_account_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BrandSoftGreenBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BrandSoftGreenTint,
                        contentColor = BrandForestGreen
                    )
                ) {
                    Text(
                        text = "Create an Account",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandForestGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Micro-copy / Footer: Terms & Privacy Policy
                Text(
                    text = "By continuing, you agree to Manaaki Health's Terms & Privacy Policy.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = BrandFooterMuted,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .clickable { showTermsDialog = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("welcome_terms_privacy_text")
                )
            }
        }
    }

    // Terms & Privacy Policy Information Dialog
    if (showTermsDialog) {
        TermsAndPrivacyDialog(onDismiss = { showTermsDialog = false })
    }
}

/**
 * Official Logo Visual Badge:
 * - Circular badge featuring a light mint green outer ring
 * - Deep forest green interior circular background
 * - Clean white medical cross in the middle
 * - Small soft-yellow heart placed at the intersection of the cross
 * - Diameter: ~124px (fits the ~110px – 130px specification)
 * - Subtle drop shadow: box-shadow: 0px 10px 25px rgba(14, 77, 42, 0.12)
 */
@Composable
fun ManaakiLogoBadge(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(124.dp)
            .shadow(
                elevation = 14.dp,
                shape = CircleShape,
                spotColor = BrandForestGreen.copy(alpha = 0.28f),
                ambientColor = BrandForestGreen.copy(alpha = 0.12f)
            )
            .clip(CircleShape)
            // Outer light mint green ring (#A3E4D7 / #7DE2D1)
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandMintTeal, BrandMintOuterRing)
                )
            )
            .padding(7.dp) // Outer ring thickness
            .clip(CircleShape)
            // Deep forest green interior circular background (#0E4D2A)
            .background(BrandForestGreen)
    ) {
        // Clean White Medical Cross (Horizontal bar)
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(3.5.dp))
                .background(Color.White)
        )

        // Clean White Medical Cross (Vertical bar)
        Box(
            modifier = Modifier
                .width(14.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(3.5.dp))
                .background(Color.White)
        )

        // Small soft-yellow heart placed at the exact intersection of the cross
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = BrandHeartYellow,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Clean clinical Terms & Privacy Policy Dialog
 */
@Composable
private fun TermsAndPrivacyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        icon = {
            Surface(
                shape = CircleShape,
                color = BrandSoftGreenTint,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BrandForestGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Privacy & Health Data",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandHeadlineDark,
                    textAlign = TextAlign.Center
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Manaaki Health is engineered with offline-first health data storage and strict NZ Health Information Privacy Code (HIPC 2020) compliance.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BrandTaglineGray,
                        lineHeight = 20.sp,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = "• All vitals telemetry, medication logs, and clinical audit trails are encrypted on device.\n• Cloud sync and caregiver telemetry links only occur with your explicit biometric authorization.\n• No health information is shared with third parties without your clinical consent.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BrandForestGreen,
                        lineHeight = 19.sp,
                        fontSize = 12.sp
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandForestGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Understood", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
