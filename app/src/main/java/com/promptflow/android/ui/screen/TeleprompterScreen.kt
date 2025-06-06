package com.promptflow.android.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.promptflow.android.ui.components.UserProfileCard
import com.promptflow.android.viewmodel.TextLibraryViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleprompterScreen(
    user: FirebaseUser?,
    onShowSettings: () -> Unit,  // Updated to show full settings screen
    onShowLibrary: () -> Unit,   // This can be removed since it's now in settings
    onLogout: () -> Unit,
    textLibraryViewModel: TextLibraryViewModel = viewModel(),
    initialText: String = sampleText,     // Allow passing text from settings
    initialSpeed: Float = 8f,             // Allow passing speed from settings
    initialFontSize: Float = 24f,         // Allow passing font size from settings
    onTextChanged: (String) -> Unit = {}, // Callback for text changes
    onSpeedChanged: (Float) -> Unit = {}, // Callback for speed changes
    onFontSizeChanged: (Float) -> Unit = {} // Callback for font size changes
) {
    var isPlaying by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(initialSpeed) }
    var fontSize by remember { mutableFloatStateOf(initialFontSize) }
    var text by remember { mutableStateOf(initialText) }
    var scrollOffset by remember { mutableIntStateOf(0) }
    var showControls by remember { mutableStateOf(true) }

    // Update callbacks when values change
    LaunchedEffect(text) { onTextChanged(text) }
    LaunchedEffect(speed) { onSpeedChanged(speed) }
    LaunchedEffect(fontSize) { onFontSizeChanged(fontSize) }

    val listState = rememberLazyListState()

    // Listen for selected text from library
    LaunchedEffect(Unit) {
        // This would be handled by navigation in a real implementation
        // For now, we'll use the existing text management
    }

    // Auto-scroll effect - PROFESSIONAL TELEPROMPTER BEHAVIOR
    LaunchedEffect(isPlaying, speed, text) {
        if (isPlaying) {
            while (isPlaying) {
                delay((50 / speed).toLong())
                scrollOffset += 1

                // Check if we can still scroll (text hasn't fully passed)
                val canScroll = listState.canScrollForward ||
                               listState.firstVisibleItemIndex == 0 ||
                               scrollOffset < (text.length / 10) + 100 // Estimate based on text length

                if (canScroll) {
                    listState.scrollToItem(0, scrollOffset)
                } else {
                    // All text has scrolled off screen - end playback
                    isPlaying = false
                    scrollOffset = 0 // Auto-reset for next play
                }
            }
        }
    }

    // Auto-hide controls during playback
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            // Hide controls after 3 seconds of playback
            delay(3000)
            showControls = false
        } else {
            // Show controls immediately when paused
            showControls = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main text display - CLICKABLE TO TOGGLE CONTROLS
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(
                    bottom = when {
                        !isPlaying -> 100.dp  // Paused - show controls
                        showControls -> 80.dp // Playing with controls visible
                        else -> 20.dp         // Playing with controls hidden - full screen
                    }
                )
                .clickable {
                    if (isPlaying) {
                        showControls = !showControls // Toggle controls during playback
                    }
                },
            contentPadding = PaddingValues(
                top = if (!isPlaying) 120.dp else 50.dp,
                bottom = 50.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = fontSize.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = (fontSize * 1.4).sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ENHANCED Control panel - AUTO-HIDE DURING PLAYBACK
        if (showControls || !isPlaying) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.9f),
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Pause button - ENHANCED
                    FloatingActionButton(
                        onClick = {
                            if (!isPlaying) {
                                // Reset scroll only if at the end (increased threshold)
                                if (scrollOffset >= 1800) { // Reset earlier to prevent reaching limit
                                    scrollOffset = 0
                                }
                            }
                            isPlaying = !isPlaying
                        },
                        containerColor = if (isPlaying)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play"
                        )
                    }

                    // Speed control
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Speed: ${speed.toInt()}x", color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = speed,
                            onValueChange = { speed = it },
                            valueRange = 1f..25f, // Expanded range: 1x to 25x
                            steps = 23, // More precise steps
                            modifier = Modifier.width(100.dp)
                        )
                        // Quick speed buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = { speed = 5f },
                                modifier = Modifier.size(width = 30.dp, height = 20.dp),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("5", color = Color.White, fontSize = 10.sp)
                            }
                            TextButton(
                                onClick = { speed = 10f },
                                modifier = Modifier.size(width = 30.dp, height = 20.dp),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("10", color = Color.White, fontSize = 10.sp)
                            }
                            TextButton(
                                onClick = { speed = 15f },
                                modifier = Modifier.size(width = 30.dp, height = 20.dp),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("15", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    // Font size control
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Size: ${fontSize.toInt()}sp", color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = fontSize,
                            onValueChange = { fontSize = it },
                            valueRange = 16f..48f,
                            modifier = Modifier.width(100.dp)
                        )
                    }

                    // Reset button
                    IconButton(
                        onClick = {
                            isPlaying = false
                            scrollOffset = 0
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Reset Position",
                            tint = Color.White
                        )
                    }

                    // Settings button - Opens full settings screen
                    IconButton(
                        onClick = {
                            if (!isPlaying) { // Only allow settings when not playing
                                onShowSettings()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (isPlaying) Color.Gray else Color.White // Visual feedback
                        )
                    }
                }
            }
        }

        // Settings and dialogs are now handled by the separate SettingsScreen
        // No more floating panels that interfere with the teleprompter experience
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
• Adjust the speed from 1x to 10x
• Change the font size from 16sp to 48sp
• Save texts to your library (requires login)
• Edit your text content

Perfect for presentations, video recordings, speeches, and any situation where you need to read text naturally while maintaining eye contact with your audience.

Sign in to unlock the full potential of PromptFlow!
""".trimIndent()