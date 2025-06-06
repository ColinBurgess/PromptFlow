# PromptFlow - Project Context 📋

> **AI Assistant Context File** - This file contains essential project information to maintain continuity between conversations.

## 📋 General Information

- **Project Name:** PromptFlow
- **Description:** Modern Android teleprompter application
- **Owner:** Colin Moreno-Burgess (@ColinBurgess)
- **Repository:** https://github.com/ColinBurgess/PromptFlow
- **Code Language:** English (user rule)
- **Chat Communication:** Spanish (user rule)
- **Start Date:** June 6, 2025

## 🎯 Design Decisions Made

### Application Name
- **Chosen:** PromptFlow
- **Reasons:**
  - Suggests smooth text flow
  - Easy to remember and pronounce
  - Professional and modern
- **Rejected:** SpeechCue, TeleScript, FlowText, CueCard

### Selected Technologies
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM (prepared for implementation)
- **Database:** Room (dependency added)
- **Theme:** Material Design 3
- **Target SDK:** 34
- **Min SDK:** 24

### Orientation and UX
- **Orientation:** Forced landscape (typical for teleprompters)
- **Background:** Black (professional for teleprompters)
- **Text:** White on black
- **Controls Location:** Bottom with transparency

## 🚀 Current Development Status

### ✅ Completed
- [x] Initial Android project structure
- [x] Gradle configuration with all dependencies
- [x] Main screen (`TeleprompterScreen`) with basic functionality
- [x] Theme system (Color, Typography, Theme)
- [x] MainActivity with Jetpack Compose
- [x] AndroidManifest with permissions and configuration
- [x] Resources (strings.xml, themes.xml)
- [x] Git repository initialized
- [x] GitHub repository created and synced
- [x] Bilingual README (Spanish/English)
- [x] Android .gitignore

### 🎛️ Implemented Features
- [x] **Playback control:** Play/Pause with FAB
- [x] **Adjustable speed:** Slider from 1x to 10x
- [x] **Font size:** Slider from 16sp to 48sp
- [x] **Text editor:** Settings panel with TextField
- [x] **Auto-scroll:** Smooth animation based on speed
- [x] **Reset:** Functionality to restart position
- [x] **Responsive UI:** Well-distributed controls in landscape

## 🏗️ Code Architecture

### Package Structure
```
com.promptflow.android/
├── MainActivity.kt
└── ui/
    ├── screen/
    │   └── TeleprompterScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

### Main Components
- **MainActivity:** Main Activity with Scaffold and Surface
- **TeleprompterScreen:** Main Composable with all logic
- **PromptFlowTheme:** Custom theme with Material Design 3

### Managed State
- `isPlaying: Boolean` - Playback control
- `speed: Float` - Scroll speed (1f-10f)
- `fontSize: Float` - Font size (16f-48f)
- `showSettings: Boolean` - Settings panel visibility
- `text: String` - Teleprompter content
- `listState: LazyListState` - Scroll state

## 📋 TODOs and Next Steps

### 🔥 High Priority
- [ ] **Mirror mode:** Implement mirrored text for physical teleprompters
- [ ] **Persistence:** Save text and settings with DataStore
- [ ] **Gestures:** Control via tap/swipe in addition to buttons
- [ ] **Full screen:** Hide status bar and navigation

### 🎯 Medium Priority
- [ ] **Remote control:** Implement with WebSocket or Bluetooth
- [ ] **Multiple texts:** Saved scripts system
- [ ] **Bookmarks:** Text bookmark system
- [ ] **Advanced settings:** Colors, fonts, etc.

### 💡 Future Improvements
- [ ] **Export/Import:** Functionality to share scripts
- [ ] **AI Prompts:** Integration to generate/improve texts
- [ ] **Analytics:** Usage and reading speed metrics
- [ ] **Widget:** Control from notification or widget

## 🔧 Technical Configuration

### Key Dependencies
```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.8.2")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Navigation (added for future use)
implementation("androidx.navigation:navigation-compose:2.7.5")

// Room (added for future persistence)
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
```

### Permissions
- `android.permission.WAKE_LOCK` - Keep screen on during use

### Specific Configuration
- **Orientation:** `android:screenOrientation="landscape"`
- **Theme:** `Theme.PromptFlow` (no ActionBar)
- **Target API:** 31+ with dynamic theme support

## 📝 Implementation Notes

### Auto-scroll Algorithm
```kotlin
LaunchedEffect(isPlaying, speed) {
    if (isPlaying) {
        while (isPlaying) {
            delay((100 / speed).toLong())
            if (listState.canScrollForward) {
                listState.animateScrollBy(1f)
            } else {
                isPlaying = false // Auto-stop at end
            }
        }
    }
}
```

### Sample Text
Includes sample text explaining app functionality.

## 🐛 Known Issues

### Resolved
- [x] **Commit signing:** Resolved using `--no-gpg-sign`
- [x] **GITHUB_TOKEN:** Resolved by clearing environment variable in fish shell
- [x] **File location:** Initially created in wrong directory, moved successfully

### To Resolve
- [ ] None currently known

## 📊 Project Metrics

- **Code files:** 14 files
- **Lines of code:** ~640 lines (initial commit)
- **Initial commit hash:** 9e49283
- **Repository size:** 8.43 KiB

## 🌐 Language Rules (IMPORTANT)

### Universal Rules for All Cursor Projects
- **Code, comments, documentation in git:** English ONLY
- **Variable names, function names:** English ONLY
- **Commit messages:** English ONLY
- **README files:** English ONLY (or bilingual if specifically requested)
- **Chat/conversation communication:** Spanish (for this user)

### Rationale
- Code maintainability for international teams
- Standard industry practice
- Better tooling and AI support
- Professional presentation

## 🔄 Instructions for Future Conversations

### For AI Assistant:
1. Read this file at the start of each conversation
2. Update this file when important decisions are made
3. Keep the TODO list updated with progress
4. Document new issues and their solutions
5. Remember language rules: code/docs in English, chat in Spanish
6. Apply English-only rule to ALL projects by default

### For Developer:
- This file should be updated manually or by assistant
- Serves as living documentation of the project
- Useful for onboarding new collaborators
- Must follow English-only rule for all git content

---
**Last Updated:** June 6, 2025
**Updated By:** Claude (Initial AI Assistant)
**Next Review:** When implementing significant new features