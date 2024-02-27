package com.example.quizeapp

data class QuizData(
    val question:String,
//    val options:List<String>
    val option1:String,
    val option2:String,
    val option3:String,
    val option4:String,
    val correctAnswer:Int)

