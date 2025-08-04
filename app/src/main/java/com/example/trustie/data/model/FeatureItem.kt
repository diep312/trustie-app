package com.example.trustie.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class FeatureItem(
    val title: String,
    val icon: ImageVector? = null,
    val iconResId: Int? = null,
    val backgroundColor: Color,
    val iconColor: Color,
    val textColor: Color,
    val onClick: () -> Unit = {}
)