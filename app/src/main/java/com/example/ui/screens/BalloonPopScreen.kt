package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Balloon(
    val id: Long,
    val textEmoji: String,
    val type: String, // "samosa", "book", "star", "bomb", "gift"
    var relativeX: Float, // percentage 10% to 85%
    var relativeY: Float, // from 800f (bottom) down to -100f (top)
    val color: Color,
    val speed: Float,
    val size: Int = Random.nextInt(20) + 65 // radius sizes
)

@Composable
fun BalloonPopScreen(
    onGameFinished: (score: Int, samosasCollected: Int, starsEarned: Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var score by remember { mutableStateOf(0) }
    var samosasCollectedQuantity by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(40) }
    var playsLeft by remember { mutableStateOf(3) } // Lives or strikes
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    var balloons by remember { mutableStateOf(emptyList<Balloon>()) }
    var balloonCounter by remember { mutableStateOf(0L) }

    // Particle effect message when popped
    var floatingEffectText by remember { mutableStateOf("") }
    var floatingEffectX by remember { mutableStateOf(0.5f) }
    var floatingEffectY by remember { mutableStateOf(0.5f) }
    var effectTimer by remember { mutableStateOf(0) }

    // Game Loop
    LaunchedEffect(isGameOver, isPaused) {
        if (isGameOver || isPaused) return@LaunchedEffect

        while (timeLeft > 0 && playsLeft > 0) {
            delay(22) // slightly slower tick for balloon float rhythm
            
            val currentBalloons = balloons.toMutableList()
            val balloonsToRemove = mutableListOf<Balloon>()

            // Rise balloons
            for (balloon in currentBalloons) {
                balloon.relativeY -= balloon.speed
                if (balloon.relativeY < -150f) {
                    balloonsToRemove.add(balloon)
                    // If a healthy balloon floats away unpopped, nothing bad happens,
                    // but let's keep things tidy
                }
            }

            currentBalloons.removeAll(balloonsToRemove)

            // Spawn dynamic balloons
            if (Random.nextInt(32) == 0 && currentBalloons.size < 7) {
                val config = listOf(
                    Triple("🥟", "samosa", Color(0xFFFF9800)),
                    Triple("🥟", "samosa", Color(0xFFFF9800)),
                    Triple("📚", "book", Color(0xFF03A9F4)),
                    Triple("⭐", "star", Color(0xFFFFEB3B)),
                    Triple("🎁", "gift", Color(0xFF9C27B0)),
                    Triple("💣", "bomb", Color(0xFF37474F))
                )
                val chosen = config[Random.nextInt(config.size)]
                
                currentBalloons.add(
                    Balloon(
                        id = balloonCounter++,
                        textEmoji = chosen.first,
                        type = chosen.second,
                        relativeX = Random.nextFloat() * 0.75f + 0.12f,
                        relativeY = 900f,
                        color = chosen.third,
                        speed = Random.nextFloat() * 2.5f + 2.5f
                    )
                )
            }

            if (effectTimer > 0) {
                effectTimer--
                if (effectTimer == 0) {
                    floatingEffectText = ""
                }
            }

            balloons = currentBalloons
        }

        isGameOver = true
    }

    // Time ticker
    LaunchedEffect(isGameOver, isPaused) {
        if (isGameOver || isPaused) return@LaunchedEffect
        while (timeLeft > 0 && playsLeft > 0) {
            delay(1000)
            timeLeft--
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
                        Color(0xFFE1F5FE), // Fresh morning pastel cyan
                        Color(0xFF81D4FA),
                        Color(0xFF4FC3F7)
                    )
                )
            )
    ) {
        
        // Clouds & mountain backings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CloudView(modifier = Modifier.scale(0.8f))
            CloudView(modifier = Modifier.scale(0.6f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            
            // HUD header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isPaused = true },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause")
                }

                Row {
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Shield strikes",
                            tint = if (i <= playsLeft) Color(0xFFE91E63) else Color.DarkGray.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(28.dp)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }

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
                        .background(Color(0xFF0288D1), RoundedCornerShape(12.dp))
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

            // Air Space for Balloon rendering (Relative Box)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                
                // Render current floating balloons
                for (balloon in balloons) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (balloon.relativeX * 360f).dp,
                                y = (balloon.relativeY * 0.65f).dp
                            )
                            .size(balloon.size.dp)
                            .clip(CircleShape)
                            .background(balloon.color)
                            .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .testTag("balloon_${balloon.id}")
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null // no bulky default rectangular ripple
                            ) {
                                // Pop balloon event
                                val currentList = balloons.toMutableList()
                                currentList.remove(balloon)
                                balloons = currentList

                                // Floating bubble positions
                                floatingEffectX = balloon.relativeX
                                floatingEffectY = balloon.relativeY
                                effectTimer = 20

                                when (balloon.type) {
                                    "samosa" -> {
                                        score += 15
                                        samosasCollectedQuantity++
                                        floatingEffectText = "+15 🥟 YUM!"
                                    }
                                    "book" -> {
                                        score += 25
                                        floatingEffectText = "+25 📚 IQ BOOST!"
                                    }
                                    "star" -> {
                                        score += 30
                                        floatingEffectText = "+30 ⭐ CHAMP!"
                                    }
                                    "gift" -> {
                                        score += 40
                                        samosasCollectedQuantity += 2
                                        floatingEffectText = "+40 🎁 GIFT! 🥟"
                                    }
                                    "bomb" -> {
                                        score = maxOf(0, score - 30)
                                        playsLeft--
                                        floatingEffectText = "💥 DHAMAKA! -30"
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = balloon.textEmoji,
                                fontSize = (balloon.size * 0.38f).sp
                            )
                            // Small decorative balloon string at bottom
                            Text("🎈", fontSize = 10.sp, modifier = Modifier.offset(y = 10.dp))
                        }
                    }
                }

                // Temporary pop scores indicator
                if (floatingEffectText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (floatingEffectX * 360f).dp,
                                y = (floatingEffectY * 0.65f - 40f).dp
                            )
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = floatingEffectText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF01579B)
                        )
                    }
                }

                // Show brainy Patlu checking at bottom corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 16.dp)
                        .size(80.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFFEE58), CircleShape)
                                .border(2.5.dp, Color(0xFF0288D1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤓", fontSize = 28.sp)
                        }
                        Text(
                            text = "PATLU",
                            fontSize = 11.sp,
                            color = Color(0xFF01579B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active State Dialogs
        if (isPaused) {
            AlertDialog(
                onDismissRequest = { isPaused = false },
                title = { Text("Patlu Brain-Freeze ⏸️", fontWeight = FontWeight.Bold) },
                text = { Text("Furfuri Nagar balloon machine is paused. Tap play to continue!", fontSize = 15.sp) },
                confirmButton = {
                    Button(onClick = { isPaused = false }) {
                        Text("RESUME")
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
            val calcSamosas = maxOf(3, score / 15)
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
                    .border(4.dp, Color(0xFF0288D1), RoundedCornerShape(24.dp))
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
                        text = "VILLAGE TRIUMPH! 🎈🎉",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0288D1),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Patlu Balloon Challenge Complete!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("🏆", fontSize = 56.sp)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Score: $score",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF01579B)
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
                            .background(Color(0xFFE1F5FE), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF29B6F6), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Gift Received: 🥟 +$calcSamosas Samosas!",
                            fontSize = 15.sp,
                            color = Color(0xFF0288D1),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onGameFinished(score, calcSamosas, starRating)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_game_button")
                    ) {
                        Text(
                            text = "CLAIM REWARDS 🎁",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
