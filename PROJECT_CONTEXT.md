# PromptFlow - Project Context

**Document Version: 1.1**
**Last Updated:** June 6, 2025

## 1. 📱 Project Overview

### 1.1. Project Description
PromptFlow is a professional Android teleprompter application with cloud synchronization. It allows users to create, edit, and view texts that scroll automatically on the screen, ideal for presentations, video recordings, and speeches. **Now with full support for horizontal and vertical orientation, especially optimized for tablets with efficient adaptive layouts.**

### 1.2. Key Features

#### ✅ **Implemented and Working**
-   **Teleprompter Core**: Smooth text scrolling with speed controls (1x-25x) and font size (16sp-48sp).
-   **Optimized Multi-Orientation**: Full support for horizontal and vertical orientation with device-specific adaptive layouts.
-   **Optimized Horizontal Layout**: Compact TopAppBar (48dp), side tabs without cut-off text, elimination of blank spaces.
-   **Tablet Optimization**: Specific interface for tablets in vertical mode with side controls.
-   **Adaptive Interface**: Automatically detects screen size and orientation to display the optimal layout.
-   **Google Authentication**: Full integration with Firebase Auth and Google Sign-In.
-   **Local Storage**: Robust system for saving/loading texts using SharedPreferences + Gson.
-   **Text Library**: Full management (create, edit, delete, select texts).
-   **Error Handling**: User-friendly messages with complete debugging.
-   **Interface States**: Informative empty states, loading states, error recovery.
-   **Configuration**: Persistence of default speed and font size.
-   **Localization**: Improved Spanish interface for better accessibility (Note: This document is in English, but the app supports Spanish).
-   **Responsive UI**: All elements adapt correctly without cut-off text.

#### 📱 **Multi-Orientation Support (Optimized December 2024)**
-   **Phones Horizontal**: Traditional layout with controls at the bottom.
-   **Phones Vertical**: Traditional tab navigation with teleprompter controls at the bottom.
-   **Tablets Horizontal**:
    -   Compact TopAppBar (48dp vs 56dp standard) to leverage vertical space.
    -   Optimized side tabs (140dp width, maxLines=2, TextOverflow.Ellipsis).
    -   Removal of excessive padding in the content area.
    -   2-column layout in settings for better space utilization.
-   **Tablets Vertical**: Side control panel that does not interfere with text reading.
-   **Automatic Detection**: Layout change based on orientation and screen size (>600dp for tablets).

#### 🚧 **Partially Implemented**
-   **Google Drive Integration**:
    -   ✅ UI updated to show "Google Drive".
    -   ✅ Simulation of local → cloud migration.
    -   ❌ Real Google Drive API pending.
    -   ❌ Creation of .txt files in Drive pending.

#### 📋 **Pending Implementation**
-   **Real Google Drive API**: Full implementation of upload/download of .txt files.
-   **Bidirectional Synchronization**: Detection of changes between devices.
-   **Text Editing**: Functionality to edit existing texts.
-   **Search**: Filtering of texts in the library.
-   **Export/Import**: Functions for backup and restore.
-   **Vertical Controls**: Custom vertical sliders for tablets in vertical mode.

## 2. 🏗️ Technical Architecture

### 2.1. Architecture Pattern
-   **MVVM (Model-View-ViewModel)**
    -   **View**: Jetpack Compose Composables with adaptive layouts.
    -   **ViewModel**: State management with StateFlow.
    -   **Model**: Data classes and repositories.

### 2.2. Core Technologies
-   **UI Framework**: Jetpack Compose + Material Design 3.
-   **Responsive Design**: `LocalConfiguration` for orientation and size detection.
-   **Architecture**: Android Architecture Components.
-   **State**: StateFlow + Coroutines.
-   **Navigation**: Navigation Compose + NavigationRail for tablets.
-   **Authentication**: Firebase Auth + Google Sign-In.
-   **Local Storage**: SharedPreferences + Gson.
-   **Cloud Storage**: Firebase (migration to Google Drive in progress).

### 2.3. Java/JDK Configuration
-   **Java Version**: OpenJDK 21 (included with Android Studio).
-   **JAVA_HOME (Typical)**: `/Applications/Android Studio.app/Contents/jbr/Contents/Home` (macOS) or equivalent for other OS.
-   **Configuration Example**: `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
-   **Advantages**: Uses the same Java version as Android Studio, ensures compatibility.

### 2.4. Data Architecture (Key Data Classes)
```kotlin
data class SavedText(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
    val userId: String, // For associating with a logged-in user
    val isLocal: Boolean, // True if only stored locally
    val driveFileId: String? = null // Google Drive file ID if synced
)

