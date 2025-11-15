# Architecture Documentation

## Overview
Connect is a simple Android application that follows a straightforward architecture pattern focused on separation of concerns.

## Architecture Pattern
The app uses a **Activity-based architecture** with clear separation between:
- **Data Layer**: Models and storage
- **NFC Layer**: NFC communication handling
- **UI Layer**: Activities and adapters

## Component Diagram

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                            │
├─────────────────────────────────────────────────────────┤
│  MainActivity          │  ProfileActivity                │
│  - Shows contact list  │  - Edit user profile           │
│  - Handles NFC events  │  - Save profile data           │
│                        │                                 │
│  ContactDetailActivity │  ContactsAdapter               │
│  - View contact info   │  - Display contact list        │
│  - Delete contacts     │  - Handle item clicks          │
└────────────┬───────────┴────────────┬───────────────────┘
             │                        │
             ▼                        ▼
┌─────────────────────────────────────────────────────────┐
│                   Business Logic                         │
├─────────────────────────────────────────────────────────┤
│  NFCManager                                              │
│  - Initialize NFC adapter                                │
│  - Create NDEF messages                                  │
│  - Read NDEF messages                                    │
│  - Enable/disable foreground dispatch                    │
└─────────────────────┬────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                            │
├─────────────────────────────────────────────────────────┤
│  Contact                                                 │
│  - Data model for contact information                    │
│  - Serializable for intent passing                       │
│                                                          │
│  ContactStorage                                          │
│  - Save/load user profile                                │
│  - Save/load/delete contacts                             │
│  - SharedPreferences + Gson                              │
└─────────────────────────────────────────────────────────┘
```

## Data Flow

### Profile Creation Flow
```
User Input → ProfileActivity → ContactStorage → SharedPreferences
                                                      ↓
                                                   Contact (JSON)
```

### NFC Sharing Flow (Sender)
```
User Profile → NFCManager → NDEF Message → NFC Hardware → Other Device
```

### NFC Receiving Flow
```
NFC Hardware → NDEF Message → NFCManager → Contact Model
                                              ↓
                                        ContactStorage
                                              ↓
                                      SharedPreferences
```

### Contact Display Flow
```
SharedPreferences → ContactStorage → Contact List → ContactsAdapter → RecyclerView
```

## Key Components

### MainActivity
**Responsibilities:**
- Display list of saved contacts
- Monitor NFC status
- Handle incoming NFC intents
- Navigate to ProfileActivity and ContactDetailActivity

**Lifecycle:**
- `onCreate()`: Initialize views and load contacts
- `onResume()`: Enable NFC and reload contacts
- `onPause()`: Disable NFC
- `onNewIntent()`: Handle NFC data

### ProfileActivity
**Responsibilities:**
- Display profile editing form
- Validate user input
- Save profile to storage

**Data Validation:**
- Name and surname are required
- Age must be numeric (optional)
- Email, phone, and URLs are optional

### ContactDetailActivity
**Responsibilities:**
- Display full contact information
- Allow contact deletion
- Show only non-empty fields

### NFCManager
**Responsibilities:**
- Manage NFC adapter initialization
- Create NDEF messages from Contact objects
- Parse NDEF messages into Contact objects
- Handle NFC lifecycle (enable/disable)

**NFC Protocol:**
- MIME type: `application/vnd.com.connect.app`
- Format: NDEF message with Gson-serialized Contact
- Android Beam for push (deprecated but supported)

### ContactStorage
**Responsibilities:**
- Persist user profile
- Persist contact list
- Provide CRUD operations
- Handle JSON serialization/deserialization

**Storage Format:**
```
SharedPreferences:
├── my_profile: JSON string of user's Contact
└── contacts: JSON array of Contact objects
```

### Contact
**Data Model:**
```kotlin
data class Contact(
    id: String,           // Unique identifier
    name: String,         // Required
    surname: String,      // Required
    age: Int,            // Optional
    gmail: String,       // Optional
    phone: String,       // Optional
    linkedIn: String,    // Optional
    github: String,      // Optional
    facebook: String,    // Optional
    twitter: String,     // Optional
    instagram: String,   // Optional
    website: String,     // Optional
    timestamp: Long      // Creation/update time
)
```

## Dependencies

### AndroidX Libraries
- `core-ktx`: Kotlin extensions
- `appcompat`: Backward compatibility
- `material`: Material Design components
- `constraintlayout`: Layout management
- `coordinatorlayout`: Advanced layouts
- `recyclerview`: List display
- `cardview`: Card UI components
- `lifecycle`: ViewModel and LiveData (for future use)

### Third-Party Libraries
- `gson`: JSON serialization/deserialization
- `room`: Database (included but not used yet)

## Threading Model
- **Main Thread**: All UI operations and NFC handling
- **No Background Threads**: App is simple enough to run on main thread
- **Future**: Consider background thread for large contact lists

## Storage Strategy

### Why SharedPreferences?
- Simple key-value storage
- Perfect for small datasets (dozens of contacts)
- No SQL overhead
- Easy serialization with Gson

### Limitations
- Not suitable for thousands of contacts
- No advanced querying
- No relationships between entities

### Future Migration Path
If needed, migrate to Room database:
```kotlin
@Entity
data class Contact(...)

