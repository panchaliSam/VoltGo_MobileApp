package lk.voltgo.voltgo.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Primary brand colors
    val DeepNavy = Color(0xFF0B1026)
    val ElectricBlue = Color(0xFF1E40AF)
    val Teal = Color(0xFF0EA5A4)

    // Accent colors
    val BrandGreen = Color(0xFF0BF50F)
    val BrandBlue = Color(0xFF1E90FF)
    val BrandWhite = Color(0xFFFFFFFF)
    val EnergyAmber = Color(0xFFFFDF21)

    // Gradient combinations
    val splashGradient = listOf(DeepNavy, ElectricBlue, Teal)
    val buttonGradient = listOf(BrandGreen, BrandBlue, ElectricBlue)
    val logoBorderGradient = listOf(BrandGreen, BrandBlue)

    // Common UI colors with alpha
    val TranslucentWhite05 = Color.White.copy(alpha = 0.05f)
    val TranslucentWhite85 = Color.White.copy(alpha = 0.85f)
    val TranslucentWhite90 = Color.White.copy(alpha = 0.9f)
    val TranslucentWhite20 = Color.White.copy(alpha = 0.2f)

    /* ---------- Tag-specific palette (unique codes) ---------- */
    // Confirmed
    val TagConfirmedBg = Color(0xFF0BF50F)      // same hue as BrandGreen but locked for tags
    val TagConfirmedText = DeepNavy

    // Pending
    val TagPendingBg = Color(0xFFFFDF21)        // same hue as EnergyAmber but locked for tags
    val TagPendingText = DeepNavy

    // Completed
    val TagCompletedBg = Color(0xFFFF4545)      // same hue as Teal but locked for tags
    val TagCompletedText = BrandWhite

    // Cancelled (neutral, readable on white)
    val TagCancelledBg = Color(0xFFE5E7EB)      // light neutral (slate-200-ish)
    val TagCancelledText = DeepNavy
    val TagCancelledBorder = Color(0xFFCBD5E1)  // subtle border (slate-300-ish)
}
