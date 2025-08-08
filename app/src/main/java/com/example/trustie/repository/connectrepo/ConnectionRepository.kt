
package com.example.trustie.repository.connectrepo

import android.util.Log
import com.example.trustie.data.api.BaseApiService
import com.example.trustie.data.api.FamilyApiService
import com.example.trustie.data.model.ConnectionResponse
import com.example.trustie.data.model.LinkFamilyResponse
import com.example.trustie.data.model.QRScanResult
import com.example.trustie.data.model.RelativeConnection
import com.example.trustie.data.model.request.LinkRequest
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepository @Inject constructor() {

    private val familyApiService = BaseApiService.createService<FamilyApiService>()

    suspend fun generateQRCode(): ConnectionResponse {
        return try {
            Log.d("ConnectionDebug", "Generating QR code...")
            delay(1500)
            val mockQRCode = "TRUSTIE_CONNECT_${System.currentTimeMillis()}"
            ConnectionResponse(
                success = true,
                qrCode = mockQRCode
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

            // Parse QR code format: "trustie:connect=family_user_id=123"
            if (qrData.startsWith("trustie:connect=family_user_id=") ||
                qrData.startsWith("trustie\\:connect=family_user_id=")) {

                val userIdString = qrData.substringAfter("family_user_id=")
                val elderlyUserId = userIdString.toIntOrNull()

                if (elderlyUserId != null) {
                    QRScanResult(
                        success = true,
                        elderlyUserId = elderlyUserId
                    )
                } else {
                    QRScanResult(
                        success = false,
                        message = "Mã QR không hợp lệ"
                    )
                }
            } else {
                QRScanResult(
                    success = false,
                    message = "Mã QR không phải của Trustie"
                )
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

            val response = familyApiService.linkFamily(linkRequest)

            LinkFamilyResponse(
                success = true,
                message = "Kết nối thành công!",
                data = response
            )
        } catch (e: Exception) {
            Log.e("ConnectionDebug", "Error linking family: ${e.message}", e)
            LinkFamilyResponse(
                success = false,
                message = "Lỗi kết nối: ${e.message}"
            )
        }
    }

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
