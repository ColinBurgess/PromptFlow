package com.promptflow.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.promptflow.android.ui.screen.LoginScreen
import com.promptflow.android.ui.screen.TeleprompterScreen
import com.promptflow.android.ui.screen.SettingsScreen
import com.promptflow.android.ui.screen.TextLibraryScreen // Added import
import com.promptflow.android.ui.theme.PromptFlowTheme
import com.promptflow.android.viewmodel.AuthenticationViewModel
import com.promptflow.android.viewmodel.TextLibraryViewModel
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PromptFlowTheme {
                PromptFlowApp()
            }
        }
    }
}

@Composable
fun PromptFlowApp() {
    val navController = rememberNavController()
    val authViewModel: AuthenticationViewModel = viewModel()
    val textLibraryViewModel: TextLibraryViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Initialize TextLibraryViewModel with context
    LaunchedEffect(Unit) {
        textLibraryViewModel.initialize(context)
    }

    // Handle migration when user logs in
    LaunchedEffect(authState.user) {
        if (authState.user != null) {
            // Initialize Drive service and load texts
            textLibraryViewModel.refreshTexts(context)

            // Migrate local texts to Drive if any exist
            if (textLibraryViewModel.state.value.localTexts.isNotEmpty()) {
                textLibraryViewModel.migrateLocalTextsToCloud()
            }
        }
    }

    // Shared state between teleprompter and settings
    var currentText by remember { mutableStateOf(sampleText) }
    var currentSpeed by remember { mutableFloatStateOf(8f) }
    var currentFontSize by remember { mutableFloatStateOf(24f) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "library"
            ) {
                composable("teleprompter") {
                    TeleprompterScreen(
                        onShowSettings = { navController.navigate("settings") },
                        onShowLibrary = { navController.navigate("library") }, // Navigate to library
                        initialText = currentText,
                        initialSpeed = currentSpeed,
                        initialFontSize = currentFontSize,
                        onTextChanged = { currentText = it },
                        onSpeedChanged = { currentSpeed = it },
                        onFontSizeChanged = { currentFontSize = it }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        user = authState.user,
                        onBackPressed = { navController.popBackStack() },
                        onLoginRequest = { authViewModel.signInWithGoogle(context) },
                        onLogoutRequest = {
                            authViewModel.signOut()
                            textLibraryViewModel.loadSavedTexts()
                        },
                        currentText = currentText,
                        onTextChanged = { currentText = it },
                        currentSpeed = currentSpeed,
                        onSpeedChanged = { currentSpeed = it },
                        currentFontSize = currentFontSize,
                        onFontSizeChanged = { currentFontSize = it },
                        authViewModel = authViewModel,
                        textLibraryViewModel = textLibraryViewModel
                    )
                }

                composable("library") {
                    TextLibraryScreen(
                        textLibraryViewModel = textLibraryViewModel,
                        onBackPressed = { navController.popBackStack() },
                        onTextSelected = { text ->
                            currentText = text
                            navController.navigate("teleprompter")
                        },
                        onNavigateToEditor = { navController.navigate("settings") } // Navigate to settings for editor
                    )
                }
            }
        }
    }
}

private val sampleText = """
Welcome to PromptFlow!

This is your professional teleprompter application with cloud sync capabilities.

🚀 NEW FEATURES:
• Sign in with Google Account
• Save your texts to the cloud
• Access from any device
• Sync settings automatically

You can control the scrolling speed, adjust the font size, and edit the text to meet your needs.

The text will scroll smoothly from bottom to top, just like a professional teleprompter.

Use the controls at the bottom to:
• Play or pause the scrolling
• Adjust the speed from 1x to 25x
• Change the font size from 16sp to 48sp
• Access settings for text editing and account management

Perfect for presentations, video recordings, speeches, and any situation where you need to read text naturally while maintaining eye contact with your audience.

Sign in to unlock the full potential of PromptFlow!
""".trimIndent()