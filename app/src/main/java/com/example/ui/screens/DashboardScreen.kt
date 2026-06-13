package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameProgress

data class CharacterAvatar(
    val id: String,
    val name: String,
    val emoji: String,
    val title: String,
    val cost: Int,
    val primaryColor: Color,
    val bio: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    progress: GameProgress,
    onSelectAvatar: (String) -> Unit,
    onUnlockAvatar: (String, Int) -> Unit,
    onNavigateToGame: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Character metadata
    val avatars = remember {
        listOf(
            CharacterAvatar("motu", "Motu", "🥟😋", "Samosa King", 0, Color(0xFFFF9800), "Loves hot Samosas! Gives him absolute superpower!"),
            CharacterAvatar("patlu", "Patlu", "👓💡", "Maha Intellect", 0, Color(0xFF03A9F4), "The brain power! Solves any complex crisis."),
            CharacterAvatar("jhatka", "Dr. Jhatka", "🧪⚡", "Mad Scientist", 40, Color(0xFF4CAF50), "Invents crazy flying gadgets that often backfire!"),
            CharacterAvatar("chingum", "Chingum", "👮💥", "Singham Cop", 80, Color(0xFF3F51B5), "Throws coconuts and shoots in the air directly!"),
            CharacterAvatar("john", "John Don", "🕶️🎩", "Rhyming Thief", 150, Color(0xFFE91E63), "Thief who speaks in funny Urdu rhymes!")
        )
    }

    var showUnlockFailedDialog by remember { mutableStateOf(false) }
    var failedAvatarName by remember { mutableStateOf("") }
    var failedAvatarCost by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4), // Yellow sand base of Furfuri village
                        Color(0xFFFFECB3),
                        Color(0xFFFFCC80)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            
            // Top App Bar Custom Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF4E342E)
                    )
                }

                Text(
                    text = "FURFURI NAGAR MAP",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF5D4037)
                )

                // High stats bar
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE64A19), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🥟", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${progress.totalSamosas}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                
                // Welcome Kids Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFFFE082), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val activeAvatar = avatars.find { it.id == progress.selectedAvatar }
                            Text(
                                text = activeAvatar?.emoji ?: "🥟",
                                fontSize = 36.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Namaste, Chota Hero! 👋",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Avatar: ${avatars.find { it.id == progress.selectedAvatar }?.name ?: "Motu"}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = "Rank: ${progress.playerTitle}",
                                fontSize = 13.sp,
                                color = Color(0xFFE64A19),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 1: Children Characters Selector (To unlock)
                Text(
                    text = "CHOOSE YOUR AVATAR (AVATAR BADALEIN)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF795548),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(avatars) { avatar ->
                        val isUnlocked = progress.unlockedAvatars.split(",").contains(avatar.id)
                        val isSelected = progress.selectedAvatar == avatar.id

                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .testTag("avatar_card_${avatar.id}")
                                .clickable {
                                    if (isUnlocked) {
                                        onSelectAvatar(avatar.id)
                                    } else {
                                        if (progress.totalSamosas >= avatar.cost) {
                                            onUnlockAvatar(avatar.id, avatar.cost)
                                        } else {
                                            failedAvatarName = avatar.name
                                            failedAvatarCost = avatar.cost
                                            showUnlockFailedDialog = true
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) avatar.primaryColor.copy(alpha = 0.15f) else Color.White
                            ),
                            border = if (isSelected) {
                                BorderStroke(3.dp, avatar.primaryColor)
                            } else {
                                BorderStroke(1.dp, Color.LightGray)
                            },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Face
                                Text(
                                    text = avatar.emoji,
                                    fontSize = 40.sp,
                                    modifier = Modifier.scale(if (isSelected) 1.15f else 1.0f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = avatar.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = avatar.title,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Button state
                                if (isUnlocked) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = avatar.primaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "PLAY",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = avatar.primaryColor
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(Color(0xFFFF5722), Color(0xFFFF9800))
                                                ),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🥟 ${avatar.cost}",
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: MINI-GAMES PANEL
                Text(
                    text = "SELECT AN ADVENTURE GALI (MINI GAMES)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF795548),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Game 1: Motu's Samosa Catch
                MiniGameCard(
                    title = "Motu's Samosa Catch! 🥟",
                    description = "Motu is starving! Drag Motu left & right to catch samosas. Avoid toxic green chillies!",
                    highScore = progress.sSamosaHighScore,
                    imageEmoji = "😋🚀",
                    difficulty = "Chota (Easy)",
                    themeColor = Color(0xFFFF9800),
                    onClick = { onNavigateToGame("samosa") }
                )

                // Game 2: Patlu's Balloon Pop
                MiniGameCard(
                    title = "Patlu's Balloon Pop! 🎈",
                    description = "Furfuri Nagar balloon carnival! Pop floating balloons loaded with points. Avoid explosives!",
                    highScore = progress.sBalloonHighScore,
                    imageEmoji = "🎯🧠",
                    difficulty = "Medium",
                    themeColor = Color(0xFF0288D1),
                    onClick = { onNavigateToGame("balloon") }
                )

                // Game 3: Inspector Chingum's Coconut Slap
                MiniGameCard(
                    title = "Chingum's Coconut Trap! 🥥",
                    description = "John the Don's goons are running away! Crack coconuts from the tree to block their path!",
                    highScore = progress.sChingumHighScore,
                    imageEmoji = "👮🚴",
                    difficulty = "Chunouti (Hard!)",
                    themeColor = Color(0xFF3F51B5),
                    onClick = { onNavigateToGame("chingum") }
                )

                // Game 4: Dr. Jhatka's Scientific Invention Room
                MiniGameCard(
                    title = "Dr. Jhatka's Invention Lab 🧪",
                    description = "Crazy sci-fi simulator button! Test lasers, jetpacks, and chemical formulas to gather Samosa fuel!",
                    highScore = 0,
                    imageEmoji = "⚡🦖",
                    difficulty = "Playroom (Fun)",
                    themeColor = Color(0xFF4CAF50),
                    onClick = { onNavigateToGame("jhatka") }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Dialog for incomplete funds
        if (showUnlockFailedDialog) {
            AlertDialog(
                onDismissRequest = { showUnlockFailedDialog = false },
                title = {
                    Text(
                        text = "Arey, Samosa Khatam! 🥟❌",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Text(
                        text = "You need $failedAvatarCost Samosas to unlock $failedAvatarName! Play games to gather more!",
                        fontSize = 15.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showUnlockFailedDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                    ) {
                        Text("THIK HAI! (OK)")
                    }
                }
            )
        }
    }
}

@Composable
fun MiniGameCard(
    title: String,
    description: String,
    highScore: Int,
    imageEmoji: String,
    difficulty: String,
    themeColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("game_card_${title.replace(" ", "_")}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left character illustration box
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(themeColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .border(2.dp, themeColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = imageEmoji,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(themeColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = difficulty,
                            fontSize = 10.sp,
                            color = themeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // subtle spacing
                
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (highScore > 0) {
                        Text(
                            text = "🏆 High Score: $highScore",
                            fontSize = 12.sp,
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "🆓 Play for fun!",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHALO RUN! 🎮",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Go",
                            tint = themeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
