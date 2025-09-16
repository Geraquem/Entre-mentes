package com.mmfsin.betweenminds.domain.models

data class ScoreQuestion(
    var discovered: Boolean = false,
    var topNumber: Pair<Int,Int>? = null,
    var bottomNumber: Pair<Int,Int>? = null,
    var points: Int? = null,
)
