package com.mmfsin.betweenminds.presentation.packs.ranges.adapter

import com.mmfsin.betweenminds.domain.models.RangesPack

interface IRangesPackListener {
    fun selectPack(packNumber: Int)
    fun seeMore(pack: RangesPack)
    fun purchase(packId: String)
}