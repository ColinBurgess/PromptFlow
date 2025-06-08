package com.promptflow.android.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser
import com.promptflow.android.viewmodel.AuthenticationViewModel
import com.promptflow.android.viewmodel.SavedText
import com.promptflow.android.viewmodel.TextLibraryViewModel
import androidx.compose.foundation.clickable

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
    val authState by authViewModel.authState.collectAsState()
    val textLibraryState by textLibraryViewModel.state.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<SavedText?>(null) }

    // Detect screen configuration for adaptive layout
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth > 600.dp

    val tabs = listOf(
        "Editor" to Icons.Default.Edit,
        "Ajustes" to Icons.Default.Settings,
        "Biblioteca" to Icons.Default.LibraryBooks,
        "Cuenta" to Icons.Default.AccountCircle
    )

    Scaffold(
        topBar = {
            // Adaptive TopAppBar - compact for landscape tablets
            if (isTablet && isLandscape) {
                // Compact TopAppBar for horizontal layout
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp), // Reduced height for horizontal
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackPressed,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Configuración",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Standard TopAppBar for portrait
                TopAppBar(
                    title = { Text("Configuración") },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        // Adaptive layout based on screen size and orientation
        if (isTablet && isLandscape) {
            // Tablet landscape - side navigation
            TabletLandscapeLayout(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                paddingValues = paddingValues,
                currentText = currentText,
                onTextChanged = onTextChanged,
                currentSpeed = currentSpeed,
                onSpeedChanged = onSpeedChanged,
                currentFontSize = currentFontSize,
                onFontSizeChanged = onFontSizeChanged,
                user = user,
                textLibraryState = textLibraryState,
                onTextSelected = onTextSelected,
                onDeleteText = { showDeleteDialog = it },
                textLibraryViewModel = textLibraryViewModel,
                onShowSaveDialog = { showSaveDialog = true },
                authState = authState,
                authViewModel = authViewModel,
                onLoginRequest = onLoginRequest,
                onLogoutRequest = onLogoutRequest
            )
        } else {
            // Phone or tablet portrait - traditional tab layout
            TraditionalTabLayout(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                paddingValues = paddingValues,
                currentText = currentText,
                onTextChanged = onTextChanged,
                currentSpeed = currentSpeed,
                onSpeedChanged = onSpeedChanged,
                currentFontSize = currentFontSize,
                onFontSizeChanged = onFontSizeChanged,
                user = user,
                textLibraryState = textLibraryState,
                onTextSelected = onTextSelected,
                onDeleteText = { showDeleteDialog = it },
                textLibraryViewModel = textLibraryViewModel,
                onShowSaveDialog = { showSaveDialog = true },
                authState = authState,
                authViewModel = authViewModel,
                onLoginRequest = onLoginRequest,
                onLogoutRequest = onLogoutRequest
            )
        }
    }

    // Dialogs
    if (showSaveDialog) {
        SaveTextDialog(
            onSave = { title, content ->
                textLibraryViewModel.saveText(title, content)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    showDeleteDialog?.let { textToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Texto") },
            text = { Text("¿Estás seguro de que quieres eliminar '${textToDelete.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    textLibraryViewModel.deleteText(textToDelete.id)
                    showDeleteDialog = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TabletLandscapeLayout(
    tabs: List<Pair<String, androidx.compose.ui.graphics.vector.ImageVector>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    paddingValues: PaddingValues,
    currentText: String,
    onTextChanged: (String) -> Unit,
    currentSpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    currentFontSize: Float,
    onFontSizeChanged: (Float) -> Unit,
    user: FirebaseUser?,
    textLibraryState: com.promptflow.android.viewmodel.TextLibraryState,
    onTextSelected: (String) -> Unit,
    onDeleteText: (SavedText) -> Unit,
    textLibraryViewModel: TextLibraryViewModel,
    onShowSaveDialog: () -> Unit,
    authState: com.promptflow.android.viewmodel.AuthState,
    authViewModel: AuthenticationViewModel,
    onLoginRequest: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(4.dp) // Reduced overall padding
    ) {
        // Enhanced side navigation - bigger and better proportioned
        Card(
            modifier = Modifier
                .width(140.dp) // Increased from 120dp to 140dp for longer text
                .fillMaxHeight()
                .padding(4.dp), // Reduced padding
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()), // Permite scroll si no caben todos los tabs
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tabs.forEachIndexed { index, (title, icon) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp) // Reducido para asegurar visibilidad de todos los tabs
                            .clickable { onTabSelected(index) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTab == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (selectedTab == index) 6.dp else 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp), // Reduced to 6dp for more space
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                icon,
                                contentDescription = title,
                                modifier = Modifier.size(24.dp), // Reduced to 24dp
                                tint = if (selectedTab == index)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp)) // Reduced to 4dp
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2, // Allow 2 lines for longer text
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                color = if (selectedTab == index)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp // Reduced to 10sp
                            )
                        }
                    }
                }
            }
        }

        // Content area - optimized spacing and full utilization
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 4.dp, top = 0.dp, bottom = 0.dp) // Removed top/bottom padding
        ) {
            when (selectedTab) {
                0 -> TextEditorTabHorizontal(
                    currentText = currentText,
                    onTextChanged = onTextChanged
                )
                1 -> DefaultsTabHorizontal(
                    currentSpeed = currentSpeed,
                    onSpeedChanged = onSpeedChanged,
                    currentFontSize = currentFontSize,
                    onFontSizeChanged = onFontSizeChanged
                )
                2 -> LibraryTabHorizontal(
                    user = user,
                    textLibraryState = textLibraryState,
                    onTextSelected = onTextSelected,
                    onDeleteText = onDeleteText,
                    textLibraryViewModel = textLibraryViewModel,
                    onShowSaveDialog = onShowSaveDialog
                )
                3 -> AccountTabHorizontal(
                    user = user,
                    authState = authState,
                    authViewModel = authViewModel,
                    onLoginRequest = onLoginRequest,
                    onLogoutRequest = onLogoutRequest
                )
            }
        }
    }
}