@Dao
interface ContactDao {...}

@Database
abstract class ContactDatabase : RoomDatabase() {...}
```

## Security Considerations

### Data Protection
- SharedPreferences in private app storage
- No external storage access
- No network permissions
- Android app sandbox protection

### NFC Security
- Short-range communication (< 10cm)
- Ephemeral connection
- User must initiate tap
- Custom MIME type prevents accidental reading

## Testing Strategy

### Manual Testing
- Profile CRUD operations
- NFC sharing between devices
- Contact list display
- Configuration changes (rotation)
- Empty states

### Future Automated Testing
```kotlin
// Unit Tests
ContactStorageTest
ContactModelTest

// Integration Tests
NFCManagerTest

// UI Tests
MainActivityTest
ProfileActivityTest
```

## Performance Considerations

### Current Performance
- Fast startup (< 500ms)
- Instant contact loading (< 10 contacts)
- Smooth scrolling (RecyclerView)
- Low memory footprint (< 50MB)

### Scalability Limits
- SharedPreferences optimal for < 100 contacts
- RecyclerView handles < 1000 items smoothly
- JSON parsing overhead with large datasets

### Future Optimizations
- Pagination for large lists
- LazyLoading for contact details
- Room database for > 100 contacts
- Image caching for profile pictures (future feature)

## Known Technical Debt

1. **Android Beam Deprecation**
   - Android deprecated Beam in API 29 (Android 10)
   - Need to migrate to Android Nearby Share or Wi-Fi Direct
   - Current implementation works on Android 5.0 - 9.0

2. **No Error Recovery**
   - Failed NFC transfers are silently ignored
   - No retry mechanism
   - Consider adding user notifications

3. **No Data Backup**
   - Only local storage
   - No cloud sync
   - Consider implementing Android Backup Service

4. **Limited Input Validation**
   - Email format not validated
   - URLs not validated
   - Consider adding regex validation

## Future Enhancements

### Short Term
- Add contact search functionality
- Implement contact filtering/sorting
- Add profile pictures
- Dark theme support

### Medium Term
- Migrate to modern sharing APIs
- Add contact groups/categories
- Implement contact export/import
- Add biometric authentication

### Long Term
- Cloud sync (optional)
- Contact sharing via QR codes
- Business card scanning (OCR)
- vCard format support

## Development Guidelines

### Adding New Features
1. Update data model if needed (Contact.kt)
2. Add storage methods (ContactStorage.kt)
3. Create/update UI (Activity + Layout)
4. Update manifest if permissions needed
5. Test on real device
6. Update documentation

### Code Review Checklist
- [ ] Follows Kotlin conventions
- [ ] No memory leaks (check lifecycle)
- [ ] Handles configuration changes
- [ ] Proper error handling
- [ ] No hardcoded strings (use strings.xml)
- [ ] Accessibility support
- [ ] Works on different screen sizes

## Troubleshooting

### NFC Not Working
- Check device has NFC hardware
- Ensure NFC is enabled in Settings
- Verify app has NFC permission
- Test with another NFC app

### Contacts Not Saving
- Check SharedPreferences write permissions
- Verify Gson serialization
- Check for exceptions in logs
- Test with simple contact

### Build Failures
- Sync Gradle files
- Clean and rebuild
- Check Android SDK installation
- Verify Kotlin plugin version

## References
- [Android NFC Guide](https://developer.android.com/guide/topics/connectivity/nfc)
- [Material Design Guidelines](https://material.io/design)
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
