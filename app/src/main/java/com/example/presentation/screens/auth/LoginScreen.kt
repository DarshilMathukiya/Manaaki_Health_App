package com.example.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodels.AuthViewModel
import com.example.presentation.viewmodels.LoginRole
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.QrCodeScanner

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onOpenPairingFlow: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state by viewModel.loginState.collectAsState()
    val emailFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // Autofocus first input field on load for senior accessibility
        try {
            emailFocusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    var showEditAvatarDialog by remember { mutableStateOf(false) }
    var newAvatarUrlInput by remember { mutableStateOf(state.cachedAvatarUrl ?: "") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        // 1. Full-screen background photo layer (50% opacity, cover resize, light scrim for WCAG AAA contrast)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.50f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GeoMintSelected.copy(alpha = 0.6f),
                            GeoSageContainer.copy(alpha = 0.4f),
                            GeoBackground
                        )
                    )
                )
        )

        // Light scrim to guarantee WCAG 2.2 AAA contrast (>7:1)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GeoBackground.copy(alpha = 0.88f),
                            GeoBackground.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // 2. Background Manaaki Health Logo Watermark
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ManaakiLogoBadge(
                modifier = Modifier
                    .size(280.dp)
                    .alpha(0.12f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (onNavigateBack != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Welcome",
                            tint = GeoPrimaryDark
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "MANAAKI HEALTH",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoTextSecondary,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimaryDark,
                    fontSize = 28.sp
                )
            )

            Text(
                text = "Sign in to access your medication reminders and health vitals.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = GeoTextSecondary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role Selector Segmented Control (Patient vs Caregiver / Admin)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_role_selector_container"),
                shape = RoundedCornerShape(16.dp),
                color = GeoSageContainer,
                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(
                        text = "LOG IN AS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoTextSecondary,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isPatient = state.selectedRole == LoginRole.PATIENT
                        val isCaregiver = state.selectedRole == LoginRole.CAREGIVER_ADMIN

                        // Option 1: Patient
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectRole(LoginRole.PATIENT) }
                                .testTag("role_selector_patient"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPatient) GeoPrimary else Color.Transparent,
                            border = if (isPatient) null else BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isPatient) Color.White else GeoPrimaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Patient",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isPatient) Color.White else GeoPrimaryDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }

                        // Option 2: Caregiver / Admin
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectRole(LoginRole.CAREGIVER_ADMIN) }
                                .testTag("role_selector_caregiver"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCaregiver) GeoPrimary else Color.Transparent,
                            border = if (isCaregiver) null else BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = if (isCaregiver) Color.White else GeoPrimaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Caregiver",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isCaregiver) Color.White else GeoPrimaryDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Plain-language Error / Offline Notification Card
            if (state.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.isOfflineBlocked) GeoSageContainer else HealthRedFlagBg
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (state.isOfflineBlocked) GeoPrimary.copy(alpha = 0.4f) else HealthRedFlag.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.isOfflineBlocked) Icons.Default.Info else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (state.isOfflineBlocked) GeoPrimaryDark else HealthRedFlag,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (state.isOfflineBlocked) "INTERNET NEEDED ONCE" else "PLEASE CHECK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isOfflineBlocked) GeoPrimaryDark else HealthRedFlag,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = state.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = GeoTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        if (state.isUnknownAccount) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { viewModel.navigateToRegister() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, GeoPrimary)
                            ) {
                                Text(
                                    text = "CREATE NEW ACCOUNT",
                                    color = GeoPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }
                }
            }

            // 1. Email or Phone Input Field (Large >=18sp text)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "EMAIL OR PHONE NUMBER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoTextSecondary,
                        letterSpacing = 1.sp,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.emailOrPhone,
                    onValueChange = { viewModel.onEmailOrPhoneChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(emailFocusRequester)
                        .testTag("login_email_input"),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeoTextPrimary
                    ),
                    placeholder = {
                        Text(
                            text = "e.g. name@example.com",
                            fontSize = 18.sp,
                            color = GeoTextSecondary.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email or Phone icon",
                            tint = GeoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GeoSurface,
                        unfocusedContainerColor = GeoSurface,
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Password Input Field (Large >=18sp text) + Show/Hide Toggle with Label
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PASSWORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoTextSecondary,
                            letterSpacing = 1.sp,
                            fontSize = 12.sp
                        )
                    )

                    // Explicit Show/Hide Password Toggle with Label & Icon for Senior Accessibility
                    Row(
                        modifier = Modifier
                            .clickable { viewModel.togglePasswordVisibility() }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password",
                            tint = GeoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.isPasswordVisible) "Hide password" else "Show password",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GeoPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeoTextPrimary
                    ),
                    placeholder = {
                        Text(
                            text = "Enter your password",
                            fontSize = 18.sp,
                            color = GeoTextSecondary.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password lock icon",
                            tint = GeoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.performLogin() }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GeoSurface,
                        unfocusedContainerColor = GeoSurface,
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Primary Full-Width Action: "Log In" Button (>=48dp height)
            Button(
                onClick = { viewModel.performLogin() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GeoPrimary,
                    disabledContainerColor = GeoPrimary.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "LOGGING IN…",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                } else {
                    Text(
                        text = "LOG IN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 17.sp
                        )
                    )
                }
            }

            // Biometric Option (Face ID / Fingerprint) if available or previously opted in
            if (state.isBiometricAvailable) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { viewModel.unlockWithBiometric() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("biometric_login_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GeoBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = GeoSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Fingerprint / Face ID login",
                        tint = GeoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIGN IN WITH BIOMETRICS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = GeoPrimaryDark,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Option: "Forgot password?" Plain-Text Link (not buried)
            TextButton(
                onClick = { viewModel.showForgotPassword(true) },
                modifier = Modifier.testTag("forgot_password_button")
            ) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GeoPrimaryDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Create Account Navigation Link (Full Width Stack)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = GeoSageContainer.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "New to Manaaki Health?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Set up your medication reminders today",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.navigateToRegister() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("login_register_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, GeoPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = GeoPrimaryDark
                        )
                    ) {
                        Text(
                            text = "Register",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimaryDark,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // "Forgot Password" Friendly Help Dialog
    if (state.showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showForgotPassword(false) },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface,
            title = {
                Text(
                    text = "Reset Your Password",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "We will send a secure password reset link to your registered email address:\n\n${state.emailOrPhone.ifBlank { "your email" }}\n\nYou can also contact your registered caregiver (David Te Aroha) or call Manaaki Health Support at 0800-MANAAKI.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GeoTextPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.showForgotPassword(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SEND RESET LINK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showForgotPassword(false) }) {
                    Text("CANCEL", color = GeoTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Biometric Opt-in Offer Dialog (Offered after first successful login)
    if (state.showBiometricOptInDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissBiometricOptIn() },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Enable Fast Biometric Sign-In?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
                Text(
                    text = "Would you like to unlock Manaaki Health using your Fingerprint or Face ID next time? You won't need to retype your password.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GeoTextPrimary,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.setBiometricOptIn(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ENABLE BIOMETRICS", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setBiometricOptIn(false) }) {
                    Text("NOT NOW", color = GeoTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Edit Profile Avatar Dialog
    if (showEditAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showEditAvatarDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Update Profile Photo",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter a photo image URL or choose default placeholder photo:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
                    )
                    OutlinedTextField(
                        value = newAvatarUrlInput,
                        onValueChange = { newAvatarUrlInput = it },
                        label = { Text("Photo Web URL (https://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("avatar_url_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAvatar(newAvatarUrlInput.ifBlank { null })
                        showEditAvatarDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("save_avatar_button")
                ) {
                    Text("SAVE PHOTO", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAvatarDialog = false }) {
                    Text("CANCEL", color = GeoTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // No Caregiver Link Guidance Dialog
    if (state.showNoCaregiverLinkDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showNoCaregiverLink(false) },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Caregiver Link Required",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You're not yet linked as a caregiver for anyone. Ask them to send you an invite, or scan their QR code, to get started.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GeoTextPrimary,
                            lineHeight = 22.sp,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You can also continue into your patient view in the meantime.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GeoTextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showNoCaregiverLink(false)
                        onOpenPairingFlow()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("scan_qr_invite_dialog_button")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SCAN QR / ENTER INVITE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.showNoCaregiverLink(false)
                        viewModel.selectRole(LoginRole.PATIENT)
                        viewModel.performLogin()
                    }
                ) {
                    Text("LOG IN AS PATIENT", color = GeoPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
