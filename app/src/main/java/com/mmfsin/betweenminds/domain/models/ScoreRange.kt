package com.mmfsin.betweenminds.domain.models

data class ScoreRange(
    var discovered: Boolean = false,
    var topNumber: Int? = null,
    var bottomNumber: Int? = null,
    var points: Int? = null,
)
