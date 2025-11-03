package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.data.repository.OfflineRepository
import com.mmfsin.betweenminds.domain.models.Question
import javax.inject.Inject

class GetPackQuestionsUseCase @Inject constructor(
    private val repository: OfflineRepository
) {
    suspend fun execute(packNumber: Int): List<Question> {
        val questions = repository.getQuestions()
        return questions.filter { it.pack == packNumber }
    }
}