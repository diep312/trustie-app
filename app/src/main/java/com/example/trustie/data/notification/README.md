# Call Detection and Notification System

This directory contains the implementation of the incoming call detection and notification system for the Trustie app.

## Overview

The system detects incoming calls, checks them against the server API for potential scams, and displays appropriate notifications to the user.

## Components

### 1. CallDetectionReceiver
- **Location**: `CallDetectionReceiver.kt`
- **Purpose**: Broadcast receiver that listens for incoming calls
- **Features**:
  - Detects phone state changes
  - Triggers scam check service on incoming calls
  - Handles boot completion to restart services

### 2. ScamCheckService
- **Location**: `ScamCheckService.kt`
- **Purpose**: Foreground service that handles background network calls
- **Features**:
  - Makes API calls to check phone numbers
  - Shows appropriate notifications based on results
  - Runs as foreground service for reliability

### 3. BaseNotificationManager
- **Location**: `BaseNotificationManager.kt`
- **Purpose**: Abstract base class for all notification managers
- **Features**:
  - Creates notification channels
  - Provides common notification utilities
  - Handles permission issues gracefully

### 4. CallAlertNotificationManager
- **Location**: `CallAlertNotificationManager.kt`
- **Purpose**: Specialized manager for call alert notifications
- **Features**:
  - Shows regular notifications for call alerts
  - Includes action buttons (Check Details, Dismiss)
  - Handles high-risk call notifications

### 5. OverlayNotificationManager
- **Location**: `OverlayNotificationManager.kt`
- **Purpose**: Manager for overlay notifications above call screen
- **Features**:
  - Shows full-screen intent notifications
  - Handles overlay permission checks
  - Provides urgent alerts for high-risk calls

### 6. CallDetectionManager
- **Location**: `CallDetectionManager.kt`
- **Purpose**: Singleton manager to coordinate all call detection functionality
- **Features**:
  - Starts/stops call detection services
  - Manages notification display
  - Handles permission checks

### 7. PermissionHelper
- **Location**: `../utils/PermissionHelper.kt`
- **Purpose**: Utility class for permission management
- **Features**:
  - Checks required permissions
  - Requests permissions
  - Handles overlay permission

## Permissions Required

The following permissions are required for the call detection system:

```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## Usage

### Starting Call Detection
```kotlin
val callDetectionManager = CallDetectionManager.getInstance(context)
callDetectionManager.startCallDetection()
```

### Showing Call Alert
```kotlin
callDetectionManager.showCallAlert(
    phoneNumber = "+1234567890",
    message = "⚠️ Potential scam detected!",
    isHighRisk = true
)
```

### Checking Permissions
```kotlin
if (PermissionHelper.hasRequiredPermissions(context)) {
    // Start call detection
} else {
    // Request permissions
    PermissionHelper.requestPermissions(activity)
}
```

## Notification Types

### 1. Regular Call Alert
- Shows when a call is detected
- Includes phone number and risk assessment
- Action buttons for details and dismiss

### 2. High-Risk Alert
- Red color and urgent styling
- Additional vibration and lights
- Block call action button

### 3. Overlay Notification
- Appears above call screen (if permission granted)
- Full-screen intent for maximum visibility
- Fallback to regular notification if overlay not available

## Integration Points

### MainActivity
- Initializes call detection on app start
- Handles permission requests
- Manages deep links for call alerts

### AndroidManifest.xml
- Declares broadcast receiver and service
- Includes required permissions
- Configures intent filters

## Testing

To test the call detection system:

1. Install the app and grant required permissions
2. Make a test call to the device
3. Check that notifications appear appropriately
4. Verify API calls are made to check phone numbers

## Troubleshooting

### Common Issues

1. **Notifications not appearing**: Check notification permissions
2. **Call detection not working**: Verify phone state permissions
3. **Overlay not showing**: Ensure SYSTEM_ALERT_WINDOW permission
4. **Service not starting**: Check foreground service permissions

### Debug Logs

Enable debug logging by checking logcat for tags:
- `CallDetectionReceiver`
- `ScamCheckService`
- `CallDetectionManager`
- `MainActivity`

## Future Enhancements

1. **Call Blocking**: Implement actual call blocking functionality
2. **Custom Overlay UI**: Create custom overlay UI instead of notifications
3. **Background Processing**: Improve background processing reliability
4. **User Preferences**: Add user preferences for notification types
5. **Analytics**: Track call detection effectiveness 