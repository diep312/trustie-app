# Trustie Authentication System

This document describes the authentication system implemented in the Trustie app.

## Overview

The authentication system provides:
- Global state management for user information
- Automatic login with a fixed user (temporary until backend auth is ready)
- Persistent storage using DataStore
- Base activity that requires authentication
- Repository pattern for authentication operations

## Architecture

### Components

1. **GlobalStateManager** - Manages global app state including user information
2. **AuthRepository** - Interface for authentication operations
3. **AuthRepositoryImpl** - Implementation using DataStore for persistence
4. **AuthViewModel** - ViewModel for authentication logic
5. **BaseAuthenticatedActivity** - Base activity that requires authentication
6. **User Data Model** - Represents user information

### Folder Structure

```
app/src/main/java/com/example/trustie/
├── data/
│   ├── GlobalStateManager.kt
│   └── model/
│       └── datamodel/
│           └── User.kt
├── repository/
│   ├── AuthRepository.kt (interface)
│   └── AuthRepositoryImpl.kt (implementation)
├── ui/
│   ├── base/
│   │   └── BaseAuthenticatedActivity.kt
│   └── screen/
│       ├── auth/
│       │   └── AuthViewModel.kt
│       └── profile/
│           ├── ProfileViewModel.kt
│           └── ProfileScreen.kt
└── di/
    └── AppModule.kt
```

## Fixed User Information

Currently, the app uses a fixed user for testing purposes:

```json
{
  "id": 2,
  "name": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "device_id": "SAMSUNG_SMF21",
  "is_elderly": true,
  "is_active": true,
  "created_at": "2025-07-31 18:33:20.031",
  "updated_at": "2025-07-31 18:33:20.031"
}
```

## Usage

### 1. Base Activity

Extend `BaseAuthenticatedActivity` for any activity that requires authentication:

```kotlin
class MainActivity : BaseAuthenticatedActivity() {
    override fun MainContent() {
        // Your main app content here
        AppNavigation(navController = navController)
    }
}
```

### 2. Global State Access

Access user information throughout the app using `GlobalStateManager`:

```kotlin
@Inject
lateinit var globalStateManager: GlobalStateManager

// Get current user
val user = globalStateManager.currentUser.value

// Get user ID
val userId = globalStateManager.getUserId()

// Get user name
val userName = globalStateManager.getUserName()

// Check if user is elderly
val isElderly = globalStateManager.isUserElderly()
```

### 3. Repository Pattern

Use repositories for data operations:

```kotlin
@Inject
lateinit var authRepository: AuthRepository

// Check if user is logged in
val isLoggedIn = authRepository.isLoggedIn()

// Login with fixed user
val user = authRepository.loginWithFixedUser()

// Logout
authRepository.logout()
```

### 4. ViewModels

ViewModels can access both repositories and global state:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val globalStateManager: GlobalStateManager
) : ViewModel() {
    
    fun someOperation() {
        val userId = globalStateManager.getUserId()
        // Use userId for API calls
    }
}
```

## Authentication Flow

1. **App Launch**: `BaseAuthenticatedActivity` checks authentication status
2. **Not Authenticated**: Automatically logs in with fixed user
3. **Authenticated**: Shows main app content
4. **Logout**: Clears user data and returns to login flow

## Data Persistence

User data is persisted using DataStore with the following keys:
- `user_id` - User ID
- `user_name` - User name
- `user_email` - User email
- `user_device_id` - Device ID
- `user_is_elderly` - Elderly status
- `user_is_active` - Active status
- `user_created_at` - Creation timestamp
- `user_updated_at` - Update timestamp
- `is_logged_in` - Login status

## Future Enhancements

When backend authentication is ready:

1. Replace `loginWithFixedUser()` with actual OTP-based authentication
2. Add token management for API calls
3. Implement proper session management
4. Add biometric authentication support
5. Implement proper error handling for authentication failures

## Testing

The current system allows for easy testing:
- Fixed user ensures consistent behavior
- Global state can be easily mocked
- Repository pattern allows for easy unit testing
- ViewModels can be tested independently

## Dependencies

The authentication system requires:
- Hilt for dependency injection
- DataStore for persistence
- Coroutines for async operations
- Compose for UI 