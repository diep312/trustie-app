package com.example.trustie.repository.imagerepo

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.trustie.data.api.BaseApiService
import com.example.trustie.data.api.ScreenshotApiService
import com.example.trustie.data.model.request.ImageVerificationRequest
import com.example.trustie.data.model.response.ImageVerificationResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageVerificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageVerificationRepository {

    private val api: ScreenshotApiService by lazy {
        BaseApiService.createService()
    }

    override suspend fun verifyImage(request: ImageVerificationRequest): Result<ImageVerificationResponse> {
        return try {
            Log.d("ImageRepoDebug", "Starting image verification for userId=${request.userId}, uri=${request.imageUri}")

            val filePart = buildFilePartFromUri(request.imageUri)
            val userIdPart = request.userId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = request.description
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.analyzeScreenshot(
                file = filePart,
                userId = userIdPart,
                description = descriptionPart
            )

            Log.d("ImageRepoDebug", "Image verification completed successfully")
            Log.d("ImageRepoDebug", "Response: screenshotId=${response.screenshotId}, riskLevel=${response.llmAnalysis.riskLevel}")
            Log.d("ImageRepoDebug", "OCR Text: ${response.ocrText}")
            Log.d("ImageRepoDebug", "Analysis: ${response.llmAnalysis.analysis}")

            Result.success(response)
        } catch (e: Exception) {
            Log.e("ImageRepoDebug", "Error during image verification: ${e.message}", e)
            Result.failure(Exception("Lỗi kết nối với server: ${e.message}", e))
        }
    }

    private fun buildFilePartFromUri(uriString: String): MultipartBody.Part {
        Log.d("ImageRepoDebug", "buildFilePartFromUri called with uriString: $uriString")

        if (uriString.isBlank()) {
            Log.e("ImageRepoDebug", "URI string is blank or empty.")
            throw IllegalArgumentException("URI ảnh không được rỗng.")
        }

        val uri: Uri = try {
            Uri.parse(uriString)
        } catch (e: Exception) {
            Log.e("ImageRepoDebug", "Error parsing URI: $uriString. Message: ${e.message}", e)
            throw IllegalArgumentException("URI ảnh không hợp lệ: $uriString", e)
        }

        val contentResolver = context.contentResolver
        val fileName = queryDisplayName(uri) ?: "upload_${System.currentTimeMillis()}.jpg"
        val mimeType = contentResolver.getType(uri) ?: "image/*"

        Log.d("ImageRepoDebug", "File info - name: $fileName, mimeType: $mimeType")

        var inputStream: InputStream? = null
        var tempFile: File? = null

        try {
            inputStream = contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Không thể đọc dữ liệu ảnh từ URI. Vui lòng kiểm tra quyền hoặc URI.")

            tempFile = File.createTempFile("upload_", "_img", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }

            if (!tempFile.exists() || tempFile.length() == 0L) {
                throw IllegalStateException("File ảnh tạm thời không hợp lệ hoặc trống.")
            }

            Log.d("ImageRepoDebug", "Temporary file created: ${tempFile.absolutePath}, size: ${tempFile.length()} bytes")

            val requestBody: RequestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("file", fileName, requestBody)

        } catch (e: Exception) {
            Log.e("ImageRepoDebug", "Error during file processing for URI: $uriString. Message: ${e.message}", e)
            tempFile?.delete()
            throw e
        } finally {
            inputStream?.close()
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        return try {
            val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else null
            }
        } catch (e: Exception) {
            Log.w("ImageRepoDebug", "Could not query display name for URI: $uri", e)
            null
        }
    }
}
