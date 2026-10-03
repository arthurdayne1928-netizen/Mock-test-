package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SampleQuestions
import com.example.parser.QuestionParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches Kerala PSC`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kerala PSC", appName)
    }

    @Test
    fun `sample questions list is populated and valid`() {
        assertTrue(SampleQuestions.list.size >= 25)
        for (q in SampleQuestions.list) {
            assertTrue(q.question.isNotBlank())
            assertTrue(q.optionA.isNotBlank())
            assertTrue(q.optionB.isNotBlank())
            assertTrue(q.optionC.isNotBlank())
            assertTrue(q.optionD.isNotBlank())
            assertTrue(q.correctAnswerIndex in 0..3)
            assertTrue(q.category.isNotBlank())
        }
    }

    @Test
    fun `question parser parses plaintext accurately`() {
        val raw = """
            1. Capital of Kerala?
            A) Kochi
            B) Thiruvananthapuram
            C) Kozhikode
            D) Thrissur
            Answer: B
            Explanation: Thiruvananthapuram is the capital of Kerala.
            
            2. Longest river in Kerala?
            (A) Bharathapuzha
            (B) Pamba
            (C) Periyar
            (D) Chaliyar
            Answer: C) Periyar
        """.trimIndent()

        val result = QuestionParser.parsePlainText(raw, "Geography")
        assertEquals(2, result.questions.size)

        val q1 = result.questions[0]
        assertEquals("Capital of Kerala?", q1.question)
        assertEquals("Kochi", q1.optionA)
        assertEquals("Thiruvananthapuram", q1.optionB)
        assertEquals(1, q1.correctAnswerIndex) // B is index 1
        assertEquals("Thiruvananthapuram is the capital of Kerala.", q1.explanation)

        val q2 = result.questions[1]
        assertEquals("Longest river in Kerala?", q2.question)
        assertEquals(2, q2.correctAnswerIndex) // C is index 2
    }

    @Test
    fun `question parser converts to and from json`() {
        val sample = SampleQuestions.list.take(3)
        val json = QuestionParser.toJson(sample)
        assertTrue(json.contains("options"))

        val parsedBack = QuestionParser.parseJson(json, "Test")
        assertEquals(3, parsedBack.questions.size)
        assertEquals(sample[0].question, parsedBack.questions[0].question)
        assertEquals(sample[0].correctAnswerIndex, parsedBack.questions[0].correctAnswerIndex)
    }

    @Test
    fun `scoring with negative marking calculates correctly`() {
        val total = 20
        val correct = 14
        val wrong = 3
        val skipped = 3
        val negativeRate = 0.33333334f // 1/3 mark

        val rawScore = (correct * 1.0f) - (wrong * negativeRate)
        val roundedScore = Math.round(rawScore * 100f) / 100f

        // 14 - (3 * 0.333333) = 14 - 1.0 = 13.0
        assertEquals(13.0f, roundedScore, 0.05f)
    }
}
