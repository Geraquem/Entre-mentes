package com.mmfsin.betweenminds.domain.usecases

import android.content.Context
import com.mmfsin.betweenminds.domain.interfaces.IOfflineRepository
import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.QuestionsPack
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetQuestionsPacksUseCase @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: IOfflineRepository,
    private val packsRepository: IPacksRepository,
) {
    suspend fun execute(): List<QuestionsPack> {
        val questions = repository.getQuestions()
        val packs = packsRepository.getQuestionsPack()

        val result = packs.map { pack ->
            pack.copy(
                questions = questions
                    .filter { it.pack == pack.packNumber }
                    .shuffled()
                    .take(4)
            )
        }

        return result.sortedBy { it.packNumber }
    }
}