package lk.voltgo.voltgo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.components.GradientButton

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.screens.auth.LoginNavigationEvent
import lk.voltgo.voltgo.ui.screens.auth.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding() // respect on-screen keyboard
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

            // Branding
            Text(
                "Welcome to VoltGo ⚡️",
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.verticalGradient(AppColors.splashGradient)
                ),
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Sign in to manage your charging, reservations and payments",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TranslucentWhite90,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // Form
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { /* TODO: Forgot password flow */ }) {
                    Text("Forgot password?", color = Color.Black)
                }
                TextButton(onClick = onNavigateToRegister) {
                    Text("Create account", color = Color.Black)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Primary action
            GradientButton(
                text = if (uiState.isLoading) "Loading..." else "Login",
                onClick = { viewModel.loginUser(username = email, password = password) },
                enabled = !uiState.isLoading,
                loading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            LaunchedEffect(uiState.navigationEvent) {
                when (val event = uiState.navigationEvent) {
                    is LoginNavigationEvent.NavigateToRegister -> {
                        onNavigateToRegister()
                        viewModel.onNavigationHandled()
                    }
                    is LoginNavigationEvent.NavigateToMain -> {
                        onLoginSuccess(event.token) // Navigate to main screen for EVOwner
                        viewModel.onNavigationHandled()
                    }
                    is LoginNavigationEvent.NavigateToOperator -> {
                        // Handle operator navigation if needed
                        viewModel.onNavigationHandled()
                    }
                    null -> {}
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    LoginScreen(
        onLoginSuccess = { _ -> },
        onNavigateToRegister = {}
    )
}
