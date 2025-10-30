package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.QuestionPack
import com.mmfsin.betweenminds.domain.models.RangesPack

interface IPacksRepository {
    suspend fun getQuestionsPack(): List<QuestionPack>
    fun getSelectedQPackId(): Int
    fun editSelectedQPackId(packNumber: Int)

    suspend fun getRangesPack(): List<RangesPack>
    fun getSelectedRPackId(): Int
    fun editSelectedRPackId(packNumber: Int)
}