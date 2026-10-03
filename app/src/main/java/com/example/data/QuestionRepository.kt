package com.example.data

import com.example.data.dao.QuestionDao
import com.example.data.dao.TestResultDao
import com.example.data.model.QuestionEntity
import com.example.data.model.TestResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class QuestionRepository(
    private val questionDao: QuestionDao,
    private val testResultDao: TestResultDao
) {
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val categories: Flow<List<String>> = questionDao.getAllCategories()
    val questionCount: Flow<Int> = questionDao.getQuestionCount()
    val testResults: Flow<List<TestResultEntity>> = testResultDao.getAllResults()

    suspend fun ensureDefaultSampleQuestionsIfEmpty() {
        val current = questionDao.getSnapshotQuestions()
        if (current.isEmpty()) {
            questionDao.insertQuestions(SampleQuestions.list)
        }
    }

    suspend fun loadSampleQuestions() {
        questionDao.insertQuestions(SampleQuestions.list)
    }

    suspend fun insertQuestion(question: QuestionEntity): Long {
        return questionDao.insertQuestion(question)
    }

    suspend fun insertQuestions(questions: List<QuestionEntity>): List<Long> {
        return questionDao.insertQuestions(questions)
    }

    suspend fun updateQuestion(question: QuestionEntity) {
        questionDao.updateQuestion(question)
    }

    suspend fun deleteQuestion(question: QuestionEntity) {
        questionDao.deleteQuestion(question)
    }

    suspend fun deleteQuestionById(id: Long) {
        questionDao.deleteQuestionById(id)
    }

    suspend fun deleteAllQuestions() {
        questionDao.deleteAllQuestions()
    }

    suspend fun getQuestionsForTest(
        category: String,
        count: Int,
        shuffleQuestions: Boolean = true
    ): List<QuestionEntity> {
        val pool = if (category == "All" || category.isBlank()) {
            questionDao.getSnapshotQuestions()
        } else {
            questionDao.getSnapshotQuestionsByCategory(category)
        }

        val selected = if (shuffleQuestions) {
            pool.shuffled()
        } else {
            pool
        }

        return if (count > 0) selected.take(count) else selected
    }

    suspend fun saveTestResult(result: TestResultEntity): Long {
        return testResultDao.insertResult(result)
    }

    suspend fun clearHistory() {
        testResultDao.clearAllResults()
    }
}
