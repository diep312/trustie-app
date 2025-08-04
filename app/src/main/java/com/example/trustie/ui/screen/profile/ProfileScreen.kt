package com.example.trustie.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        when (profileState) {
            is ProfileState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileState.Success -> {
                val user = (profileState as ProfileState.Success).user
                UserProfileContent(user = user, onLogout = { viewModel.logout() })
            }
            is ProfileState.Error -> {
                ErrorContent(
                    message = (profileState as ProfileState.Error).message,
                    onRetry = { /* Retry logic */ }
                )
            }
            is ProfileState.LoggedOut -> {
                LoggedOutContent()
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    user: com.example.trustie.data.model.datamodel.User,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProfileItem("User ID", user.id.toString())
            ProfileItem("Name", user.name)
            ProfileItem("Email", user.email ?: "Not provided")
            ProfileItem("Device ID", user.deviceId)
            ProfileItem("Is Elderly", if (user.isElderly) "Yes" else "No")
            ProfileItem("Is Active", if (user.isActive) "Yes" else "No")
            ProfileItem("Created At", user.createdAt)
            ProfileItem("Updated At", user.updatedAt)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun ProfileItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun LoggedOutContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Logged Out",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "You have been successfully logged out.",
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
} 