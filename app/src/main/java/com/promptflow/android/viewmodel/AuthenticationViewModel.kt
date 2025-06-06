package com.promptflow.android.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

class AuthenticationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Check if user is already signed in
        auth.currentUser?.let { user ->
            _authState.value = AuthState(
                user = user,
                isSignedIn = true
            )
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                println("🔍 Starting Google Sign-In process...")

                val credentialManager = CredentialManager.create(context)
                println("🔍 CredentialManager created successfully")

                // Configure Google ID option
                // Web Client ID from Firebase Console
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("***REMOVED***")
                    .build()
                println("🔍 GoogleIdOption configured")

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                println("🔍 GetCredentialRequest built")

                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                println("✅ CredentialManager getCredential completed successfully")

                handleSignIn(result)

            } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                println("❌ GetCredentialException: ${e.message}")
                println("❌ Exception type: ${e.type}")
                println("❌ Full exception: $e")

                // Handle specific error types with user-friendly messages
                val userFriendlyError = when (e.type) {
                    "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL" -> {
                        "No hay cuentas de Google configuradas en este dispositivo. Para usar esta función:\n\n" +
                        "1. Ve a Configuración > Cuentas\n" +
                        "2. Toca 'Agregar cuenta'\n" +
                        "3. Selecciona 'Google'\n" +
                        "4. Inicia sesión con tu cuenta\n" +
                        "5. Vuelve a la app e intenta de nuevo"
                    }
                    "android.credentials.GetCredentialException.TYPE_USER_CANCELED" -> {
                        "Inicio de sesión cancelado. Si deseas sincronizar tus textos en la nube, intenta de nuevo."
                    }
                    "android.credentials.GetCredentialException.TYPE_INTERRUPTED" -> {
                        "El proceso de inicio de sesión fue interrumpido. Por favor, intenta de nuevo."
                    }
                    else -> {
                        "Error al iniciar sesión con Google: ${e.message}\n\n" +
                        "Verifica que tengas una cuenta de Google configurada en tu dispositivo y que el dispositivo tenga conexión a internet."
                    }
                }

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = userFriendlyError
                )
            } catch (e: Exception) {
                println("❌ General Exception: ${e.message}")
                println("❌ Exception class: ${e.javaClass.simpleName}")
                println("❌ Full exception: $e")
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Sign-in failed: ${e.message}"
                )
            }
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val authCredential = GoogleAuthProvider.getCredential(
                            googleIdTokenCredential.idToken,
                            null
                        )

                        val authResult = auth.signInWithCredential(authCredential).await()

                        _authState.value = AuthState(
                            user = authResult.user,
                            isSignedIn = true,
                            isLoading = false
                        )

                    } catch (e: GoogleIdTokenParsingException) {
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            error = "Invalid Google ID token"
                        )
                    }
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Unexpected credential type"
                    )
                }
            }
            else -> {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Unexpected credential type"
                )
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}