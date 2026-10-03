package com.example.ui

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.QuestionRepository
import com.example.data.SampleQuestions
import com.example.data.model.QuestionEntity
import com.example.data.model.TestResultEntity
import com.example.parser.ParseResult
import com.example.parser.QuestionParser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    SETUP,
    QUIZ,
    RESULT,
    ADD_QUESTIONS,
    QUESTION_BANK,
    HISTORY
}

data class QuizItem(
    val entity: QuestionEntity,
    val options: List<String>,
    val correctIndex: Int,
    val selectedIndex: Int? = null,
    val isMarkedForReview: Boolean = false
) {
    val isAnswered: Boolean get() = selectedIndex != null
    val isCorrect: Boolean get() = selectedIndex != null && selectedIndex == correctIndex
    val isWrong: Boolean get() = selectedIndex != null && selectedIndex != correctIndex
    val isSkipped: Boolean get() = selectedIndex == null
}

data class QuizSummary(
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val negativeMarkRate: Float,
    val finalScore: Float,
    val maxScore: Float,
    val percentage: Float,
    val accuracyPercentage: Float,
    val timeTakenSeconds: Int,
    val totalTimeSeconds: Int,
    val category: String,
    val items: List<QuizItem>
)

data class UiNotification(val message: String)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuestionRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = QuestionRepository(db.questionDao(), db.testResultDao())

        // Preload default sample questions on first run so the bank is never bare
        viewModelScope.launch {
            repository.ensureDefaultSampleQuestionsIfEmpty()
        }
    }

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questionCount: StateFlow<Int> = repository.questionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val testResults: StateFlow<List<TestResultEntity>> = repository.testResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.SETUP)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Test Config in Setup Screen
    val selectedCategory = MutableStateFlow("All")
    val questionCountInput = MutableStateFlow(20)
    val timeMinutesInput = MutableStateFlow(15) // 0 for untimed
    val negativeMarkRate = MutableStateFlow(0.33333334f) // 0f, 0.25f, 0.3333f
    val shuffleOptions = MutableStateFlow(true)

    // Live Quiz State
    val quizItems = MutableStateFlow<List<QuizItem>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val remainingSeconds = MutableStateFlow(0)
    val isTimerRunning = MutableStateFlow(false)
    private var totalAllowedSeconds = 0
    private var countDownTimer: CountDownTimer? = null

    // Result State
    val lastQuizSummary = MutableStateFlow<QuizSummary?>(null)

    // Toast / Feedback
    private val _notifications = MutableSharedFlow<UiNotification>()
    val notifications = _notifications.asSharedFlow()

    // Editing Question (in Question Bank)
    val editingQuestion = MutableStateFlow<QuestionEntity?>(null)

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun postNotification(message: String) {
        viewModelScope.launch {
            _notifications.emit(UiNotification(message))
        }
    }

    fun startTest() {
        viewModelScope.launch {
            val cat = selectedCategory.value
            val count = questionCountInput.value
            val shouldShuffleOpts = shuffleOptions.value

            val entities = repository.getQuestionsForTest(
                category = cat,
                count = count,
                shuffleQuestions = true
            )

            if (entities.isEmpty()) {
                postNotification("No questions available for this category!")
                return@launch
            }

            val items = entities.map { entity ->
                val originalOpts = entity.getOptionsList()
                if (shouldShuffleOpts) {
                    val correctText = entity.getCorrectAnswerText()
                    val shuffled = originalOpts.shuffled()
                    val newCorrectIndex = shuffled.indexOf(correctText).coerceAtLeast(0)
                    QuizItem(
                        entity = entity,
                        options = shuffled,
                        correctIndex = newCorrectIndex
                    )
                } else {
                    QuizItem(
                        entity = entity,
                        options = originalOpts,
                        correctIndex = entity.correctAnswerIndex
                    )
                }
            }

            quizItems.value = items
            currentQuestionIndex.value = 0

            val minutes = timeMinutesInput.value
            totalAllowedSeconds = minutes * 60
            remainingSeconds.value = totalAllowedSeconds

            stopTimer()
            if (totalAllowedSeconds > 0) {
                startTimer(totalAllowedSeconds)
            } else {
                isTimerRunning.value = false
            }

            _currentScreen.value = AppScreen.QUIZ
        }
    }

    private fun startTimer(seconds: Int) {
        countDownTimer?.cancel()
        isTimerRunning.value = true

        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                remainingSeconds.value = 0
                isTimerRunning.value = false
                submitTest(autoSubmitted = true)
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        isTimerRunning.value = false
    }

    fun selectOption(optionIndex: Int) {
        val currIdx = currentQuestionIndex.value
        val items = quizItems.value.toMutableList()
        if (currIdx in items.indices) {
            val item = items[currIdx]
            val newSelection = if (item.selectedIndex == optionIndex) null else optionIndex
            items[currIdx] = item.copy(selectedIndex = newSelection)
            quizItems.value = items
        }
    }

    fun clearCurrentSelection() {
        val currIdx = currentQuestionIndex.value
        val items = quizItems.value.toMutableList()
        if (currIdx in items.indices) {
            items[currIdx] = items[currIdx].copy(selectedIndex = null)
            quizItems.value = items
        }
    }

    fun toggleMarkForReview() {
        val currIdx = currentQuestionIndex.value
        val items = quizItems.value.toMutableList()
        if (currIdx in items.indices) {
            val item = items[currIdx]
            items[currIdx] = item.copy(isMarkedForReview = !item.isMarkedForReview)
            quizItems.value = items
        }
    }

    fun goToQuestion(index: Int) {
        if (index in quizItems.value.indices) {
            currentQuestionIndex.value = index
        }
    }

    fun nextQuestion() {
        if (currentQuestionIndex.value < quizItems.value.size - 1) {
            currentQuestionIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (currentQuestionIndex.value > 0) {
            currentQuestionIndex.value -= 1
        }
    }

    fun submitTest(autoSubmitted: Boolean = false) {
        stopTimer()
        val items = quizItems.value
        val total = items.size
        val attempted = items.count { it.isAnswered }
        val correct = items.count { it.isCorrect }
        val wrong = items.count { it.isWrong }
        val skipped = total - attempted

        val negRate = negativeMarkRate.value
        val rawScore = (correct * 1.0f) - (wrong * negRate)
        val finalScore = (Math.round(rawScore * 100f) / 100f)
        val maxScore = total * 1.0f
        val percentage = if (maxScore > 0) ((finalScore / maxScore) * 100f).coerceAtLeast(0f) else 0f
        val accuracy = if (attempted > 0) ((correct.toFloat() / attempted) * 100f) else 0f

        val timeTaken = if (totalAllowedSeconds > 0) {
            (totalAllowedSeconds - remainingSeconds.value).coerceAtLeast(1)
        } else 0

        val summary = QuizSummary(
            totalQuestions = total,
            attemptedCount = attempted,
            correctCount = correct,
            wrongCount = wrong,
            skippedCount = skipped,
            negativeMarkRate = negRate,
            finalScore = finalScore,
            maxScore = maxScore,
            percentage = Math.round(percentage * 100f) / 100f,
            accuracyPercentage = Math.round(accuracy * 100f) / 100f,
            timeTakenSeconds = timeTaken,
            totalTimeSeconds = totalAllowedSeconds,
            category = selectedCategory.value,
            items = items
        )

        lastQuizSummary.value = summary
        _currentScreen.value = AppScreen.RESULT

        // Save in room database
        viewModelScope.launch {
            repository.saveTestResult(
                TestResultEntity(
                    category = selectedCategory.value,
                    totalQuestions = total,
                    attemptedCount = attempted,
                    correctCount = correct,
                    wrongCount = wrong,
                    skippedCount = skipped,
                    negativeMarkRate = negRate,
                    finalScore = finalScore,
                    percentage = summary.percentage,
                    timeTakenSeconds = timeTaken,
                    totalTimeAllowedSeconds = totalAllowedSeconds
                )
            )
            if (autoSubmitted) {
                postNotification("Time up! Test submitted automatically.")
            } else {
                postNotification("Test completed!")
            }
        }
    }

    fun retakeCurrentTest() {
        val current = quizItems.value
        if (current.isEmpty()) {
            startTest()
            return
        }

        val resetItems = current.map { item ->
            item.copy(selectedIndex = null, isMarkedForReview = false)
        }
        quizItems.value = resetItems
        currentQuestionIndex.value = 0

        val minutes = timeMinutesInput.value
        totalAllowedSeconds = minutes * 60
        remainingSeconds.value = totalAllowedSeconds
        stopTimer()
        if (totalAllowedSeconds > 0) {
            startTimer(totalAllowedSeconds)
        }
        _currentScreen.value = AppScreen.QUIZ
    }

    // Question Bank Actions
    fun insertQuestion(
        question: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int,
        category: String,
        explanation: String
    ) {
        viewModelScope.launch {
            val entity = QuestionEntity(
                question = question.trim(),
                optionA = optA.trim(),
                optionB = optB.trim(),
                optionC = optC.trim(),
                optionD = optD.trim(),
                correctAnswerIndex = correctIdx,
                category = if (category.isBlank()) "General" else category.trim(),
                explanation = explanation.trim()
            )
            repository.insertQuestion(entity)
            postNotification("Question saved to bank")
        }
    }

    fun updateQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.updateQuestion(question)
            editingQuestion.value = null
            postNotification("Question updated")
        }
    }

    fun deleteQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
            postNotification("Question deleted")
        }
    }

    fun importQuestions(questions: List<QuestionEntity>) {
        viewModelScope.launch {
            repository.insertQuestions(questions)
            postNotification("Imported ${questions.size} questions!")
        }
    }

    fun loadSampleQuestions() {
        viewModelScope.launch {
            repository.loadSampleQuestions()
            postNotification("Loaded 35 Kerala PSC sample questions!")
        }
    }

    fun clearAllQuestions() {
        viewModelScope.launch {
            repository.deleteAllQuestions()
            postNotification("Question bank cleared")
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            postNotification("Test history cleared")
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
