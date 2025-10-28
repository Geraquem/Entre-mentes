package com.mmfsin.betweenminds.domain.models

open class RangesPackDTO(
    var packId: Long = 0,
    var packName: String = "",
    var rages: List<Range> = emptyList()
)
