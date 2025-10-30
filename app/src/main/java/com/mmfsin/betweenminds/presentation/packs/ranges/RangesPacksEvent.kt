package com.mmfsin.betweenminds.presentation.packs.ranges

import com.mmfsin.betweenminds.domain.models.RangesPack

sealed class RangesPacksEvent {
    data class RangesPacks(val packs: List<RangesPack>) : RangesPacksEvent()
    data class SelectedPack(val selected: Int) : RangesPacksEvent()
    data class NewPackSelected(val packNumber: Int) : RangesPacksEvent()
    data object SomethingWentWrong : RangesPacksEvent()
}