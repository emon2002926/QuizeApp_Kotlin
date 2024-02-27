package com.example.quizeapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.quizeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedOption: Int? = null
    private var quizFinished = false

    private val viewModel:MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing UI components and listeners
        initializeUI()

        // Fetching quiz questions and displaying the first one
        fetchQuestionsAndShowFirst()

    }

    private fun initializeUI() {
        // Stopping shimmer animation and hiding layout until data is fetched
        binding.shimmerLayout.let {
            it.stopShimmer()
            it.visibility = View.VISIBLE
            binding.layout.visibility = View.GONE
        }

        // Setting click listeners for option layouts
        binding.option1Layout.setOnClickListener { selectedOption = 1; selectedOption(binding.option1Layout, binding.option1Icon) }
        binding.option2Layout.setOnClickListener { selectedOption = 2; selectedOption(binding.option2Layout, binding.option2Icon) }
        binding.option3Layout.setOnClickListener { selectedOption = 3; selectedOption(binding.option3Layout, binding.option3Icon) }
        binding.option4Layout.setOnClickListener { selectedOption = 4; selectedOption(binding.option4Layout, binding.option4Icon) }

        // Setting click listener for the "Next" button
        binding.nextQuestionButton.setOnClickListener {
            showNextQuestion()
        }
    }

    // Fetch questions from API and handle data callbacks
    private fun fetchQuestionsAndShowFirst() {
        viewModel.fetchDataFromApi(this, object : MainActivityViewModel.DataFetchCallback {
            override fun onDataFetched() {
                binding.shimmerLayout.let { it.stopShimmer()
                    it.visibility= View.GONE }
                binding.layout.visibility= View.VISIBLE
                showCurrentQuestion()
                startQuizTimer()

            }
            override fun onDataFetchError(error: String) {
                Log.e("DataFetchError", error)
            }
        })
    }

    // Display the current question
    private fun showCurrentQuestion() {
        val currentQuestion = viewModel.getCurrentQuestion()
        if (currentQuestion != null) {
            binding.questionTv.text = currentQuestion.question
            binding.option1Tv.text = currentQuestion.option1
            binding.option2Tv.text = currentQuestion.option2
            binding.option3Tv.text = currentQuestion.option3
            binding.option4Tv.text = currentQuestion.option4
            binding.currentQuestionPositionTv.text= "Question:  ${(viewModel.getCurrentQuestionIndex())+1}"
            Log.d("correctAnswer","totalCorrectAnswer:${viewModel.getCorrectAnswer()}")
        }
    }

    // Start the quiz timer
    private fun startQuizTimer() {
        viewModel.quizTimer(150, { currentTime ->
            // Update UI with current time
            binding.quizTimerTV.text = currentTime
        }, {
            // Timer finished,
            if (!quizFinished){
                finishQuiz()
            }

        })
    }

    // Show the next question or finish the quiz if there are no more questions
    private fun showNextQuestion() {
        if (selectedOption != null) {
            val nextQuestion = viewModel.getNextQuestion()
            if (nextQuestion != null) {
                viewModel.checkAnswer(selectedOption!!)
                showCurrentQuestion()
                selectedOption = null
                restOption()


            }
            else {
                viewModel.checkAnswer(selectedOption!!)
                finishQuiz()
            }
        } else {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
        }
    }

    // Finish the quiz and navigate to the result activity
    private fun finishQuiz(){
        quizFinished= true
        val intent = Intent(this@MainActivity, ActivityQuizResult::class.java)
        intent.putExtra("correctAnswer", viewModel.getCorrectAnswer())
        intent.putExtra("wrongAnswer", viewModel.getWrongAnswer())
        startActivity(intent)
        finish()
    }

    // Highlight the selected option
    private fun selectedOption(selectedOptionLayout: RelativeLayout, selectedOptionIcon: ImageView) {
        restOption()
        selectedOptionIcon.setImageResource(R.drawable.baseline_check_24)
        selectedOptionLayout.setBackgroundResource(R.drawable.round_back_selected_option)
    }

    // Reset all options to default state
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
}