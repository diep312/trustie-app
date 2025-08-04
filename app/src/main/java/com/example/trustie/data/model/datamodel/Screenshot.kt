package com.example.trustie.data.model.datamodel

import com.google.gson.annotations.SerializedName

data class Screenshot(
    val id: Int,
    @SerializedName("image_path")
    val imagePath: String,
    @SerializedName("image_size")
    val imageSize: Long,
    @SerializedName("image_format")
    val imageFormat: String, // "jpg", "png", etc.
    val description: String? = null,
    @SerializedName("is_processed")
    val isProcessed: Boolean = false,
    @SerializedName("ocr_text")
    val ocrText: String? = null,
    val timestamp: String,
    @SerializedName("user_id")
    val userId: Int
) 