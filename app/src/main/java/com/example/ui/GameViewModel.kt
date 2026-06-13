package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameProgress
import com.example.data.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameProgressDao())
    }

    // Expose reactive stream of game progress
    val gameProgress: StateFlow<GameProgress> = repository.progressFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameProgress()
        )

    fun onGameFinished(gameType: String, score: Int, samosasCollected: Int, starsEarned: Int) {
        viewModelScope.launch {
            repository.submitScore(gameType, score)
            repository.earnRewards(samosasCollected, starsEarned)
        }
    }

    fun selectAvatar(avatar: String) {
        viewModelScope.launch {
            repository.selectAvatar(avatar)
        }
    }

    fun unlockAvatar(avatar: String, cost: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val success = repository.unlockAvatar(avatar, cost)
            if (success) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun toggleAudio(sound: Boolean, music: Boolean) {
        viewModelScope.launch {
            repository.toggleAudio(sound, music)
        }
    }
}
