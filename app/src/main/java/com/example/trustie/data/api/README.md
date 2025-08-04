# Trustie API Structure

This directory contains a clean and organized API structure for the Trustie app, based on the OpenAPI specification.

## Structure Overview

### Base Configuration
- `BaseApiService.kt` - Centralized Retrofit configuration with logging and timeout settings
- `ApiManager.kt` - Provides easy access to all API services

### API Services
Each service corresponds to a different API category:

1. **AuthApiService** - Authentication endpoints
   - `sendOtp()` - Send OTP to phone number
   - `verifyOtp()` - Verify OTP

2. **PhoneApiService** - Phone number management
   - `checkPhoneNumber()` - Check if phone is flagged
   - `flagPhoneNumber()` - Flag a phone number
   - `addPhoneNumber()` - Add new phone number
   - `getFlaggedPhones()` - Get all flagged phones
   - `getPhoneById()` - Get phone by ID
   - `updatePhoneRiskScore()` - Update risk score
   - `searchPhones()` - Search phones
   - `getUserPhones()` - Get user's phones
   - `checkPhoneAndCreateAlert()` - Check phone and create alert

3. **AlertApiService** - Alert management
   - `getUserAlerts()` - Get user alerts
   - `getUnreadAlertCount()` - Get unread count
   - `markAlertAsRead()` - Mark alert as read
   - `acknowledgeAlert()` - Acknowledge alert
   - `deleteAlert()` - Delete alert
   - `getAlertsBySeverity()` - Get alerts by severity
   - `getCriticalAlerts()` - Get critical alerts
   - `createAlert()` - Create new alert
   - `markAllAlertsAsRead()` - Mark all as read

4. **UserApiService** - User management
   - `createUser()` - Create new user
   - `getUsers()` - Get all users
   - `getUser()` - Get user by ID
   - `updateUser()` - Update user
   - `deleteUser()` - Delete user

5. **FamilyApiService** - Family linking
   - `linkFamily()` - Link family members
   - `checkLinkStatus()` - Check link status
   - `unlinkFamilyMember()` - Unlink family member

6. **ReportApiService** - Reporting system
   - `reportPhone()` - Report phone number
   - `reportWebsite()` - Report website
   - `reportSMS()` - Report SMS

7. **ScreenshotApiService** - Screenshot analysis
   - `analyzeScreenshot()` - Analyze screenshot for scams

8. **TextToSpeechApiService** - Text-to-speech
   - `textToSpeech()` - Convert text to speech

### Data Models

#### Request Models (`request/` package)
- `PhoneCheckRequest.kt`
- `FlagPhoneRequest.kt`
- `PhoneNumberCreate.kt`
- `PhoneSearchRequest.kt`
- `UserCreate.kt`
- `LinkRequest.kt`
- `PhoneReportRequest.kt`
- `WebsiteReportRequest.kt`
- `SMSReportRequest.kt`
- `CreateAlertRequest.kt`

#### Response Models (`response/` package)
- `PhoneCheckResponse.kt`
- `PhoneNumber.kt`
- `User.kt`
- `AlertResponse.kt`
- `ReportResponse.kt`

#### Enums (`enums/` package)
- `ApiEnums.kt` - Contains AlertType, Severity, and ReportPriority enums

## Usage

### Basic Usage
```kotlin
// Get API service
val phoneApi = ApiManager.phoneApi

// Make API call
val response = phoneApi.checkPhoneNumber(
    PhoneCheckRequest(phoneNumber = "+1234567890")
)
```

### Error Handling
All API calls are suspend functions that can throw exceptions. Handle them appropriately:

```kotlin
try {
    val response = phoneApi.checkPhoneNumber(request)
    // Handle success
} catch (e: Exception) {
    // Handle error
}
```

### Configuration
Update the `BASE_URL` in `BaseApiService.kt` to point to your actual API server.

## Benefits of This Structure

1. **Clean Separation**: Each API category has its own service interface
2. **Type Safety**: All request/response models are strongly typed
3. **Centralized Configuration**: Single place to configure Retrofit
4. **Easy Testing**: Services can be easily mocked for testing
5. **Consistent**: All APIs follow the same pattern
6. **Maintainable**: Easy to add new endpoints or modify existing ones 