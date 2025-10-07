package lk.voltgo.voltgo.ui.screens.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

import lk.voltgo.voltgo.ui.components.GradientButton
import lk.voltgo.voltgo.ui.screens.auth.RegistrationViewModel
import lk.voltgo.voltgo.ui.screens.auth.RegistrationNavigationEvent
import lk.voltgo.voltgo.ui.theme.AppColors

data class RegisterRequest(
    val email: String,
    val phone: String,
    val password: String,
    val role: String = "EVOwner",
    val nic: String,
    val fullName: String,
    val address: String
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: (() -> Unit)? = null, // optional: if you want to go to main after success
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Visible fields (as per your JSON)
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nic by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Passwords
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Hidden, fixed role
    val role = "EVOwner"

    // Simple local validation
    val canSubmit by derivedStateOf {
        fullName.isNotBlank() &&
                email.contains("@") &&
                password.isNotBlank() &&
                confirmPassword == password &&
                nic.isNotBlank() &&
                phone.isNotBlank() &&
                address.isNotBlank()
    }

    // React to navigation events from VM
    LaunchedEffect(uiState.navigationEvent) {
        when (val evt = uiState.navigationEvent) {
            RegistrationNavigationEvent.NavigateToMain -> {
                onNavigateToMain?.invoke() ?: onNavigateToLogin() // fallback if not provided
                viewModel.onNavigationHandled()
            }
            RegistrationNavigationEvent.NavigateBackToLogin -> {
                onNavigateToLogin()
                viewModel.onNavigationHandled()
            }
            null -> Unit
        }
    }

    // Show one-shot messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
        }
        uiState.successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
        }
    }

    // Optional blocking progress
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 480.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    "Join VoltGo ⚡️",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        brush = Brush.verticalGradient(AppColors.splashGradient)
                    ),
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Create your account to find stations, reserve slots and pay easily.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TranslucentWhite90
                )

                Spacer(Modifier.height(20.dp))

                // fullName
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full name") },
                    isError = uiState.fieldErrors["fullName"] != null,
                    supportingText = { uiState.fieldErrors["fullName"]?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.height(12.dp))

                // email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    isError = uiState.fieldErrors["email"] != null,
                    supportingText = { uiState.fieldErrors["email"]?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email)
                )
                Spacer(Modifier.height(12.dp))

                // phone (allow leading + and digits)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { input ->
                        val cleaned = input.filterIndexed { index, c ->
                            c.isDigit() || (c == '+' && index == 0)
                        }
                        phone = cleaned
                    },
                    label = { Text("Phone") },
                    isError = uiState.fieldErrors["phone"] != null,
                    supportingText = { uiState.fieldErrors["phone"]?.let { Text(it) } },
                    placeholder = { Text("+9470XXXXXXX") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone)
                )
                Spacer(Modifier.height(12.dp))

                // NIC
                OutlinedTextField(
                    value = nic,
                    onValueChange = { nic = it.uppercase() },
                    label = { Text("NIC") },
                    isError = uiState.fieldErrors["nic"] != null,
                    supportingText = { uiState.fieldErrors["nic"]?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Ascii)
                )
                Spacer(Modifier.height(12.dp))

                // address (multiline)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    isError = uiState.fieldErrors["address"] != null,
                    supportingText = { uiState.fieldErrors["address"]?.let { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 96.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(Modifier.height(16.dp))

                // password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    isError = uiState.fieldErrors["password"] != null,
                    supportingText = { uiState.fieldErrors["password"]?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))

                // confirm password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm password") },
                    isError = confirmPassword.isNotEmpty() && confirmPassword != password,
                    supportingText = {
                        if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                            Text("Passwords do not match")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                GradientButton(
                    text = "Create account",
                    onClick = {
                        if (canSubmit) {
                            // Call the ViewModel directly
                            viewModel.registerUser(
                                email = email.trim(),
                                phone = phone.trim(),
                                password = password,
                                nic = nic.trim(),
                                fullName = fullName.trim(),
                                address = address.trim()
                            )
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Fill all fields and ensure passwords match.")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already have an account?", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    RegisterScreen(onNavigateToLogin = {})
}

/* ------- Optional: Backward-compatible wrapper (keeps your old onRegister signature) ------- */

@Composable
fun RegisterScreen(
    onRegister: (RegisterRequest) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    RegisterScreen(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToMain = null // or pass a lambda
    )
}
