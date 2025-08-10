package com.example.trustie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustie.ui.theme.TrustieTheme
import com.example.trustie.R


@Composable
fun NotificationButton(
    onClick: () -> Unit,
    hasUnreadNotifications: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFDBD5E8),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.ic_bell_2),
                    contentDescription = "Thông báo",
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = "Thông báo",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )


        }
        // Unread notification indicator
        if (hasUnreadNotifications) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color(0xFFF44336),
                        shape = RoundedCornerShape(100)
                    )
                    .align(Alignment.TopEnd)
            )
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
