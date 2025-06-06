# Firebase Setup Instructions

Follow these steps to configure Firebase Authentication and Firestore for PromptFlow.

## 🔥 Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Project name: `PromptFlow` (or your preferred name)
4. Enable Google Analytics (optional but recommended)
5. Click **"Create project"**

## 📱 Step 2: Add Android App

1. In your Firebase project, click **"Add app"** > **Android**
2. **Package name**: `com.promptflow.android`
3. **App nickname**: `PromptFlow Android`
4. **Debug signing certificate SHA-1**: Get this by running:
   ```bash
   cd android
   ./gradlew signingReport
   ```
   Copy the SHA1 from "Variant: debug"

## 🔐 Step 3: Enable Authentication

1. In Firebase Console, go to **Authentication**
2. Click **"Get started"**
3. Go to **"Sign-in method"** tab
4. Enable **Google** provider:
   - Toggle to enable
   - Set project support email
   - Click **"Save"**

## 🗄️ Step 4: Setup Firestore Database

1. In Firebase Console, go to **Firestore Database**
2. Click **"Create database"**
3. Choose **"Start in test mode"** (for development)
4. Select your preferred location
5. Click **"Done"**

## 📄 Step 5: Download Configuration File

1. In Firebase Console, go to **Project Settings** (gear icon)
2. In "Your apps" section, find your Android app
3. Click **"Download google-services.json"**
4. **IMPORTANT**: Place this file in: `app/google-services.json`

## 🔧 Step 6: Get Web Client ID

1. In Firebase Console, go to **Project Settings**
2. Go to **"General"** tab
3. In "Your apps" section, find your Android app
4. Click the app to expand details
5. Find and copy the **"Web client ID"**
6. Update `AuthenticationViewModel.kt` line 51:
   ```kotlin
   .setServerClientId("YOUR_WEB_CLIENT_ID") // Replace with your actual Web Client ID
   ```

## 🛡️ Step 7: Setup Firestore Security Rules (Production)

When ready for production, update Firestore rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId}/texts/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Deny all other access
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

## ✅ Verification Steps

After completing setup:

1. **Build the app** - Should compile without errors
2. **Test login** - Google Sign-In should work
3. **Test save text** - Texts should save to Firestore
4. **Check Firestore** - Data should appear in Firebase Console

## 🚨 Common Issues & Solutions

### Issue: "Web client ID not found"
**Solution**: Make sure you copied the **Web client ID** (not Android client ID) from Firebase Console.

### Issue: "google-services.json not found"
**Solution**: Ensure the file is placed exactly at `app/google-services.json`

### Issue: "SHA-1 fingerprint mismatch"
**Solution**:
- Re-run `./gradlew signingReport`
- Add the SHA-1 to your Firebase project
- Download new `google-services.json`

### Issue: "FirebaseAuth network error"
**Solution**: Check internet connection and Firebase project status.

## 📊 Testing in Firebase Console

### Authentication Users
- Go to **Authentication** > **Users**
- You should see signed-in users appear here

### Firestore Data
- Go to **Firestore Database** > **Data**
- Check collections: `users/{uid}/texts`
- You should see saved texts appear here

## 🔐 Security Best Practices

1. **Never commit** `google-services.json` to public repositories
2. **Use environment variables** for sensitive configuration in production
3. **Implement proper Firestore rules** before going live
4. **Enable App Check** for production apps
5. **Monitor usage** in Firebase Console

## 🚀 Next Steps

Once Firebase is configured:
- Users can sign in with Google
- Texts are automatically saved to the cloud
- Data syncs across devices
- User profiles are displayed
- Text library is fully functional

## 📱 App Features Now Available

✅ **Google Sign-In Authentication**
✅ **Cloud Text Storage (Firestore)**
✅ **User Profile Management**
✅ **Text Library with CRUD operations**
✅ **Cross-device synchronization**
✅ **Automatic backup**

---

**Need Help?** Check the [Firebase Documentation](https://firebase.google.com/docs) or create an issue in the repository.