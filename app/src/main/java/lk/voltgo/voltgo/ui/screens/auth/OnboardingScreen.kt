package lk.voltgo.voltgo.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import lk.voltgo.voltgo.ui.theme.AppColors
import kotlinx.coroutines.launch

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material3.Icon

data class OnboardPage(val title: String, val desc: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val pages = listOf(
        OnboardPage("Find Stations", "Discover nearby AC/DC chargers with live availability."),
        OnboardPage("Reserve a Slot", "Book a time window that suits your trip."),
        OnboardPage("Seamless Charging", "Arrive, plug, and pay—effortlessly.")
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(AppColors.splashGradient)
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .widthIn(max = 520.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hero illustration
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(
                                    colors = AppColors.logoBorderGradient
                                ),
                                shape = CircleShape
                            )
                            .background(
                                AppColors.TranslucentWhite05,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EvStation,
                            contentDescription = null,
                            tint = AppColors.EnergyAmber,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = pages[page].title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = AppColors.TranslucentWhite90,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = pages[page].desc,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.TranslucentWhite85,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Pager indicators
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(pages.size) { i ->
                    val active = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(if (active) 12.dp else 8.dp)
                            .background(
                                color = if (active) AppColors.BrandGreen else AppColors.TranslucentWhite20,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val next = (pagerState.currentPage + 1).coerceAtMost(pages.lastIndex)
                            pagerState.animateScrollToPage(next)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.BrandGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text("Next", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.TranslucentWhite20),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text(
                        if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Skip",
                        color = AppColors.TranslucentWhite90,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    OnboardingScreen(onNavigateToLogin = {})
}
