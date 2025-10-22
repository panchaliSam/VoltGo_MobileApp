/**
 * ---------------------------------------------------------
 * File: EditProfileScreen.kt
 * Project: VoltGo ⚡ Mobile App
 * Description:
 *   Provides the user interface for editing profile details such as
 *   full name, email, phone number, NIC, and address.
 *   Handles validation, saving changes, and displaying success/error messages.
 *
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.1
 * Notes (v1.1):
 *   - NIC is display-only (read-only)
 *   - Phone now validates E.164 (+9470...)
 *   - Password is NOT shown and NOT sent from this screen (sent as null/empty)
 * ---------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.data.remote.dto.UserProfileResponse
import lk.voltgo.voltgo.data.remote.dto.UpdateProfileRequest
import lk.voltgo.voltgo.ui.components.GradientButton
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.auth.ProfileViewModel
import lk.voltgo.voltgo.ui.viewmodel.auth.UiState

data class ProfileForm(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val nic: String = "",
    val address: String = "",
    val password: String = "", // not used here (kept for DTO compatibility)
    val role: String = "",
    val isActive: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    when (profileState) {
        is UiState.Success -> {
            val profile = (profileState as UiState.Success<UserProfileResponse>).data
            EditProfileContent(
                initial = ProfileForm(
                    fullName = profile.fullName,
                    email = profile.email,
                    phone = profile.phone,
                    nic = profile.nic,
                    address = profile.address,
                    role = profile.role,
                    isActive = profile.isActive
                ),
                isSaving = updateState is UiState.Loading,
                onBack = onBack,
                onSave = { profileForm ->
                    // IMPORTANT:
                    // We do not update password here. Send null (preferred) or empty if DTO requires non-null.
                    viewModel.updateProfile(
                        UpdateProfileRequest(
                            email = profileForm.email.trim(),
                            phone = profileForm.phone.trim(),
                            fullName = profileForm.fullName.trim(),
                            address = profileForm.address.trim()
                        )
                    )
                },
                snackbarHostState = snackbarHostState
            )
        }
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> {
            ErrorContent(
                message = (profileState as UiState.Error).message,
                onRetry = { viewModel.loadProfile() }
            )
        }
    }

    // Handle update state changes
    LaunchedEffect(updateState) {
        when (updateState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully")
                viewModel.loadProfile() // Reload profile for fresh values
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((updateState as UiState.Error).message)
            }
            else -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    initial: ProfileForm,
    isSaving: Boolean = false,
    onBack: () -> Unit,
    onSave: (ProfileForm) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    // --- state ---
    var fullName by rememberSaveable { mutableStateOf(initial.fullName) }
    var email by rememberSaveable { mutableStateOf(initial.email) }
    var phone by rememberSaveable { mutableStateOf(initial.phone) } // E.164 allowed
    var nic by rememberSaveable { mutableStateOf(initial.nic) }     // read-only
    var address by rememberSaveable { mutableStateOf(initial.address) }

    // --- validators ---
    val emailValid = remember(email) {
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            .matches(email.trim())
    }

    // E.164: optional +, first digit 1-9, total digits 8..15
    val e164Regex = remember { Regex("^\\+?[1-9]\\d{7,14}$") }
    val phoneValid = remember(phone) { e164Regex.matches(phone.trim()) }

    val nameValid = remember(fullName) { fullName.trim().length >= 3 }

    // NIC no longer affects validity (display-only here)
    val formValid = nameValid && emailValid && phoneValid

    val formChanged = remember(fullName, email, phone, address, initial) {
        (fullName != initial.fullName) ||
                (email != initial.email) ||
                (phone != initial.phone) ||
                (address != initial.address)
        // NIC intentionally excluded (read-only)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (formValid) {
                                onSave(
                                    ProfileForm(
                                        fullName = fullName.trim(),
                                        email = email.trim(),
                                        phone = phone.trim(),
                                        nic = initial.nic, // unchanged
                                        address = address.trim(),
                                        password = initial.password,     // not used
                                        role = initial.role,
                                        isActive = initial.isActive
                                    )
                                )
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fix the errors before saving.")
                                }
                            }
                        },
                        enabled = formValid && formChanged && !isSaving
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with subtle gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(AppColors.splashGradient))
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Update your details",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Keep your contact info current",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Form
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Full name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full name") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !nameValid,
                    supportingText = {
                        if (!nameValid) Text("Name must be at least 3 characters")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                // Email (read-only)
                OutlinedTextField(
                    value = email,
                    onValueChange = { /* read-only */ },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    readOnly = true
                )

                // Phone (E.164)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { raw ->
                        // Allow leading '+' once; digits otherwise; max 15 total
                        val sanitized = buildString {
                            raw.forEachIndexed { i, c ->
                                if (c.isDigit()) append(c)
                                else if (i == 0 && c == '+') append(c)
                            }
                        }.take(15)
                        phone = sanitized
                    },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = phone.isNotBlank() && !phoneValid,
                    supportingText = {
                        if (phone.isNotBlank() && !phoneValid) Text("Use E.164 format, e.g. +94707789099")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )

                // NIC (read-only)
                OutlinedTextField(
                    value = nic,
                    onValueChange = { /* read-only */ },
                    label = { Text("NIC") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    readOnly = true
                )

                // Address (multiline)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(Modifier.height(6.dp))

                // Primary action button (duplicate of top-right save for reachability)
                GradientButton(
                    text = if (isSaving) "Saving…" else "Save changes",
                    onClick = {
                        if (formValid) {
                            onSave(
                                ProfileForm(
                                    fullName = fullName.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    nic = initial.nic,
                                    address = address.trim(),
                                    password = "", // not used here
                                    role = initial.role,
                                    isActive = initial.isActive
                                )
                            )
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please fix the errors before saving.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formValid && formChanged && !isSaving,
                    loading = isSaving
                )

            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(onBack = {})
}
