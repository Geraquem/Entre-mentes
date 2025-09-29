package com.mmfsin.betweenminds.domain.models

data class ScoreQuestion(
    var discovered: Boolean = false,
    var actualQuestion: String? = null,
    var topNumbers: Pair<Int?, Int?>? = null,
    var bottomNumbers: Pair<Int?, Int?>? = null,
    var points: Int? = null,
    var activeRound: Boolean = false
)
