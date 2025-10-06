package lk.voltgo.voltgo.ui.screens.main

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import lk.voltgo.voltgo.ui.theme.AppColors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EVOperatorScreenThemed() {
    val context = LocalContext.current
    var qrData by remember { mutableStateOf<String?>(null) }

    // QR Scanner launcher
    val qrLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) qrData = result.contents
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.DeepNavy)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EV Operator",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AppColors.BrandWhite,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Scan QR Code Button
        Button(
            onClick = {
                val options = ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Scan QR Code")
                    setBeepEnabled(true)
                    setOrientationLocked(true)
                }
                qrLauncher.launch(options)
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EnergyAmber),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Scan QR Code",
                color = AppColors.DeepNavy,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        qrData?.let {
            Spacer(modifier = Modifier.height(24.dp))
            // QR Data Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.TranslucentWhite20)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "QR Data:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AppColors.BrandWhite,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AppColors.BrandBlue,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { qrData = null },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.BrandGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Complete Charging Session",
                            color = AppColors.DeepNavy,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EVOperatorPreviewThemed() {
    EVOperatorScreenThemed()
}