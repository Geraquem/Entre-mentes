package com.mmfsin.betweenminds.domain.models

data class OnlineData(
    val roomId: String,
    val isCreator: Boolean,
    val data: List<OnlineRoundData>
)

data class OnlineRoundData(
    val round: Int,
    val bullseyePosition: Int,
    val hint: String,
    val leftRange: String,
    val rightRange: String,
)
