package com.mmfsin.betweenminds.domain.models

data class SavedScore(
    val playerOneName: String,
    val playerTwoName: String,
    val points: Int,
    val notes: String,
    val date: String,
    val mode: String
)
