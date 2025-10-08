package com.mmfsin.betweenminds.domain.models

data class OnlineData(
    val roomId: String,
    val round: Int,
    val isCreator: Boolean,
    val bullseyePosition: Int,
    val hint: String,
    val leftRange: String,
    val rightRange: String,
)
