package com.mmfsin.betweenminds.presentation.online.ranges.creator

import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.domain.models.Range

sealed class ORangesCreatorEvent {
    data class GetRanges(val ranges: List<Range>) : ORangesCreatorEvent()
    data class OtherPlayerData(val data: List<OnlineRoundData>) : ORangesCreatorEvent()
    data class OtherPlayerPoints(val otherPlayerPoints: Int) : ORangesCreatorEvent()
    data object GameRestarted : ORangesCreatorEvent()
    data object SomethingWentWrong : ORangesCreatorEvent()
}