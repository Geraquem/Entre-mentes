package com.mmfsin.betweenminds.presentation.online.ranges.creator

import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.domain.models.Range

sealed class ORangesCreatorEvent {
    data class GetRanges(val ranges: List<Range>) : ORangesCreatorEvent()
    data class OtherPlayerRanges(val ranges: List<OnlineRoundData>) : ORangesCreatorEvent()
    data object SomethingWentWrong : ORangesCreatorEvent()
}