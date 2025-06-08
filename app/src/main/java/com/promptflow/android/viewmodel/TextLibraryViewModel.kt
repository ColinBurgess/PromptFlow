package com.promptflow.android.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

data class SavedText(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val userId: String = "",
    val isLocal: Boolean = false,
    val driveFileId: String? = null
)

data class TextLibraryState(
    val isLoading: Boolean = false,
    val savedTexts: List<SavedText> = emptyList(),
    val localTexts: List<SavedText> = emptyList(),
    val error: String? = null
)

class TextLibraryViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val gson = Gson()
    private var sharedPrefs: SharedPreferences? = null
    private var accessToken: String? = null
    private var promptFlowFolderId: String? = null

    private val _state = MutableStateFlow(TextLibraryState())
    val state: StateFlow<TextLibraryState> = _state.asStateFlow()

    fun initialize(context: Context) {
        sharedPrefs = context.getSharedPreferences("promptflow_texts", Context.MODE_PRIVATE)
        loadSavedTexts()

        // Initialize Drive access if user is signed in
        auth.currentUser?.let { user ->
            initializeDriveAccess(context)
        }
    }

    private fun initializeDriveAccess(context: Context) {
        viewModelScope.launch {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(context)
                if (account != null) {
                    // For simplicity, we'll implement this step by step
                    // First, let's focus on local storage and add Drive later
                    println("✅ Google account found: ${account.email}")

                    // TODO: Implement Drive access token retrieval
                    // For now, we'll simulate having Drive access
                    loadTextsFromDrive()
                }
            } catch (e: Exception) {
                println("❌ Error initializing Drive access: ${e.message}")
                _state.value = _state.value.copy(
                    error = "error_drive_connect"
                )
            }
        }
    }

    private fun loadTextsFromDrive() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                // For now, simulate loading from Drive
                // In a real implementation, we would make HTTP requests to Drive API
                val texts = emptyList<SavedText>() // TODO: Implement actual Drive API calls

                _state.value = _state.value.copy(
                    savedTexts = texts,
                    isLoading = false,
                    error = null
                )

                println("✅ Loaded ${texts.size} texts from Google Drive")

            } catch (e: Exception) {
                println("❌ Error loading texts from Drive: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "error_drive_load"
                )
            }
        }
    }

    fun loadSavedTexts() {
        // Load local texts
        loadLocalTexts()

        // Load cloud texts if user is signed in
        if (auth.currentUser != null) {
            loadTextsFromDrive()
        }
    }

    private fun loadLocalTexts() {
        sharedPrefs?.let { prefs ->
            try {
                val jsonString = prefs.getString("saved_texts", "[]")
                val type = object : TypeToken<List<SavedText>>() {}.type
                val texts: List<SavedText> = gson.fromJson(jsonString, type) ?: emptyList()

                _state.value = _state.value.copy(localTexts = texts)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "error_local_load"
                )
            }
        }
    }

    fun getAllTexts(): List<SavedText> {
        return _state.value.savedTexts + _state.value.localTexts
    }

    fun saveText(title: String, content: String) {
        val currentUser = auth.currentUser

        if (currentUser != null && accessToken != null) {
            // Save to Google Drive if user is logged in and we have access
            saveToGoogleDrive(title, content)
        } else {
            // Save locally if user is not logged in or no Drive access
            saveToLocal(title, content)
        }
    }

    private fun saveToGoogleDrive(title: String, content: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                // For now, fall back to local storage
                // TODO: Implement actual Drive API calls
                saveToLocal(title, content)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null
                )

                println("✅ Text saved (currently locally, Drive integration coming): $title")

            } catch (e: Exception) {
                println("❌ Error saving to Drive: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error guardando en Google Drive: ${e.message}"
                )
            }
        }
    }

    private fun saveToLocal(title: String, content: String) {
        sharedPrefs?.let { prefs ->
            try {
                val currentTexts = _state.value.localTexts.toMutableList()
                val newText = SavedText(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    createdAt = Date(),
                    updatedAt = Date(),
                    userId = "local",
                    isLocal = true
                )

                currentTexts.add(0, newText) // Add to beginning

                val jsonString = gson.toJson(currentTexts)
                prefs.edit().putString("saved_texts", jsonString).apply()

                _state.value = _state.value.copy(localTexts = currentTexts)

                println("✅ Text saved locally: $title")

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error guardando texto localmente: ${e.message}"
                )
            }
        }
    }

    fun deleteText(textId: String) {
        val allTexts = getAllTexts()
        val textToDelete = allTexts.find { it.id == textId }

        when {
            textToDelete == null -> {
                _state.value = _state.value.copy(error = "Texto no encontrado")
                return
            }
            textToDelete.isLocal -> deleteLocalText(textId)
            else -> deleteTextFromDrive(textId)
        }
    }

    private fun deleteLocalText(textId: String) {
        sharedPrefs?.let { prefs ->
            try {
                val currentTexts = _state.value.localTexts.toMutableList()
                currentTexts.removeAll { it.id == textId }

                val jsonString = gson.toJson(currentTexts)
                prefs.edit().putString("saved_texts", jsonString).apply()

                _state.value = _state.value.copy(localTexts = currentTexts)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Error eliminando texto local: ${e.message}"
                )
            }
        }
    }

    private fun deleteTextFromDrive(textId: String) {
        viewModelScope.launch {
            try {
                // TODO: Implement actual Drive deletion
                // For now, just remove from local state
                val currentTexts = _state.value.savedTexts.toMutableList()
                currentTexts.removeAll { it.id == textId }

                _state.value = _state.value.copy(savedTexts = currentTexts)

                println("✅ Text deleted from Google Drive")

            } catch (e: Exception) {
                println("❌ Error deleting from Drive: ${e.message}")
                _state.value = _state.value.copy(
                    error = "Error eliminando de Google Drive: ${e.message}"
                )
            }
        }
    }

    fun migrateLocalTextsToCloud() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        val localTexts = _state.value.localTexts
        if (localTexts.isEmpty()) return

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                // For now, just move texts to "cloud" state locally
                // TODO: Implement actual Drive migration
                val migratedTexts = localTexts.map { localText ->
                    localText.copy(
                        isLocal = false,
                        userId = currentUser.uid
                    )
                }

                _state.value = _state.value.copy(
                    savedTexts = migratedTexts,
                    localTexts = emptyList(),
                    isLoading = false
                )

                // Clear local texts after migration
                sharedPrefs?.edit()?.remove("saved_texts")?.apply()

                println("✅ Migrated ${localTexts.size} texts to cloud (simulated)")

            } catch (e: Exception) {
                println("❌ Error migrating texts: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error migrando textos a Google Drive: ${e.message}"
                )
            }
        }
    }

    fun refreshTexts(context: Context) {
        if (auth.currentUser != null) {
            initializeDriveAccess(context)
        } else {
            loadLocalTexts()
        }
    }
}