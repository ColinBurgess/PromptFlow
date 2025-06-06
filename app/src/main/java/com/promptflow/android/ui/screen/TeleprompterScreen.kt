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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeleprompterScreen() {
    var isPlaying by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(2f) }
    var fontSize by remember { mutableFloatStateOf(24f) }
    var showSettings by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(sampleText) }

    val listState = rememberLazyListState()

    // Auto-scroll effect
    LaunchedEffect(isPlaying, speed) {
        if (isPlaying) {
            while (isPlaying) {
                delay((100 / speed).toLong())
                if (listState.canScrollForward) {
                    listState.animateScrollBy(1f)
                } else {
                    isPlaying = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main text display
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            contentPadding = PaddingValues(vertical = 100.dp),
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

        // Control panel
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause button
            FloatingActionButton(
                onClick = { isPlaying = !isPlaying },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            // Speed control
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Speed: ${speed.toInt()}x", color = Color.White)
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 1f..10f,
                    modifier = Modifier.width(120.dp)
                )
            }

            // Font size control
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Size: ${fontSize.toInt()}sp", color = Color.White)
                Slider(
                    value = fontSize,
                    onValueChange = { fontSize = it },
                    valueRange = 16f..48f,
                    modifier = Modifier.width(120.dp)
                )
            }

            // Settings button
            IconButton(onClick = { showSettings = !showSettings }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // Settings panel
        if (showSettings) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .width(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Text Editor", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Enter your text here...") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        TextButton(onClick = { showSettings = false }) {
                            Text("Close")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            // Reset scroll position
                            isPlaying = false
                        }) {
                            Text("Reset")
                        }
                    }
                }
            }
        }
    }
}

private val sampleText = """
Welcome to PromptFlow!

This is your professional teleprompter application.

You can control the scrolling speed, adjust the font size, and edit the text to meet your needs.

The text will scroll smoothly from bottom to top, just like a professional teleprompter.

Use the controls at the bottom to:
• Play or pause the scrolling
• Adjust the speed from 1x to 10x
• Change the font size from 16sp to 48sp
• Edit your text content

Perfect for presentations, video recordings, speeches, and any situation where you need to read text naturally while maintaining eye contact with your audience.

Enjoy using PromptFlow for all your teleprompter needs!
""".trimIndent()