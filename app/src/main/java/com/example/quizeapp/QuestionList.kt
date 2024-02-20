package com.example.quizeapp

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class QuestionList(
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val answer: Int
) : Serializable {
    var userSelectedAnswer: Int = 0
}