data class AuthState( // Represents ViewModel state for authentication
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

data class TextLibraryState( // Represents ViewModel state for text library
    val isLoading: Boolean = false,
    val savedTexts: List<SavedText> = emptyList(), // All texts (local or cloud-synced indication via isLocal/driveFileId)
    val localTexts: List<SavedText> = emptyList(), // Potentially a subset if distinguishing is needed
    val error: String? = null
)
```

### 2.5. Layout Architecture (Adaptive Logic)
```kotlin
// Configuration detection within a Composable
val configuration = LocalConfiguration.current
val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
val screenWidth = configuration.screenWidthDp.dp
val isTablet = screenWidth > 600.dp // Breakpoint for tablet UI

// Example of adaptive layout switching
if (isLandscape || !isTablet) { // Phone in any orientation, or Tablet in Landscape
    LandscapeOrPhoneLayout()
} else { // Tablet in Portrait
    PortraitTabletLayout()
}
```

### 2.6. Directory and File Structure

#### Root Directory: `/` (Project Root)
*   **Files:** `build.gradle.kts`, `settings.gradle.kts`, etc. (Gradle project setup)

#### Directory: `/app`
*   **Purpose:** Main module of the Android application.
    *   **File:** `/app/build.gradle.kts`
        *   **Description:** Gradle build configuration for the app module. Defines dependencies, plugins, and build settings.
        *   **Key Configuration:** Android SDK versions, Kotlin version, application ID, and main dependencies list.
    *   **File:** `/app/google-services.json`
        *   **Description:** Google services configuration file for integrating Firebase and Google APIs. (Not committed to VCS).
    *   **File:** `/app/proguard-rules.pro`
        *   **Description:** ProGuard rules for code obfuscation and optimization in production builds.

#### Directory: `/app/src/main`
*   **Purpose:** Main source code and resources for the Android app.
    *   **File:** `/app/src/main/AndroidManifest.xml`
        *   **Description:** Defines main app components (activities, services), permissions (e.g., internet, potentially Google Drive access in future), and intent filters. Key attribute: `android:configChanges="orientation|screenSize|keyboardHidden"` for handling orientation changes.

#### Directory: `/app/src/main/java/com/promptflow/android`
*   **Purpose:** Root package for the app's Kotlin source code.
    *   **File:** `/app/src/main/java/com/promptflow/android/MainActivity.kt`
        *   **Description:** Main activity and entry point of the application. Initializes the Jetpack Compose UI and manages high-level navigation.
        *   **Key Classes/Functions:**
            *   `MainActivity`: Sets up the main Composable content (`PromptFlowApp`).

    ##### Subdirectory: `/app/src/main/java/com/promptflow/android/ui/screen`
    *   **Purpose:** Contains Composable functions representing different screens of the application.
        *   **File:** `/app/src/main/java/com/promptflow/android/ui/screen/TeleprompterScreen.kt`
            *   **Description:** The core teleprompter screen. Manages text display, scrolling, speed/font controls. Implements adaptive layouts for different orientations and device types (phone/tablet).
            *   **Key Composables/Functions:**
                *   `TeleprompterScreen(...)`: Main entry Composable for this screen.
                *   `LandscapeLayout(...)`, `PortraitTabletLayout(...)`: Specific layout Composables selected based on device configuration.
                *   Controls for scroll speed, font size, play/pause.
        *   **File:** `/app/src/main/java/com/promptflow/android/ui/screen/SettingsScreen.kt`
            *   **Description:** Screen for application settings. For tablets, it uses a `NavigationRail` for tab navigation (Editor, Settings, Library, Account). For phones, it uses traditional bottom or top tabs.
            *   **Key Composables/Functions:**
                *   `SettingsScreen(...)`: Main entry Composable.
                *   `TabletLandscapeLayout(...)`, `TraditionalTabLayout(...)`: Adaptive layout choices.
                *   Composables for individual tabs: `LibraryTabHorizontal`, `AccountTabHorizontal`, etc.
        *   **File:** `/app/src/main/java/com/promptflow/android/ui/screen/TextLibraryScreen.kt`
            *   **Description:** Screen for managing the user's library of saved texts. Allows creating, viewing, selecting, and deleting texts.
            *   **Key Composables/Functions:**
                *   `TextLibraryScreen(...)`: Main entry Composable.
                *   `TextItem(...)`: Composable for displaying a single text entry in the list.
                *   `EmptyLibraryMessage(...)`: Shown when the library is empty.
        *   **File:** `/app/src/main/java/com/promptflow/android/ui/screen/LoginScreen.kt`
            *   **Description:** Screen for user authentication using Google Sign-In via Firebase. Displays login benefits and handles authentication flow.
            *   **Key Composables/Functions:**
                *   `LoginScreen(...)`: Main entry Composable.
                *   Handles UI updates based on `AuthState` from `AuthenticationViewModel`.

    ##### Subdirectory: `/app/src/main/java/com/promptflow/android/ui/theme`
    *   **Purpose:** Theming and styling.
        *   **File:** `/app/src/main/java/com/promptflow/android/ui/theme/Theme.kt`
            *   **Description:** Defines the Material Design 3 theme for the application, including color palettes, typography, and shapes.
        *   **File:** `/app/src/main/java/com/promptflow/android/ui/theme/Color.kt`, `/app/src/main/java/com/promptflow/android/ui/theme/Type.kt`
            *   **Description:** Specific color definitions and typography scales.

    ##### Subdirectory: `/app/src/main/java/com/promptflow/android/viewmodel`
    *   **Purpose:** Contains ViewModels responsible for preparing and managing UI-related data.
        *   **File:** `/app/src/main/java/com/promptflow/android/viewmodel/AuthenticationViewModel.kt`
            *   **Description:** Manages the state and logic for user authentication (Google Sign-In with Firebase). Exposes `AuthState`.
            *   **Key Functions:**
                *   `signInWithGoogle(context)`: Initiates the Google Sign-In flow.
                *   `signOut()`: Signs out the current user.
        *   **File:** `/app/src/main/java/com/promptflow/android/viewmodel/TextLibraryViewModel.kt`
            *   **Description:** Manages the state and logic for the text library. Handles loading, saving, deleting texts (locally via SharedPreferences/Gson, and simulated Google Drive interaction). Exposes `TextLibraryState`.
            *   **Key Functions:**
                *   `saveText(title, content)`: Saves a new text.
                *   `deleteText(textId)`: Deletes an existing text.
                *   `loadTexts()`: Loads texts from storage.
                *   `migrateLocalTextsToCloud()`: Simulates migration.
                *   `saveToGoogleDrive(title, content)`: Simulates saving to Drive.

#### Directory: `/app/src/main/res`
*   **Purpose:** Application resources (drawables, mipmaps for icons, values like strings.xml).

### 2.7. Key Components/Modules (Logical Grouping)

*   **Module: UI & Presentation (Jetpack Compose)**
    *   **Files:** All files under `/app/src/main/java/com/promptflow/android/ui/`
    *   **Responsibility:** Rendering the user interface, handling user input at the UI level, adapting layouts based on device configuration. Navigation between screens.

*   **Module: State Management & Business Logic (ViewModels)**
    *   **Files:** All files under `/app/src/main/java/com/promptflow/android/viewmodel/`
    *   **Responsibility:** Holding and managing UI-related data, exposing state via `StateFlow`, handling business logic (e.g., authentication, text storage operations), interacting with data sources/repositories (though repositories are not explicitly detailed here, ViewModels would use them).

*   **Module: Authentication**
    *   **Files:** `AuthenticationViewModel.kt`, `LoginScreen.kt`, relevant Firebase SDKs.
    *   **Responsibility:** Managing user sign-in, sign-out, and authentication state with Google/Firebase.

*   **Module: Text Management & Storage**
    *   **Files:** `TextLibraryViewModel.kt`, `TextLibraryScreen.kt`, `TeleprompterScreen.kt` (for displaying selected text).
    *   **Responsibility:** CRUD operations for user texts, local persistence (SharedPreferences/Gson), and planned Google Drive synchronization.

### 2.8. Key External Dependencies and Their Usage
```kotlin
// Core Android & Jetpack Compose (UI Framework)
implementation("androidx.core:core-ktx:1.12.0") // Kotlin extensions for core Android
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // Lifecycle-aware components
implementation("androidx.activity:activity-compose:1.8.2") // Integration for Jetpack Compose in Activities
implementation(platform("androidx.compose:compose-bom:2023.08.00")) // Compose Bill of Materials
implementation("androidx.compose.ui:ui") // Core Compose UI
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview") // For previews in Android Studio
implementation("androidx.compose.material3:material3") // Material Design 3 components
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // ViewModel integration with Compose
implementation("androidx.navigation:navigation-compose:2.7.5") // Navigation within Compose

// Firebase & Authentication
implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Firebase Bill of Materials
implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication (Kotlin extensions)
implementation("androidx.credentials:credentials:1.2.0") // AndroidX Credentials Manager (for Google Sign-In)
implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0") // Google Identity Services (for Google Sign-In)

// Local Storage
implementation("com.google.code.gson:gson:2.10.1") // For serializing/deserializing objects to/from JSON for SharedPreferences

// Google Drive API (Future - Currently commented out or simulated)
// TODO: Add Google Drive API dependencies when fully implemented, e.g.:
// implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
// implementation("com.google.api-client:google-api-client-android:2.0.0")
// implementation("com.google.http-client:google-http-client-gson:1.42.3")
```
*   **Jetpack Compose:** Used as the primary UI toolkit for building the entire user interface declaratively.
*   **Material Design 3:** Provides the visual components, styling, and theming for the app.
*   **Navigation Compose:** Handles in-app navigation between different Composable screens.
*   **Lifecycle ViewModel Compose:** Integrates ViewModels with Composables, allowing UI to observe and react to state changes.
*   **Firebase Auth & Google Sign-In (via `androidx.credentials` and `googleid`):** Used for authenticating users with their Google accounts.
*   **Gson:** Used to serialize/deserialize `SavedText` objects into JSON strings for storage in SharedPreferences.
*   **(Future) Google Drive API:** Will be used to upload/download user text files to/from their Google Drive.

## 3. 🔧 Project Configuration Details

### 3.1. Orientation Configuration
-   **AndroidManifest.xml:**
    -   `android:screenOrientation="sensorPortrait"` (or `fullSensor` if both are desired by default and handled adaptively)
    -   `<activity android:name=".MainActivity" ... android:configChanges="orientation|screenSize|screenLayout|keyboardHidden|smallestScreenSize">` to allow manual handling of configuration changes in Compose.
-   **Adaptive Layouts Logic (in Composables):**
    -   Uses `LocalConfiguration.current` to get `configuration.orientation` and `configuration.screenWidthDp`.
-   **Breakpoints:**
    -   Tablets: `screenWidthDp > 600.dp`.
    -   Orientation detection: `configuration.orientation == Configuration.ORIENTATION_LANDSCAPE` or `Configuration.ORIENTATION_PORTRAIT`.

### 3.2. Firebase Setup
-   **Project Name (Firebase Console):** `promptflow-55398`
-   **Application Package Name:** `com.promptflow.android`
-   **Web Client ID (for Google Sign-In):** `421864875906-257v99qbn9v2sn2vud8edjjb1o92gkg7.apps.googleusercontent.com` (Should be stored securely, e.g., in `strings.xml` or build config, not hardcoded directly if possible, or fetched from `google-services.json` processing).
-   **SHA-1 Debug Fingerprint:** `2F:10:AD:A8:9F:DC:F2:84:7D:49:C0:1E:5A:B2:71:C3:57:42:7E:B4` (Ensure this is added to the Firebase project settings for debug builds).

## 4. 🚀 Current Application State

### 4.1. Multi-Device Experience

#### **Phones in Vertical**
1.  Navigation by tabs at the bottom.
2.  Teleprompter with controls at the bottom.
3.  Text centered for better readability.

#### **Phones in Horizontal**
1.  Full-screen teleprompter.
2.  Floating controls that auto-hide.
3.  Immersive experience for presentations.

#### **Tablets in Vertical**
1.  **Teleprompter**: Side control panel (approx. 120dp) that doesn't block text.
2.  **Settings/Other Screens**: NavigationRail for efficient side navigation.
3.  **Text Editor (if separate)**: Larger area with better line spacing.
4.  **Text in Teleprompter**: Typically left-aligned for better readability in vertical mode on wider screens.

#### **Tablets in Horizontal**
1.  **Teleprompter**: Similar to phones but optimized for large screens, possibly with more controls visible.
2.  **Settings/Other Screens**: NavigationRail on the side to leverage horizontal space.
3.  **Controls**: Better spacing and optimized button sizes.

### 4.2. User Flow Without Login
1.  User opens app → Adaptive `TeleprompterScreen` based on device/orientation.
2.  User navigates to Settings → Adaptive layout (tabs for phone, NavigationRail for tablet).
3.  User navigates to Library → Sees "Local Storage" (or equivalent UI), can save texts locally.
4.  **Rotation**: App automatically adapts layout without losing current text or settings state (due to ViewModel and Compose state handling).

### 4.3. User Flow With Login
1.  User navigates to Account Tab/Login Screen.
2.  User clicks "Continue with Google" → Google Sign-In flow initiated.
3.  Upon successful authentication:
    *   UI updates to show user is logged in.
    *   Library might indicate "Google Drive" availability or sync status.
4.  **Orientation**: All layouts adapt while maintaining login state and cloud-related functionality.

### 4.4. Implemented Error Handling
-   **No Google accounts on device**: Detailed message with steps to add a Google account.
-   **Sign-in process canceled by user**: Friendly message, option to retry.
-   **Network errors during sign-in/future cloud operations**: Connectivity check, retry options.
-   **Loading states**: Visual indicators (e.g., progress bars) during async operations like sign-in or loading texts.
-   **Rotation & State**: Error messages and loading states are preserved across orientation changes.

## 5. 📊 Debugging and Logging

### 5.1. Implemented Debug System (Logcat)
```kotlin
// Example log messages using emojis for easy filtering
println("🔵 AUTH: Starting Google Sign-In process...")
println("🔍 AUTH: CredentialManager created successfully.")
println("✅ GDrive: Text saved to Google Drive (simulated): $title")
println("❌ GDrive: Error saving to Drive (simulated): ${e.message}")
println("📱 LAYOUT: Detected ${if (isTablet) "Tablet" else "Phone"} - ${if (isLandscape) "Landscape" else "Portrait"}")
```

### 5.2. Recommended Logcat Filters
-   **Tag:** `PromptFlow` (if a consistent tag is used in `Log.d("PromptFlow", ...)`).
-   **Package:** `com.promptflow.android` (using "Edit Filter Configuration" in Logcat).
-   **String Search (for emojis or keywords):** `🔵|🔍|✅|❌|📱` or `AUTH:|GDRIVE:|LAYOUT:`.

### 5.3. Compilation and Build Commands (Gradle Wrapper)
```bash
# Configure JAVA_HOME (if not already set for the terminal session)
# Example for macOS with Android Studio default JBR:
# export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Verify Java version (should match Android Studio's JBR, e.g., OpenJDK 21)
java -version

# Clean project
./gradlew clean

# Build project (assembles debug and release, runs linters, tests)
./gradlew build

# Assemble debug APK
./gradlew assembleDebug

# Install debug APK on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests (Android Tests)
./gradlew connectedDebugAndroidTest
```

## 6. 🎯 Priority Next Steps

### 6.1. Implement Real Google Drive Integration (High Priority)
```kotlin
// Target: TextLibraryViewModel.kt and new GoogleDriveService.kt
// - Implement OAuth 2.0 token handling (refresh, secure storage).
// - Use Google Drive REST API (e.g., via google-api-services-drive library).
// - Create a dedicated "PromptFlow" app folder in user's Drive.
// - Implement file upload (.txt format) with metadata.
// - Implement file download, listing, and parsing from Drive.
// - Handle API errors, rate limits, and offline scenarios gracefully.
// - Implement robust bidirectional synchronization logic:
//   - Detect local vs. remote changes (timestamps, checksums).
//   - Conflict resolution strategy (e.g., last write wins, user prompt).
```

### 6.2. Enhance Multi-Orientation UX (Medium Priority)
```kotlin
// Target: Various Screen Composables
// - Design and implement custom vertical sliders for speed/font size on tablets in portrait mode.
// - Explore orientation-specific navigation gestures (e.g., swipe to dismiss controls).
// - Implement smooth animated transitions between layout changes due to orientation.
// - Consider allowing users to set layout preferences per device type/orientation.
```

### 6.3. General Optimizations and Polish (Low Priority)
```kotlin
// Target: Various parts of the codebase
// - Performance: Implement lazy loading for very large text libraries.
// - Offline-First: Further improve offline capabilities and sync reliability.
// - Background Sync: Investigate background synchronization for Google Drive.
// - UI Polish: Refine animations, transitions, and micro-interactions.
// - Analytics: Add basic usage analytics (e.g., feature usage by orientation) if desired.
```

## 7. 🔐 Security Configuration

### 7.1. Firebase Security Rules (For Firestore/Realtime Database - if used)
*(Note: Currently, the app uses SharedPreferences and plans Google Drive. If Firestore were used for metadata or other features, these rules would be relevant.)*
```javascript
// Example for Firestore if user texts metadata were stored there
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own text metadata
    match /users/{userId}/textMetadata/{textId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```
-   **Google Drive:** Access is managed via OAuth 2.0 scopes. The app will request specific permissions (e.g., `drive.file` or `drive.appdata`) to access only files/folders it creates or is explicitly granted access to.
-   **API Keys & Client IDs:** Store sensitive keys securely (e.g., not directly in version-controlled code, use `local.properties`, build secrets, or obfuscation). The `google-services.json` file is typically not committed.

## 8. 📱 Device Compatibility

### 8.1. Supported Screen Sizes (based on `screenWidthDp`)
-   **Small Phones (< 600dp):** Standard phone layout.
-   **Large Phones / Small Tablets (approx. 600dp - 839dp):** Tablet layout (e.g., with NavigationRail if applicable).
-   **Large Tablets (>= 840dp):** Full tablet experience, potentially with more content visible or different control arrangements.

### 8.2. Supported Orientations & Layouts
-   ✅ **Phone Portrait**: Typically bottom tabs, teleprompter controls at bottom.
-   ✅ **Phone Landscape**: Often full-screen teleprompter, auto-hiding floating controls.
-   ✅ **Tablet Portrait**: Side NavigationRail, teleprompter with side control panel.
-   ✅ **Tablet Landscape**: Side NavigationRail, teleprompter optimized for wide view.

### 8.3. Tested Resolutions (Examples)
-   **Phones**: 360x640dp, 411x731dp (Pixel 3a), 390x844dp (iPhone 13-like)
-   **Tablets**: 600x960dp (Nexus 7 Portrait), 768x1024dp (iPad Portrait-like), 1280x800dp (Pixel Tablet Landscape)

## 9. 🏆 Technical Achievements

### 9.1. Robust Responsive Design
-   Automatic detection of orientation and screen size using `LocalConfiguration`.
-   Fully adaptive layouts for phones and tablets in both orientations, often without significant code duplication in core logic, leveraging Composable flexibility.
-   Smooth transitions (or near-smooth) between orientations without loss of critical state (e.g., current text, scroll position, settings).

### 9.2. Optimized User Experience (UX) per Form Factor
-   Controls are positioned optimally for reachability and visibility in each orientation/device type.
-   Text alignment in teleprompter (e.g., center for narrow phone landscape, left for wider tablet portrait) considers readability.
-   Navigation patterns (bottom tabs vs. NavigationRail) are intuitive and appropriate for the screen real estate.

### 9.3. Scalable MVVM Architecture
-   Modular Composable UI components that are reusable and testable.
-   Centralized state management in ViewModels, decoupling UI from business logic.
-   Clear separation of concerns, making the codebase easier to maintain and extend with new features or further orientation-specific enhancements.

## 10. 📝 Development Notes

### 10.1. Useful Gradle Commands
```bash
./gradlew clean               # Clean build directory
./gradlew build               # Build and run checks
./gradlew assembleDebug       # Create debug APK
./gradlew installDebug        # Install debug APK on connected device/emulator
./gradlew signingReport       # Display SHA-1, SHA-256 fingerprints for app signing
./gradlew testDebugUnitTest   # Run local unit tests
./gradlew connectedDebugAndroidTest # Run instrumented tests on device/emulator
```

### 10.2. Critical Files *Not* to Commit to Version Control
-   `app/google-services.json` (contains Firebase project identifiers and API keys).
-   `local.properties` (contains SDK paths, potentially API keys if added by user).
-   Any `.env` files if used for local development secrets.
-   Keystore files (`*.jks`, `*.keystore`) used for signing release builds.

### 10.3. Key Reference URLs
-   **Firebase Console:** [https://console.firebase.google.com/project/promptflow-55398](https://console.firebase.google.com/project/promptflow-55398)
-   **Google Drive API Documentation:** [https://developers.google.com/drive/api/v3/reference](https://developers.google.com/drive/api/v3/reference)
-   **Material Design 3 Guidelines:** [https://m3.material.io/](https://m3.material.io/)
-   **Jetpack Compose Documentation:** [https://developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose)
-   **Android Responsive Design:** [https://developer.android.com/guide/topics/large-screens/overview](https://developer.android.com/guide/topics/large-screens/overview)

---

**Overall Status:** ✅ Compiling, basic features functional. 🚧 Google Drive integration is the main pending high-priority item.
**Next Immediate Priority:** Full implementation of Google Drive API for text synchronization.