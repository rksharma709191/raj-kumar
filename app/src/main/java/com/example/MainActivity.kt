package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.GameViewModel
import com.example.ui.screens.IntroScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SamosaCatchScreen
import com.example.ui.screens.BalloonPopScreen
import com.example.ui.screens.ChingumChaseScreen
import com.example.ui.screens.JhatkaLabScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: GameViewModel = viewModel()
                val progress by viewModel.gameProgress.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "intro",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("intro") {
                            IntroScreen(
                                currentStars = progress.totalStars,
                                isSoundEnabled = progress.isSoundEnabled,
                                onToggleSound = {
                                    viewModel.toggleAudio(!progress.isSoundEnabled, progress.isMusicEnabled)
                                },
                                onStartGame = {
                                    navController.navigate("dashboard")
                                }
                            )
                        }

                        composable("dashboard") {
                            DashboardScreen(
                                progress = progress,
                                onSelectAvatar = { avatar ->
                                    viewModel.selectAvatar(avatar)
                                },
                                onUnlockAvatar = { avatar, cost ->
                                    viewModel.unlockAvatar(
                                        avatar = avatar,
                                        cost = cost,
                                        onSuccess = {},
                                        onFailure = {}
                                    )
                                },
                                onNavigateToGame = { route ->
                                    navController.navigate(route)
                                },
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable("samosa") {
                            SamosaCatchScreen(
                                onGameFinished = { score, samosas, stars ->
                                    viewModel.onGameFinished("samosa", score, samosas, stars)
                                    navController.navigateUp()
                                },
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable("balloon") {
                            BalloonPopScreen(
                                onGameFinished = { score, samosas, stars ->
                                    viewModel.onGameFinished("balloon", score, samosas, stars)
                                    navController.navigateUp()
                                },
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable("chingum") {
                            ChingumChaseScreen(
                                onGameFinished = { score, samosas, stars ->
                                    viewModel.onGameFinished("chingum", score, samosas, stars)
                                    navController.navigateUp()
                                },
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable("jhatka") {
                            JhatkaLabScreen(
                                onEarnBonusSamosas = { bonus ->
                                    viewModel.onGameFinished("jhatka", 0, bonus, 0)
                                },
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
