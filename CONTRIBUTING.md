# Contributing to Connect

Thank you for your interest in contributing to the Connect NFC Contact Sharing app!

## Getting Started

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or later
- Android SDK with API 21 (Android 5.0) minimum
- NFC-enabled Android device for testing
- Git for version control

### Setting Up Development Environment

1. Clone the repository:
```bash
git clone https://github.com/OnlyMaxon/Connect.git
cd Connect
```

2. Open the project in Android Studio

3. Sync Gradle files and download dependencies

4. Connect an NFC-enabled Android device or use an emulator with NFC support

5. Build and run the app:
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Code Style

### Kotlin Style Guide
- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused
- Add comments for complex logic only

### Android Best Practices
- Follow Material Design guidelines
- Use AndroidX libraries
- Implement proper lifecycle management
- Handle configuration changes appropriately

### Code Organization
```
app/src/main/java/com/connect/app/
├── data/           # Data models and storage
├── nfc/            # NFC-related functionality
├── ui/             # Activities and UI components
└── utils/          # Utility classes (if needed)
```

## Making Changes

### Branch Naming
- Feature: `feature/description`
- Bug fix: `bugfix/description`
- Documentation: `docs/description`

### Commit Messages
- Use clear, descriptive commit messages
- Start with a verb (Add, Fix, Update, Remove, etc.)
- Keep the first line under 50 characters
- Add detailed description if needed

Example:
```
Add biometric authentication for profile access

- Implement BiometricPrompt for Android 9+
- Add fallback to device credentials
- Update settings to enable/disable feature
```

### Pull Request Process

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly on multiple devices
5. Update documentation if needed
6. Submit a pull request

#### PR Requirements
- [ ] Code builds without errors
- [ ] App runs on target devices
- [ ] No new lint warnings
- [ ] Changes are tested manually
- [ ] Documentation is updated
- [ ] Commit messages are clear

## Testing

### Manual Testing
1. Install the app on an NFC-enabled device
2. Test all user flows:
   - Profile creation and editing
   - NFC sharing between two devices
   - Contact viewing and deletion
   - App lifecycle (rotation, background, etc.)

### Test Coverage
- Test on different Android versions (API 21 - latest)
- Test on different screen sizes
- Test with NFC enabled and disabled
- Test with empty state and populated contacts

## Areas for Contribution

### High Priority
- Migrate from deprecated Android Beam to modern sharing APIs
- Implement encrypted storage for sensitive data
- Add biometric authentication
- Improve error handling and user feedback

### Medium Priority
- Add contact search and filtering
- Implement contact export/import
- Add contact categories/tags
- Improve UI/UX with animations

### Low Priority
- Add dark theme support
- Implement contact sharing via QR codes
- Add contact statistics
- Internationalization (i18n)

## Reporting Issues

### Bug Reports
Include:
- Android version
- Device model
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots if applicable

### Feature Requests
Include:
- Clear description of the feature
- Use case and benefits
- Potential implementation approach
- Any relevant examples

## Code Review Process

1. Maintainers will review PRs within 1-2 weeks
2. Address any feedback or requested changes
3. Once approved, your PR will be merged
4. Your contribution will be credited in the release notes

## Questions?

- Open an issue for general questions
- Check existing issues and PRs first
- Be respectful and constructive

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

Thank you for contributing to Connect! 🚀
