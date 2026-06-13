package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Falling element models
data class FallingItem(
    val id: Long,
    val relativeX: Float, // 0f to 1f
    var relativeY: Float, // 0f to 1200f (height px)
    val type: ItemType,
    val speed: Float
)

enum class ItemType {
    SAMOSA,
    GOLDEN_SAMOSA,
    CHILI,
    TEA
}

@Composable
fun SamosaCatchScreen(
    onGameFinished: (score: Int, samosasCollected: Int, starsEarned: Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableStateOf(0) }
    var samosasCollectedQuantity by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(45) }
    var lives by remember { mutableStateOf(3) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Motu position (0f to 1f horizontal relative)
    var motuRelativeX by remember { mutableStateOf(0.5f) }
    
    // List of falling items
    var fallingItems by remember { mutableStateOf(emptyList<FallingItem>()) }
    var itemCounter by remember { mutableStateOf(0L) }

    // Fun toast/text popping overlay when item is caught
    var caughtBubbleText by remember { mutableStateOf("") }
    var caughtBubbleTimer by remember { mutableStateOf(0) }

    // Tick Game Loop
    LaunchedEffect(isGameOver, isPaused) {
        if (isGameOver || isPaused) return@LaunchedEffect
        
        while (timeLeft > 0 && lives > 0) {
            delay(16) // tick around 60fps
            
            // Move item states
            val currentItems = fallingItems.toMutableList()
            val itemsToRemove = mutableListOf<FallingItem>()
            
            // Speed factor
            val speedFactor = 1f + (score / 400f)

            // Iterate and calculate hits
            for (item in currentItems) {
                item.relativeY += item.speed * speedFactor
                
                // Bottom boundary hit - collision with Motu!
                // Motu is active at approx relativeY 82% to 92% of the screen.
                if (item.relativeY >= 900f && item.relativeY <= 960f) {
                    val distance = kotlin.math.abs(item.relativeX - motuRelativeX)
                    if (distance < 0.16f) {
                        // HIT! Got samosa or chili
                        itemsToRemove.add(item)
                        when (item.type) {
                            ItemType.SAMOSA -> {
                                score += 10
                                samosasCollectedQuantity++
                                caughtBubbleText = "YUM! +10 🥟"
                                caughtBubbleTimer = 25
                            }
                            ItemType.GOLDEN_SAMOSA -> {
                                score += 30
                                samosasCollectedQuantity += 3
                                caughtBubbleText = "SURE SUPER POWER! +30 👑🥟"
                                caughtBubbleTimer = 35
                            }
                            ItemType.CHILI -> {
                                score = maxOf(0, score - 15)
                                lives--
                                caughtBubbleText = "AAYEE MIRCHI! 🔥🥵 -15"
                                caughtBubbleTimer = 30
                            }
                            ItemType.TEA -> {
                                score += 15
                                caughtBubbleText = "EK CHAI CHALU! +15 ☕"
                                caughtBubbleTimer = 25
                            }
                        }
                    }
                }

                // If fall below screen limit
                if (item.relativeY > 1050f) {
                    itemsToRemove.add(item)
                    if (item.type == ItemType.SAMOSA || item.type == ItemType.GOLDEN_SAMOSA) {
                        // Missing Samosas doesn't reduce lives to keep it child friendly,
                        // but gives a tiny sigh
                    }
                }
            }
            
            currentItems.removeAll(itemsToRemove)

            // Periodically spawn a new item (every ~60 ticks = ~1 second)
            if (Random.nextInt(55) == 0 && currentItems.size < 6) {
                val types = listOf(ItemType.SAMOSA, ItemType.SAMOSA, ItemType.SAMOSA, ItemType.GOLDEN_SAMOSA, ItemType.CHILI, ItemType.TEA)
                val chosenType = types[Random.nextInt(types.size)]
                currentItems.add(
                    FallingItem(
                        id = itemCounter++,
                        relativeX = Random.nextFloat() * 0.88f + 0.06f,
                        relativeY = 0f,
                        type = chosenType,
                        speed = Random.nextFloat() * 4f + 5f
                    )
                )
            }

            if (caughtBubbleTimer > 0) {
                caughtBubbleTimer--
                if (caughtBubbleTimer == 0) {
                    caughtBubbleText = ""
                }
            }

            fallingItems = currentItems
        }
        
        // Loop exited - Game Over
        isGameOver = true
    }

    // Time progress effect
    LaunchedEffect(isGameOver, isPaused) {
        if (isGameOver || isPaused) return@LaunchedEffect
        while (timeLeft > 0 && lives > 0) {
            delay(1000)
            timeLeft--
        }
    }

    // Calculate stars awarded (1 to 3)
    val starRating = when {
        score >= 350 -> 3
        score >= 150 -> 2
        score >= 40 -> 1
        else -> 0
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF81D4FA), // Sunny sky above Furfuri hill
                        Color(0xFF29B6F6)
                    )
                )
            )
    ) {
        
        // Game Board Viewport
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            
            // Top HUD Status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Return button
                IconButton(
                    onClick = { isPaused = true },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Filled.Pause, contentDescription = "Pause Game")
                }

                // Lives Hearts
                Row {
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Hearts",
                            tint = if (i <= lives) Color.Red else Color.LightGray.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(28.dp)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }

                // Live Points
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🏆 Score: $score",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Seconds timer
                Box(
                    modifier = Modifier
                        .background(Color(0xFFD84315), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⏰ $timeLeft s",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }

            // Central Game Engine Canvas View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Update Motu relative position based on standard dragging limits
                            val adjustment = dragAmount.x / size.width
                            motuRelativeX = (motuRelativeX + adjustment).coerceIn(0.08f, 0.92f)
                        }
                    }
            ) {
                // Sky background element drawings
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Ground line at bottom
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                        ),
                        topLeft = Offset(0f, size.height * 0.95f),
                        size = Size(size.width, size.height * 0.05f)
                    )
                }

                // Render dynamically spawned Samosas and items
                for (item in fallingItems) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (item.relativeX * 360f).dp, // Adaptive display translate
                                y = (item.relativeY * 0.6f).dp
                            )
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (item.type) {
                            ItemType.SAMOSA -> Text("🥟", fontSize = 28.sp)
                            ItemType.GOLDEN_SAMOSA -> Text("👑", fontSize = 20.sp) // gold crown icon overlay
                            ItemType.CHILI -> Text("🌶️", fontSize = 28.sp)
                            ItemType.TEA -> Text("☕", fontSize = 24.sp)
                        }
                        
                        if (item.type == ItemType.GOLDEN_SAMOSA) {
                            Text("🥟", fontSize = 28.sp, modifier = Modifier.offset(y = 10.dp))
                        }
                    }
                }

                // Motu at the bottom! (Drawn symmetrically centered on the Canvas position)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(
                            x = (motuRelativeX * 360f - 35f).dp, // offset back by half of size
                            y = (-25).dp
                        )
                        .size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Motu Big Fun Foodie Face
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFFFCC33), CircleShape)
                                .border(3.dp, Color(0xFF8D6E63), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Huge laughing eyes
                                Text("😂", fontSize = 32.sp)
                            }
                        }
                        
                        // Motu signature red vest
                        Box(
                            modifier = Modifier
                                .size(48.dp, 24.dp)
                                .background(Color.Red, RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Yellow, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("MOTU", color = Color.Yellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Bubble caught text indicator feedback
                if (caughtBubbleText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-50).dp)
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = caughtBubbleText,
                            color = Color.Yellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Bottom on-screen tap helper controls for kids
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BAAYEEN (Left) Button
                Button(
                    onClick = {
                        motuRelativeX = (motuRelativeX - 0.12f).coerceIn(0.08f, 0.92f)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(54.dp)
                        .testTag("left_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Move Left", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("← BAAYEEN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // DAAYEEN (Right) Button
                Button(
                    onClick = {
                        motuRelativeX = (motuRelativeX + 0.12f).coerceIn(0.08f, 0.92f)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE64A19)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(54.dp)
                        .testTag("right_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("DAAYEEN →", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Move Right", tint = Color.White)
                    }
                }
            }
        }

        // Active Overlays (Pause / Game Over Modals)
        if (isPaused) {
            AlertDialog(
                onDismissRequest = { isPaused = false },
                title = { Text("Furfuri Nagar Break ⏸️", fontWeight = FontWeight.Bold) },
                text = { Text("Game helper: Motu is resting. Resume whenever you are ready!", fontSize = 15.sp) },
                confirmButton = {
                    Button(
                        onClick = { isPaused = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("RESUME KREIN!")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isPaused = false
                        onNavigateBack()
                    }) {
                        Text("MERE MAP (EXIT)")
                    }
                }
            )
        }

        if (isGameOver) {
            // Samosas earned calculation (always give at least 3 for playing to maintain kids' dopamine levels!)
            val calculatedSamosasEarned = maxOf(3, score / 15)

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .border(4.dp, Color(0xFFE64A19), RoundedCornerShape(24.dp))
                    .testTag("game_over_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "MAZA AA GAYA! 🎉",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE64A19),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Round Complete!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("😋", fontSize = 56.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Final Tally
                    Text(
                        text = "Score: $score",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reward summary with stars
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Stars",
                                tint = if (i <= starRating) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Loot summary
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Gift: ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("🥟 +$calculatedSamosasEarned Samosas!", fontSize = 16.sp, color = Color(0xFFE64A19), fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onGameFinished(score, calculatedSamosasEarned, starRating)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_game_button")
                    ) {
                        Text(
                            text = "SEVA SAVED & EXIT 🏠",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