// Optimized horizontal layouts for each tab
@Composable
private fun TextEditorTabHorizontal(
    currentText: String,
    onTextChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Compact header
        Text(
            text = "Editor de Texto del Teleprompter",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Maximized text area
        OutlinedTextField(
            value = currentText,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Escribe aquí el texto de tu teleprompter...") },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3f
            )
        )
    }
}

@Composable
private fun DefaultsTabHorizontal(
    currentSpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    currentFontSize: Float,
    onFontSizeChanged: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp), // Reduced from 16dp to 8dp
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Reduced from 24dp to 16dp
    ) {
        // Speed Setting - Left column
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(), // Reduced from 20dp to 12dp
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Velocidad por Defecto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${currentSpeed.toInt()}x",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

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
                        FilledTonalButton(
                            onClick = { onSpeedChanged(speed) },
                            modifier = Modifier.size(40.dp),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text("${speed.toInt()}", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Font Size Setting - Right column
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(), // Reduced from 20dp to 12dp
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Tamaño de Fuente por Defecto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${currentFontSize.toInt()}sp",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

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
                        FilledTonalButton(
                            onClick = { onFontSizeChanged(size) },
                            modifier = Modifier.size(40.dp),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text("${size.toInt()}", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryTabHorizontal(
    user: FirebaseUser?,
    textLibraryState: com.promptflow.android.viewmodel.TextLibraryState,
    onTextSelected: (String) -> Unit,
    onDeleteText: (SavedText) -> Unit,
    textLibraryViewModel: TextLibraryViewModel,
    onShowSaveDialog: () -> Unit
) {
    // Use the existing LibraryTab but with horizontal padding optimization
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Get all texts (local + cloud)
        val allTexts = textLibraryViewModel.getAllTexts()

        // Compact storage info for horizontal layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (user == null) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Storage, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Almacenamiento Local",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Cloud, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Google Drive",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = { onShowSaveDialog() }
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            textLibraryState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            allTexts.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LibraryBooks, null, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tu Biblioteca está Vacía",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Agrega tu primer texto para comenzar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                // Grid layout for horizontal space efficiency
                val columns = 2
                val chunkedTexts = allTexts.chunked(columns)

                chunkedTexts.forEach { rowTexts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTexts.forEach { savedText ->
                            Box(modifier = Modifier.weight(1f)) {
                                LibraryTextItem(
                                    savedText = savedText,
                                    onTextClick = { onTextSelected(savedText.content) },
                                    onDeleteClick = { onDeleteText(savedText) }
                                )
                            }
                        }
                        // Fill empty spaces in incomplete rows
                        repeat(columns - rowTexts.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun AccountTabHorizontal(
    user: FirebaseUser?,
    authState: com.promptflow.android.viewmodel.AuthState,
    authViewModel: AuthenticationViewModel,
    onLoginRequest: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (user == null) {
            // Login section - Left side
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Accede a Google Drive",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Sincroniza tus textos en la nube",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    ElevatedButton(
                        onClick = { authViewModel.signInWithGoogle(context) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !authState.isLoading
                    ) {
                        if (authState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continuar con Google")
                        }
                    }
                }
            }

            // Benefits section - Right side
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Beneficios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        BenefitItem(Icons.Default.Cloud, "Sincronización", "Accede desde cualquier dispositivo")
                        BenefitItem(Icons.Default.Backup, "Respaldo", "Nunca pierdas tus textos")
                        BenefitItem(Icons.Default.Share, "Compartir", "Comparte fácilmente")
                    }
                }
            }
        } else {
            // User info - Full width when logged in
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Column {
                            Text(
                                text = user.displayName ?: "Usuario de Google",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.email ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Conectado a Google Drive",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tus textos se sincronizan automáticamente",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            authViewModel.signOut()
                            onLogoutRequest()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}

@Composable
private fun TraditionalTabLayout(
    tabs: List<Pair<String, androidx.compose.ui.graphics.vector.ImageVector>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    paddingValues: PaddingValues,
    currentText: String,
    onTextChanged: (String) -> Unit,
    currentSpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    currentFontSize: Float,
    onFontSizeChanged: (Float) -> Unit,
    user: FirebaseUser?,
    textLibraryState: com.promptflow.android.viewmodel.TextLibraryState,
    onTextSelected: (String) -> Unit,
    onDeleteText: (SavedText) -> Unit,
    textLibraryViewModel: TextLibraryViewModel,
    onShowSaveDialog: () -> Unit,
    authState: com.promptflow.android.viewmodel.AuthState,
    authViewModel: AuthenticationViewModel,
    onLoginRequest: () -> Unit,
    onLogoutRequest: () -> Unit
) {
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
                    onClick = { onTabSelected(index) },
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
                onDeleteText = onDeleteText,
                textLibraryViewModel = textLibraryViewModel,
                onShowSaveDialog = onShowSaveDialog
            )
            3 -> AccountTab(
                user = user,
                authState = authState,
                authViewModel = authViewModel,
                onLoginRequest = onLoginRequest,
                onLogoutRequest = onLogoutRequest
            )
        }
    }
}

@Composable
private fun TextEditorTab(
    currentText: String,
    onTextChanged: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp.dp > 600.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Editor de Texto del Teleprompter",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentText,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Escribe aquí el texto de tu teleprompter...") },
            minLines = if (isTablet) 15 else 10,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4f
            )
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
            .verticalScroll(rememberScrollState())
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
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
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LibraryBooks,
                                contentDescription = "Empty Library",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (user == null)
                                    "Tu Biblioteca Local está Vacía"
                                else
                                    "Tu Biblioteca está Vacía",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (user == null)
                                    "Comienza agregando tu primer texto de teleprompter. Se guardará localmente en este dispositivo."
                                else
                                    "Comienza agregando tu primer texto de teleprompter. Se guardará como archivo .txt en tu Google Drive.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Call to action button
                            Button(
                                onClick = { onShowSaveDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar Primer Texto")
                            }
                        }
                    }

                    // Helpful tips card - more compact for horizontal layout
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "💡 Consejos:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Usa el Editor para escribir tu contenido\n" +
                                      "• Guarda textos frecuentes para acceso rápido\n" +
                                      "• Cada texto puede tener un título personalizado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            else -> {
                // Text list - now in a Column instead of LazyColumn for better scroll compatibility
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allTexts.forEach { savedText ->
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

        // Add extra space at bottom for better scrolling
        Spacer(modifier = Modifier.height(24.dp))
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Cuenta",
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

                    Text(
                        text = "Accede a Google Drive",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inicia sesión para sincronizar tus textos con Google Drive",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Show errors if any
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
                                        text = when {
                                            error.contains("No hay cuentas de Google") -> "Configura una cuenta de Google"
                                            error.contains("cancelled") -> "Operación cancelada"
                                            error.contains("network") -> "Error de conexión"
                                            else -> "Error de autenticación"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = when {
                                        error.contains("No hay cuentas de Google") ->
                                            "Ve a Configuración > Cuentas > Agregar cuenta > Google para configurar tu cuenta."
                                        error.contains("cancelled") ->
                                            "La autenticación fue cancelada. Inténtalo de nuevo."
                                        error.contains("network") ->
                                            "Verifica tu conexión a internet y vuelve a intentar."
                                        else ->
                                            "Hubo un problema con la autenticación. Inténtalo de nuevo."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )

                                if (error.contains("No hay cuentas de Google")) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            // Open Android Settings to add Google account
                                            try {
                                                val intent = android.content.Intent(android.provider.Settings.ACTION_ADD_ACCOUNT).apply {
                                                    putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                println("❌ Could not open account settings: ${e.message}")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                            contentColor = MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Abrir Configuración")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Sign-In Button - ENHANCED STYLE
                    ElevatedButton(
                        onClick = {
                            println("🔵 BUTTON CLICKED! Starting Google Sign-In process...")
                            authViewModel.signInWithGoogle(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !authState.isLoading,
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
                                    text = "Iniciando sesión...",
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
                                Text(
                                    text = "G",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4285F4)
                                )
                                Text(
                                    text = "Continuar con Google",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Benefits section - ENHANCED WITH ICONS
                    Text(
                        text = "Beneficios de iniciar sesión:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BenefitItem(
                            icon = Icons.Default.Cloud,
                            title = "Sincronización en la Nube",
                            description = "Accede a tus textos desde cualquier dispositivo"
                        )
                        BenefitItem(
                            icon = Icons.Default.Backup,
                            title = "Respaldo Automático",
                            description = "Nunca pierdas tus textos importantes"
                        )
                        BenefitItem(
                            icon = Icons.Default.Share,
                            title = "Compartir Fácil",
                            description = "Comparte textos con otros dispositivos al instante"
                        )
                    }
                }
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
                                text = user.displayName ?: "Usuario de Google",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Connected",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Conectado a Google Drive",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tus textos se sincronizan automáticamente",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Logout button
                    OutlinedButton(
                        onClick = {
                            authViewModel.signOut()
                            onLogoutRequest()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión")
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
                fontWeight = FontWeight.Bold
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