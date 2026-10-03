package com.example.parser

import com.example.data.model.QuestionEntity
import org.json.JSONArray
import org.json.JSONObject

data class ParseResult(
    val questions: List<QuestionEntity>,
    val errors: List<String>
)

object QuestionParser {

    /**
     * Parses plain text with multiple questions in standard Kerala PSC format:
     *
     * 1. A straight line that touches a circle at only one point is called:
     * A) Chord
     * B) Tangent
     * C) Secant
     * D) Radius
     * Answer: B
     * Explanation: A tangent touches the circle at one point.
     */
    fun parsePlainText(rawText: String, defaultCategory: String = "General"): ParseResult {
        if (rawText.isBlank()) return ParseResult(emptyList(), emptyList())

        val lines = rawText.lines()
        val questions = mutableListOf<QuestionEntity>()
        val errors = mutableListOf<String>()

        // Split raw text into question blocks based on numbered prefixes or answer separators
        val blocks = splitIntoBlocks(lines)

        for ((index, blockLines) in blocks.withIndex()) {
            val blockNum = index + 1
            try {
                val parsed = parseSingleBlock(blockLines, defaultCategory, blockNum)
                if (parsed != null) {
                    questions.add(parsed)
                }
            } catch (e: Exception) {
                errors.add("Question #$blockNum: ${e.message ?: "Failed to parse question block"}")
            }
        }

        return ParseResult(questions, errors)
    }

    private fun splitIntoBlocks(lines: List<String>): List<List<String>> {
        val blocks = mutableListOf<List<String>>()
        var currentBlock = mutableListOf<String>()

        val questionStartRegex = Regex("""^(\d+[\.\)]|Q\d*[\.:\)]|\(\d+\))\s+.*""", RegexOption.IGNORE_CASE)

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                if (currentBlock.isNotEmpty() && currentBlock.any { it.contains(Regex("""^Ans(wer)?[\s\:\-]+""", RegexOption.IGNORE_CASE)) }) {
                    blocks.add(currentBlock)
                    currentBlock = mutableListOf()
                }
                continue
            }

