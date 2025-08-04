package com.example.trustie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.example.trustie.R
import com.example.trustie.ui.theme.TrustieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHeader(
    title: String,
    onBackClick: () -> Unit,
    backgroundColor: Color = Color(0xFFFDF2E9),
    titleColor: Color = Color(0xFF000000),
    returnText: String = "Quay về"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            onClick = onBackClick,
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDBD5E8)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = returnText,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = titleColor
        )
    }
}


@Preview
@Composable
fun ScreenHeaderPreview() {
    TrustieTheme {
        ScreenHeader(
            title = "Screen Title",
            onBackClick = { /* Handle back click */ }
        )
    }
}