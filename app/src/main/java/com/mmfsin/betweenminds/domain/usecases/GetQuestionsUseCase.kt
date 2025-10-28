package com.mmfsin.betweenminds.domain.usecases

import com.mmfsin.betweenminds.domain.interfaces.IOfflineRepository
import java.util.Random
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val repository: IOfflineRepository
) {
    suspend fun execute() = repository.getQuestions().shuffled(Random(System.nanoTime()))
}