# Security Considerations

## Overview
This document outlines the security considerations for the Connect NFC Contact Sharing application.

## Data Storage

### Local Storage
- All contact data is stored locally on the device using Android's SharedPreferences
- Data is stored in private app storage, accessible only to the app itself
- No data is transmitted to external servers or cloud services
- SharedPreferences files are protected by Android's app sandbox

### Data Serialization
- Contact data is serialized using Google's Gson library
- Gson provides safe JSON serialization/deserialization
- No custom JSON parsing is used to avoid injection vulnerabilities

## NFC Security

### NDEF Message Format
- Contacts are shared using NDEF (NFC Data Exchange Format) messages
- Custom MIME type `application/vnd.com.connect.app` ensures app-specific data exchange
- NFC communication is peer-to-peer, short-range (< 10cm), and ephemeral

### Android Beam
- Uses Android's built-in NFC framework (NfcAdapter)
- Beam sessions are established only when devices are in close proximity
- Data transfer occurs only during the brief contact period

### Data Validation
- Received NFC data is validated before storage
- Try-catch blocks prevent crashes from malformed data
- Invalid contacts are silently discarded

## User Input

### Input Validation
- Profile editing validates that name and surname are non-empty
- Age field accepts only numeric input (InputType.number)
- Email field uses email keyboard (InputType.textEmailAddress)
- Phone field uses phone keyboard (InputType.phone)
- URLs use URI keyboard (InputType.textUri)

### No SQL Injection Risk
- Application uses SharedPreferences, not SQL databases
- No raw SQL queries are executed
- Gson handles all data serialization safely

## Permissions

### Required Permissions
- `android.permission.NFC` - Required for NFC functionality
- No additional dangerous permissions are requested
- No network permissions (offline-only app)
- No location, camera, or microphone permissions

### NFC Hardware Requirement
- App requires NFC hardware (android:required="true")
- Prevents installation on devices without NFC capability

## Privacy

### Data Sharing
- Users explicitly control what information to include in their profile
- Sharing occurs only through intentional NFC tap
- No automatic or background data sharing
- No telemetry or analytics collection

### Contact Management
- Users can view and delete saved contacts at any time
- All data remains on the local device
- No cloud sync or backup (unless user enables Android backup)

## Known Limitations

1. **Android Beam Deprecation**: Android Beam is deprecated in Android 10+
   - Alternative: Consider implementing Android's new Nearby Share or Wi-Fi Direct
   - Current implementation works on devices with Android 5.0 - 9.0

2. **No Encryption**: Data stored in SharedPreferences is not encrypted
   - Mitigation: Android's app sandbox protects from other apps
   - Consider: Implement EncryptedSharedPreferences for sensitive deployments

3. **No Authentication**: Anyone with physical access can use the app
   - Consideration: Add biometric authentication for sensitive use cases

## Best Practices Implemented

1. **Minimal Permissions**: Only requests NFC permission
2. **Input Validation**: All user inputs are validated
3. **Safe Libraries**: Uses well-maintained libraries (Gson, AndroidX)
4. **Error Handling**: Try-catch blocks prevent crashes
5. **No Network**: Offline-only reduces attack surface
6. **Local Storage**: Data never leaves the device
7. **Type Safety**: Kotlin's type system prevents common errors

## Recommendations for Production Use

1. **Enable EncryptedSharedPreferences**: For storing sensitive contact data
2. **Add Biometric Authentication**: Optional lock screen protection
3. **Implement Modern Sharing**: Migrate from Android Beam to newer APIs
4. **Add Data Expiry**: Automatic cleanup of old contacts
5. **Implement Export/Import**: Allow secure backup/restore
6. **Add Contact Verification**: QR code or cryptographic signatures

## Security Reporting

If you discover a security vulnerability, please report it responsibly:
- Do not create public GitHub issues for security vulnerabilities
- Contact the repository owner directly
- Provide detailed information about the vulnerability
- Allow reasonable time for fixes before public disclosure
