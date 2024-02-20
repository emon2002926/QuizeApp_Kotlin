package com.example.quizeapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.quizeapp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.concurrent.TimeUnit
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentQuestionPosition:Int = 0
    private var selectedOption:Int= 0
    private var countDownTimer: CountDownTimer? = null
    private var quizFinished = false
    private var questionLists = ArrayList<QuestionList>()  // List to hold the questions retrieved from the server

    private lateinit var questionList:QuestionList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start shimmer animation and hide layout until questions are loaded
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.layout.visibility = View.GONE

        // Set click listeners for option layouts
        binding.option1Layout.setOnClickListener {selectedOption = 1; selectedOption(binding.option1Layout,binding.option1Icon)}
        binding.option2Layout.setOnClickListener {selectedOption = 2; selectedOption(binding.option2Layout,binding.option2Icon)}
        binding.option3Layout.setOnClickListener {selectedOption = 3; selectedOption(binding.option3Layout,binding.option3Icon)}
        binding.option4Layout.setOnClickListener {selectedOption = 4; selectedOption(binding.option4Layout,binding.option4Icon)}

        binding.nextQuestionButton.setOnClickListener{

            if (selectedOption !=0){
                // Record user's answer and move to the next question
                questionLists[currentQuestionPosition].userSelectedAnswer= selectedOption
                currentQuestionPosition++
                if (currentQuestionPosition == 10) {
                    // If all questions are answered, finish the quiz
                    countDownTimer?.cancel()
                    finishQuiz()
                }else{
                    // Display the next question
                    selectQuestion(currentQuestionPosition)
                }
                selectedOption= 0
            }else {
                Toast.makeText(this, "please Select A Option", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // Fetch questions from the server
        get()

    }

    fun get() {
        val queue = Volley.newRequestQueue(this)

        val query = "SELECT * FROM `question` WHERE `subjects` LIKE 'BA' ORDER BY id DESC LIMIT 10;"
        val API_URL = "https://www.emon.pixatone.com/Test%20Api%27s/holder.php?apiKey=abc123&apiNum=1&IA=10&query=$query"

        val stringRequest = StringRequest(
            Request.Method.GET, API_URL,
            { response ->
                try {
                    val data = JSONArray(response)
                    for (i in 0 until data.length()) {
                        // Stop shimmer animation, show layout, and parse question data
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.layout.visibility = View.VISIBLE

                        val row = data.getJSONObject(i)
                        val question = row.getString("question")
                        val option1 = row.getString("option1")
                        val option2 = row.getString("option2")
                        val option3 = row.getString("option3")
                        val option4 = row.getString("option4")
                        val answer = row.getInt("answer")

                        // Create a QuestionList object and add it to the list
                        questionList = QuestionList(question,option1,option2,option3,option4,answer)
                        questionLists.add(questionList)

                        // Display the first question and start the quiz timer
                        selectQuestion(currentQuestionPosition)
                        startQuizTimer(25)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("VolleyError", error.toString())
            }
        )
        queue.add(stringRequest)
    }

    private fun convertToUTF8(inputString: String): String {
        return String(inputString.toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
    }

    private fun selectQuestion(questionPosition:Int){
        // Reset option backgrounds and set question and options text
        restOption()
        binding.questionTv.text= convertToUTF8(questionLists[questionPosition].question)
        binding.option1Tv.text= convertToUTF8(questionLists[questionPosition].option1)
        binding.option2Tv.text= convertToUTF8(questionLists[questionPosition].option2)
        binding.option3Tv.text= convertToUTF8(questionLists[questionPosition].option3)
        binding.option4Tv.text= convertToUTF8(questionLists[questionPosition].option4)

        var i =questionPosition+1

        binding.currentQuestionPositionTv.text = "Question: ${i++}"

    }

    private fun restOption() {
        binding.option1Layout.setBackgroundResource(R.drawable.round_back_white50_10)
        binding.option2Layout.setBackgroundResource(R.drawable.round_back_white50_10)
        binding.option3Layout.setBackgroundResource(R.drawable.round_back_white50_10)
        binding.option4Layout.setBackgroundResource(R.drawable.round_back_white50_10)
        binding.option1Icon.setImageResource(R.drawable.round_back_white50_100)
        binding.option2Icon.setImageResource(R.drawable.round_back_white50_100)
        binding.option3Icon.setImageResource(R.drawable.round_back_white50_100)
        binding.option4Icon.setImageResource(R.drawable.round_back_white50_100)

    }
    /**
     * Highlights the selected option layout and icon.
     */
    private fun selectedOption(
        selectedOptionLayout: RelativeLayout,
        selectedOptionIcon: ImageView) {
        restOption()
        selectedOptionIcon.setImageResource(R.drawable.baseline_check_24)
        selectedOptionLayout.setBackgroundResource(R.drawable.round_back_selected_option)
    }

    private fun startQuizTimer(maxTimerSeconds: Int) {
        countDownTimer = object : CountDownTimer((maxTimerSeconds * 1000L), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val getHour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val getMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val getSecond = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                val generateTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    getHour,
                    getMinutes - TimeUnit.HOURS.toMinutes(getHour),
                    getSecond - TimeUnit.MINUTES.toSeconds(getMinutes)
                )
                binding.quizTimerTV.text = generateTime
            }
            override fun onFinish() {
                if (!quizFinished){
                    finishQuiz()
                }

            }
        }
        (countDownTimer as CountDownTimer).start()
    }

    private fun finishQuiz() {
        quizFinished = true
        val intent = Intent(this@MainActivity, ActivityQuizResult::class.java)
        val bundle = Bundle()
        // Start quiz result activity and pass question details
        bundle.putSerializable("questionDetails", questionLists as Serializable?)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }


}