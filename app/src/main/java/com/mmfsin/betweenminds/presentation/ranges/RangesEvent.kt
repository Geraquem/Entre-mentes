package com.mmfsin.betweenminds.presentation.ranges

import com.mmfsin.betweenminds.domain.models.Range

sealed class RangesEvent {
    data class Ranges(val ranges: List<Range>) : RangesEvent()
    data object SomethingWentWrong : RangesEvent()
}