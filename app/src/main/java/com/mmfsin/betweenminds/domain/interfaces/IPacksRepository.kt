package com.mmfsin.betweenminds.domain.interfaces

import com.mmfsin.betweenminds.domain.models.Pack
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import com.mmfsin.betweenminds.domain.models.RangesPack

interface IPacksRepository {
    suspend fun getPackById(packId: String): Pack?

    suspend fun getDataSelectedPack(gameType: String): Pair<String?, String?>

    suspend fun getQuestionsPack(): List<QuestionsPack>
    fun getSelectedQPackId(): Int
    fun editSelectedQPackId(packNumber: Int)

    suspend fun getRangesPack(): List<RangesPack>
    fun getSelectedRPackId(): Int
    fun editSelectedRPackId(packNumber: Int)

    fun setFreePacks()
    fun checkIfPacksAreFree(): Boolean
}