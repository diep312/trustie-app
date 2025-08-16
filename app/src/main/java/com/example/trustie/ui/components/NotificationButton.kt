package com.example.trustie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.R


@Composable
fun NotificationButton(
    onClick: () -> Unit,
    hasUnreadNotifications: Boolean = false,
    modifier: Modifier = Modifier,
    width: Dp = 96.dp,
    height: Dp = 84.dp,
    label: String = "Thông báo"
) {
    val shape = RoundedCornerShape(12.dp)

    Surface(
        onClick = onClick,
        shape = shape,
        color = Color(0xFFDBD5E8),
        shadowElevation = 2.dp,
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape) // ensure children (badge) don’t draw outside rounded corners
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon + badge
                Box(modifier = Modifier.size(28.dp)) {
                    Image(
                        painter = painterResource(R.drawable.ic_bell_2),
                        contentDescription = "Thông báo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF44336))
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Preview(showBackground = false)
@Composable
fun NotificationButtonPreview() {
    TrustieTheme {
       NotificationButton(
           onClick = {},
           hasUnreadNotifications = true
       )
    }
}
