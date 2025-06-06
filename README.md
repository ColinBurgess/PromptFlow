# PromptFlow 📱

> **Professional Android Teleprompter with Cloud Sync**

A modern, feature-rich teleprompter application for Android with Google Sign-In integration and cloud synchronization capabilities.

## ✨ Features

### 🎬 **Core Teleprompter**
- **Smooth Auto-Scroll**: Variable speed control (1x-25x)
- **Adjustable Font Size**: 16sp to 48sp for optimal readability
- **Professional Controls**: Play/pause, reset, speed adjustment
- **Landscape Optimized**: Full-screen experience with transparent controls

### 📚 **Text Library Management**
- **Local Storage**: Save texts locally when not signed in
- **Google Drive Integration**: Cloud storage and sync across devices *(UI ready, API implementation pending)*
- **Easy Text Management**: Create, select, and delete saved texts
- **Automatic Migration**: Local texts migrate to cloud when signing in

### 🔐 **Authentication & Sync**
- **Google Sign-In**: Secure authentication with Firebase
- **Cross-Device Sync**: Access your texts from any device *(planned)*
- **Offline-First**: Works perfectly without internet connection
- **Smart Migration**: Seamless transition from local to cloud storage

### 🎨 **Modern UI/UX**
- **Material Design 3**: Beautiful, adaptive interface
- **Dark Theme**: Professional teleprompter appearance
- **Intuitive Navigation**: Tab-based interface for easy access
- **Error Handling**: User-friendly error messages with recovery options

## 🚀 Quick Start

### Installation
1. Clone the repository
```bash
git clone https://github.com/ColinBurgess/PromptFlow.git
cd PromptFlow
```

2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on device or emulator with Google Play Services

### Usage
1. **Without Sign-In**: Start using immediately with local text storage
2. **With Google Account**: Sign in for cloud sync and cross-device access
3. **Create Texts**: Use the Text Editor tab to write your scripts
4. **Save & Organize**: Build your personal teleprompter library
5. **Present**: Select any saved text and start teleprompter mode

## 🛠️ Technical Stack

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM with StateFlow
- **Authentication**: Firebase Auth + Google Sign-In
- **Local Storage**: SharedPreferences + Gson
- **Cloud Storage**: Google Drive (in development)
- **Navigation**: Navigation Compose
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## 📱 Screenshots & Demo

### Main Teleprompter Interface
- Full-screen text display with smooth scrolling
- Speed and font size controls at bottom
- Professional black background for optimal reading

### Settings & Library
- **Text Editor**: Write and edit teleprompter scripts
- **Defaults**: Configure speed and font size preferences
- **Library**: Manage saved texts with local/cloud indicators
- **Account**: Google Sign-In with professional branding

### Error Handling
- Clear, actionable error messages
- Step-by-step guidance for Google account setup
- Retry mechanisms with visual feedback

## 🔧 Development Setup

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK 34
- Google Play Services enabled device/emulator

### Firebase Configuration
1. Create Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add Android app with package `com.promptflow.android`
3. Download `google-services.json` to `app/` directory
4. Enable Authentication → Google provider
5. Configure SHA-1 fingerprint:
```bash
./gradlew signingReport
```

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

## 🎯 Roadmap

### ✅ v1.0.0 (Current)
- [x] Core teleprompter functionality
- [x] Google Sign-In authentication
- [x] Local text storage and management
- [x] Material Design 3 interface
- [x] Professional error handling

### 🚧 v1.1.0 (Next Release)
- [ ] **Real Google Drive API integration**
- [ ] **Edit existing texts functionality**
- [ ] **Search and filter in library**
- [ ] **Text sharing capabilities**
- [ ] **Advanced teleprompter settings**

### 🔮 v2.0.0 (Future)
- [ ] **Real-time sync across devices**
- [ ] **Collaborative text editing**
- [ ] **Remote control via companion app**
- [ ] **Advanced presentation features**
- [ ] **Analytics and reading metrics**

## 🐛 Known Issues

### Current Limitations
- **Google Drive**: UI shows "Google Drive" but files are currently stored locally
- **Text Editing**: Can only create new texts, editing existing ones pending
- **Device Sync**: Real-time synchronization not yet implemented

### Workarounds
- All functionality works perfectly with local storage
- Texts "migrate" to cloud state when signing in (UI simulation)
- Manual refresh required when switching between devices

## 🤝 Contributing

We welcome contributions! Please see our contribution guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Standards
- **Language**: All code, comments, and documentation in English
- **Architecture**: Follow MVVM pattern with Compose
- **Style**: Android Kotlin Style Guide
- **Testing**: Include unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Colin Moreno-Burgess** ([@ColinBurgess](https://github.com/ColinBurgess))

## 🙏 Acknowledgments

- **Firebase Team** for authentication services
- **Google Material Design** for UI components
- **Jetpack Compose Team** for modern Android UI toolkit
- **Android Developer Community** for best practices and patterns

---

### 📊 Project Status

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Language](https://img.shields.io/badge/language-Kotlin-purple)
![License](https://img.shields.io/badge/license-MIT-blue)

**Active Development** | **Stable for Daily Use** | **Cloud Integration In Progress**