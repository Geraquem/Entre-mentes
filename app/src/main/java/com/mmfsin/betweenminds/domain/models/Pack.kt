package com.mmfsin.betweenminds.domain.models

open class Pack(
    var packId: String = "",
    var packType: String = "",
    var packNumber: Int = 0,
    var packTitle: String,
    var packDescription: String,
    var packPrice: String,
    var packIcon: String,
    var selected: Boolean = false,
    var purchased: Boolean = false,
)