package com.example.quizeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.quizeapp.databinding.ActivityQuizResultBinding

class ActivityQuizResult : AppCompatActivity() {
    private var questionLists: List<QuestionList> = java.util.ArrayList()
    private lateinit var binding: ActivityQuizResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        questionLists = intent.getSerializableExtra("questionDetails") as List<QuestionList>

        binding.scoreTv.text = getCorrectAnswer().toString()
        binding.correctTv.text = getCorrectAnswer().toString()
        binding.incorrectTv.text= (10 - getCorrectAnswer()).toString()
        binding.backToHome.setOnClickListener {
            startActivity(Intent(this@ActivityQuizResult,StartActivity::class.java))
            finish()
        }
    }
    private fun getCorrectAnswer(): Int {
        var correctAnswer = 0
        for (i in questionLists.indices) {
            val getUserSelectedOption: Int = questionLists[i].userSelectedAnswer //Get User Selected Option
            val getQuestionAnswer: Int = questionLists[i].answer
//             Check UserSelected Answer is correct Answer
            if (getQuestionAnswer == getUserSelectedOption) {
                correctAnswer++
            }
        }
        return correctAnswer
    }
}