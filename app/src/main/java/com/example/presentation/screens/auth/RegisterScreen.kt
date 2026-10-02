package com.example.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.registerState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        // Background Manaaki Health Logo Watermark
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top Navigation & Step Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.previousRegisterStep() },
                    modifier = Modifier.testTag("register_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GeoPrimaryDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = GeoMintSelected,
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "STEP ${state.currentStep} OF 4",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                TextButton(onClick = { viewModel.navigateToLogin() }) {
                    Text("LOG IN", color = GeoPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linear Step Progress Bar
            LinearProgressIndicator(
                progress = { state.currentStep / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = GeoPrimary,
                trackColor = GeoSageContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Content Area for Current Step
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when (state.currentStep) {
                    1 -> StepOneBasicDetails(state = state, viewModel = viewModel)
                    2 -> StepTwoPassword(state = state, viewModel = viewModel)
                    3 -> StepThreeCaregiver(state = state, viewModel = viewModel)
                    4 -> StepFourConsent(state = state, viewModel = viewModel)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Navigation Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.currentStep == 3) {
                    // Step 3 is optional: offer explicit "Skip for now" button
                    OutlinedButton(
                        onClick = { viewModel.nextRegisterStep() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("skip_caregiver_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Text(
                            text = "SKIP FOR NOW",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GeoTextSecondary,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }

                Button(
                    onClick = { viewModel.nextRegisterStep() },
                    enabled = if (state.currentStep == 4) state.consentAgreed && !state.isRegistering else !state.isRegistering,
                    modifier = Modifier
                        .weight(if (state.currentStep == 3) 1.2f else 1f)
                        .height(52.dp)
                        .testTag("register_next_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        disabledContainerColor = GeoPrimary.copy(alpha = 0.4f)
                    )
                ) {
                    if (state.isRegistering) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CREATING ACCOUNT…", color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = if (state.currentStep == 4) "CREATE ACCOUNT" else "CONTINUE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Full Privacy Policy Modal
    if (state.showPrivacyPolicyModal) {
        AlertDialog(
            onDismissRequest = { viewModel.showPrivacyModal(false) },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface,
            title = {
                Text(
                    text = "Health Privacy Policy Summary",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark
                    )
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "1. Local-First Storage:\nAll your daily medication doses, blood pressure, blood glucose, and movement activity are encrypted and stored locally on your device.\n\n2. New Zealand Health Information Privacy Code 2020:\nManaaki Health complies strictly with Rules 1–12 of the Health Information Privacy Code 2020.\n\n3. Caregiver Sync Control:\nYour data is only shared with family caregivers you explicitly link. You can revoke access at any time.\n\n4. Emergency Telemetry:\nIn an emergency (SOS button or fall alert), only essential triage information (Medical ID, location, allergies) is transmitted to emergency services.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GeoTextPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.showPrivacyModal(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I UNDERSTAND", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ---------------- Step 1: Basic Details ----------------
@Composable
private fun StepOneBasicDetails(
    state: com.example.presentation.viewmodels.RegisterUiState,
    viewModel: AuthViewModel
) {
    Column {
        Text(
            text = "Basic Details",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimaryDark,
                fontSize = 24.sp
            )
        )
        Text(
            text = "We will use this information to set up your personal health profile.",
            style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Full Name Field
        Text(
            text = "FULL NAME",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.fullName,
            onValueChange = { viewModel.onRegisterFullNameChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_fullname_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("e.g. Margaret Te Aroha", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GeoPrimary) },
            singleLine = true,
            isError = state.errors.containsKey("fullName"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )
        if (state.errors.containsKey("fullName")) {
            Text(
                text = state.errors["fullName"] ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = HealthRedFlag, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        } else {
            Text(
                text = "Used to identify you on your clinical GP reports.",
                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 12.sp),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Email Field
        Text(
            text = "EMAIL ADDRESS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onRegisterEmailChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_email_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("e.g. margaret@example.nz", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GeoPrimary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            isError = state.errors.containsKey("email"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )
        if (state.errors.containsKey("email")) {
            Text(
                text = state.errors["email"] ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = HealthRedFlag, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        } else {
            Text(
                text = "We'll use this to log in and send account recovery links.",
                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 12.sp),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Number Field
        Text(
            text = "PHONE NUMBER",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = { viewModel.onRegisterPhoneChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_phone_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("e.g. +64 21 555 8392", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GeoPrimary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            isError = state.errors.containsKey("phone"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )
        if (state.errors.containsKey("phone")) {
            Text(
                text = state.errors["phone"] ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = HealthRedFlag, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        } else {
            Text(
                text = "Used for SMS alerts and to contact you in an emergency.",
                style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary, fontSize = 12.sp),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

// ---------------- Step 2: Password ----------------
@Composable
private fun StepTwoPassword(
    state: com.example.presentation.viewmodels.RegisterUiState,
    viewModel: AuthViewModel
) {
    val isLengthOk = state.password.length >= 8
    val hasNumber = state.password.any { it.isDigit() }

    Column {
        Text(
            text = "Create Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimaryDark,
                fontSize = 24.sp
            )
        )
        Text(
            text = "Choose a strong password you can easily remember.",
            style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Password input with Show/Hide toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASSWORD",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
            )
            Row(
                modifier = Modifier.clickable { viewModel.toggleRegisterPasswordVisibility() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (state.isPasswordVisible) "Hide password" else "Show password",
                    style = MaterialTheme.typography.labelSmall.copy(color = GeoPrimaryDark, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onRegisterPasswordChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_password_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("Enter a password", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GeoPrimary) },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = state.errors.containsKey("password"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Confirm Password
        Text(
            text = "RE-TYPE PASSWORD",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onRegisterConfirmPasswordChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_confirm_password_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("Re-type password to verify", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GeoPrimary) },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = state.errors.containsKey("confirmPassword"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )

        if (state.errors.containsKey("confirmPassword")) {
            Text(
                text = state.errors["confirmPassword"] ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = HealthRedFlag, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Live Password Requirements Checklist Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GeoSageContainer.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, GeoBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "PASSWORD CHECKLIST",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimaryDark,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isLengthOk) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (isLengthOk) HealthSuccessGreen else GeoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "At least 8 characters long",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isLengthOk) GeoPrimaryDark else GeoTextSecondary,
                            fontWeight = if (isLengthOk) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (hasNumber) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (hasNumber) HealthSuccessGreen else GeoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Contains at least one number (0-9)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (hasNumber) GeoPrimaryDark else GeoTextSecondary,
                            fontWeight = if (hasNumber) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

// ---------------- Step 3: Emergency Contact / Caregiver (Optional) ----------------
@Composable
private fun StepThreeCaregiver(
    state: com.example.presentation.viewmodels.RegisterUiState,
    viewModel: AuthViewModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Caregiver Link",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimaryDark,
                    fontSize = 24.sp
                )
            )
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = GeoSageContainer
            ) {
                Text(
                    text = "OPTIONAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GeoPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "This lets a family member or caregiver see your medication and activity summaries, and be notified in an emergency.",
            style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Caregiver Full Name
        Text(
            text = "CAREGIVER OR NEXT OF KIN NAME",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.caregiverName,
            onValueChange = { viewModel.onRegisterCaregiverNameChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_caregiver_name_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("e.g. David Te Aroha (Son)", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GeoPrimary) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Caregiver Phone
        Text(
            text = "CAREGIVER PHONE NUMBER",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GeoTextSecondary, letterSpacing = 1.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = state.caregiverPhone,
            onValueChange = { viewModel.onRegisterCaregiverPhoneChange(it) },
            modifier = Modifier.fillMaxWidth().testTag("register_caregiver_phone_input"),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = GeoTextPrimary),
            placeholder = { Text("e.g. +64 21 555 0192", fontSize = 18.sp, color = GeoTextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GeoPrimary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            border = BorderStroke(1.dp, GeoBorder)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "You can add or update your caregiver link at any time from the app Settings or Sync tab.",
                    style = MaterialTheme.typography.bodySmall.copy(color = GeoTextSecondary)
                )
            }
        }
    }
}

// ---------------- Step 4: Consent & Account Finalization ----------------
@Composable
private fun StepFourConsent(
    state: com.example.presentation.viewmodels.RegisterUiState,
    viewModel: AuthViewModel
) {
    Column {
        Text(
            text = "Health Privacy & Consent",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimaryDark,
                fontSize = 24.sp
            )
        )
        Text(
            text = "Please review our health data principles before creating your account.",
            style = MaterialTheme.typography.bodyMedium.copy(color = GeoTextSecondary)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            border = BorderStroke(1.dp, GeoBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEW ZEALAND HEALTH PRIVACY CODE 2020",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimaryDark,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Your medication, vitals, and activity records are encrypted and stored directly on this device first. Health telemetry is only shared with family or GPs when you explicitly request or link them.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GeoTextPrimary,
                        lineHeight = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = { viewModel.showPrivacyModal(true) },
                    modifier = Modifier.testTag("view_privacy_policy_button")
                ) {
                    Text(
                        text = "Read Full Privacy Policy Summary",
                        color = GeoPrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Agreement Checkbox
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.onConsentAgreedChange(!state.consentAgreed) },
            shape = RoundedCornerShape(14.dp),
            color = if (state.consentAgreed) GeoMintSelected.copy(alpha = 0.5f) else GeoSurface,
            border = BorderStroke(1.dp, if (state.consentAgreed) GeoPrimary else GeoBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.consentAgreed,
                    onCheckedChange = { viewModel.onConsentAgreedChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GeoPrimary,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.testTag("consent_checkbox")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I understand and agree to the storage and protection of my health data.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = GeoPrimaryDark,
                        fontSize = 15.sp
                    )
                )
            }
        }

        if (state.errors.containsKey("consent")) {
            Text(
                text = state.errors["consent"] ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = HealthRedFlag, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 6.dp, start = 8.dp)
            )
        }

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HealthRedFlagBg)
            ) {
                Text(
                    text = state.errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(color = HealthRedFlag, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
