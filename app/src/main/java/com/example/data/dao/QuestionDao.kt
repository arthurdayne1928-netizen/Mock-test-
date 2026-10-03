package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY id DESC")
    fun getQuestionsByCategory(category: String): Flow<List<QuestionEntity>>

    @Query("SELECT DISTINCT category FROM questions WHERE category != '' ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM questions")
    fun getQuestionCount(): Flow<Int>

    @Query("SELECT * FROM questions ORDER BY id DESC")
    suspend fun getSnapshotQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY id DESC")
    suspend fun getSnapshotQuestionsByCategory(category: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>): List<Long>

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
}
