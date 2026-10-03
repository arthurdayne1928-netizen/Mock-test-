package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswerIndex: Int, // 0 for A, 1 for B, 2 for C, 3 for D
    val category: String = "General",
    val explanation: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getOptionsList(): List<String> = listOf(optionA, optionB, optionC, optionD)

    fun getCorrectAnswerLetter(): String = when (correctAnswerIndex) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> "A"
    }

    fun getCorrectAnswerText(): String = when (correctAnswerIndex) {
        0 -> optionA
        1 -> optionB
        2 -> optionC
        3 -> optionD
        else -> optionA
    }
}
