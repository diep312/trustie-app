package com.example.trustie.repository.connectrepo

import android.util.Log
import com.example.trustie.data.model.ConnectionResponse
import com.example.trustie.data.model.RelativeConnection
import kotlinx.coroutines.delay

class ConnectionRepository {
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