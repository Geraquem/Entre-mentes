package com.mmfsin.betweenminds.domain.interfaces

interface IPacksRepository {
    fun getSelectedQPackId(): Int
    fun editSelectedQPackId(packId: Int)

    fun getSelectedRPackId(): Int
    fun editSelectedRPackId(packId: Int)
}