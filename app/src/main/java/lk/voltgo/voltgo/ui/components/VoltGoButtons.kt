package lk.voltgo.voltgo.ui.components

/**
 * ------------------------------------------------------------
 * File: VoltGoButtons.kt
 * Author: Panchali Samarasinghe
 * Created: October 20, 2025
 * Version: 1.0
 *
 * Description:
 * Reusable VoltGo button components:
 *  - VoltGoGradientButton (primary CTA with gradient fill)
 *  - VoltGoOutlinedButton (secondary neutral outline)
 *  - VoltGoDangerOutlinedButton (destructive outline)
 *  - VoltGoFilterPill (compact segmented/pill button for filters)
 *
 * Notes:
 *  - Supports leading icons and loading states
 *  - Keeps typography/colors aligned with AppColors and Material3
 * ------------------------------------------------------------
 */

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.theme.AppColors

private val DefaultButtonShape: Shape = RoundedCornerShape(14.dp)
private val DefaultButtonHeight = 48.dp

@Composable
fun VoltGoGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    shape: Shape = DefaultButtonShape,
    height: Dp = DefaultButtonHeight,
) {
    // Transparent M3 Button, we paint gradient in a Box inside
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .height(height)
            .semantics { contentDescription = text },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        shape = shape,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(brush = Brush.horizontalGradient(AppColors.buttonGradient)),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = AppColors.BrandWhite
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = AppColors.BrandWhite
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        color = AppColors.BrandWhite,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun VoltGoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    shape: Shape = DefaultButtonShape,
    height: Dp = DefaultButtonHeight,
    borderColor: Color = AppColors.DeepNavy,
    contentColor: Color = AppColors.DeepNavy
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = contentColor)
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, color = contentColor)
        }
    }
}

@Composable
fun VoltGoDangerOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    shape: Shape = DefaultButtonShape,
    height: Dp = DefaultButtonHeight
) {
    VoltGoOutlinedButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        shape = shape,
        height = height,
        borderColor = AppColors.TagCancelledText,
        contentColor = AppColors.TagCancelledText
    )
}

/**
 * Compact pill used for segmented filters (“All / Confirmed / …”).
 * When selected:
 *  - Adds gradient border
 *  - Bold-ish label and darker foreground
 */
@Composable
fun VoltGoFilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val selectedBg = AppColors.ElectricBlue
    val selectedFg = AppColors.BrandWhite
    val unselectedBg = Color(0xFFF2F4F7)       // soft gray
    val unselectedFg = Color(0xFF000000)       // black text

    val bg = if (selected) selectedBg else unselectedBg
    val fg = if (selected) selectedFg else unselectedFg

    Surface(
        modifier = modifier.height(36.dp),
        color = bg,
        contentColor = fg,
        shape = shape
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = fg)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = fg
            )
        }
    }
}

