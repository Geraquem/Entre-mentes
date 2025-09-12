package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IQuestionsRepository
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val repository: IQuestionsRepository
) {
    suspend fun execute() = repository.getQuestions()//.shuffled()
}