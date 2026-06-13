package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Science
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

@Composable
fun JhatkaLabScreen(
    onEarnBonusSamosas: (samosas: Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFormula by remember { mutableStateOf("Red Fire 🔥") }
    var chargedVolts by remember { mutableStateOf(0) }
    var totalSparkCount by remember { mutableStateOf(0) }
    var discoveredGidgetsCount by remember { mutableStateOf(0) }

    var feedbackHeadline by remember { mutableStateOf("Select raw materials to calibrate!") }
    
    // Funny Dr Jhatka dialogues
    val dialogues = listOf(
        "Arey, Mein invention is 200% safe! No worry! 🧪⚡",
        "Ghasitaram says he has 20 years experience of this! 👴",
        "Wait, the voltmeter is smoking! Tap faster!",
        "Das Samosa Fuel Mixer is fully charged! 🥟💥",
        "Arey, don't press that red button! Oh wait, you already did..."
    )
    var currentDialogueText by remember { mutableStateOf(dialogues[0]) }

    var latestDiscoveredGadget by remember { mutableStateOf("") }
    var showBonusAwardDialog by remember { mutableStateOf(false) }

    // Floating particles state for laser sparkles
    var laserSparkTriggered by remember { mutableStateOf(false) }
    var particleCount by remember { mutableStateOf(0) }

    // Dialogue timer cycle
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentDialogueText = dialogues[Random.nextInt(dialogues.size)]
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF263238), // Futuristic laboratory slate dark/matrix theme
                        Color(0xFF37474F),
                        Color(0xFF455A64)
                    )
                )
            )
    ) {
        // Neon network grid in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rows = 12
            val cols = 8
            for (r in 0..rows) {
                drawLine(
                    color = Color.Green.copy(alpha = 0.04f),
                    start = Offset(0f, r * size.height / rows),
                    end = Offset(size.width, r * size.height / rows),
                    strokeWidth = 2f
                )
            }
            for (c in 0..cols) {
                drawLine(
                    color = Color.Green.copy(alpha = 0.04f),
                    start = Offset(c * size.width / cols, 0f),
                    end = Offset(c * size.width / cols, size.height),
                    strokeWidth = 2f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "JHATKA RESEARCH LAB",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )

                Box(
                    modifier = Modifier
                        .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔋 $discoveredGidgetsCount discovered",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Glass Jar / Fluid Simulator Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                border = BorderStroke(2.dp, Color.Green.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Fluid color fill depending on chemical selected
                    val fluidColor = when (selectedFormula) {
                        "Red Fire 🔥" -> Color(0xFFEF5350)
                        "Green Matrix 🧪" -> Color(0xFF66BB6A)
                        "Laser Voltage ⚡" -> Color(0xFF29B6F6)
                        else -> Color(0xFFFFCA28)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.3f + (chargedVolts / 200f))
                            .background(
                                Brush.verticalGradient(
                                    listOf(fluidColor.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )

                    // Flying micro bubbles
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        for (i in 0..10) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.25f),
                                radius = Random.nextFloat() * 12f + 4f,
                                center = Offset(
                                    x = (0.1f + i * 0.08f) * size.width,
                                    y = size.height - (Random.nextFloat() * size.height * (0.3f + (chargedVolts / 150f)))
                                )
                            )
                        }
                    }

                    // Gauge Indicator
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$chargedVolts%",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Invention Fuel Level",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dr Jhatka Character Profile Bubble
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFF81C784), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍🔬", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Dr. Jhatka says:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Green
                    )
                    Text(
                        text = currentDialogueText,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Select Formula buttons
            Text(
                text = "1. CHOOSE FLUID CHEMICAL FORMULA",
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val formulas = listOf("Red Fire 🔥", "Green Matrix 🧪", "Laser Voltage ⚡", "Samosa Blast 🥟")
                formulas.forEach { formula ->
                    val isChecked = selectedFormula == formula
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChecked) Color.Green.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f))
                            .border(1.5.dp, if (isChecked) Color.Green else Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable {
                                selectedFormula = formula
                                feedbackHeadline = "Selected formula: $formula! Ready to spark volts!"
                            }
                            .padding(horizontal = 6.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formula,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) Color.Green else Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2: Continuous spark button
            Text(
                text = "2. SPARK REACTION CHAMBER",
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Button(
                onClick = {
                    totalSparkCount++
                    chargedVolts += 10
                    
                    // Trigger funny laser spark overlay effects
                    laserSparkTriggered = true
                    particleCount = Random.nextInt(10) + 5
                    
                    if (chargedVolts >= 100) {
                        // Max Volts! Trigger deep formula synthesis discovery
                        discoveredGidgetsCount++
                        
                        val gadgetGifts = listOf(
                            "The Samosa Jetpack Flying Ring",
                            "Dr. Jhatka's Anti-Gravity Toaster Hat",
                            "Universal Tea Cup Boiler Ray",
                            "Chingum Flying Cop Helicopter Shoes",
                            "The automatic samosa maker machine"
                        )
                        latestDiscoveredGadget = gadgetGifts[Random.nextInt(gadgetGifts.size)]
                        
                        chargedVolts = 0 // Reset voltmeter indicator
                        showBonusAwardDialog = true
                    } else {
                        feedbackHeadline = "Charging: $chargedVolts volts! Keep hammering Dr Jhatka's charger!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(64.dp)
                    .testTag("charge_invention_button"),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = "Spark", tint = Color.Black)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SPARK LAB MAGNETO! ⚡🔋",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Status output log
            Text(
                text = feedbackHeadline,
                fontSize = 13.sp,
                color = Color.Yellow,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Invention discovery award modal dialog
        if (showBonusAwardDialog) {
            AlertDialog(
                onDismissRequest = { showBonusAwardDialog = false },
                title = {
                    Text(
                        text = "INVENTION SUCCESS! 🧪🎆",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Eureka! You have successfully built:",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = latestDiscoveredGadget,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Dr Jhatka is super happy! Samosas cooked directly into your permanent bank!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("🎁 🥟 +15 Samosas! 🎁", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF9800))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBonusAwardDialog = false
                            onEarnBonusSamosas(15) // Adds bonus directly to saving progress DB
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("SHUKRIYA DOC! (OK)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
