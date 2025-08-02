//package com.example.trustie.data.repository
//
//import android.content.Context
//import android.net.Uri
//import com.example.trustie.data.remote.ImageVerificationApiService
//import com.example.trustie.data.remote.dto.ImageVerificationResponse
//import com.example.trustie.domain.repository.ImageVerificationRepository
//import com.example.trustie.utils.FileUtils
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//
//class ImageVerificationRepositoryImpl(
//    private val apiService: ImageVerificationApiService,
//    private val context: Context // Cần context để xử lý Uri
//) : ImageVerificationRepository {
//
//    override suspend fun verifyImage(imageUri: Uri, userId: Int, description: String?): Result<ImageVerificationResponse> {
//        return try {
//            val file = FileUtils.uriToFile(context, imageUri)
//            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile) // Đã đổi tên form data thành 'file'
//
//            val userIdPart = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull()) // Chuyển Int thành String RequestBody
//            val descriptionPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())
//
//            val response = apiService.verifyImage(imagePart, userIdPart, descriptionPart)
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}

package com.example.trustie.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log // Đảm bảo đã import Log
import com.example.trustie.data.remote.ImageVerificationApiService
import com.example.trustie.data.remote.dto.ImageVerificationResponse
import com.example.trustie.domain.repository.ImageVerificationRepository
import com.example.trustie.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ImageVerificationRepositoryImpl(
    private val apiService: ImageVerificationApiService,
    private val context: Context
) : ImageVerificationRepository {

    override suspend fun verifyImage(imageUri: Uri, userId: Int, description: String?): Result<ImageVerificationResponse> {
        return try {
            Log.d("ImageRepoDebug", "Starting image verification for URI: $imageUri, userId: $userId")

            // Bước 1: Chuyển đổi Uri thành File
            val file = FileUtils.uriToFile(context, imageUri)
            if (!file.exists() || file.length() == 0L) {
                Log.e("ImageRepoDebug", "File does not exist or is empty after conversion: ${file.absolutePath}")
                return Result.failure(IllegalStateException("Selected image file is invalid or empty."))
            }
            Log.d("ImageRepoDebug", "File created from URI: ${file.absolutePath}, exists: ${file.exists()}, size: ${file.length()} bytes")

            // Bước 2: Tạo RequestBody và MultipartBody.Part
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            Log.d("ImageRepoDebug", "MultipartBody.Part 'file' created with name: ${file.name}")

            val userIdPart = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            Log.d("ImageRepoDebug", "RequestBody 'user_id' created: $userId")

            val descriptionPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            if (descriptionPart != null) {
                Log.d("ImageRepoDebug", "RequestBody 'description' created: $description")
            } else {
                Log.d("ImageRepoDebug", "Description is null, no 'description' part created.")
            }

            // Bước 3: Gọi API service
            Log.d("ImageRepoDebug", "Calling API service for image verification...")
            val response = apiService.verifyImage(imagePart, userIdPart, descriptionPart)
            Log.d("ImageRepoDebug", "API service call finished. Response: $response")

            Result.success(response)
        } catch (e: Exception) {
            Log.e("ImageRepoDebug", "Error during image verification: ${e.message}", e)
            Result.failure(e)
        } finally {
            Log.d("ImageRepoDebug", "Image verification process finished (try-catch block exited).")
        }
    }
}
