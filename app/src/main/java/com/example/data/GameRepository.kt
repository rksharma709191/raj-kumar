package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val dao: GameProgressDao) {

    // Helper to get default or existing progress
    val progressFlow: Flow<GameProgress> = dao.getProgress().map { 
        it ?: GameProgress() 
    }

    suspend fun getProgress(): GameProgress {
        return dao.getProgressDirect() ?: GameProgress()
    }

    suspend fun saveProgress(progress: GameProgress) {
        dao.saveProgress(progress)
    }

    // Award Samosas and Stars
    suspend fun earnRewards(samosasEarned: Int, starsEarned: Int) {
        val current = getProgress()
        val newSamosas = current.totalSamosas + samosasEarned
        val newStars = current.totalStars + starsEarned
        
        // Dynamic title upgrades based on progress for extra engagement
        val newTitle = when {
            newStars >= 100 -> "Furfuri Legend 👑"
            newStars >= 50 -> "Samosa Champion 🌟"
            newStars >= 20 -> "Village Protector 🦸"
            else -> current.playerTitle
        }

        saveProgress(
            current.copy(
                totalSamosas = newSamosas,
                totalStars = newStars,
                playerTitle = newTitle
            )
        )
    }

    // Submit mini game scores
    suspend fun submitScore(gameType: String, score: Int) {
        val current = getProgress()
        val updated = when (gameType) {
            "samosa" -> {
                if (score > current.sSamosaHighScore) {
                    current.copy(sSamosaHighScore = score)
                } else current
            }
            "balloon" -> {
                if (score > current.sBalloonHighScore) {
                    current.copy(sBalloonHighScore = score)
                } else current
            }
            "chingum" -> {
                if (score > current.sChingumHighScore) {
                    current.copy(sChingumHighScore = score)
                } else current
            }
            else -> current
        }
        saveProgress(updated)
    }

    // Select avatar
    suspend fun selectAvatar(avatar: String) {
        val current = getProgress()
        saveProgress(current.copy(selectedAvatar = avatar))
    }

    // Unlock character using Samosas
    suspend fun unlockAvatar(avatar: String, cost: Int): Boolean {
        val current = getProgress()
        if (current.unlockedAvatars.split(",").contains(avatar)) {
            return true // Already unlocked
        }
        
        if (current.totalSamosas >= cost) {
            val updatedUnlocked = current.unlockedAvatars + ",$avatar"
            val remainingSamosas = current.totalSamosas - cost
            saveProgress(
                current.copy(
                    totalSamosas = remainingSamosas,
                    unlockedAvatars = updatedUnlocked,
                    selectedAvatar = avatar // auto select on purchase
                )
            )
            return true
        }
        return false // Insufficient samosas
    }

    suspend fun toggleAudio(sound: Boolean, music: Boolean) {
        val current = getProgress()
        saveProgress(current.copy(isSoundEnabled = sound, isMusicEnabled = music))
    }
}
