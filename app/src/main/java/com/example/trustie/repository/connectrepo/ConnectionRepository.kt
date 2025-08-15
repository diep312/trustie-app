

package com.example.trustie.repository.connectrepo

import android.util.Log
import com.example.trustie.data.api.BaseApiService
import com.example.trustie.data.api.FamilyApiService
import com.example.trustie.data.model.ConnectionResponse
import com.example.trustie.data.model.LinkFamilyResponse
import com.example.trustie.data.model.QRScanResult
import com.example.trustie.data.model.RelativeConnection
import com.example.trustie.data.model.request.LinkRequest
import com.example.trustie.utils.UserUtils
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepository @Inject constructor() {

    private val familyApiService = BaseApiService.createService<FamilyApiService>()

    suspend fun generateQRCode(): ConnectionResponse {
        return try {
            Log.d("ConnectionDebug", "Generating QR code...")

            val currentUserId = UserUtils.getCurrentUserId()
            Log.d("ConnectionDebug", "Current user ID for QR: $currentUserId")

            if (currentUserId <= 0) {
                return ConnectionResponse(
                    success = false,
                    message = "ID người dùng không hợp lệ"
                )
            }

            delay(1500)
                val qrCode = "$currentUserId"
            Log.d("ConnectionDebug", "Generated QR code: $qrCode")

            ConnectionResponse(
                success = true,
                qrCode = qrCode
            )
        } catch (e: Exception) {
            Log.e("ConnectionDebug", "Error generating QR code: ${e.message}", e)
            ConnectionResponse(
                success = false,
                message = e.message
            )
        }
    }

    suspend fun parseQRCode(qrData: String): QRScanResult {
        return try {
            Log.d("ConnectionDebug", "Parsing QR code: $qrData")

            // Handle multiple QR code formats
            val cleanedData = qrData.replace("\\", "") // Remove escaped backslashes

            when {
                cleanedData.toIntOrNull() != null -> {
                    QRScanResult(success = true)
                }
                cleanedData.startsWith("family_user_id=") -> {
                    QRScanResult(success = true)
                }
                else -> {
                    QRScanResult(
                        success = false,
                        message = "Mã QR không phải của Trustie"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ConnectionDebug", "Error parsing QR code: ${e.message}", e)
            QRScanResult(
                success = false,
                message = "Lỗi đọc mã QR: ${e.message}"
            )
        }
    }

    suspend fun linkFamily(linkRequest: LinkRequest): LinkFamilyResponse {
        return try {
            Log.d("ConnectionDebug", "Linking family member: ${linkRequest.name}")
            Log.d("ConnectionDebug", "Request data: $linkRequest")

            // Validate request data before sending
            if (linkRequest.familyUserId <= 0) {
                return LinkFamilyResponse(
                    success = false,
                    message = "ID người dùng không hợp lệ"
                )
            }

            if (linkRequest.name.isBlank()) {
                return LinkFamilyResponse(
                    success = false,
                    message = "Tên không được để trống"
                )
            }

            // Phone number is optional now

            val response = familyApiService.linkFamily(linkRequest)

            LinkFamilyResponse(
                success = true,
                message = "Kết nối thành công!",
                data = response
            )
        } catch (e: retrofit2.HttpException) {
            Log.e("ConnectionDebug", "HTTP Error linking family: ${e.code()} - ${e.message()}", e)
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("ConnectionDebug", "Error body: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> "Dữ liệu không hợp lệ - $errorBody"
                401 -> "Không có quyền truy cập"
                404 -> "Không tìm thấy người dùng"
                409 -> "Kết nối đã tồn tại"
                500 -> "Lỗi server"
                else -> "Lỗi kết nối: ${e.message()}"
            }

            LinkFamilyResponse(
                success = false,
                message = errorMessage
            )
        } catch (e: Exception) {
            Log.e("ConnectionDebug", "Error linking family: ${e.message}", e)
            LinkFamilyResponse(
                success = false,
                message = "Lỗi kết nối: ${e.message}"
            )
        }
    }

    // ... rest of the methods remain the same
    suspend fun getConnections(): ConnectionResponse {
        return try {
            Log.d("ConnectionDebug", "Getting connections...")
            delay(1000)
            val mockConnections = listOf(
                RelativeConnection(
                    id = "1",
                    name = "Con Trai",
                    phoneNumber = "0914908102",
                    relationship = "Con",
                    isConnected = true,
                    connectedAt = "2023-10-26 10:00:00"
                ),
                RelativeConnection(
                    id = "2",
                    name = "Con Gai",
                    phoneNumber = "0914908112",
                    relationship = "Con",
                    isConnected = true,
                    connectedAt = "2023-10-26 10:05:00"
                ),
                RelativeConnection(
                    id = "3",
                    name = "Con Dau",
                    phoneNumber = "0914908112",
                    relationship = "Con",
                    isConnected = true,
                    connectedAt = "2023-10-26 10:10:00"
                )
            )
            ConnectionResponse(
                success = true,
                connections = mockConnections
            )
        } catch (e: Exception) {
            Log.e("ConnectionDebug", "Error getting connections: ${e.message}", e)
            ConnectionResponse(
                success = false,
                message = e.message
            )
        }
    }

    suspend fun removeConnection(connectionId: String): Boolean {
        return try {
            Log.d("ConnectionDebug", "Removing connection: $connectionId")
            delay(500)
            true
        } catch (e: Exception) {
            Log.e("ConnectionDebug", "Error removing connection: ${e.message}", e)
            false
        }
    }
}
