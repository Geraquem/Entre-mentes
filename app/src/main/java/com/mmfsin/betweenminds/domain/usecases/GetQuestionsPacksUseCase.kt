package com.mmfsin.betweenminds.domain.usecases

import android.content.Context
import com.mmfsin.betweenminds.R
import com.mmfsin.betweenminds.domain.interfaces.IOfflineRepository
import com.mmfsin.betweenminds.domain.models.QuestionPack
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetQuestionsPacksUseCase @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: IOfflineRepository
) {
    suspend fun execute(): List<QuestionPack> {
        val questions = repository.getQuestions()
        val result = mutableListOf<QuestionPack>()

        for (i in 0..2) {
            val titleAndDescription = getPackTitleAndDescription(i)
            val pack = QuestionPack(
                packId = i,
                packName = getPackName(i),
                packTitle = titleAndDescription.first,
                packDescription = titleAndDescription.second,
                questions = questions.filter { it.pack.toInt() == i }
            )
            result.add(pack)
        }

        return result
    }

    /** Poner el mismo pack que tiene el Google Play Console */
    private fun getPackName(i: Int): String {
        return when (i) {
            1 -> "CouplesPack"
            2 -> "????Pack"
            else -> "Free"
        }
    }

    /** Poner el mismo pack que tiene el Google Play Console */
    private fun getPackTitleAndDescription(i: Int): Pair<Int, Int> {
        return when (i) {
            1 -> Pair(R.string.pack_questions_1_title, R.string.pack_questions_1_description)
            2 -> Pair(R.string.pack_questions_2_title, R.string.pack_questions_2_description)
            else -> Pair(R.string.pack_questions_0_title, R.string.pack_questions_0_description)
        }
    }
}