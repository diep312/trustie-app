package com.example.trustie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.data.model.enums.AlertType

@Composable
fun NotificationCard(
    title: String,
    message: String,
    time: String,
    type: AlertType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val visuals = remember(type) { visualsFor(type) }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = visuals.containerColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Leading icon with small status badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(visuals.leadingBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visuals.leadingIcon,
                    contentDescription = null,
                    tint = visuals.leadingTint,
                    modifier = Modifier.size(28.dp)
                )
                if (visuals.badgeIcon != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(visuals.badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = visuals.badgeIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = visuals.titleColor,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.75f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class AlertVisuals(
    val containerColor: Color,
    val leadingIcon: ImageVector,
    val leadingBg: Color,
    val leadingTint: Color,
    val badgeIcon: ImageVector?,
    val badgeBg: Color,
    val titleColor: Color
)

// Color palette tuned to the screenshots
private fun visualsFor(type: AlertType): AlertVisuals {
    // Base tones
    val lavender = Color(0xFFEDE7F6)
    val lavenderDarker = Color(0xFFDCCEF2)
    val infoBlue = Color(0xFF2196F3)
    val danger = Color(0xFFD32F2F)
    val warning = Color(0xFFFFA000)
    val success = Color(0xFF2E7D32)
    val purple = Color(0xFF6A1B9A)

    return when (type) {
        AlertType.SCAM_DETECTED -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.Security,
            leadingBg = lavenderDarker,
            leadingTint = infoBlue,
            badgeIcon = Icons.Rounded.Report,
            badgeBg = danger,
            titleColor = Color.Black
        )
        AlertType.SUSPICIOUS_ACTIVITY -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.VisibilityOff,
            leadingBg = lavenderDarker,
            leadingTint = purple,
            badgeIcon = Icons.Rounded.Warning,
            badgeBg = warning,
            titleColor = Color.Black
        )
        AlertType.HIGH_RISK -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.Security,
            leadingBg = lavenderDarker,
            leadingTint = danger,
            badgeIcon = Icons.Rounded.PriorityHigh,
            badgeBg = danger,
            titleColor = Color.Black
        )
        AlertType.URGENT -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.NotificationsActive,
            leadingBg = lavenderDarker,
            leadingTint = warning,
            badgeIcon = Icons.Rounded.PriorityHigh,
            badgeBg = danger,
            titleColor = Color.Black
        )
        AlertType.FAMILY_MEMBER_ALERT -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.Groups,
            leadingBg = lavenderDarker,
            leadingTint = infoBlue,
            badgeIcon = Icons.Rounded.Report,
            badgeBg = danger,
            titleColor = Color.Black
        )
        AlertType.DAILY_REMINDER -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.Event,
            leadingBg = lavenderDarker,
            leadingTint = success,
            badgeIcon = Icons.Rounded.Check,
            badgeBg = success,
            titleColor = Color.Black
        )
        AlertType.PHONE_RISK -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.PhoneIphone,
            leadingBg = lavenderDarker,
            leadingTint = infoBlue,
            badgeIcon = Icons.Rounded.Report,
            badgeBg = danger,
            titleColor = Color.Black
        )
        AlertType.FAMILY_ONLY_ALERT -> AlertVisuals(
            containerColor = lavender,
            leadingIcon = Icons.Rounded.Lock,
            leadingBg = lavenderDarker,
            leadingTint = purple,
            badgeIcon = Icons.Rounded.Groups,
            badgeBg = infoBlue,
            titleColor = Color.Black
        )
    }
}