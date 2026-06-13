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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(
    currentStars: Int,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Slogan cyclic change
    val slogans = listOf(
        "Motu aur Patlu ki Jodi... Kamaal ki Jodi!",
        "Khaali dimaag shaitan ka ghar hota hai! 🧠",
        "Chingum ke seeng se bachna namumkin hai! 👮",
        "Dr. Jhatka ka naya invention ho gaya taiyar! 🧪",
        "Arey, Motu ko lag gayi bhookh! 🥟"
    )
    var sloganIndex by remember { mutableStateOf(0) }
    var currentSlogan by remember { mutableStateOf(slogans[0]) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            sloganIndex = (sloganIndex + 1) % slogans.size
            currentSlogan = slogans[sloganIndex]
        }
    }

    // Gentle floating offset
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutBack),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Animated character rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    // Background playful sun ray spinner
    val sunRayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rays"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00C6FF), // Sky blue
                        Color(0xFF0072FF)
                    )
                )
            )
    ) {
        // Rotating Sun rays behind for a cheerful kids-game effect
        Canvas(
            modifier = Modifier
                .size(600.dp)
                .align(Alignment.Center)
                .rotate(sunRayRotation)
        ) {
            val rayCount = 12
            val rayWidth = 20f
            for (i in 0 until rayCount) {
                val angle = (360f / rayCount) * i
                drawArc(
                    color = Color.White.copy(alpha = 0.12f),
                    startAngle = angle - rayWidth / 2,
                    sweepAngle = rayWidth,
                    useCenter = true,
                )
            }
        }

        // Floating fluffy clouds in background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp)
        ) {
            CloudView(modifier = Modifier.align(Alignment.TopStart).offset(x = (-30).dp + (floatOffset * 0.5f).dp, y = 20.dp).scale(0.8f))
            CloudView(modifier = Modifier.align(Alignment.TopEnd).offset(x = 20.dp - (floatOffset * 0.4f).dp, y = 10.dp).scale(0.7f))
        }

        // Main Contents
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Top Header Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stars score
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Stars Count",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$currentStars Stars",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Audio configuration
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .testTag("audio_toggle")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeMute,
                        contentDescription = "Sound Configuration",
                        tint = Color.White
                    )
                }
            }

            // Central Animated Mascot & Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .offset(y = floatOffset.dp)
            ) {
                // Game Title
                Text(
                    text = "MOTU PATLU",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFEC40),
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = Color(0xFFFF2A00),
                            offset = Offset(8f, 8f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier.animateContentSize()
                )
                
                Text(
                    text = "FURFURI NAGAR CHUGGY RUN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(2f, 2f),
                            blurRadius = 2f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Procedural Samosa Mascot (drawn dynamically with cartoon properties)
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(rotationAngle)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .border(3.dp, Color(0xFFFFCC00).copy(alpha = 0.5f), CircleShape)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Let's draw a funny, delicious SAMOSA!
                        val path = Path().apply {
                            moveTo(size.width / 2f, 15f) // Top tip
                            lineTo(size.width - 20f, size.height - 25f) // Bottom right
                            lineTo(20f, size.height - 25f) // Bottom left
                            close()
                        }
                        
                        // Golden brown samosa fill
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                            )
                        )
                        // Thick crisp borders
                        drawPath(
                            path = path,
                            color = Color(0xFF8D6E63),
                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Samosa funny folds (flaky crust lines)
                        drawLine(
                            color = Color(0xff5D4037),
                            start = Offset(size.width / 2f, 15f),
                            end = Offset(size.width / 2f, size.height - 25f),
                            strokeWidth = 4f
                        )
                        
                        // Playful cartoon eyes!
                        drawCircle(
                            color = Color.White,
                            radius = 20f,
                            center = Offset(size.width / 2f - 24f, size.height / 2f - 10f)
                        )
                        drawCircle(
                            color = Color.Black,
                            radius = 8f,
                            center = Offset(size.width / 2f - 24f, size.height / 2f - 10f)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 20f,
                            center = Offset(size.width / 2f + 24f, size.height / 2f - 10f)
                        )
                        drawCircle(
                            color = Color.Black,
                            radius = 8f,
                            center = Offset(size.width / 2f + 24f, size.height / 2f - 10f)
                        )

                        // Comic smile
                        drawArc(
                            color = Color(0xFFE53935),
                            startAngle = 10f,
                            sweepAngle = 160f,
                            useCenter = false,
                            topLeft = Offset(size.width / 2f - 25f, size.height / 2f + 10f),
                            size = Size(50f, 35f),
                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                        )
                    }

                    // Steam indicator (Samosa is fresh & hot!)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-25).dp)
                    ) {
                        Text("♨️", fontSize = 22.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("♨️", fontSize = 24.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            // Bottom controls with the slogan banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Slogan Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFF176), Color(0xFFFFF9C4))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Crossfade(
                        targetState = currentSlogan,
                        animationSpec = tween(600),
                        label = "sloganCross"
                    ) { slogan ->
                        Text(
                            text = slogan,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Giant Play Button
                Button(
                    onClick = onStartGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF3D00) // Deep warm vibrant orange
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 2.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(64.dp)
                        .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(28.dp))
                        .testTag("play_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play Button Icon",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CHALO KHELEIN! (PLAY)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloudView(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(100.dp, 60.dp)) {
        val paint = Paint().apply {
            color = Color.White.copy(alpha = 0.75f)
            style = PaintingStyle.Fill
        }
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Draw standard fluffy bubbles
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = canvasHeight * 0.4f,
            center = Offset(canvasWidth * 0.3f, canvasHeight * 0.55f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = canvasHeight * 0.5f,
            center = Offset(canvasWidth * 0.55f, canvasHeight * 0.45f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = canvasHeight * 0.35f,
            center = Offset(canvasWidth * 0.8f, canvasHeight * 0.6f)
        )
    }
}
