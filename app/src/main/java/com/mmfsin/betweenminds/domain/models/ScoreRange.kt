package com.mmfsin.betweenminds.domain.models

data class ScoreRange(
    var discovered: Boolean = false,
    var activeRound: Boolean = false,
    var points: Int? = null,
)
