/**
 * ------------------------------------------------------------
 * File: AppColors.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * Defines all custom color constants and gradient schemes used
 * throughout the VoltGo app. This centralizes the color palette
 * for consistent theming and visual identity.
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    /* ---------- Brand palette ---------- */
    val DeepNavy = Color(0xFF0B1026)
    val ElectricBlue = Color(0xFF1E40AF)
    val Teal = Color(0xFF0EA5A4)

    val BrandGreen = Color(0xFF0BF50F)
    val BrandBlue = Color(0xFF1E90FF)
    val BrandWhite = Color(0xFFFFFFFF)
    val EnergyAmber = Color(0xFFFFDF21)

    /* ---------- Gradients ---------- */
    val splashGradient = listOf(DeepNavy, ElectricBlue, Teal)
    val buttonGradient = listOf(BrandGreen, BrandBlue, ElectricBlue)
    val logoBorderGradient = listOf(BrandGreen, BrandBlue)

    /* ---------- Translucent whites ---------- */
    val TranslucentWhite05 = Color.White.copy(alpha = 0.05f)
    val TranslucentWhite20 = Color.White.copy(alpha = 0.20f)
    val TranslucentWhite85 = Color.White.copy(alpha = 0.85f)
    val TranslucentWhite90 = Color.White.copy(alpha = 0.90f)

    /* ---------- Tag / Status colors (aligned to StatusChip) ---------- */

    // Confirmed
    // BG: ElectricBlue @ 10%; FG: ElectricBlue
    val TagConfirmedBg = ElectricBlue.copy(alpha = 0.10f)
    val TagConfirmedText = ElectricBlue
    val TagConfirmedBorder = TagConfirmedText.copy(alpha = 0.35f)

    // Pending
    // BG: DeepNavy @ 8%; FG: DeepNavy
    val TagPendingBg = DeepNavy.copy(alpha = 0.08f)
    val TagPendingText = DeepNavy
    val TagPendingBorder = TagPendingText.copy(alpha = 0.35f)

    // Completed
    // BG: #E6F4EA; FG: #1E4620
    val TagCompletedBg = Color(0xFFE6F4EA)
    val TagCompletedText = Color(0xFF1E4620)
    val TagCompletedBorder = TagCompletedText.copy(alpha = 0.35f)

    // Cancelled
    // BG: #FFE8E6; FG: #7A1F1F
    val TagCancelledBg = Color(0xFFFFE8E6)
    val TagCancelledText = Color(0xFF7A1F1F)
    // Kept old name for compatibility; matches pill border behavior
    val TagCancelledBorder = TagCancelledText.copy(alpha = 0.35f)
}
