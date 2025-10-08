package com.mmfsin.betweenminds.presentation.online.ranges.creator

import com.mmfsin.betweenminds.domain.models.Range

sealed class ORangesCreatorEvent {
    data class GetRanges(val ranges: List<Range>) : ORangesCreatorEvent()
    data object SomethingWentWrong : ORangesCreatorEvent()
}