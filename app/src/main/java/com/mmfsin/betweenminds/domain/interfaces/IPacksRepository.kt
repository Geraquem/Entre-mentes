package com.mmfsin.betweenminds.domain.interfaces

interface IPacksRepository {
    fun getQuestionsPackId(): Int
    fun editQuestionsPackId(packId: Int)

    fun getRangesPackId(): Int
    fun editRangesPackId(packId: Int)
}