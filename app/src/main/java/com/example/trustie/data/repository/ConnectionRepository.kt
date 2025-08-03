//package com.example.trustie.data.repository
//import android.util.Log
//import com.example.trustie.data.api.ConnectionApiService
//import com.example.trustie.ui.model.ConnectionResponse
//import com.example.trustie.ui.model.RelativeConnection
//import kotlinx.coroutines.delay
//
//class ConnectionRepository(
//    private val apiService: ConnectionApiService = ConnectionApiService()
//) {
//    suspend fun generateQRCode(): ConnectionResponse {
//        return try {
//            Log.d("ConnectionDebug", "Generating QR code...")
//            delay(1500) // Mô phỏng độ trễ mạng
//
//            // Dữ liệu giả để test UI - QR code giả
//            val mockQRCode = "TRUSTIE_CONNECT_${System.currentTimeMillis()}"
//
//            ConnectionResponse(
//                success = true,
//                qrCode = mockQRCode
//            )
//        } catch (e: Exception) {
//            Log.e("ConnectionDebug", "Error generating QR code: ${e.message}", e)
//            ConnectionResponse(
//                success = false,
//                message = e.message
//            )
//        }
//    }
//
//    suspend fun getConnections(): ConnectionResponse {
//        return try {
//            Log.d("ConnectionDebug", "Getting connections...")
//            delay(1000)
//
//            // Dữ liệu giả để test UI
//            val mockConnections = listOf(
//                RelativeConnection(
//                    id = "1",
//                    name = "Nguyễn Văn A",
//                    phoneNumber = "0912345678",
//                    relationship = "Cha",
//                    isConnected = true,
//                    connectedAt = "2023-10-26 10:00:00"
//                ),
//                RelativeConnection(
//                    id = "2",
//                    name = "Trần Thị B",
//                    phoneNumber = "0987654321",
//                    relationship = "Mẹ",
//                    isConnected = true,
//                    connectedAt = "2023-10-26 10:05:00"
//                ),
//                RelativeConnection(
//                    id = "3",
//                    name = "Nguyễn Văn C",
//                    phoneNumber = "0901234567",
//                    relationship = "Con",
//                    isConnected = false,
//                    connectedAt = null
//                )
//            )
//
//            ConnectionResponse(
//                success = true,
//                connections = mockConnections
//            )
//        } catch (e: Exception) {
//            Log.e("ConnectionDebug", "Error getting connections: ${e.message}", e)
//            ConnectionResponse(
//                success = false,
//                message = e.message
//            )
//        }
//    }
//
//    suspend fun removeConnection(connectionId: String): Boolean {
//        return try {
//            Log.d("ConnectionDebug", "Removing connection: $connectionId")
//            delay(500)
//            true // Giả lập thành công
//        } catch (e: Exception) {
//            Log.e("ConnectionDebug", "Error removing connection: ${e.message}", e)
//            false
//        }
//    }
//}






package com.example.trustie.data.repository

import android.util.Log
import com.example.trustie.data.api.ConnectionApiService
import com.example.trustie.data.model.ConnectionResponse
import com.example.trustie.data.model.RelativeConnection
import kotlinx.coroutines.delay

class ConnectionRepository(
    private val apiService: ConnectionApiService = ConnectionApiService()
) {
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