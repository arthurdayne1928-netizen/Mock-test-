package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String,
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val negativeMarkRate: Float, // e.g. 0.3333f or 0.25f or 0f
    val finalScore: Float,
    val percentage: Float,
    val timeTakenSeconds: Int,
    val totalTimeAllowedSeconds: Int
)
