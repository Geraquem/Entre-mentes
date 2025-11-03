package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.Question
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.domain.models.Range
import com.mmfsin.betweenminds.domain.models.RangesPack

interface IPacksRepository {
    suspend fun getDataSelectedPack(gameType: String): Pair<String?, String?>
    suspend fun getPackQuestions(packId: String): List<Question>
    suspend fun getPackRanges(packId: String): List<Range>

    suspend fun getQuestionsPack(): List<QuestionsPack>
    fun getSelectedQPackId(): Int
    fun editSelectedQPackId(packNumber: Int)

    suspend fun getRangesPack(): List<RangesPack>
    fun getSelectedRPackId(): Int
    fun editSelectedRPackId(packNumber: Int)
}