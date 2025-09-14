package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IQuestionsRepository
import javax.inject.Inject

class GetRangesUseCase @Inject constructor(
    private val repository: IQuestionsRepository
) {
    suspend fun execute() = repository.getRanges()//.shuffled()
}