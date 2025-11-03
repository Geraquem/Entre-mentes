package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IPacksRepository
import com.mmfsin.betweenminds.domain.models.Question
import javax.inject.Inject

class GetPackQuestionsUseCase @Inject constructor(
    private val repository: IPacksRepository,
) {
    suspend fun execute(packId: String): List<Question> = repository.getPackQuestions(packId)
}