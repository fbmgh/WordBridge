package com.example.cardteacherapp

import android.os.Bundle
import android.view.View
import android.graphics.Color
import android.content.Context
import android.os.Build
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var closeButton: ImageButton
    private lateinit var word: TextView
    private lateinit var record: TextView
    private lateinit var firstAnswer: TextView
    private lateinit var secondAnswer: TextView
    private lateinit var thirdAnswer: TextView
    private lateinit var fourthAnswer: TextView
    private lateinit var correctLayout: ConstraintLayout
    private lateinit var incorrectLayout: ConstraintLayout
    private lateinit var skipButton: android.widget.Button
    private lateinit var continueCorrect: android.widget.Button
    private lateinit var continueIncorrect: android.widget.Button
    private lateinit var heart3: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart1: ImageView

    private var localRecord: Int = 0
    private var maxRecord: Int = 0

    var dictionary = Dictionary()
    private var correctAnswer: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        closeButton = findViewById(R.id.CloseButton)
        word = findViewById(R.id.Word)
        record = findViewById(R.id.Record)
        firstAnswer = findViewById(R.id.Answer1)
        secondAnswer = findViewById(R.id.Answer2)
        thirdAnswer = findViewById(R.id.Answer3)
        fourthAnswer = findViewById(R.id.Answer4)
        skipButton = findViewById(R.id.SkipButton)
        correctLayout = findViewById(R.id.CorrectLayout)
        incorrectLayout = findViewById(R.id.IncorrectLayout)
        continueCorrect = findViewById(R.id.ContinueCorrect)
        continueIncorrect = findViewById(R.id.ContinueIncorrect)
        heart3 = findViewById(R.id.Heart3)
        heart2 = findViewById(R.id.Heart2)
        heart1 = findViewById(R.id.Heart1)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        maxRecord = sharedPreferences.getInt("maxRecord", 0)

        record.text = maxRecord.toString()
        record.setTextColor(Color.parseColor("#C0C0C0"))

        closeButton.setOnClickListener { finish() }

        skipButton.setOnClickListener { handleSkip() }

        continueCorrect.setOnClickListener {
            correctLayout.visibility = View.GONE
            skipButton.visibility = View.VISIBLE
            generateQuizQuestion()
        }

        continueIncorrect.setOnClickListener {
            incorrectLayout.visibility = View.GONE
            skipButton.visibility = View.VISIBLE
            generateQuizQuestion()
        }

        firstAnswer.setOnClickListener { handleAnswerClick(firstAnswer) }
        secondAnswer.setOnClickListener { handleAnswerClick(secondAnswer) }
        thirdAnswer.setOnClickListener { handleAnswerClick(thirdAnswer) }
        fourthAnswer.setOnClickListener { handleAnswerClick(fourthAnswer) }

        generateQuizQuestion()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        DailyNotification(this)
    }

    private fun handleSkip() {
        when {
            heart3.visibility == View.VISIBLE -> heart3.visibility = View.INVISIBLE
            heart2.visibility == View.VISIBLE -> heart2.visibility = View.INVISIBLE
            heart1.visibility == View.VISIBLE -> {
                heart1.visibility = View.INVISIBLE

                if (maxRecord < localRecord) {
                    maxRecord = localRecord
                    val editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
                    editor.putInt("maxRecord", maxRecord)
                    editor.apply()
                }

                localRecord = 0
                record.text = maxRecord.toString()
                record.setTextColor(Color.parseColor("#C0C0C0"))

                heart3.visibility = View.VISIBLE
                heart2.visibility = View.VISIBLE
                heart1.visibility = View.VISIBLE
            }
        }

        generateQuizQuestion()
    }

    private fun handleAnswerClick(answer: TextView) {
        skipButton.visibility = View.GONE

        if (answer.text == correctAnswer) {
            correctLayout.visibility = View.VISIBLE
            localRecord++
            record.text = localRecord.toString()

            if (localRecord > maxRecord) {
                record.setTextColor(Color.parseColor("#FFDB4D"))
            } else {
                record.setTextColor(Color.BLACK)
            }
        } else {
            incorrectLayout.visibility = View.VISIBLE

            when {
                heart3.visibility == View.VISIBLE -> heart3.visibility = View.INVISIBLE
                heart2.visibility == View.VISIBLE -> heart2.visibility = View.INVISIBLE
                heart1.visibility == View.VISIBLE -> {
                    heart1.visibility = View.INVISIBLE

                    if (maxRecord < localRecord) {
                        maxRecord = localRecord
                        val editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
                        editor.putInt("maxRecord", maxRecord)
                        editor.apply()
                    }

                    localRecord = 0
                    record.text = maxRecord.toString()
                    record.setTextColor(Color.parseColor("#C0C0C0"))

                    heart3.visibility = View.VISIBLE
                    heart2.visibility = View.VISIBLE
                    heart1.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun generateQuizQuestion() {
        record.text = localRecord.toString()
        record.setTextColor(Color.BLACK)

        val randomWord = dictionary.getRandomWord()
        word.text = randomWord
        correctAnswer = dictionary.getTranslation(randomWord)

        val allWords = dictionary.getWords().values.toMutableList()
        allWords.remove(correctAnswer)
        val incorrectWords = allWords.shuffled().take(3)

        val answerOptions = incorrectWords.toMutableList().apply { correctAnswer?.let { add(it) } }.shuffled()

        listOf(firstAnswer, secondAnswer, thirdAnswer, fourthAnswer).forEachIndexed { index, textView ->
            textView.text = answerOptions[index]
        }
    }
}
