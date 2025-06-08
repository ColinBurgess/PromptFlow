package com.promptflow.android.ui.screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser
import com.promptflow.android.R
import com.promptflow.android.viewmodel.TextLibraryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleprompterScreen(
    onShowSettings: () -> Unit,  // Updated to show full settings screen
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
    var showControls by remember { mutableStateOf(true) }

    // Detect screen orientation and size
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = screenWidth > 600.dp || screenHeight > 600.dp

    // Update callbacks when values change
    LaunchedEffect(text) { onTextChanged(text) }
    LaunchedEffect(speed) { onSpeedChanged(speed) }
    LaunchedEffect(fontSize) { onFontSizeChanged(fontSize) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Reset function to scroll back to top
    val resetScroll: () -> Unit = {
        isPlaying = false
        coroutineScope.launch {
            listState.animateScrollToItem(0, 0)
        }
    }

    // Auto-scroll effect - SMOOTH TELEPROMPTER WITH PROPER MANUAL SCROLL SYNC
    LaunchedEffect(isPlaying, speed) {
        if (isPlaying) {
            while (isPlaying) {
                delay(16) // ~60 FPS for ultra-smooth scrolling

                // Calculate scroll increment based on speed
                val increment = (speed * 0.5f).toInt()

                try {
                    // Check if user is manually scrolling
                    if (listState.isScrollInProgress) {
                        // User is manually scrolling, pause auto-scroll for this frame
                        continue
                    }

                    // Get current scroll position and add increment
                    val currentOffset = listState.firstVisibleItemScrollOffset
                    val newOffset = currentOffset + increment

                    // Scroll to the new position
                    listState.scrollToItem(
                        index = 0,
                        scrollOffset = newOffset
                    )
                } catch (e: Exception) {
                    // If there's a conflict, just continue
                    // This allows manual scroll to work
                }

                // Check if we've reached the end using the actual scroll position
                val canScrollForward = listState.canScrollForward
                if (!canScrollForward) {
                    isPlaying = false
                    // Don't reset scroll position, let user manually reset if needed
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

    // Adaptive layout based on orientation and device type
    if (isLandscape || !isTablet) {
        // Landscape mode or phone - use bottom controls (original design)
        LandscapeLayout(
            isPlaying = isPlaying,
            showControls = showControls,
            listState = listState,
            text = text,
            fontSize = fontSize,
            speed = speed,
            onPlayPauseClick = { isPlaying = !isPlaying },
            onSpeedChange = { speed = it },
            onFontSizeChange = { fontSize = it },
            onResetClick = resetScroll,
            onSettingsClick = {
                if (!isPlaying) onShowSettings()
            },
            onToggleControls = {
                if (isPlaying) showControls = !showControls
            }
        )
    } else {
        // Portrait mode on tablet - use side controls for better accessibility
        PortraitTabletLayout(
            isPlaying = isPlaying,
            showControls = showControls,
            listState = listState,
            text = text,
            fontSize = fontSize,
            speed = speed,
            onPlayPauseClick = { isPlaying = !isPlaying },
            onSpeedChange = { speed = it },
            onFontSizeChange = { fontSize = it },
            onResetClick = resetScroll,
            onSettingsClick = {
                if (!isPlaying) onShowSettings()
            },
            onToggleControls = {
                if (isPlaying) showControls = !showControls
            }
        )
    }
}

@Composable
private fun LandscapeLayout(
    isPlaying: Boolean,
    showControls: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    text: String,
    fontSize: Float,
    speed: Float,
    onPlayPauseClick: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onResetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggleControls: () -> Unit
) {
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
                .clickable { onToggleControls() },
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

        // Control panel at bottom
        AnimatedVisibility(
            visible = !isPlaying || showControls,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Pause button
                    IconButton(onClick = onPlayPauseClick) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Speed control
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.teleprompter_speed_label, speed.toInt()), color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = speed,
                            onValueChange = onSpeedChange,
                            valueRange = 1f..25f,
                            modifier = Modifier.width(100.dp)
                        )
                    }

                    // Font size control
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.teleprompter_size_label, fontSize.toInt()), color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = fontSize,
                            onValueChange = onFontSizeChange,
                            valueRange = 16f..48f,
                            modifier = Modifier.width(100.dp)
                        )
                    }

                    // Reset button
                    IconButton(onClick = onResetClick) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.content_desc_reset_position),
                            tint = Color.White
                        )
                    }

                    // Settings button
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = if (isPlaying) Color.Gray else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortraitTabletLayout(
    isPlaying: Boolean,
    showControls: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    text: String,
    fontSize: Float,
    speed: Float,
    onPlayPauseClick: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onResetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggleControls: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main text display - full width
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(
                    bottom = when {
                        !isPlaying -> 140.dp  // Paused - larger space for controls
                        showControls -> 100.dp // Playing with controls visible
                        else -> 20.dp         // Playing with controls hidden
                    }
                )
                .clickable { onToggleControls() },
            contentPadding = PaddingValues(
                top = if (!isPlaying) 60.dp else 40.dp,
                bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = fontSize.sp,
                    textAlign = TextAlign.Left, // Left align for portrait reading
                    lineHeight = (fontSize * 1.4).sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bottom control panel - improved for tablet portrait
        AnimatedVisibility(
            visible = !isPlaying || showControls,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top row: Play/Pause, Reset, Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPlayPauseClick,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        IconButton(
                            onClick = onResetClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset Position",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = if (isPlaying) Color.Gray else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Speed and Font Size controls - horizontal sliders for better usability
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Speed control
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.teleprompter_speed_label, speed.toInt()),
                                color = Color.White,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(
                                value = speed,
                                onValueChange = onSpeedChange,
                                valueRange = 1f..25f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.Gray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Font size control
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.teleprompter_size_label, fontSize.toInt()),
                                color = Color.White,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(
                                value = fontSize,
                                onValueChange = onFontSizeChange,
                                valueRange = 16f..48f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.Gray
                                )
                            )
                        }
                    }
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
• Adjust the speed from 1x to 10x
• Change the font size from 16sp to 48sp
• Save texts to your library (requires login)
• Edit your text content

Perfect for presentations, video recordings, speeches, and any situation where you need to read text naturally while maintaining eye contact with your audience.

Sign in to unlock the full potential of PromptFlow!
""".trimIndent()