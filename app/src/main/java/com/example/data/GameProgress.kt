package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey val id: Int = 1,
    val selectedAvatar: String = "motu", // "motu", "patlu", "chingum", "jhatka"
    val playerTitle: String = "Furfuri Cadet",
    val totalSamosas: Int = 0,
    val totalStars: Int = 0,
    val sSamosaHighScore: Int = 0,
    val sBalloonHighScore: Int = 0,
    val sChingumHighScore: Int = 0,
    val unlockedAvatars: String = "motu", // "motu,patlu"
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true
)
