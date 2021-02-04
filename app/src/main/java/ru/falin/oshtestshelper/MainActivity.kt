package ru.falin.oshtestshelper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private val OPEN_REQUEST_CODE = 41
    lateinit var sharedPreference: SharedPreferences


    private var currentSelectedQuestion = 0
    private lateinit var shuffledListOfQuestion: MutableList<Question>
    private var wrongAnswersListOfQuestion: MutableList<Question> = mutableListOf()
    private var currentQuestion: Int = 0
    private var correctAnswerCount: Int = 0
    private var wrongAnswerCount: Int = 0
    private var isAnswered = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        question_text.movementMethod = ScrollingMovementMethod()

        answer_1_text.setOnClickListener {
            if (isAnswered) {
                return@setOnClickListener
            }

            it.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCurrentSelectedQuestion))

            answer_2_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_3_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_4_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            currentSelectedQuestion = 1
            //Toast.makeText(this, "Answ1", Toast.LENGTH_SHORT).show()


        }

        answer_2_text.setOnClickListener {
            if (isAnswered) {
                return@setOnClickListener
            }

            it.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCurrentSelectedQuestion))

            answer_1_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_3_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_4_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            currentSelectedQuestion = 2

        }

        answer_3_text.setOnClickListener {
            if (isAnswered) {
                return@setOnClickListener
            }

            it.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCurrentSelectedQuestion))

            answer_1_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_2_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_4_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            currentSelectedQuestion = 3

        }

        answer_4_text.setOnClickListener {
            if (isAnswered) {
                return@setOnClickListener
            }

            it.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCurrentSelectedQuestion))

            answer_1_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_2_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_3_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            currentSelectedQuestion = 4

        }


        get_answer_button.setOnClickListener {

            if (currentSelectedQuestion == 0) {
                Toast.makeText(this, "Нужно выбрать ответ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isAnswered) {
                return@setOnClickListener
            }

            isAnswered = true

            for (i in 0 until answers_layout.childCount) {
                val ch = answers_layout.getChildAt(i)

                if (ch.tag == true) {
                    ch.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorrectAnswer))
                } else if (currentSelectedQuestion == i + 1) {
                    ch.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWrongAnswer))
                    wrongAnswersListOfQuestion.add(shuffledListOfQuestion[currentQuestion])
                }

                // Считаем количество правильных / неправильных ответов


            }

            // Согрешил против веры в производительность
            if (!wrongAnswersListOfQuestion.contains(shuffledListOfQuestion[currentQuestion])) correctAnswerCount++ else wrongAnswerCount++

            correct_answers_count.text = correctAnswerCount.toString()
            wrong_answer_count.text = wrongAnswerCount.toString()

        }

        go_next_button.setOnClickListener {
            if (!isAnswered) {
                Toast.makeText(this@MainActivity, "Надо ответить на вопрос", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentQuestion++
            currentSelectedQuestion = 0
            isAnswered = false

            answer_1_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_2_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_3_text.background = ContextCompat.getDrawable(this, R.drawable.border)
            answer_4_text.background = ContextCompat.getDrawable(this, R.drawable.border)

            if (currentQuestion < shuffledListOfQuestion.count()) {
                setupContent(shuffledListOfQuestion[currentQuestion])
            } else {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Все вопросы показаны")
                    .setMessage("Что делать дальше?")
                    .setPositiveButton("Начать весь тест заново") { _, _ ->
                        Toast.makeText(applicationContext, "Вопросы будут перемешаны", Toast.LENGTH_SHORT).show()
                        finish() // Прости производительность опять
                        startActivity(intent)
                        //reDoWrongQuestions()
                    }
                    .setNegativeButton("Повторить неправильные ответы") { _, _ ->
                        //shuffledListOfQuestion.clear()
                        //shuffledListOfQuestion.addAll(wrongAnswersListOfQuestion)
                        if (wrongAnswersListOfQuestion.count() != 0) {
                            reDoWrongQuestions()
                        } else {
                            Toast.makeText(applicationContext, "Все вопросы отвечены правильно", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(intent)
                        }

                    }
                    .setCancelable(false)
                    .create()
                    .show()
            }
        }

//        go_back_button.setOnClickListener {
//            if (currentQuestion > 0) {
//                currentQuestion--
//            }
//
//            currentSelectedQuestion = 0
//
//            answer_1_text.background = ContextCompat.getDrawable(this, R.drawable.border)
//            answer_2_text.background = ContextCompat.getDrawable(this, R.drawable.border)
//            answer_3_text.background = ContextCompat.getDrawable(this, R.drawable.border)
//            answer_4_text.background = ContextCompat.getDrawable(this, R.drawable.border)
//
//            setupContent()
//        }

        sharedPreference = getSharedPreferences("prefs_oshhelper2", Context.MODE_PRIVATE)

        val pathToFile = sharedPreference.getString("uri", "none")


        if (pathToFile != "none") {
            val currentUri = Uri.parse(pathToFile)
            val questions = readFileContent(currentUri)
            shuffledListOfQuestion = questions.shuffled().toMutableList()
            if (questions.count() != 0) {
                setupContent(shuffledListOfQuestion[currentQuestion])
            } else {
                askForPathToFile()
            }

        } else {
            askForPathToFile()
        }


    }

    private fun askForPathToFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, OPEN_REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var currentUri: Uri? = null

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == OPEN_REQUEST_CODE) {

                data?.let {
                    currentUri = it.data



                    try {
                        val questions = readFileContent(currentUri)
                        shuffledListOfQuestion = questions.toMutableList().shuffled().toMutableList()

                        sharedPreference.edit()
                            .putString("uri", currentUri.toString())
                            .apply()

                        setupContent(shuffledListOfQuestion[currentQuestion])
                    } catch (e: IOException) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Ошибочка")
                            .setMessage("Необходимо указать путь к файлу с вопросами")
                            .setPositiveButton("Указать путь") { _, _ ->
                                askForPathToFile()
                            }
                            .setNegativeButton("Нет, закрой приложение") { _, _ ->
                                finishAffinity()
                            }
                            .create()
                            .show()

                    }

                }

            }

        }

    }

    private fun setupContent(question: Question) {
        scrollview.fullScroll(ScrollView.FOCUS_UP)
        isAnswered = false


        question_text.text = question.questionText
        val answers = question.answers.shuffled()

        answer_1_text.apply {
            text = answers[0].text
            tag = answers[0].isCorrect
        }

        answer_2_text.apply {
            text = answers[1].text
            tag = answers[1].isCorrect
        }

        answer_3_text.apply {
            text = answers[2].text
            tag = answers[2].isCorrect
        }

        answer_4_text.apply {
            text = answers[3].text
            tag = answers[3].isCorrect
        }

        question_count_text.text = "${currentQuestion + 1}/${shuffledListOfQuestion.count()}"

    }

    private fun reDoWrongQuestions() {
        //shuffledListOfQuestion = shuffledListOfQuestion.shuffled()
        shuffledListOfQuestion.clear()
        shuffledListOfQuestion.addAll(wrongAnswersListOfQuestion.shuffled())
        wrongAnswersListOfQuestion.clear()
        currentQuestion = 0
        correctAnswerCount = 0
        wrongAnswerCount = 0
        correct_answers_count.text = correctAnswerCount.toString()
        wrong_answer_count.text = wrongAnswerCount.toString()

        setupContent(shuffledListOfQuestion[currentQuestion])

    }

    private fun readFileContent(uri: Uri?): List<Question> {
        val listOfQuestions = mutableListOf<Question>()

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileReader = BufferedReader(
                InputStreamReader(
                    inputStream
                )
            )

            val csvParser = CSVParser(
                fileReader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withDelimiter(';')
            )


            for (csvRecord in csvParser.records) {
                val question = Question(
                    csvRecord[0].toInt(),                       // Номер вопроса
                    csvRecord[1],                               // Текст вопроса
                    listOf(
                        Answer(csvRecord[2], true),      // Правильный ответ
                        Answer(csvRecord[3], false),
                        Answer(csvRecord[4], false),
                        Answer(csvRecord[5], false)
                    )
                )
                listOfQuestions.add(question)
            }
        } catch (e: IOException) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Ошибочка")
                .setMessage("Что-то не так со структурой файла ${e.message}")
                .setPositiveButton("Это печально. Надо указать другой файл") { _, _ ->
                    askForPathToFile()
                }
                .setNegativeButton("Это печально. Закрой приложение") { _, _ ->
                    finishAffinity()
                }
                .create()
                .show()
        } catch (e: Exception) {
            //Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                //fileReader!!.close()
                //csvParser!!.close()
            } catch (e: IOException) {
                println("Closing fileReader/csvParser Error!")
                e.printStackTrace()
            }
        }

        return listOfQuestions
    }


}

