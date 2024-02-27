package com.example.quizeapp

import ApiResponseParser
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivityViewModel: ViewModel() {
    private var questionList: List<QuizData>? = null
    private var currentQuestionPosition = 0
    private var correctAnswer = 0
    private var wrongAnswer = 0
    private var callback: DataFetchCallback? = null
    private var countDownTimer: CountDownTimer? = null

    /**
     * Callback interface for fetching data from API.
     */
    interface DataFetchCallback {
        fun onDataFetched()
        fun onDataFetchError(error: String)
    }

    /**
     * Fetches data from the API.
     * @param context The context of the calling activity.
     * @param callback The callback for handling data fetch events.
     */
    fun fetchDataFromApi(context: Context, callback: DataFetchCallback) {
        this.callback = callback
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val query =
            "SELECT * FROM `question` WHERE `subjects` LIKE 'BA' ORDER BY id DESC LIMIT 5"
        val API_URL =
            "https://www.emon.pixatone.com/Test%20Api%27s/holder.php?apiKey=abc123&apiNum=1&IA=10&query=$query"

        val stringRequest = StringRequest(
            Request.Method.GET, API_URL,
            { response ->
                questionList = ApiResponseParser.parseResponse(response)
                callback.onDataFetched()
            },
            { error ->
                Log.e("VolleyError", error.toString())
                callback.onDataFetchError(error.toString())
            }
        )
        queue.add(stringRequest)
    }

    fun getCurrentQuestion(): QuizData? {
        return questionList?.getOrNull(currentQuestionPosition)
    }

    fun getNextQuestion(): QuizData? {
        currentQuestionPosition++
        return questionList?.getOrNull(currentQuestionPosition)
    }

    fun checkAnswer(userSelectedAnswer: Int) {
        val answer = questionList?.getOrNull(currentQuestionPosition)
        if (answer != null) {
            Log.d("correctAnswer","answer:${answer.correctAnswer}")
        }

        Log.d("correctAnswer","userSelectedAnswer: $userSelectedAnswer")
        if (answer != null && userSelectedAnswer == answer.correctAnswer) {
            correctAnswer ++
            Log.d("correctAnswer","Answer is correct")

        } else {
            wrongAnswer++
            Log.d("correctAnswer","Answer is wrong")

        }
    }

    fun getCorrectAnswer():Int{
        return correctAnswer
    }
    fun getWrongAnswer():Int{
        return wrongAnswer
    }
    fun getCurrentQuestionIndex(): Int {
        return currentQuestionPosition
    }

    /**
     * Initiates a countdown timer for the quiz.
     * @param maxTimerSeconds The maximum duration of the timer in seconds.
     * @param timerTickCallback The callback function to be called on each timer tick.
     * @param timerFinishCallback The callback function to be called when the timer finishes.
     */
    fun quizTimer(
        maxTimerSeconds: Int,
        timerTickCallback: (String) -> Unit,
        timerFinishCallback: () -> Unit
    ) {
        countDownTimer = object : CountDownTimer((maxTimerSeconds * 1000L), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val getHour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val getMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val getSecond = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                val currentTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    getHour,
                    getMinutes - TimeUnit.HOURS.toMinutes(getHour),
                    getSecond - TimeUnit.MINUTES.toSeconds(getMinutes)
                )
                timerTickCallback(currentTime) // Notify UI with current time
            }

            override fun onFinish() {
                timerFinishCallback() // Notify UI when the timer finishes
            }
        }
        countDownTimer?.start()
    }



}
