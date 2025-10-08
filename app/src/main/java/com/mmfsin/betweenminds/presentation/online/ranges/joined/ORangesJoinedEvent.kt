package com.mmfsin.betweenminds.presentation.online.ranges.joined

import com.mmfsin.betweenminds.domain.models.OnlineRoundData
import com.mmfsin.betweenminds.domain.models.Range

sealed class ORangesJoinedEvent {
    data class GetRanges(val ranges: List<Range>) : ORangesJoinedEvent()
    data class OtherPlayerRanges(val ranges: List<OnlineRoundData>) : ORangesJoinedEvent()
    data object SomethingWentWrong : ORangesJoinedEvent()
}