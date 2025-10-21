/**
 * ------------------------------------------------------------
 * File: GradientActionCard.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This composable component displays a solid Electric Blue action card
 * with a title, optional subtitle, and an optional icon.
 * It supports click handling and consistent VoltGo styling.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.theme.AppColors

@Composable
fun GradientActionCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = AppColors.ElectricBlue,
                    shape = MaterialTheme.shapes.large
                )
                .padding(20.dp)
                .heightIn(min = 120.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TranslucentWhite85
                        )
                    }
                }
            }
        }
    }
}
