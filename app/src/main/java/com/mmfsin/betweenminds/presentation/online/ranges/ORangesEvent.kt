package com.mmfsin.betweenminds.presentation.online.ranges

import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.domain.models.Range

sealed class ORangesEvent {
    data class GetRanges(val ranges: List<Range>) : ORangesEvent()
    data class OtherPlayerData(val data: List<OnlineRoundData>) : ORangesEvent()
    data class OtherPlayerPoints(val otherPlayerPoints: Int) : ORangesEvent()
    data object GameRestarted : ORangesEvent()
    data object SomethingWentWrong : ORangesEvent()
}