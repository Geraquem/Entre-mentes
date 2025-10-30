package com.mmfsin.betweenminds.domain.models

data class RangesPack(
    var packId: String = "",
    var packNumber: Int = 0,
    var packTitle: String,
    var packDescription: String,
    var selected: Boolean = false,
    var purchased: Boolean = false,
    var ranges: List<Range> = emptyList()
)