            if (questionStartRegex.matches(trimmed) && currentBlock.isNotEmpty()) {
                blocks.add(currentBlock)
                currentBlock = mutableListOf(line)
            } else {
                currentBlock.add(line)
            }
        }

        if (currentBlock.isNotEmpty()) {
            blocks.add(currentBlock)
        }

        return blocks
    }

    private fun parseSingleBlock(
        lines: List<String>,
        defaultCategory: String,
        blockNum: Int
    ): QuestionEntity? {
        if (lines.isEmpty()) return null

        var questionText = ""
        var optA = ""
        var optB = ""
        var optC = ""
        var optD = ""
        var answerIndex = -1
        var explanation = ""
        var category = defaultCategory

        val optARegex = Regex("""^[\(\[]?[Aa][\)\.\:\-\]]\s*(.*)$""")
        val optBRegex = Regex("""^[\(\[]?[Bb][\)\.\:\-\]]\s*(.*)$""")
        val optCRegex = Regex("""^[\(\[]?[Cc][\)\.\:\-\]]\s*(.*)$""")
        val optDRegex = Regex("""^[\(\[]?[Dd][\)\.\:\-\]]\s*(.*)$""")

        val answerRegex = Regex("""^(?:Answer|Ans|Correct|Correct\s*Answer)[\s\:\-\.]+(.+)$""", RegexOption.IGNORE_CASE)
        val expRegex = Regex("""^(?:Explanation|Exp|Note)[\s\:\-\.]+(.+)$""", RegexOption.IGNORE_CASE)
        val catRegex = Regex("""^(?:Category|Subject)[\s\:\-\.]+(.+)$""", RegexOption.IGNORE_CASE)

        val questionLines = mutableListOf<String>()
        var foundOptions = false

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            when {
                optARegex.matches(trimmed) -> {
                    foundOptions = true
                    optA = optARegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                }
                optBRegex.matches(trimmed) -> {
                    foundOptions = true
                    optB = optBRegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                }
                optCRegex.matches(trimmed) -> {
                    foundOptions = true
                    optC = optCRegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                }
                optDRegex.matches(trimmed) -> {
                    foundOptions = true
                    optD = optDRegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                }
                answerRegex.matches(trimmed) -> {
                    val rawAns = answerRegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                    answerIndex = parseAnswerIndex(rawAns, listOf(optA, optB, optC, optD))
                }
                expRegex.matches(trimmed) -> {
                    explanation = expRegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                }
                catRegex.matches(trimmed) -> {
                    val catVal = catRegex.find(trimmed)?.groupValues?.get(1)?.trim() ?: ""
                    if (catVal.isNotBlank()) category = catVal
                }
                !foundOptions -> {
                    questionLines.add(trimmed)
                }
            }
        }

        questionText = questionLines.joinToString(" ")
        // Strip leading number like "1. " or "1) "
        questionText = questionText.replace(Regex("""^\d+[\.\)]\s*"""), "").trim()

        if (questionText.isBlank()) return null
        if (optA.isBlank() || optB.isBlank() || optC.isBlank() || optD.isBlank()) {
            throw IllegalArgumentException("Missing one or more options (A, B, C, D)")
        }
        if (answerIndex < 0 || answerIndex > 3) {
            throw IllegalArgumentException("Could not detect valid answer (A, B, C, or D)")
        }

        return QuestionEntity(
            question = questionText,
            optionA = optA,
            optionB = optB,
            optionC = optC,
            optionD = optD,
            correctAnswerIndex = answerIndex,
            category = if (category.isBlank()) "General" else category,
            explanation = explanation
        )
    }

    private fun parseAnswerIndex(rawAnswer: String, options: List<String>): Int {
        val trimmed = rawAnswer.trim()
        val upper = trimmed.uppercase()

        // Direct letter matches: "A", "B", "C", "D", or "A) ...", "(B)"
        val letterMatch = Regex("""^[\(\[]?([A-D])[\)\.\:\-\]]?""").find(upper)
        if (letterMatch != null) {
            val letter = letterMatch.groupValues[1]
            return when (letter) {
                "A" -> 0
                "B" -> 1
                "C" -> 2
                "D" -> 3
                else -> -1
            }
        }

        // Direct numeric matches: 1, 2, 3, 4
        if (upper.startsWith("1")) return 0
        if (upper.startsWith("2")) return 1
        if (upper.startsWith("3")) return 2
        if (upper.startsWith("4")) return 3

        // Match against option content text
        for (i in options.indices) {
            if (options[i].isNotBlank() && trimmed.contains(options[i], ignoreCase = true)) {
                return i
            }
        }

        return -1
    }

    /**
     * Parses JSON string array of questions
     */
    fun parseJson(jsonString: String, defaultCategory: String = "General"): ParseResult {
        val questions = mutableListOf<QuestionEntity>()
        val errors = mutableListOf<String>()

        try {
            val jsonArray = JSONArray(jsonString.trim())
            for (i in 0 until jsonArray.length()) {
                try {
                    val obj = jsonArray.getJSONObject(i)
                    val qText = obj.optString("question", obj.optString("q", "")).trim()
                    var optA = ""
                    var optB = ""
                    var optC = ""
                    var optD = ""

                    if (obj.has("options")) {
                        val opts = obj.getJSONArray("options")
                        if (opts.length() >= 4) {
                            optA = opts.getString(0)
                            optB = opts.getString(1)
                            optC = opts.getString(2)
                            optD = opts.getString(3)
                        }
                    } else {
                        optA = obj.optString("optionA", obj.optString("a", ""))
                        optB = obj.optString("optionB", obj.optString("b", ""))
                        optC = obj.optString("optionC", obj.optString("c", ""))
                        optD = obj.optString("optionD", obj.optString("d", ""))
                    }

                    var ansIndex = -1
                    if (obj.has("answer")) {
                        val ansAny = obj.get("answer")
                        if (ansAny is Int) {
                            ansIndex = if (ansAny in 0..3) ansAny else ansAny - 1
                        } else {
                            ansIndex = parseAnswerIndex(ansAny.toString(), listOf(optA, optB, optC, optD))
                        }
                    } else if (obj.has("correctAnswerIndex")) {
                        ansIndex = obj.getInt("correctAnswerIndex")
                    }

                    val cat = obj.optString("category", defaultCategory)
                    val exp = obj.optString("explanation", "")

                    if (qText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank() && optC.isNotBlank() && optD.isNotBlank() && ansIndex in 0..3) {
                        questions.add(
                            QuestionEntity(
                                question = qText,
                                optionA = optA,
                                optionB = optB,
                                optionC = optC,
                                optionD = optD,
                                correctAnswerIndex = ansIndex,
                                category = if (cat.isBlank()) defaultCategory else cat,
                                explanation = exp
                            )
                        )
                    } else {
                        errors.add("Item #${i + 1}: Missing fields or invalid answer")
                    }
                } catch (e: Exception) {
                    errors.add("Item #${i + 1}: ${e.message ?: "Invalid JSON object"}")
                }
            }
        } catch (e: Exception) {
            errors.add("Failed to parse JSON array: ${e.message}")
        }

        return ParseResult(questions, errors)
    }

    /**
     * Converts list of QuestionEntity into standard JSON export format
     */
    fun toJson(questions: List<QuestionEntity>): String {
        val array = JSONArray()
        val letters = listOf("A", "B", "C", "D")
        for (q in questions) {
            val obj = JSONObject()
            obj.put("id", q.id)
            obj.put("question", q.question)
            val opts = JSONArray()
            opts.put(q.optionA)
            opts.put(q.optionB)
            opts.put(q.optionC)
            opts.put(q.optionD)
            obj.put("options", opts)
            val letter = letters.getOrElse(q.correctAnswerIndex) { "A" }
            obj.put("answer", letter)
            obj.put("answerText", q.getCorrectAnswerText())
            obj.put("category", q.category)
            obj.put("explanation", q.explanation)
            array.put(obj)
        }
        return array.toString(2)
    }
}
