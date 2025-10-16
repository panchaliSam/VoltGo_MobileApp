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
 * Version: 1.0
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
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.data.remote.dto.UpdateProfileRequest
import lk.voltgo.voltgo.ui.viewmodel.auth.ProfileViewModel
import lk.voltgo.voltgo.ui.viewmodel.auth.UiState

data class ProfileForm(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val nic: String = "",
    val address: String = "",
    val password: String = "",
    val role: String = "",
    val isActive: Boolean = true
)

// Main composable that manages the profile edit flow including loading, saving, and error handling
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
                    viewModel.updateProfile(
                        UpdateProfileRequest(
                            email = profileForm.email.trim(),
                            phone = profileForm.phone.trim(),
                            password = profileForm.password,
                            role = profileForm.role,
                            isActive = profileForm.isActive
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
                viewModel.loadProfile() // Reload profile
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    (updateState as UiState.Error).message
                )
            }
            else -> {} // Handle other states if needed
        }
    }
}

// Displays the editable profile form and handles input validation and saving logic
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

    var fullName by rememberSaveable { mutableStateOf(initial.fullName) }
    var email by rememberSaveable { mutableStateOf(initial.email) }
    var phone by rememberSaveable { mutableStateOf(initial.phone) }
    var nic by rememberSaveable { mutableStateOf(initial.nic) }
    var address by rememberSaveable { mutableStateOf(initial.address) }

    val emailValid = remember(email) {
        // simple email check – adjust if you need stricter rules
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email)
    }
    val phoneValid = remember(phone) { phone.length in 9..12 && phone.all { it.isDigit() } }
    val nameValid = remember(fullName) { fullName.trim().length >= 3 }
    val nicValid = remember(nic) { nic.trim().length >= 10 } // tweak for your NIC rules if needed

    val formChanged = remember(fullName, email, phone, nic, address, initial) {
        fullName != initial.fullName ||
                email != initial.email ||
                phone != initial.phone ||
                nic != initial.nic ||
                address != initial.address
    }

    val formValid = nameValid && emailValid && phoneValid && nicValid

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
                                        nic = nic.trim(),
                                        address = address.trim(),
                                        password = initial.password,
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

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = email.isNotBlank() && !emailValid,
                    supportingText = {
                        if (email.isNotBlank() && !emailValid) Text("Enter a valid email address")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 12) phone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = phone.isNotBlank() && !phoneValid,
                    supportingText = {
                        if (phone.isNotBlank() && !phoneValid) Text("Digits only (9–12)")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = nic,
                    onValueChange = { nic = it },
                    label = { Text("NIC") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = nic.isNotBlank() && !nicValid,
                    supportingText = {
                        if (nic.isNotBlank() && !nicValid) Text("NIC seems too short")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next
                    )
                )

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
                Button(
                    onClick = {
                        if (formValid) {
                            onSave(
                                ProfileForm(
                                    fullName = fullName.trim(),
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    nic = nic.trim(),
                                    address = address.trim(),
                                    password = initial.password,
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
                    enabled = formValid && formChanged && !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Saving…")
                    } else {
                        Text("Save changes")
                    }
                }
            }
        }
    }
}

// Displays error message and retry button when loading profile fails
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

// Preview composable for Android Studio to visualize the Edit Profile screen
@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(
        onBack = {}
    )
}
