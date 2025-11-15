# Connect - NFC Contact Sharing App

Connect is an Android application that allows users to share their contact information using NFC (Near Field Communication) technology. Users can store their personal and professional information locally and share it instantly by tapping two Android phones together.

## Features

- **Personal Profile Management**: Store your name, surname, age, email, phone, and social media links
- **NFC Contact Sharing**: Share your contact information instantly by tapping phones together
- **Local Storage**: All contacts are stored locally on your device using SharedPreferences
- **Contact Management**: View, save, and delete received contacts
- **Social Media Integration**: Support for LinkedIn, GitHub, Facebook, Twitter, Instagram, and personal websites

## Requirements

- Android device with NFC capability
- Android API Level 21+ (Android 5.0 Lollipop or higher)
- NFC must be enabled on the device

## How to Use

1. **Setup Your Profile**:
   - Launch the app
   - Tap the edit (pencil) button at the bottom right
   - Fill in your personal information
   - Save your profile

2. **Share Your Contact**:
   - Ensure NFC is enabled on both devices
   - Open the Connect app on both phones
   - Tap the backs of the phones together
   - The contact information will be automatically transferred

3. **View Received Contacts**:
   - Received contacts appear in the main screen list
   - Tap any contact to view full details
   - Delete contacts by opening the detail view and tapping "Delete"

## Technical Details

- **Language**: Kotlin
- **Minimum SDK**: 21
- **Target SDK**: 34
- **Architecture**: Activity-based with local data persistence
- **Data Storage**: SharedPreferences with Gson serialization
- **NFC**: NDEF message format with custom MIME type

## Project Structure

```
app/
├── src/main/
│   ├── java/com/connect/app/
│   │   ├── data/
│   │   │   ├── Contact.kt          # Contact data model
│   │   │   └── ContactStorage.kt   # Local storage manager
│   │   ├── nfc/
│   │   │   └── NFCManager.kt       # NFC handling
│   │   └── ui/
│   │       ├── MainActivity.kt      # Main contacts list
│   │       ├── ProfileActivity.kt   # Profile editor
│   │       ├── ContactDetailActivity.kt  # Contact details
│   │       └── ContactsAdapter.kt   # RecyclerView adapter
│   ├── res/
│   │   ├── layout/                 # UI layouts
│   │   ├── values/                 # Resources (strings, colors, themes)
│   │   └── xml/                    # Backup rules
│   └── AndroidManifest.xml
```

## Building the Project

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## Permissions

The app requires the following permissions:
- `android.permission.NFC` - For reading and writing NFC tags

## License

This project is open source and available for personal and commercial use.