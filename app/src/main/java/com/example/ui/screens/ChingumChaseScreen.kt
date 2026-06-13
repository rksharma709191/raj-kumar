package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Coconut dropping physics state
data class CoconutProjectile(
    val id: Long,
    val relativeX: Float, // static X where dropped
    var relativeY: Float, // falls down from 150f to 850f
    var speedY: Float = 0f,
    val gravity: Float = 0.5f
)

@Composable
fun ChingumChaseScreen(
    onGameFinished: (score: Int, samosasCollected: Int, starsEarned: Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableStateOf(0) }
    var samosasCollectedQuantity by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(40) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Goon Cart Position (X from 0f to 1f, riding back and forth)
    var goonRelativeX by remember { mutableStateOf(0f) }
    var goonMovingRight by remember { mutableStateOf(true) }
    var goonSpeed by remember { mutableStateOf(0.015f) } // relative speed per tick

    // Coconuts in flight
    var activeCoconuts by remember { mutableStateOf(emptyList<CoconutProjectile>()) }
    var coconutIdCounter by remember { mutableStateOf(0L) }

    // Trees layout: 3 branches we can drop from
    val branchXOffsets = listOf(0.25f, 0.50f, 0.75f)
    var activeBranchIndex by remember { mutableStateOf(1) } // Default Middle tree branch selected

    // Hilarious Chingum Dialogue Bubbles
    val copPhrases = listOf(
        "Aey! Chingum ke seeng se bachna namumkin hai! 👮",
        "Don't write rules, I make laws! 💥",
        "John the Don, surrender krro! 🥥",
        "This coconut will slap John's goon!",
        "Chingum will fire bullet in the sky! 🥥💨"
    )
    var currentPhrasesText by remember { mutableStateOf(copPhrases[0]) }

    // Collision particles feedback
    var showExplosion by remember { mutableStateOf(false) }
    var explosionX by remember { mutableStateOf(0.5f) }
    var explosionText by remember { mutableStateOf("") }
    var explosionTimer by remember { mutableStateOf(0) }

    // Game loop tick
    LaunchedEffect(isGameOver, isPaused) {
        if (isGameOver || isPaused) return@LaunchedEffect

        while (timeLeft > 0) {
            delay(16) // 60fps physics simulation

            // 1. Move Goon Cart horizontally
            if (goonMovingRight) {
                goonRelativeX += goonSpeed
                if (goonRelativeX >= 0.88f) {
                    goonMovingRight = false
                }
            } else {
                goonRelativeX -= goonSpeed
                if (goonRelativeX <= 0.04f) {
                    goonMovingRight = true
                }
            }

            // 2. Fall coconuts projectiles
            val currentCoconuts = activeCoconuts.toMutableList()
            val coconutsToRemove = mutableListOf<CoconutProjectile>()

            for (coconut in currentCoconuts) {
                coconut.speedY += coconut.gravity
                coconut.relativeY += coconut.speedY

                // Hits bottom ground lane (approx 850f is the goon level)
                if (coconut.relativeY >= 820f && coconut.relativeY <= 890f) {
                    // Check intersection with Goon coordinate X
                    val distance = kotlin.math.abs(coconut.relativeX - goonRelativeX)
                    if (distance < 0.15f) {
                        // COLLISION SUCCESS! Hit John's goon!
                        score += 50
                        samosasCollectedQuantity += 2 // awards samosas directly!
                        coconutsToRemove.add(coconut)

                        // Explosion burst position
                        explosionX = goonRelativeX
                        showExplosion = true
                        explosionText = "💥 DHAMAKA HIT! +50"
                        explosionTimer = 30

                        // Respawn goon on other side with slightly higher speed!
                        goonRelativeX = if (Random.nextBoolean()) 0.05f else 0.85f
                        goonMovingRight = goonRelativeX < 0.5f
                        goonSpeed = (0.012f + (score / 1500f) * 0.01f).coerceAtMost(0.035f)

                        // Update phrase
                        currentPhrasesText = copPhrases[Random.nextInt(copPhrases.size)]
                    }
                }

                // If coconut hits ground and rolls away without hitting goon
                if (coconut.relativeY > 950f) {
                    coconutsToRemove.add(coconut)
                    
                    explosionX = coconut.relativeX
                    showExplosion = true
                    explosionText = "💦 CRACK! MISSED!"
                    explosionTimer = 18
                }
            }

            if (explosionTimer > 0) {
                explosionTimer--
                if (explosionTimer == 0) {
                    showExplosion = false
                    explosionText = ""
                }
            }

            currentCoconuts.removeAll(coconutsToRemove)
            activeCoconuts = currentCoconuts
        }

        isGameOver = true
    }

    // Time progress
    LaunchedEffect(isGameOver, isPaused) {
        if (isGameOver || isPaused) return@LaunchedEffect
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft % 8 == 0) {
                // cycle funny police phrase
                currentPhrasesText = copPhrases[Random.nextInt(copPhrases.size)]
            }
        }
    }

    val starRating = when {
        score >= 400 -> 3
        score >= 200 -> 2
        score >= 50 -> 1
        else -> 0
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F7FA), // Hot sun of Indian Furfuri plains
                        Color(0xFFFFE0B2),
                        Color(0xFFFFB74D)
                    )
                )
            )
    ) {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            
            // Top HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isPaused = true },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause")
                }

                // Title
                Text(
                    text = "CHINGUM COPS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF3F51B5)
                )

                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Score: $score",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFF3F51B5), RoundedCornerShape(12.dp))
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

            // Central Game Field (Upper half: Tree branches, Lower half: Highway chase)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                
                // Draw Highway horizon line
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Gray highway road
                    drawRect(
                        color = Color(0xFF78909C),
                        topLeft = Offset(0f, size.height * 0.82f),
                        size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.18f)
                    )
                    // Yellow road stripes
                    for (i in 0..6) {
                        drawRect(
                            color = Color(0xFFFFD54F),
                            topLeft = Offset((i * size.width / 6f) + 20f, size.height * 0.90f),
                            size = androidx.compose.ui.geometry.Size(50f, 10f)
                        )
                    }
                }

                // Giant Animated Coconut Overhanging branches
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    branchXOffsets.forEachIndexed { index, relX ->
                        val isSelected = index == activeBranchIndex
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x223F51B5) else Color.Transparent)
                                .clickable { activeBranchIndex = index }
                                .padding(8.dp)
                        ) {
                            Text("🌴", fontSize = 48.sp)
                            Text(
                                text = "Branch ${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF3F51B5) else Color.Gray
                            )
                            Row {
                                Text("🥥", fontSize = 16.sp)
                                Text("🥥", fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Render falling coconuts projectles
                for (coconut in activeCoconuts) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (coconut.relativeX * 360f).dp,
                                y = (coconut.relativeY * 0.65f).dp
                            )
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🥥", fontSize = 28.sp)
                    }
                }

                // John the Don's Goon escape cart at bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(
                            x = (goonRelativeX * 360f).dp,
                            y = (-45).dp // riding highways
                        )
                        .size(76.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (goonMovingRight) "🚴‍♂️💨" else "💨🚴‍♂️",
                            fontSize = 36.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp)
                        ) {
                            Text("GOON CART", color = Color.White, fontSize = 7.sp)
                        }
                    }
                }

                // Hit / explosion indicators
                if (showExplosion) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(
                                x = (explosionX * 360f).dp,
                                y = (-120).dp
                            )
                            .background(Color.Yellow, RoundedCornerShape(12.dp))
                            .border(2.dp, Color.Red, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = explosionText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Red
                        )
                    }
                }

                // Inspector Chingum Cop at left side waving!
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 50.dp, start = 12.dp)
                        .size(72.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF8C9EFF), CircleShape)
                                .border(2.dp, Color(0xFF3F51B5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👮", fontSize = 26.sp)
                        }
                        Text("CHINGUM", color = Color(0xFF3F51B5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Dialog bubbles of Chingum
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 80.dp, y = (-100).dp)
                        .widthIn(max = 180.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.5.dp, Color(0xFF3F51B5), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = currentPhrasesText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }

            // Large action drop trigger button at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        // Max 2 active drops to force timing challenges for kids!
                        if (activeCoconuts.size < 2) {
                            val targetX = branchXOffsets[activeBranchIndex]
                            activeCoconuts = activeCoconuts + CoconutProjectile(
                                id = coconutIdCounter++,
                                relativeX = targetX,
                                relativeY = 150f
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00)),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("shoot_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🥥 DROP COCONUT (X MARKO) 🥥", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        }

        // Active State Dialogs
        if (isPaused) {
            AlertDialog(
                onDismissRequest = { isPaused = false },
                title = { Text("Chingum Headquarters ⏸️", fontWeight = FontWeight.Bold) },
                text = { Text("John the Don is waiting! Resume policing duties!", fontSize = 15.sp) },
                confirmButton = {
                    Button(onClick = { isPaused = false }) {
                        Text("RESUME CHASE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isPaused = false
                        onNavigateBack()
                    }) {
                        Text("EXIT")
                    }
                }
            )
        }

        if (isGameOver) {
            val SamosasEarned = maxOf(3, score / 20)
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .border(4.dp, Color(0xFF3F51B5), RoundedCornerShape(24.dp))
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
                        text = "CASE SOLVED! 👮‍♀️👮‍♂️",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF3F51B5),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Great shots! Furfuri Nagar is secure!",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("🥥🤩👮‍♂️", fontSize = 48.sp)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Score: $score",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A237E)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = if (i <= starRating) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8EAF6), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF7986CB), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Reward Loot: 🥟 +$SamosasEarned Samosas!",
                            fontSize = 14.sp,
                            color = Color(0xFF3F51B5),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onGameFinished(score, SamosasEarned, starRating)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_game_button")
                    ) {
                        Text(
                            text = "PROCEED TO REWARDS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
