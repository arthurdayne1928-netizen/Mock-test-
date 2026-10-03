package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.KasavuHeader
import com.example.ui.screens.AddQuestionsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LiveQuizScreen
import com.example.ui.screens.MockTestSetupScreen
import com.example.ui.screens.QuestionBankScreen
import com.example.ui.screens.ResultReviewScreen
import com.example.ui.theme.KeralaIvoryBg
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val bankCount by viewModel.questionCount.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.notifications.collectLatest { notification ->
            snackbarHostState.showSnackbar(notification.message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = KeralaIvoryBg,
        topBar = {
            // Keep the KasavuHeader persistent across general tabs;
            // During live quiz, we allow full screen focus with the quiz bar
            if (currentScreen != AppScreen.QUIZ) {
                KasavuHeader(
                    currentScreen = currentScreen,
                    questionBankCount = bankCount,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(KeralaIvoryBg)
        ) {
            when (currentScreen) {
                AppScreen.SETUP -> {
                    MockTestSetupScreen(viewModel = viewModel)
                }
                AppScreen.QUIZ -> {
                    LiveQuizScreen(viewModel = viewModel)
                }
                AppScreen.RESULT -> {
                    ResultReviewScreen(viewModel = viewModel)
                }
                AppScreen.ADD_QUESTIONS -> {
                    AddQuestionsScreen(viewModel = viewModel)
                }
                AppScreen.QUESTION_BANK -> {
                    QuestionBankScreen(viewModel = viewModel)
                }
                AppScreen.HISTORY -> {
                    HistoryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
