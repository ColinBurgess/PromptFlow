package com.promptflow.android.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser
import com.promptflow.android.viewmodel.AuthenticationViewModel
import com.promptflow.android.viewmodel.SavedText
import com.promptflow.android.viewmodel.TextLibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: FirebaseUser?,
    onBackPressed: () -> Unit,
    onTextSelected: (String) -> Unit,
    onLoginRequest: () -> Unit,
    onLogoutRequest: () -> Unit,
    currentText: String,
    onTextChanged: (String) -> Unit,
    currentSpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    currentFontSize: Float,
    onFontSizeChanged: (Float) -> Unit,
    authViewModel: AuthenticationViewModel = viewModel(),
    textLibraryViewModel: TextLibraryViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val textLibraryState by textLibraryViewModel.state.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<SavedText?>(null) }

    val tabs = listOf(
        "Text Editor" to Icons.Default.Edit,
        "Defaults" to Icons.Default.Settings,
        "Library" to Icons.Default.LibraryBooks,
        "Account" to Icons.Default.Person
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTab == 2) { // Library tab - allow adding texts both for logged and non-logged users
                        IconButton(onClick = { showSaveDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Text")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        icon = { Icon(icon, contentDescription = title) }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> TextEditorTab(
                    currentText = currentText,
                    onTextChanged = onTextChanged
                )
                1 -> DefaultsTab(
                    currentSpeed = currentSpeed,
                    onSpeedChanged = onSpeedChanged,
                    currentFontSize = currentFontSize,
                    onFontSizeChanged = onFontSizeChanged
                )
                2 -> LibraryTab(
                    user = user,
                    textLibraryState = textLibraryState,
                    onTextSelected = onTextSelected,
                    onDeleteText = { showDeleteDialog = it },
                    textLibraryViewModel = textLibraryViewModel,
                    onShowSaveDialog = { showSaveDialog = true }
                )
                3 -> AccountTab(
                    user = user,
                    authState = authState,
                    authViewModel = authViewModel,
                    onLoginRequest = {
                        println("🔵 Login request received in SettingsScreen")
                        authViewModel.signInWithGoogle(context)
                    },
                    onLogoutRequest = {
                        authViewModel.signOut()
                        onLogoutRequest()
                    }
                )
            }
        }
    }

    // Save Text Dialog
    if (showSaveDialog) {
        SaveTextDialog(
            onSave = { title, content ->
                textLibraryViewModel.saveText(title, content)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { textToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Text") },
            text = { Text("Are you sure you want to delete '${textToDelete.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        textLibraryViewModel.deleteText(textToDelete.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TextEditorTab(
    currentText: String,
    onTextChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Teleprompter Text",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentText,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Enter your teleprompter text here...") },
            minLines = 10
        )
    }
}

@Composable
private fun DefaultsTab(
    currentSpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    currentFontSize: Float,
    onFontSizeChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Default Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Speed Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Default Speed: ${currentSpeed.toInt()}x",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = currentSpeed,
                    onValueChange = onSpeedChanged,
                    valueRange = 1f..25f,
                    steps = 23
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1x", style = MaterialTheme.typography.bodySmall)
                    Text("25x", style = MaterialTheme.typography.bodySmall)
                }

                // Quick preset buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(5f, 8f, 12f, 15f, 20f).forEach { speed ->
                        TextButton(onClick = { onSpeedChanged(speed) }) {
                            Text("${speed.toInt()}x")
                        }
                    }
                }
            }
        }

        // Font Size Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Default Font Size: ${currentFontSize.toInt()}sp",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = currentFontSize,
                    onValueChange = onFontSizeChanged,
                    valueRange = 16f..48f,
                    steps = 31
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("16sp", style = MaterialTheme.typography.bodySmall)
                    Text("48sp", style = MaterialTheme.typography.bodySmall)
                }

                // Quick preset buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(20f, 24f, 32f, 40f).forEach { size ->
                        TextButton(onClick = { onFontSizeChanged(size) }) {
                            Text("${size.toInt()}sp")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryTab(
    user: FirebaseUser?,
    textLibraryState: com.promptflow.android.viewmodel.TextLibraryState,
    onTextSelected: (String) -> Unit,
    onDeleteText: (SavedText) -> Unit,
    textLibraryViewModel: TextLibraryViewModel = viewModel(),
    onShowSaveDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Get all texts (local + cloud)
        val allTexts = textLibraryViewModel.getAllTexts()

        // Show info about storage location
        if (user == null) {
            // Local storage info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Storage,
                        contentDescription = "Local Storage",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Almacenamiento Local",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Tus textos se guardan en este dispositivo. Inicia sesión para sincronizar con Google Drive.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // Cloud storage info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = "Google Drive Storage",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Google Drive",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Tus textos se guardan como archivos .txt en tu Google Drive y se sincronizan automáticamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            textLibraryState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            allTexts.isEmpty() -> {
                // IMPROVED EMPTY STATE - More clear and helpful
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LibraryBooks,
                                contentDescription = "Empty Library",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (user == null)
                                    "Tu Biblioteca Local está Vacía"
                                else
                                    "Tu Biblioteca está Vacía",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (user == null)
                                    "Comienza agregando tu primer texto de teleprompter. Se guardará localmente en este dispositivo."
                                else
                                    "Comienza agregando tu primer texto de teleprompter. Se guardará como archivo .txt en tu Google Drive.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Call to action button
                            Button(
                                onClick = { onShowSaveDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Your First Text")
                            }
                        }
                    }

                    // Helpful tips card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "💡 Tips:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Use the Text Editor tab to write and edit your teleprompter content\n" +
                                      "• Save frequently used texts to access them quickly\n" +
                                      "• Each saved text can have a custom title for easy identification",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allTexts) { savedText ->
                        LibraryTextItem(
                            savedText = savedText,
                            onTextClick = { onTextSelected(savedText.content) },
                            onDeleteClick = { onDeleteText(savedText) }
                        )
                    }
                }
            }
        }

        // Error Message
        textLibraryState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun AccountTab(
    user: FirebaseUser?,
    authState: com.promptflow.android.viewmodel.AuthState,
    authViewModel: AuthenticationViewModel,
    onLoginRequest: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (user == null) {
            // Not logged in - IMPROVED UI WITH CLEAR GOOGLE BRANDING
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google-themed icon
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // More realistic Google "G" with colors
                            Text(
                                text = "G",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4285F4) // Google Blue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // DEBUG: Log current state
                    println("🔍 Button state - isLoading: ${authState.isLoading}, user: ${user?.email}")

                    Text(
                        text = "Sign in with your Google Account",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Connect your Google Account to sync your teleprompter texts across all your devices and never lose your content.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Show error if exists
                    authState.error?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (error.contains("No hay cuentas de Google"))
                                            Icons.Default.AccountCircle
                                        else
                                            Icons.Default.Error,
                                        contentDescription = "Error",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (error.contains("No hay cuentas de Google"))
                                            "Cuenta de Google requerida"
                                        else
                                            "Error de conexión",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Action buttons based on error type
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (error.contains("No hay cuentas de Google")) {
                                        OutlinedButton(
                                            onClick = {
                                                // Clear error and provide guidance
                                                authViewModel.clearError()
                                                android.widget.Toast.makeText(
                                                    context,
                                                    "Ve a Configuración del dispositivo para agregar una cuenta de Google",
                                                    android.widget.Toast.LENGTH_LONG
                                                ).show()
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                                            ),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer)
                                        ) {
                                            Icon(
                                                Icons.Default.Settings,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Ir a Config.")
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            authViewModel.clearError()
                                            println("🔄 Retry button clicked after error!")
                                            authViewModel.signInWithGoogle(context)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer)
                                    ) {
                                        Icon(
                                            Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reintentar")
                                    }

                                    TextButton(
                                        onClick = {
                                            authViewModel.clearError()
                                        }
                                    ) {
                                        Text(
                                            "Cerrar",
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ENHANCED GOOGLE SIGN-IN BUTTON - WITH TOAST DEBUG
                    ElevatedButton(
                        onClick = {
                            // VISUAL DEBUG WITH TOAST
                            android.widget.Toast.makeText(
                                context,
                                "🔵 Button clicked! Starting Google Sign-In...",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                            println("🔵 BUTTON CLICKED! Starting Google Sign-In process...")
                            println("🔍 Current auth state: isLoading=${authState.isLoading}, error=${authState.error}")
                            try {
                                onLoginRequest()
                                println("✅ onLoginRequest() called successfully")

                                // Show another toast to confirm the call
                                android.widget.Toast.makeText(
                                    context,
                                    "✅ Login request sent!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {
                                println("❌ Error calling onLoginRequest: ${e.message}")
                                android.widget.Toast.makeText(
                                    context,
                                    "❌ Error: ${e.message}",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        enabled = !authState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F),
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp,
                            disabledElevation = 0.dp
                        ),
                        border = BorderStroke(1.dp, Color(0xFFDDDDDD))
                    ) {
                        if (authState.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF4285F4),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Signing in...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Simplified Google icon (just the "G")
                                Text(
                                    text = "G",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4285F4)
                                )

                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                    }

                    // Show loading state
                    if (authState.isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Opening Google Sign-In...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Benefits section
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Benefits of signing in:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BenefitItem(
                    icon = Icons.Default.Cloud,
                    title = "Cloud Sync",
                    description = "Access your texts from any device"
                )
                BenefitItem(
                    icon = Icons.Default.Backup,
                    title = "Automatic Backup",
                    description = "Never lose your important texts"
                )
                BenefitItem(
                    icon = Icons.Default.Share,
                    title = "Easy Sharing",
                    description = "Share texts with other devices instantly"
                )
            }

        } else {
            // Logged in - ENHANCED USER INFO
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // User info header
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = user.displayName ?: "Google User",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = user.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )

                            // Account status
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Connected",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Account actions
                    OutlinedButton(
                        onClick = onLogoutRequest,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out")
                    }
                }
            }

            // Storage info for logged in users
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "Cloud Active",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Cloud Sync Active",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Your texts are automatically backed up and synced",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LibraryTextItem(
    savedText: SavedText,
    onTextClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onTextClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = savedText.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Storage indicator
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        if (savedText.isLocal) Icons.Default.Storage else Icons.Default.Cloud,
                        contentDescription = if (savedText.isLocal) "Local" else "Cloud",
                        modifier = Modifier.size(16.dp),
                        tint = if (savedText.isLocal)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = savedText.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SaveTextDialog(
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            // DISABLED - Only close with buttons, not by tapping outside
            // onDismiss()
        },
        title = null, // Remove title to save space
        text = {
            // ANDROID-FRIENDLY LAYOUT - Buttons at top, content below
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp), // Increased minimum height
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // BUTTONS AT THE TOP - Always visible above keyboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSave(title.trim(), content.trim())
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save")
                    }
                }

                // Divider for visual separation
                Divider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )

                // Form fields below buttons
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter a descriptive title...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    placeholder = { Text("Enter your teleprompter text here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // Fixed height
                    maxLines = 8
                )

                // Character count indicator
                Text(
                    text = "${content.length} characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            // Empty - buttons are now in the text section
        },
        dismissButton = {
            // Empty - buttons are now in the text section
        },
        // IMPORTANT: This prevents the dialog from being dismissed by tapping outside
        properties = DialogProperties(
            dismissOnBackPress = true, // Allow back button to close
            dismissOnClickOutside = false // PREVENT closing by tapping outside
        )
    )
}