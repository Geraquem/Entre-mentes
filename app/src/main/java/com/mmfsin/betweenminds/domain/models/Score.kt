package com.mmfsin.betweenminds.domain.models

data class Score(
    var discovered: Boolean = false,
    var topNumber: Int? = null,
    var resultNumber: Int? = null,
    var points: Int? = null,
)
