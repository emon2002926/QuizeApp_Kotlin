package com.example.quizeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.quizeapp.databinding.ActivityQuizResultBinding

class ActivityQuizResult : AppCompatActivity() {
    private lateinit var binding: ActivityQuizResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showQuizResult()
        binding.backToHome.setOnClickListener{
            startActivity(Intent(this@ActivityQuizResult,StartActivity::class.java))}
    }

    private fun showQuizResult(){
        val correctAnswer = intent.getIntExtra("correctAnswer",0)
        val wrongAnswer = intent.getIntExtra("wrongAnswer",0)

        val score = (correctAnswer * 2) - wrongAnswer

        // Avoid recalculating the score for each condition
        val displayScore = if (score < 0) 0 else score
        binding.scoreTv.text = displayScore.toString()

        if (displayScore < 4) {
            binding.greetingsTV.text = "Oops?"
            binding.greetingsTitleTV.text = "You have failed to complete the quiz"
            binding.winnerIv.setImageResource(R.drawable.losser)
        }



        binding.correctTv.text=correctAnswer.toString()
        binding.incorrectTv.text= wrongAnswer.toString()

    }
}